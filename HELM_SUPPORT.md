# Helm-Wrapper — Umsetzungsstand

Diese Datei dokumentiert, wie weit das `kube-kts` CLI-Tool Helm bereits als Wrapper abdeckt. Sie ist
die zentrale Merkhilfe für den Fortschritt „CLI ↔ vollumfängliche Helm-Unterstützung" und wird von
der [CLAUDE.md](CLAUDE.md) referenziert.

> **Pflege:** Beim Hinzufügen/Ändern von Kommandos oder Flags in `apps/cli` bitte diese Datei
> aktualisieren. Stand zuletzt geprüft: 2026-06-18.

## Architektur (Kurz)

- Render-basierte Helm-Kommandos leiten von `BaseRenderedHelmCommand` ab (`apps/cli/.../commands/`); sie
  durchlaufen *Scan → Compile → Render* und benötigen ein Repository.
- Render-lose Helm-Kommandos (operieren auf einem bestehenden Release, z. B. `status`) leiten von
  `BaseDirectHelmCommand` ab: **kein** Repository, **kein** Rendering — die Argumente werden direkt
  an Helm weitergereicht. Beide Basisklassen implementieren `HelmCommandLineProvider`.
- Verschachtelte Kommando-Gruppen (`get`, `repo`, `search`, `registry`, `show`, `dependency`, `diff`)
  leiten von `BaseGroupCommand` ab: sie rufen selbst kein Helm auf, sondern bündeln nur ihre
  Unterbefehle und geben bei Aufruf ohne Subcommand die Usage aus.
- Flags sind in wiederverwendbare Mixins gruppiert (`apps/cli/.../commands/helm/`):
  `HelmGlobalOptions`, `HelmValueOptions`, `HelmChartSourceOptions`, `HelmChartDownloadOptions`,
  `HelmRenderSharedOptions`, `HelmValueFileOptions`.
- `buildHelmCommandLine()` setzt die finale Argumentliste zusammen; die Ausführung läuft über den
  injizierbaren `HelmExecutor` (mockbar in Tests).
- Hilfe-Marker: `---->` = an Helm weitergereicht, `*` = experimentell, `!!!` = gefährlich
  (`AnsiLoggerUtils.kt`: `HELM_MARKER` / `EXPERIMENTAL_MARKER` / `DANGER_MARKER`).

## Kommando-Abdeckung

Legende: ✅ implementiert · 🟡 teilweise · ❌ nicht implementiert

### Eigene / interne Kommandos (kein direktes Helm-Pendant)

| Kommando | Status | Hinweis |
|---|---|---|
| `validate` | ✅ | Reine Strukturprüfung des KTS-Repos. |
| `compile` | ✅ | Führt die KTS-Skripte aus, baut das Objektmodell. |
| `render` | ✅ | Rendert das Repo zu einem Helm-Chart (`helm template`-Vorstufe ohne Helm-Aufruf). |

### Helm-gestützte Kommandos

| Kommando | Status | Flag-Abdeckung | Hinweis |
|---|---|---|---|
| `lint` | ✅ | vollständig | `helm lint` + globale Flags + `--set*`/`-f`. |
| `template` | ✅ | vollständig | `helm template` inkl. `-a`, `--show-only`, `--validate`, `--output-dir`, … |
| `install` | ✅ | vollständig | `helm install` inkl. `--atomic`, `--wait[-for-jobs]`, Chart-Source-Flags, … |
| `upgrade` | ✅ | vollständig | `helm upgrade` inkl. `-i/--install`, `--reuse-values`/`--reset-values`/`--reset-then-reuse-values`, `--cleanup-on-fail`, `--history-max`, `--take-ownership`, … |
| `uninstall` | ✅ | vollständig | `helm uninstall` inkl. `--cascade`, `--keep-history`, `--ignore-not-found`, … |
| `status` | ✅ | vollständig | `helm status` inkl. `--revision`, `--output`, `--show-desc`, `--show-resources`. **Render-los** (`BaseDirectHelmCommand`), kein Repository nötig. |
| `list` / `ls` | ✅ | vollständig | `helm list` inkl. `-a/-A`, `--deployed`/`--failed`/`--pending`/…, `-o`, `-l`, `--time-format`. Render-los. |
| `history` / `hist` | ✅ | vollständig | `helm history RELEASE` inkl. `--max`, `-o`. Render-los. |
| `rollback` | ✅ | vollständig | `helm rollback RELEASE [REVISION]` inkl. `--cleanup-on-fail`, `--force`, `--wait[-for-jobs]`, … Render-los. |
| `test` | ✅ | vollständig | `helm test RELEASE` inkl. `--filter`, `--hide-notes`, `--logs`, `--timeout`. Render-los. |
| `get` (all/values/manifest/hooks/notes/metadata) | ✅ | vollständig | Verschachtelt; je `RELEASE` inkl. `--revision`, `-o`, `--template`, `-a`. Render-los. |
| `repo` (add/update/list/remove) | ✅ | vollständig | Verschachtelt; `add NAME URL` inkl. Auth/TLS-Flags, `--no-update`/`--force-update`. Render-los. |
| `search` (repo/hub) | ✅ | vollständig | Verschachtelt; `[KEYWORD]` inkl. `--devel`, `-r`, `-l`, `--endpoint`, … Render-los. |
| `registry` (login/logout) | ✅ | vollständig | Verschachtelt; `HOST` inkl. `-u/-p`, `--password-stdin`, `--insecure`, … Render-los. |
| `show` (all/chart/values/readme/crds) | ✅ | vollständig | Verschachtelt; `CHART` inkl. Chart-Download-Flags; `show values` zusätzlich `--jsonpath`. Render-los. |
| `pull` / `fetch` | ✅ | vollständig | `helm pull CHART` inkl. `-d`, `--prov`, `--untar[dir]` + Chart-Download-Flags. Render-los. |
| `push` | ✅ | vollständig | `helm push CHART REMOTE` inkl. `--ca-file`/`--cert-file`/`--key-file`, `--plain-http`. Render-los. |
| `verify` | ✅ | vollständig | `helm verify PATH` inkl. `--keyring`. Render-los. |
| `version` | ✅ | vollständig | `helm version` inkl. `--short`, `--template`. Render-los. |
| `env` | ✅ | vollständig | `helm env [NAME]`. Render-los. |
| `package` | ✅ | vollständig | `helm package .` inkl. `--app-version`/`--version`, `-d`, `-u`, `--sign`/`--key`/`--keyring`/`--pass-stdin`. **Render-basiert**. |
| `dependency` (build/update/list) | ✅ | vollständig | Verschachtelt; `dependency <sub> .` inkl. `--keyring`, `--skip-refresh`, `--verify` (list: `--max-col-width`). **Render-basiert**. |
| `diff upgrade` | ✅ | vollständig | Verschachtelt; `diff upgrade RELEASE .` (Release via `--name`) inkl. `--detailed-exitcode`, `--context`, `--reset/reuse-values`, … + `--set*`/`-f`. **Render-basiert**, benötigt **helm-diff-Plugin**. |

**Anmerkung zum Release-Namen:** Bei den render-basierten Kommandos sind `REPOSITORY` (Index 0) und
`TARGET` (Index 1) bereits positional belegt, daher wird der Release-Name dort über `--name`
übergeben (bei `uninstall` wiederholbar) und an Helm positional weitergereicht. `-n` ist für
`--namespace` reserviert (Helm-konform). Render-lose Kommandos wie `status` haben **kein**
`REPOSITORY`-Positional und reichen den Release-Namen direkt als Positional (`RELEASE`) an Helm durch.

### Noch nicht gewrappte Helm-Kommandos

Alle für den Wrapper vorgesehenen Helm-Kommandos sind umgesetzt. Bewusst **ausgenommen** bleiben die
reinen Helm-Meta-Kommandos:

| Helm-Kommando | Status | Bemerkung |
|---|---|---|
| `plugin`, `completion`, `create` | ❌ (bewusst) | Reine Helm-Meta-Befehle, außerhalb des Wrapper-Scopes (Nutzer-Entscheid). |

## YAML-Rendering-Abhängigkeit (KTS-Relevanz)

Entscheidend für die Wrapper-Sinnhaftigkeit ist, ob ein Kommando das gerenderte Chart (also das
Ergebnis der KTS-Skripte) benötigt. Daraus ergibt sich, ob die KTS-Skripte überhaupt relevant sind —
und damit, ob man dem Kommando überhaupt ein **Repository** (KTS, reines YAML oder gemischt) übergeben
muss.

### Benötigen YAML-Rendering — **Repository erforderlich (KTS relevant)**

Diese Kommandos arbeiten mit dem aus den KTS-Skripten erzeugten Chart. Sie durchlaufen die
Pipeline *Scan → Compile → Render* und sind nur sinnvoll, wenn ein Repository vorliegt — ohne
Repository (bzw. ohne gültiges Render-Ergebnis) brechen sie ab, bevor Helm aufgerufen wird.

| Kommando | Warum Rendering nötig |
|---|---|
| `validate` | Prüft das KTS-Repo selbst (Struktur). |
| `compile` | Führt die KTS-Skripte aus und baut das Objektmodell. |
| `render` | Erzeugt das Chart aus den KTS-Skripten. |
| `lint` | `helm lint` benötigt das gerenderte Chart. |
| `template` | `helm template` benötigt das gerenderte Chart. |
| `install` | `helm install` installiert das gerenderte Chart. |
| `upgrade` | `helm upgrade` upgradet auf Basis des gerenderten Charts. |
| `package` | `helm package .` packt das gerenderte Chart zu `.tgz`. |
| `dependency` (build/update/list) | operiert auf dem gerenderten Chart (`dependency <sub> .`). |
| `diff upgrade` | diffed das gerenderte Chart gegen den Cluster (benötigt **helm-diff-Plugin**). |

### Unabhängig vom Rendering — **kein Repository nötig (KTS irrelevant)**

Diese Helm-Kommandos operieren auf einem bereits installierten Release, auf der Cluster-/Repo-Ebene
oder rein informativ. Sie brauchen **kein** gerendertes Chart und damit auch **kein** Repository,
d. h. die KTS-Skripte sind dafür unerheblich. Sie leiten **nicht** von `BaseRenderCommand` ab,
sondern von `BaseDirectHelmCommand` und rufen Helm direkt auf.

| Kommando | Bezugspunkt | Status |
|---|---|---|
| `status`, `list`, `history`, `get`, `rollback`, `test` | bestehendes Release | ✅ implementiert (`BaseDirectHelmCommand`) |
| `repo`, `search`, `pull`, `push`, `registry` | Repository / Registry | ✅ implementiert (`BaseDirectHelmCommand`) |
| `show`, `version`, `env`, `verify` | Chart-Metadaten / Diagnose | ✅ implementiert (`BaseDirectHelmCommand`) |

> **Sonderfall `uninstall`:** Benötigt fachlich **kein** Rendering (es entfernt ein Release per Name),
> leitet aber aktuell aus Konsistenzgründen dennoch von `BaseRenderedHelmCommand`/`BaseRenderCommand` ab und
> rendert vor dem Aufruf. Das ist ein bewusster, aber optimierbarer Kompromiss — KTS ist hier streng
> genommen irrelevant.

## Globale Helm-Flags

Die globalen Helm-Flags (`--namespace/-n`, `--kube-context`, `--kubeconfig`, `--kube-*`,
`--burst-limit`, `--qps`, `--registry-config`, `--repository-cache`, `--repository-config`) sind über
`HelmGlobalOptions` für **alle** Helm-gestützten Kommandos abgedeckt. `--debug` wird aus dem internen
`--debug` abgeleitet und ebenfalls weitergereicht.

## Tests

- `HelmArgsTest` — prüft Flag-Weiterleitung pro Kommando ohne Helm-Aufruf (inkl. verschachtelter Befehle).
- `HelpRenderTest` — prüft die Marker-Spalte in der Hilfe (helm/experimentell/gefährlich), auch für verschachtelte Befehle.
- Render-los, gemockter `HelmExecutor`: `StatusTest`, `ListTest`, `RollbackTest`, `GetValuesTest`, `RepoAddTest`.
- Render-basiert, gemockter `HelmExecutor`: `InstallTest`, `UpgradeTest`, `TemplateTest`, `UninstallTest`, `PackageTest`, `DependencyUpdateTest`, `DiffTest`.

## Doku

Pro Kommando eine Seite unter `docs/docs/cli/` in en/zh/ja/ko; Übersicht in `cli/index.md`.
