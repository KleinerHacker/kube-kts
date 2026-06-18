# pull

```bash
kube-kts pull <CHART> [OPTIONS]
```

Runs `helm pull` to download a chart from a repository. It operates on a remote chart, so it needs
**neither a repository nor a render step** — the call is forwarded directly to Helm.

## How it works

1. No repository is scanned, compiled or rendered; the KTS scripts are irrelevant here.
2. `helm pull <CHART>` is executed with all forwarded options appended.
3. Helm downloads the chart; the command's exit code mirrors Helm's result.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `CHART` | yes | Chart reference to download. Forwarded to Helm as the positional `CHART`. |

## Pull options

| Option | Marker | Description |
|---|---|---|
| `-d`, `--destination=DIR` | `---->` | Location to write the chart. Combined with `--untardir` when both are given. |
| `--prov` | `---->` | Fetch the provenance file without verifying it. |
| `--untar` | `---->` | Untar the chart after downloading. |
| `--untardir=DIR` | `---->` | Directory the chart is expanded into when `--untar` is set. |

## Chart download options

| Option | Marker | Description |
|---|---|---|
| `--repo=URL` | `---->` | Chart repository URL to locate the chart. |
| `--username=USER`, `--password=PASSWORD` | `---->` | Repository credentials. |
| `--pass-credentials` | `---->` | Pass credentials to all domains. |
| `--ca-file=FILE`, `--cert-file=FILE`, `--key-file=FILE` | `---->` | TLS material for the download. |
| `--insecure-skip-tls-verify` | `---->` | Skip TLS certificate checks for the download. |
| `--plain-http` | `---->` | Use insecure HTTP for the download. |
| `--keyring=FILE` | `---->` | Public keys used for `--verify`. |
| `--verify` | `---->` | Verify the package before using it. |
| `--version=VERSION` | `---->` | Version constraint for the chart version to use. |
| `--devel` | `---->` | Allow development versions (equivalent to `>0.0.0-0`). |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Download and untar a specific chart version
kube-kts pull bitnami/nginx --version 15.0.0 --untar -d ./charts
```
