# render

```bash
kube-kts render <REPOSITORY> [TARGET] [-f FILE...]
```

Renders the repository into a plain Helm chart on disk. It runs the full pipeline — scan, compile,
evaluate — and writes the result as a regular Helm chart (with `Chart.yaml`, `values.yaml` and a
`templates/` directory of plain YAML) into the target directory. The output contains no Kotlin and no
Go templating logic, so it can be consumed by `helm` directly, committed, diffed, or inspected by
hand.

`render` is the bridge between the Kube KTS world and standard tooling: everything the Helm-backed
commands do internally starts with exactly this step. Running it explicitly is the best way to *see*
what Kube KTS produces.

!!! tip "Inspect the output"
    Pass an explicit `TARGET` so the chart is kept after the command finishes. Without it, the chart
    is written to a temporary directory that the operating system may clean up.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the repository to render. If omitted, the current working directory is used. |
| `TARGET` | no | Directory the Helm chart is written to. If omitted, a temporary directory is created. An existing target is overwritten with the freshly rendered chart. |

## Options

In addition to the [global options](index.md#global-options):

| Option | Marker | Description |
|---|---|---|
| `-f`, `--values=FILE` | `---->` | A YAML values file made available to the Kotlin scripts during rendering. Repeatable; files are layered in order, with later files overriding earlier ones. Each file must exist. (For `render` the values influence the generated chart; they are not handed to Helm because `render` does not call Helm.) |

## Examples

```bash
# Render the current directory into ./out
kube-kts render . ./out

# Render a specific repository with two layered values files
kube-kts render /path/to/repository ./out -f base.yaml -f prod.yaml

# Render into a temporary directory just to check that it succeeds
kube-kts render ./helm
```
