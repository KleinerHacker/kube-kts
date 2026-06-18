# rollback

```bash
kube-kts rollback <RELEASE> [REVISION] [OPTIONS]
```

Runs `helm rollback` to roll a release back to a previous revision. It operates on an existing
release, so it needs **neither a repository nor a render step** — the call is forwarded directly to
Helm.

## How it works

1. No repository is scanned, compiled or rendered; the KTS scripts are irrelevant here.
2. `helm rollback <RELEASE> [REVISION]` is executed with all forwarded options appended.
3. Helm performs the rollback; the command's exit code mirrors Helm's result.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `RELEASE` | yes | Name of the release to roll back. Forwarded to Helm as the positional `RELEASE`. |
| `REVISION` | no | Revision to roll back to. If omitted, Helm rolls back to the previous revision. |

## Rollback options

| Option | Marker | Description |
|---|---|---|
| `--cleanup-on-fail` | `---->` | Allow deletion of new resources created during the rollback if it fails. |
| `--dry-run` | `---->` | Simulate a rollback without changing anything. |
| `--force` | `---->` | Force resource updates through delete/recreate if needed. |
| `--history-max=INT` | `---->` | Limit the maximum number of revisions saved per release (`0` for no limit). |
| `--no-hooks` | `---->` | Prevent hooks from running during the rollback. |
| `--recreate-pods` | `---->` | Restart pods of the resource if applicable. |
| `--timeout=DURATION` | `---->` | Time to wait for any individual Kubernetes operation. |
| `--wait` | `---->` | Wait until all resources are ready before marking the release successful. |
| `--wait-for-jobs` | `---->` | With `--wait`, also wait until all Jobs have completed. |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Roll back to the previous revision
kube-kts rollback my-app

# Roll back to revision 3 and wait for readiness
kube-kts rollback my-app 3 --wait
```
