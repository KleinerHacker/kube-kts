# search

```bash
kube-kts search <SUBCOMMAND> [KEYWORD] [OPTIONS]
```

Groups the `helm search …` sub-commands that search for charts. They operate on repositories /
Artifact Hub, so they need **neither a repository nor a render step** — the call is forwarded
directly to Helm. Invoking `search` without a sub-command prints the usage listing.

## Sub-commands

| Sub-command | Helm | Description |
|---|---|---|
| `search repo [KEYWORD]` | `helm search repo` | Search the repositories you have added. |
| `search hub [KEYWORD]` | `helm search hub` | Search the Artifact Hub or your own hub instance. |

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `KEYWORD` | no | Keyword to search for. Forwarded to Helm as the positional `KEYWORD`. |

## Options per sub-command

| Sub-command | Options (all `---->`) |
|---|---|
| `repo` | `--devel`, `--fail-on-no-result`, `--max-col-width=UINT`, `-o`/`--output=FORMAT`, `-r`/`--regexp`, `--version=VERSION`, `-l`/`--versions` |
| `hub` | `--endpoint=URL`, `--fail-on-no-result`, `--list-repo-url`, `--max-col-width=UINT`, `-o`/`--output=FORMAT` |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Search added repositories for "nginx", listing all versions
kube-kts search repo nginx -l

# Search the Artifact Hub
kube-kts search hub nginx -o json
```
