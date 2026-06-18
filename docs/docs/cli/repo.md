# repo

```bash
kube-kts repo <SUBCOMMAND> [ARGS] [OPTIONS]
```

Groups the `helm repo …` sub-commands that manage chart repositories. These operate on your local
Helm configuration, so they need **neither a repository nor a render step** — the call is forwarded
directly to Helm. Invoking `repo` without a sub-command prints the usage listing.

## Sub-commands

| Sub-command | Helm | Description |
|---|---|---|
| `repo add <NAME> <URL>` | `helm repo add` | Add a chart repository. |
| `repo update [REPO...]` | `helm repo update` | Update local cache of the given (or all) repositories. |
| `repo list` | `helm repo list` | List configured chart repositories. |
| `repo remove <REPO...>` | `helm repo remove` | Remove one or more chart repositories. |

## Options per sub-command

| Sub-command | Options (all `---->`) |
|---|---|
| `add` | `--username=USER`, `--password=PASSWORD`, `--pass-credentials`, `--ca-file=FILE`, `--cert-file=FILE`, `--key-file=FILE`, `--insecure-skip-tls-verify`, `--no-update`, `--force-update`, `--allow-deprecated-repos` |
| `update` | `--fail-on-repo-update-fail` |
| `list` | `-o`/`--output=FORMAT` |
| `remove` | – |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Add the Bitnami repository
kube-kts repo add bitnami https://charts.bitnami.com/bitnami

# Update all repositories
kube-kts repo update

# List repositories as JSON
kube-kts repo list -o json

# Remove a repository
kube-kts repo remove bitnami
```
