# Helm-Wrapper — Umsetzungsstand

Diese Datei dokumentiert, wie weit das `kube-kts` CLI-Tool Helm bereits als Wrapper abdeckt. Sie ist
die zentrale Merkhilfe für den Fortschritt „CLI ↔ vollumfängliche Helm-Unterstützung" und wird von
der [CLAUDE.md](CLAUDE.md) referenziert.

> **Pflege:** Beim Hinzufügen/Ändern von Kommandos oder Flags in `apps/cli` bitte diese Datei
> aktualisieren. Stand zuletzt geprüft: 2026-06-17.

## Architektur (Kurz)

- Helm-Kommandos leiten von `BaseHelmCommand` ab (`apps/cli/.../commands/`).
- Flags sind in wiederverwendbare Mixins gruppiert (`apps/cli/.../commands/helm/`):
  `HelmGlobalOptions`, `HelmValueOptions`, `HelmChartSourceOptions`, `HelmRenderSharedOptions`,
  `HelmValueFileOptions`.
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
| `uninstall` | ✅ | vollständig | `helm uninstall` inkl. `--cascade`, `--keep-history`, `--ignore-not-found`, … |

**Anmerkung zum Release-Namen:** Da `REPOSITORY` (Index 0) und `TARGET` (Index 1) bereits positional
belegt sind, wird der Release-Name über `--name` übergeben (bei `uninstall` wiederholbar) und an Helm
positional weitergereicht. `-n` ist für `--namespace` reserviert (Helm-konform).

### Noch nicht gewrappte Helm-Kommandos

Diese Helm-Kommandos haben aktuell **kein** `kube-kts`-Pendant. Reihenfolge grob nach Nutzen:

| Helm-Kommando | Status | Bemerkung / Sinnhaftigkeit als Wrapper |
|---|---|---|
| `upgrade` | ❌ | Hoher Wert — natürliche Ergänzung zu `install` (inkl. `--install`, `--reset-values`, `--reuse-values`). |
| `status` | ❌ | Status eines Releases; kein Rendern nötig. |
| `list` / `ls` | ❌ | Releases auflisten. |
| `history` | ❌ | Revisionsverlauf eines Releases. |
| `rollback` | ❌ | Auf frühere Revision zurückrollen. |
| `get` (all/values/manifest/hooks/notes/metadata) | ❌ | Details eines installierten Releases abrufen. |
| `test` | ❌ | Release-Tests ausführen. |
| `diff` | ❌ | Nur via Plugin (`helm-diff`); für „render → diff"-Workflows interessant. |
| `dependency` (update/build/list) | ❌ | Teilweise indirekt über `--dependency-update` bei install/template abgedeckt. |
| `package` | ❌ | Chart zu `.tgz` packen — könnte an `render` anschließen. |
| `pull` / `push` / `registry` | ❌ | OCI/Repo-Transfer; Auth-Flags existieren bereits in `HelmChartSourceOptions`. |
| `repo` (add/update/list/remove) | ❌ | Repo-Verwaltung. |
| `show` (chart/values/readme/crds) | ❌ | Chart-Metadaten anzeigen. |
| `search` (repo/hub) | ❌ | Charts suchen. |
| `verify` | ❌ | Signatur eines gepackten Charts prüfen (`--verify` als Flag bereits vorhanden). |
| `version` / `env` | ❌ | Diagnose/Info. |
| `plugin`, `completion`, `create` | ❌ | Eher außerhalb des Wrapper-Scopes. |

## Globale Helm-Flags

Die globalen Helm-Flags (`--namespace/-n`, `--kube-context`, `--kubeconfig`, `--kube-*`,
`--burst-limit`, `--qps`, `--registry-config`, `--repository-cache`, `--repository-config`) sind über
`HelmGlobalOptions` für **alle** Helm-gestützten Kommandos abgedeckt. `--debug` wird aus dem internen
`--debug` abgeleitet und ebenfalls weitergereicht.

## Tests

- `HelmArgsTest` — prüft Flag-Weiterleitung pro Kommando ohne Helm-Aufruf.
- `HelpRenderTest` — prüft die Marker-Spalte in der Hilfe (helm/experimentell/gefährlich).
- `InstallTest` / `TemplateTest` / `UninstallTest` — vollständige Pipeline mit gemocktem `HelmExecutor`.

## Doku

Pro Kommando eine Seite unter `docs/docs/cli/` in en/zh/ja/ko; Übersicht in `cli/index.md`.
