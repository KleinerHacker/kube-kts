# push

```bash
kube-kts push <CHART> <REMOTE> [OPTIONS]
```

Runs `helm push` to upload a packaged chart to a remote (OCI) registry. It operates on an existing
chart package, so it needs **neither a repository nor a render step** — the call is forwarded
directly to Helm.

## How it works

1. No repository is scanned, compiled or rendered; the KTS scripts are irrelevant here.
2. `helm push <CHART> <REMOTE>` is executed with all forwarded options appended.
3. Helm uploads the chart; the command's exit code mirrors Helm's result.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `CHART` | yes | Path to the packaged chart (`.tgz`). Forwarded to Helm as the positional `CHART`. |
| `REMOTE` | yes | Remote registry reference. Forwarded to Helm as the positional `REMOTE`. |

## Push options

| Option | Marker | Description |
|---|---|---|
| `--ca-file=FILE` | `---->` | CA bundle to verify the registry's TLS certificate. |
| `--cert-file=FILE` | `---->` | SSL certificate file to identify the client. |
| `--key-file=FILE` | `---->` | SSL key file to identify the client. |
| `--insecure-skip-tls-verify` | `---->` | Skip TLS certificate checks for the upload. |
| `--plain-http` | `---->` | Use insecure HTTP for the upload. |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Push a packaged chart to an OCI registry
kube-kts push my-app-1.0.0.tgz oci://registry.example.com/charts
```
