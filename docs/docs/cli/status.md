# status

```bash
kube-kts status <RELEASE> [OPTIONS]
```

Runs `helm status` to show the status of an already installed release. Unlike the rendering
commands, `status` operates on a release that already lives in the cluster, so it needs **neither a
repository nor a render step** — the call is forwarded directly to Helm.

## How it works

1. No repository is scanned, compiled or rendered; the KTS scripts are irrelevant here.
2. `helm status <RELEASE>` is executed with all forwarded options appended.
3. Helm prints the release status; the command's exit code mirrors Helm's result.

!!! note "Release name is a positional, namespace is `-n`"
    The release name is passed as the first positional argument (`RELEASE`) and forwarded to Helm
    unchanged. The `-n` shorthand is reserved for `--namespace`.

!!! note "No repository needed"
    Because `status` does not render anything, you do not point it at a Kube KTS repository. This is
    the first command built on the render-less `BaseDirectHelmCommand` base class.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `RELEASE` | yes | Name of the release to query. Forwarded to Helm as the positional `RELEASE`. |

## Status options

| Option | Marker | Description |
|---|---|---|
| `--revision=INT` | `---->` | Show the status of a specific revision of the release instead of the latest one. |
| `--output=FORMAT` | `---->` | Print the output in the given format: `table` (default), `json` or `yaml`. |
| `--show-desc` | `---->` | Additionally display the description message recorded for the release. |
| `--show-resources` | `---->` | Additionally list the Kubernetes resources that belong to the release. |

## Helm global options

| Option | Marker | Description |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | Namespace the release lives in. Defaults to the current kube-context's namespace; set it explicitly to query the right namespace. |
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
# Show the status of a release in the "prod" namespace
kube-kts status my-app -n prod

# Show a specific revision as JSON
kube-kts status my-app --revision 3 --output json

# Include the release description and its resources
kube-kts status my-app --show-desc --show-resources
```
