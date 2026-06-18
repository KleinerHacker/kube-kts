# env

```bash
kube-kts env [NAME] [OPTIONS]
```

Runs `helm env` to print Helm's environment information. It is purely informational and needs
**neither a repository nor a render step** — the call is forwarded directly to Helm.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `NAME` | no | Name of a single environment variable to print. If omitted, all variables are printed. Forwarded to Helm as the positional `NAME`. |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Print all Helm environment variables
kube-kts env

# Print a single variable
kube-kts env HELM_BIN
```
