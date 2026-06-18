# package

```bash
kube-kts package <REPOSITORY> [TARGET] [OPTIONS]
```

Renders the KTS repository to a plain Helm chart and then runs `helm package .` against it to produce
a versioned chart archive (`.tgz`). Unlike the release commands, `package` **needs a repository** and
runs the full *scan → compile → render* pipeline first.

## How it works

1. The repository is scanned, the Kotlin scripts are compiled and evaluated, and the result is
   rendered into the `TARGET` directory (a temporary directory if omitted).
2. `helm package .` is executed in that directory with all forwarded options appended.
3. Helm writes the chart archive; the command's exit code mirrors Helm's result.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the Kube KTS repository to render and package. |
| `TARGET` | no | Directory the chart is rendered into. A temporary directory is used if omitted. |

## Package options

| Option | Marker | Description |
|---|---|---|
| `--app-version=VERSION` | `---->` | Set the `appVersion` on the chart. |
| `--version=VERSION` | `---->` | Set the (semver) `version` on the chart. |
| `-d`, `--destination=DIR` | `---->` | Location to write the chart archive. |
| `-u`, `--dependency-update` | `---->` | Update dependencies from `Chart.yaml` into `charts/` before packaging. |
| `--sign` | `---->` | Sign the package with a PGP private key. |
| `--key=NAME` | `---->` | Name of the signing key (used with `--sign`). |
| `--keyring=FILE` | `---->` | Location of the public keyring. |
| `--pass-stdin` | `---->` | Read the PGP passphrase from stdin (used with `--sign`). |

## Values

`package` accepts values files via [`-f`/`--values`](index.md#values), which feed the Kotlin scripts
during rendering.

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Render and package a repository with an explicit version
kube-kts package ./my-repo --version 1.2.3 --app-version 2.0.0 -d ./dist
```
