# version

```bash
kube-kts version [OPTIONS]
```

Runs `helm version` to print the Helm version information. It is purely informational and needs
**neither a repository nor a render step** — the call is forwarded directly to Helm.

## Version options

| Option | Marker | Description |
|---|---|---|
| `--short` | `---->` | Print only the version number. |
| `--template=TEMPLATE` | `---->` | Go template for the version string format. |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Print the full Helm version
kube-kts version

# Print just the version number
kube-kts version --short
```
