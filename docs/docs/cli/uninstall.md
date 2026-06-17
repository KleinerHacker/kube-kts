# uninstall

```bash
kube-kts uninstall <REPOSITORY> [TARGET] --name <RELEASE> [--name <RELEASE>...] [OPTIONS]
```

Renders the repository and then runs `helm uninstall` to remove one or more releases from a cluster.
Uninstalling deletes the resources that belong to a release and, by default, its history as well.
Like the other Helm-backed commands it talks to the cluster through your kube-context and forwards
every supported Helm uninstall flag.

## How it works

1. The repository is scanned, compiled and rendered (the render step keeps the workflow uniform with
   the other commands; the rendered chart is used as the working directory).
2. `helm uninstall <RELEASE>...` is executed with all forwarded options appended.
3. Helm removes each release; the command's exit code mirrors Helm's result.

!!! note "Release names are options, namespace is `-n`"
    Release names are passed with `--name` (repeatable) and forwarded to Helm as positional `RELEASE`
    arguments. The `-n` shorthand is reserved for `--namespace`. At least one `--name` is required.

!!! warning "This deletes cluster resources"
    Uninstall is destructive. Use `--dry-run` first to preview, and `--keep-history` if you want to
    retain the release history for later inspection or rollback.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the repository. If omitted, the current working directory is used. |
| `TARGET` | no | Directory the chart is rendered into. If omitted, a temporary directory is used. |

## Uninstall options

| Option | Marker | Description |
|---|---|---|
| `--name=RELEASE` | | A release to uninstall. Repeatable to remove several releases in one call; each is forwarded to Helm as a positional `RELEASE`. **At least one is required.** |
| `--cascade=STRING` | `---->` | How dependent objects are deleted: `background` (default, delete in the background), `foreground` (wait for dependents first) or `orphan` (leave dependents behind). |
| `--description=TEXT` | `---->` | Custom, human-readable description recorded for the uninstall operation. |
| `--dry-run` | `---->` | Simulate the uninstall and show what would be removed without changing the cluster. |
| `--ignore-not-found` | `---->` | Treat a missing release as success instead of an error. Makes the command idempotent — handy in cleanup scripts. |
| `--keep-history` | `---->` | Delete the resources and mark the release as deleted, but retain its history so it can be inspected or rolled back later. |
| `--no-hooks` | `---->` | Skip pre/post-delete hooks during the uninstall. |
| `--timeout=DURATION` | `---->` | Maximum time to wait for any single Kubernetes operation, as a Go duration (default `5m0s`). Relevant with `--wait`. |
| `--wait` | `---->` | Block until all of the release's resources are actually deleted before returning. |

## Helm global options

| Option | Marker | Description |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | Namespace the release(s) live in. Defaults to the current kube-context's namespace; set it explicitly to avoid uninstalling from the wrong namespace. |
| `--kube-context=CONTEXT` | `---->` | Context inside your kubeconfig to use, targeting a specific cluster/user without switching your active context. |
| `--kubeconfig=FILE` | `---->` | Path to the kubeconfig file to use instead of the default. |
| `--kube-apiserver=ADDRESS` | `---->` | Override the API server address and port from the kubeconfig. |
| `--kube-as-user=USER` | `---->` | Impersonate this user; requires impersonation rights. |
| `--kube-as-group=GROUP` | `---->` | Impersonate this group. Repeatable. |
| `--kube-ca-file=FILE` | `---->` | CA certificate file used to verify the API server's TLS certificate. |
| `--kube-token=TOKEN` | `---->` | Bearer token used to authenticate instead of the kubeconfig credentials. |
| `--kube-tls-server-name=NAME` | `---->` | Server name used when validating the API server certificate. |
| `--kube-insecure-skip-tls-verify` | `---->` | Skip verification of the API server certificate. Insecure — trusted/test only. |
| `--burst-limit=INT` | `---->` | Client-side throttling burst limit for API requests (default `100`). |
| `--qps=QPS` | `---->` | Client-side queries-per-second limit toward the API server. |
| `--registry-config=FILE` | `---->` | Path to the OCI registry configuration (credentials) file. |
| `--repository-cache=DIR` | `---->` | Directory of cached repository indexes used to resolve dependencies. |
| `--repository-config=FILE` | `---->` | Path to the file mapping repository names to URLs (`repositories.yaml`). |

## Global options

All [global options](index.md#global-options) (e.g. `--debug`, `--unsafe`) are available as well.

## Examples

```bash
# Uninstall a single release from the "prod" namespace
kube-kts uninstall /path/to/repository --name my-app -n prod

# Uninstall several releases and keep their history
kube-kts uninstall . --name my-app --name my-worker --keep-history

# Preview an uninstall and treat a missing release as success
kube-kts uninstall ./helm --name my-app --dry-run --ignore-not-found
```
