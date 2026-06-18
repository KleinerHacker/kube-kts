# dependency

```bash
kube-kts dependency <SUBCOMMAND> <REPOSITORY> [TARGET] [OPTIONS]
```

Groups the `helm dependency …` sub-commands (alias `dep`) that manage a chart's dependencies. Each
sub-command **needs a repository**: it runs the full *scan → compile → render* pipeline first and
then operates on the rendered chart (`helm dependency <sub> .`). Invoking `dependency` without a
sub-command prints the usage listing.

## Sub-commands

| Sub-command | Helm | Description |
|---|---|---|
| `dependency build <REPOSITORY> [TARGET]` | `helm dependency build .` | Rebuild `charts/` from `Chart.lock`. |
| `dependency update <REPOSITORY> [TARGET]` | `helm dependency update .` | Update `charts/` from the contents of `Chart.yaml`. |
| `dependency list <REPOSITORY> [TARGET]` | `helm dependency list .` | List the dependencies of the chart. |

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the Kube KTS repository to render. |
| `TARGET` | no | Directory the chart is rendered into. A temporary directory is used if omitted. |

## Options per sub-command

| Sub-command | Options (all `---->`) |
|---|---|
| `build` | `--keyring=FILE`, `--skip-refresh`, `--verify` |
| `update` | `--keyring=FILE`, `--skip-refresh`, `--verify` |
| `list` | `--max-col-width=UINT` |

## Values

These commands accept values files via [`-f`/`--values`](index.md#values), which feed the Kotlin
scripts during rendering.

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Render and update the chart dependencies
kube-kts dependency update ./my-repo

# List the dependencies of the rendered chart
kube-kts dependency list ./my-repo
```
