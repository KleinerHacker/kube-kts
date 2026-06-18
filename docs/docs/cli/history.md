# history

```bash
kube-kts history <RELEASE> [OPTIONS]
```

Runs `helm history` to show the revision history of a release. It operates on an existing release, so
it needs **neither a repository nor a render step** — the call is forwarded directly to Helm.

## How it works

1. No repository is scanned, compiled or rendered; the KTS scripts are irrelevant here.
2. `helm history <RELEASE>` is executed with all forwarded options appended.
3. Helm prints the revision history; the command's exit code mirrors Helm's result.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `RELEASE` | yes | Name of the release to query. Forwarded to Helm as the positional `RELEASE`. |

## History options

| Option | Marker | Description |
|---|---|---|
| `--max=INT` | `---->` | Maximum number of revisions to include in the history. |
| `-o`, `--output=FORMAT` | `---->` | Output format: `table` (default), `json` or `yaml`. |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Show the full revision history of a release
kube-kts history my-app

# Show the last 5 revisions as JSON
kube-kts history my-app --max 5 -o json
```
