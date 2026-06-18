# list

```bash
kube-kts list [OPTIONS]
```

Runs `helm list` to list releases. Like `status`, `list` operates on the cluster, so it needs
**neither a repository nor a render step** — the call is forwarded directly to Helm.

## How it works

1. No repository is scanned, compiled or rendered; the KTS scripts are irrelevant here.
2. `helm list` is executed with all forwarded options appended.
3. Helm prints the releases; the command's exit code mirrors Helm's result.

## List options

| Option | Marker | Description |
|---|---|---|
| `-a`, `--all` | `---->` | Show all releases without any filter applied. |
| `-A`, `--all-namespaces` | `---->` | List releases across all namespaces. |
| `-d`, `--date` | `---->` | Sort by release date. |
| `--deployed` | `---->` | Show deployed releases. Enabled automatically if no other state filter is given. |
| `--failed` | `---->` | Show failed releases. |
| `--filter=REGEXP` | `---->` | Perl-compatible regular expression; only matching releases are listed. |
| `-m`, `--max=INT` | `---->` | Maximum number of releases to fetch. |
| `--no-headers` | `---->` | Don't print headers in the default output format. |
| `--offset=INT` | `---->` | Next release index in the list, used to offset from the start. |
| `-o`, `--output=FORMAT` | `---->` | Output format: `table` (default), `json` or `yaml`. |
| `--pending` | `---->` | Show pending releases. |
| `-r`, `--reverse` | `---->` | Reverse the sort order. |
| `-l`, `--selector=SELECTOR` | `---->` | Label query to filter on (supports `=`, `==`, `!=`). |
| `-q`, `--short` | `---->` | Output short (quiet) listing format. |
| `--superseded` | `---->` | Show superseded releases. |
| `--time-format=FORMAT` | `---->` | Format time using a Go time formatter. |
| `--uninstalled` | `---->` | Show uninstalled releases (if `--keep-history` was used on uninstall). |
| `--uninstalling` | `---->` | Show releases that are currently being uninstalled. |

## Helm global options

All [Helm global options](status.md#helm-global-options) (e.g. `-n`/`--namespace`, `--kube-context`)
are forwarded to Helm.

## Global options

All [global options](index.md#global-options) (e.g. `--debug`, `--unsafe`) are available as well.

## Examples

```bash
# List releases in the current namespace
kube-kts list

# List all releases across all namespaces as JSON
kube-kts list -A -o json

# Show only failed releases, newest first
kube-kts list --failed --date --reverse
```
