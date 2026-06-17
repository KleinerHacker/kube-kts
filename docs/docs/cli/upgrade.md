# upgrade

```bash
kube-kts upgrade <REPOSITORY> [TARGET] --name <NAME> [OPTIONS]
```

Renders the repository to a Helm chart and then runs `helm upgrade` to update an existing release in
a Kubernetes cluster (or, with `--install`, install it if it does not exist yet). It is the natural
counterpart to [`install`](install.md) and shares almost all of its flags, plus a few upgrade-specific
ones that control how the previous release's values are reused.

## How it works

1. The repository is scanned, compiled and rendered into a (temporary or explicit) chart directory,
   with your `-f`/`--set*` values applied.
2. `helm upgrade <NAME> .` is executed in that directory with all forwarded options appended.
3. Helm creates a new release revision; the command's exit code mirrors Helm's result. On failure
   nothing is rolled back unless you pass `--atomic`.

!!! note "Release name is an option, namespace is `-n`"
    The release name is passed with `--name` and forwarded to Helm as the positional `NAME`. The `-n`
    shorthand is reserved for `--namespace`, to stay in sync with Helm.

!!! tip "Upgrade-or-install and value handling"
    Use `-i`/`--install` to install the release if it does not exist yet. By default an upgrade uses
    the values you pass; `--reuse-values`, `--reset-values` and `--reset-then-reuse-values` control how
    the previous release's values are taken into account.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the repository to upgrade from. If omitted, the current working directory is used. |
| `TARGET` | no | Directory the chart is rendered into before upgrading. If omitted, a temporary directory is used. |

## Upgrade options

| Option | Marker | Description |
|---|---|---|
| `--name=NAME` | | Name of the release to upgrade. Forwarded to Helm as the positional `NAME`. |
| `-i`, `--install` | `---->` | If a release with this name does not exist yet, run an install instead of failing. |
| `--atomic` | `---->` | If the upgrade fails, roll back the changes made during this upgrade. Implies `--wait`. |
| `--cleanup-on-fail` | `---->` | Allow deletion of new resources created during this upgrade when the upgrade fails. |
| `--create-namespace` | `---->` | With `--install`, create the release namespace if it does not exist. |
| `--dry-run` | `---->` | Simulate the upgrade without changing the cluster. |
| `--enable-dns` | `---->` | Allow templates to perform DNS lookups during rendering. |
| `--force` | `---->` | Force resource updates through a replacement strategy. Can be disruptive. |
| `--history-max=INT` | `---->` | Limit the number of revisions kept per release (`0` = no limit; Helm default `10`). |
| `-l`, `--labels=KEY=VALUE` | `---->` | Add a label to the release metadata. Repeatable. |
| `--description=TEXT` | `---->` | Attach a custom description to this release revision. |
| `-o`, `--output=FORMAT` | `---->` | Output format: `table` (default), `json` or `yaml`. |
| `--reset-values` | `---->` | Reset the values to the ones built into the chart, ignoring the previous release. |
| `--reuse-values` | `---->` | Reuse the previous release's values and merge in any new overrides. |
| `--reset-then-reuse-values` | `---->` | Reset to the chart's values, then apply the previous release's values and merge in overrides. |
| `--take-ownership` | `---->` | Ignore the Helm ownership annotations and take ownership of existing resources. Use with care. |
| `--wait` | `---->` | Wait until all resources are ready before marking the release successful. Respects `--timeout`. |
| `--wait-for-jobs` | `---->` | With `--wait`, also wait until all Jobs have completed. |

## Value options

| Option | Marker | Description |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | Override a value inline, e.g. `--set image.tag=1.2.3`. Types are inferred. Repeatable; later `--set` wins and overrides `-f` files. |
| `--set-string=KEY=VALUE` | `---->` | Like `--set` but always keeps the value as a string. Repeatable. |
| `--set-file=KEY=PATH` | `---->` | Use the entire content of the file at `PATH` as the value of `KEY`. Repeatable. |
| `--set-json=KEY=JSON` | `---->` | Set a value from a JSON expression, enabling objects/arrays. Repeatable. |
| `--set-literal=KEY=VALUE` | `---->` | Set the value exactly as written, with no type conversion. Repeatable. |
| `-f`, `--values=FILE` | `---->` | YAML values file used for rendering and forwarded to Helm. Repeatable and layered in order; `--set*` overrides these. |

## Chart source & verification options

| Option | Marker | Description |
|---|---|---|
| `--repo=URL` | `---->` | Pull the chart from this repository URL instead of a local path. |
| `--username=USER` | `---->` | Username for the chart repository (used with `--password`). |
| `--password=PASSWORD` | `---->` | Password for the chart repository. Prefer secrets/environment over the command line. |
| `--pass-credentials` | `---->` | Send the repository credentials to all domains. Trusted repositories only. |
| `--ca-file=FILE` | `---->` | CA bundle used to verify the TLS certificate of the HTTPS chart server. |
| `--cert-file=FILE` | `---->` | Client TLS certificate for mutual TLS against the chart server. |
| `--key-file=FILE` | `---->` | Client TLS private key matching `--cert-file`. |
| `--insecure-skip-tls-verify` | `---->` | Skip TLS verification when downloading the chart. Insecure — avoid in production. |
| `--keyring=FILE` | `---->` | Keyring of public keys used to verify a signed chart (with `--verify`). Default `~/.gnupg/pubring.gpg`. |
| `--verify` | `---->` | Verify the chart's provenance signature before upgrading; fails if missing or invalid. |
| `--version=VERSION` | `---->` | SemVer constraint selecting the chart version. Defaults to the latest stable version. |
| `--devel` | `---->` | Also consider pre-release versions (equivalent to `>0.0.0-0`). |
| `--dependency-update` | `---->` | Download/update missing chart dependencies before upgrading. |

## Rendering options

| Option | Marker | Description |
|---|---|---|
| `--no-hooks` | `---->` | Skip all chart lifecycle hooks during the upgrade. |
| `--disable-openapi-validation` | `---->` | Do not validate the rendered manifests against the cluster's OpenAPI schema. |
| `--name-template=TEMPLATE` | `---->` | Go template used to compute the release name, as an alternative to a fixed `--name`. |
| `--render-subchart-notes` | `---->` | Also render the NOTES.txt of subcharts. |
| `--skip-crds` | `---->` | Do not install the CRDs that ship with the chart. |
| `--post-renderer=PATH` | `---->` | Executable run on the rendered manifests before they are applied. |
| `--post-renderer-args=ARG` | `---->` | An argument passed to the `--post-renderer` executable. Repeatable. |
| `--timeout=DURATION` | `---->` | Maximum time to wait for any single Kubernetes operation, as a Go duration (default `5m0s`). Most relevant with `--wait`. |

## Helm global options

| Option | Marker | Description |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | Namespace the release lives in. Defaults to the current kube-context's namespace. |
| `--kube-context=CONTEXT` | `---->` | Context inside your kubeconfig to use, targeting a specific cluster/user. |
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
# Upgrade release "my-app", installing it if it does not exist yet
kube-kts upgrade /path/to/repository --name my-app -i -n prod

# Atomic upgrade that reuses the previous values and only overrides the image tag
kube-kts upgrade . --name my-app --atomic --reuse-values --set image.tag=1.3.0

# Preview an upgrade without touching the cluster
kube-kts upgrade ./helm --name my-app --dry-run
```
