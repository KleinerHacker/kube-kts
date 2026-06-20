# Kube KTS — Claude Context

## Projektbeschreibung

Kube KTS ist ein **Helm-Wrapper für Kubernetes**, der YAML-Dateien mit Go-Templates durch **Kotlin Scripts (KTS)** ersetzt — ähnlich wie Gradle-Build-Scripts. Entwickelt von KleinerHacker (alias Pfeiffer C Soft), lizenziert unter Apache 2.0.

**Kernvorteile gegenüber klassischen Helm-Templates:**
- Typsicherheit und Null-Safety (Compile-Zeit statt Runtime-Fehler)
- Volle IDE-Unterstützung (IntelliJ IDEA, VS Code)
- Saubere YAML-Struktur ohne Template-Logik-Interferenz
- Debuggbarkeit

> **Helm-Abdeckung:** Der aktuelle Umsetzungsstand des CLI-Wrappers gegenüber dem vollen
> Helm-Funktionsumfang wird in [HELM_SUPPORT.md](HELM_SUPPORT.md) gepflegt. Bei Änderungen an
> Kommandos/Flags in `apps/cli` diese Datei mitführen.

## Modulstruktur (Gradle Multi-Projekt)

| Modul | Pfad | Zweck |
|---|---|---|
| `libs:logging` | `libs/logging` | Logging-Abstraktion |
| `libs:api` | `libs/api` | Öffentliche DSL-API (`*Spec` + `*SpecBuilder` für K8s-Ressourcen) |
| `libs:definition` | `libs/definition` | Interne Definitionen |
| `libs:core` | `libs/core` | Kern-Engine (Kompilierung, Rendering, Validierung) |
| `apps:cli` | `apps/cli` | CLI-Applikation (`kube-kts` Kommando) |

Abhängigkeitsfluss: `cli` → `core` → (`api`, `definition`) → `logging`. Die Pipeline ist immer
**scan → compile → render → (merge) → Helm**.

### Wofür ist welches Modul zuständig? (Wo suche ich was?)

**`libs:logging`** — Reine Logging-/Konsolen-Hilfsmittel, keine Fachlogik.
- `LoggerUtils.kt` (Logger-Factory `logger()`), `AnsiUtils.kt` (ANSI-Farben), `SymbolUtils.kt`
  (Status-Symbole wie `symbolSubProcess` in Log-Ausgaben).
- *Hierher bei:* Änderungen an Log-Format, Farben, Symbolen.

**`libs:api`** — Die **öffentliche DSL**, die KTS-Skripte benutzen. Größtes Modul. Pro K8s-Konzept
ein Paar aus Datenmodell und Builder.
- `*Spec.kt` = Datenmodell (mit `@NoArg` für Jackson), `*SpecBuilder.kt` = Kotlin-Lambda-DSL
  (`deployment { }`, `service { }`, `container { } `, …). Einstieg/Wurzeltyp: `KubeSpec.kt`.
- Wert-Typen & Einheiten: `CpuValue.kt`, `MemoryValue.kt`, `RelativeValue.kt`, `KubeVersion.kt`,
  `Protocol.kt`, `MailAddress.kt` (Extensions wie `250.mCpu`, `1.giBytes`, `25.percent`).
- Values-Zugriff aus Skripten: `ValueAccess.kt` (`value`, `valueOrNull`, `array`, `map`, `exists`).
- Spezielle YAML-Serialisierung: `intern/jackson/*Serializer.kt` (z. B. `ProbeSpecSerializer`,
  `VolumeSpecSerializer`, `DurationInSecondsSerializer`), Jackson-Setup in `intern/utils/JsonUtils.kt`.
- *Hierher bei:* neue/erweiterte K8s-Ressource, neue DSL-Funktion, neues Feld, geänderte
  YAML-Ausgabeform einer Ressource.

**`libs:definition`** — Verbindet die DSL mit dem **Kotlin-Scripting-Host**: Definition der
Script-Templates und ihrer Compile-/Eval-Konfiguration (Default-Imports, Receiver).
- `SpecTemplate.kt` / `LibTemplate.kt` (Script-Definitionen für `*.spec.kts` bzw. `*.lib.kts`).
- `KubeKtsSpecCompilationConfiguration.kt` / `KubeKtsLibCompilationConfiguration.kt` (Default-Imports —
  hier wird festgelegt, was ohne `import` verfügbar ist) und die `*EvaluationConfiguration.kt`.
- *Hierher bei:* neue Default-Imports, Änderungen am Script-Receiver/-Kontext, Verhalten von
  `*.spec.kts` vs. `*.lib.kts`.

**`libs:core`** — Die **Engine**, die ein Repository einliest, kompiliert, rendert und YAML mergt.
- `scanner/` — `KubeKtsRepositoryScanner` + `Default…`: Verzeichnisbaum scannen, Dateitypen einordnen
  (`KubeKtsFile`, `KubeFile`).
- `builder/` — `DefaultKubeKtsRepositoryBuilder` (Orchestrierung) und `DefaultKotlinScriptProcessor`
  (Kompilieren/Ausführen der KTS; **Safe-/Unsafe-Prüfung** für `import`/FQN lebt hier).
- `renderer/` — `DefaultKubeHelmRenderer` / `DefaultKubeHelmRepositoryRenderer`: Specs → Helm-YAML-Dateien.
- `merge/` — `HelmYamlMerging` (Helm-Semantik) und `DefaultYamlMerging` (internes, `--experimental`-
  Verfahren mit Array-Merge-Strategien, `YamlMergeStrategy`).
- *Hierher bei:* Scan-/Compile-/Render-/Merge-Verhalten, Safe-vs-Unsafe-Logik, Fehler in der Pipeline.

**`apps:cli`** — Die ausführbare CLI (`kube-kts`), gebaut mit picocli; dünne Schicht über `core`.
- Einstieg: `Runner.kt` / `MainCommand.kt`; gemeinsame Basisklassen `Base*Command.kt`
  (z. B. `BaseRenderCommand`, `BaseRenderedHelmCommand`, `BaseDirectHelmCommand`).
- Ein Command pro CLI-Befehl: `RenderCommand.kt`, `TemplateCommand.kt`, `InstallCommand.kt`, … ;
  Helm-Weiterleitung gruppiert unter `commands/helm/` (`HelmArgsProvider`, `Helm*Options`).
- Globale Flags: `Flags.kt` (`--unsafe`, `--experimental`, `--debug`, …).
- *Hierher bei:* neuer/angepasster CLI-Befehl oder -Flag (dann auch `HELM_SUPPORT.md` + `docs/` pflegen).

## Helm-Repository-Struktur

```
helm/
├── Chart.spec.kts      # → Chart.yaml
├── values.yaml
└── templates/
    ├── deployment.spec.kts  # → deployment.yaml
    ├── service.spec.kts     # → service.yaml
    ├── helpers.lib.kts      # Hilfsfunktionen (wird nicht gerendert)
    └── ...
```

**KTS-Dateitypen:**
- `*.spec.kts` — Definiert eine Kubernetes-Ressource, wird zu YAML gerendert
- `*.lib.kts` — Definiert Hilfsfunktionen, die in allen `*.spec.kts`-Dateien verfügbar sind (nicht untereinander)
- `*.kts` — Legacy (wird weiterhin als Spec-Datei behandelt)

Legacy `.yaml`/`.yml`-Dateien werden unverändert kopiert (vollständige Rückwärtskompatibilität).

## CLI-Befehle

```bash
kube-kts validate <repo>                    # Repository validieren
kube-kts compile <repo>                     # KTS kompilieren
kube-kts render <repo> <target>             # Zu Helm-YAML rendern
kube-kts lint <repo> <target>               # Lint via Helm
kube-kts template <repo> <target> -n <name> # helm template ausführen
```

Wichtige Flags: `--unsafe` (erlaubt Imports in KTS), `--experimental` (YAML-Merge-Algorithmen).

## DSL-API

### Unterstützte Kubernetes-Ressourcen

| Ressource | DSL-Funktion | Spec / Builder |
|---|---|---|
| Chart-Metadaten | `chart(name, version) { }` | `ChartSpec` / `ChartSpecBuilder` |
| Deployment | `deployment { }` | `DeploymentSpec` / `DeploymentSpecBuilder` |
| Service | `service { }` | `ServiceSpec` / `ServiceSpecBuilder` |
| Ingress | `ingress { }` | `IngressSpec` / `IngressSpecBuilder` |
| Route (OpenShift, kein Standard-K8s) | `route { }` | `RouteSpec` / `RouteSpecBuilder` |
| Job | `job { }` | `JobSpec` / `JobSpecBuilder` |

### Values-Zugriff in KTS-Skripten

Pfade sind relativ zum `values:`-Root (kein `values.` Prefix nötig).

```kotlin
value<String>("spec.image")           // Exception wenn nicht vorhanden
valueOrNull<Int>("spec.replicas")      // null wenn nicht vorhanden
array<String>("metadata.tags")        // Liste
map<String>("metadata.labels")        // Key-Value-Map
exists("spec.replicas") { }           // Lambda nur wenn vorhanden

// Nested Access
value("spec.resources") {
    val cpu = it.value<String>("limits.cpu")
}
```

### Code-Konventionen

- Datenmodell: `*Spec.kt` (mit `@NoArg` für Jackson-Deserialisierung)
- DSL-Builder: `*SpecBuilder.kt` (Kotlin-Lambda-Extensions)
- Jackson-Serializer für spezielle YAML-Ausgaben: `libs/api/.../intern/jackson/`
- Besondere Extensions: `250.mCpu`, `1.giBytes`, `30.seconds.toJavaDuration()`, `25.percent`, `1.absolute`

## Dokumentation

- Quelle: `docs/` (MkDocs + Material Theme)
- Online: https://kleinerhacker.github.io/kube-kts/
- API-Docs (Dokka): https://kleinerhacker.github.io/kube-kts/dokka/html
- Lokal starten: Gradle-Task `runDocs`
- Deployen: Gradle-Task `deployDocs`