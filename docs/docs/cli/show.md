# show

```bash
kube-kts show <SUBCOMMAND> <CHART> [OPTIONS]
```

Groups the `helm show …` sub-commands (alias `inspect`) that display information about a chart. They
operate on a remote/local chart, so they need **neither a repository nor a render step** — the call
is forwarded directly to Helm. Invoking `show` without a sub-command prints the usage listing.

## Sub-commands

| Sub-command | Helm | Description |
|---|---|---|
| `show all <CHART>` | `helm show all` | Show all information of the chart. |
| `show chart <CHART>` | `helm show chart` | Show the chart definition (`Chart.yaml`). |
| `show values <CHART>` | `helm show values` | Show the chart's default values (`values.yaml`). |
| `show readme <CHART>` | `helm show readme` | Show the chart's README. |
| `show crds <CHART>` | `helm show crds` | Show the chart's CRDs. |

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `CHART` | yes | Chart reference to inspect. Forwarded to Helm as the positional `CHART`. |

## Options

Every sub-command accepts the [chart download options](pull.md#chart-download-options) (`--repo`,
`--version`, credentials, TLS, `--verify`, …). In addition, `show values` accepts:

| Option | Marker | Description |
|---|---|---|
| `--jsonpath=EXPRESSION` | `---->` | JSONPath expression to filter the output. |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Show the default values of a chart from a repository
kube-kts show values bitnami/nginx --version 15.0.0

# Show just the image from the values via JSONPath
kube-kts show values bitnami/nginx --jsonpath '{.image}'
```
