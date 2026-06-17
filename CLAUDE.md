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