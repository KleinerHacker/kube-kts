# diff

```bash
kube-kts diff upgrade <REPOSITORY> [TARGET] --name <RELEASE> [OPTIONS]
```

Groups the diff sub-commands built on the external **`helm-diff` plugin**. They preview the changes
an operation would apply by rendering the KTS repository and diffing the result against the cluster.
Each sub-command **needs a repository** and runs the full *scan → compile → render* pipeline first.
Invoking `diff` without a sub-command prints the usage listing.

!!! note "Requires the helm-diff plugin"
    These commands invoke `helm diff …`, which is provided by the external plugin. Install it with
    `helm plugin install https://github.com/databus23/helm-diff`.

## Sub-commands

| Sub-command | Helm | Description |
|---|---|---|
| `diff upgrade <REPOSITORY> [TARGET]` | `helm diff upgrade <RELEASE> .` | Preview the changes an `upgrade` would apply. |

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the Kube KTS repository to render. |
| `TARGET` | no | Directory the chart is rendered into. A temporary directory is used if omitted. |

!!! note "Release name is passed via `--name`"
    Because `REPOSITORY`/`TARGET` already occupy the positionals, the release name is passed via
    `--name` and forwarded to the plugin as the positional `RELEASE`. The `-n` shorthand stays
    reserved for `--namespace`.

## Diff options

| Option | Marker | Description |
|---|---|---|
| `--name=NAME` | | Name of the release (forwarded to the plugin as the positional `RELEASE`). |
| `--detailed-exitcode` | `---->` | Return exit code `2` when there are changes. |
| `--context=NUM` | `---->` | Output `NUM` lines of context around changes (`-1` for full context). |
| `--show-secrets` | `---->` | Do not redact secret values in the output. |
| `--no-hooks` | `---->` | Disable diffing of hooks. |
| `--include-tests` | `---->` | Enable diffing of Helm test hooks. |
| `--reset-values` | `---->` | Reset values to the ones built into the chart and merge in new values. |
| `--reuse-values` | `---->` | Reuse the last release's values and merge in new values. |
| `--normalize-manifests` | `---->` | Normalize manifests before diffing to exclude style differences. |

## Values

`diff upgrade` accepts the [`--set` family](install.md) and values files via
[`-f`/`--values`](index.md#values), which feed both the Kotlin scripts and the plugin.

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Preview the changes an upgrade would apply
kube-kts diff upgrade ./my-repo --name my-app

# Fail the build (exit 2) if there are any changes
kube-kts diff upgrade ./my-repo --name my-app --detailed-exitcode
```
