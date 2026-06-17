# template

```bash
kube-kts template <REPOSITORY> [TARGET] --name <NAME> [OPTIONS]
```

Renders the repository to a Helm chart and then runs `helm template` to print the fully rendered
Kubernetes manifests to stdout. Unlike [`install`](install.md), it does **not** contact or change a
cluster (unless you opt in with `--validate`), which makes it the safest way to see exactly what would
be applied. It is ideal for diffing changes, feeding the output into `kubectl apply`/GitOps, or
debugging how your values affect the result.

## How it works

1. The repository is scanned, compiled and rendered into a (temporary or explicit) chart directory,
   with your `-f`/`--set*` values applied.
2. `helm template <NAME> .` is executed in that directory with all forwarded options appended.
3. The rendered manifests are written to stdout (or to files with `--output-dir`).

!!! note "Release name is an option, namespace is `-n`"
    The release name is passed with `--name` and forwarded to Helm as the positional `NAME`. The `-n`
    shorthand is reserved for `--namespace`, matching Helm. Use `--generate-name` if you do not care
    about the name.

!!! tip "Local vs. cluster-aware rendering"
    By default templating is fully offline. Add `--validate` to check the manifests against the live
    cluster, or `-a`/`--kube-version` to pin the API capabilities used during rendering.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the repository to template. If omitted, the current working directory is used. |
| `TARGET` | no | Directory the chart is rendered into before templating. If omitted, a temporary directory is used. |

## Template options

| Option | Marker | Description |
|---|---|---|
| `--name=NAME` | | Release name used while rendering. Forwarded to Helm as the positional `NAME`. Omit only with `--generate-name`. |
| `-a`, `--api-versions=VERSIONS` | `---->` | API versions to report as available via `Capabilities.APIVersions`, e.g. `networking.k8s.io/v1`. Lets you render as if certain APIs exist without querying a cluster. Repeatable. |
| `--include-crds` | `---->` | Include the chart's CRDs in the output. By default `helm template` omits them. |
| `--is-upgrade` | `---->` | Render as if this were an upgrade by setting `.Release.IsUpgrade` (and clearing `.Release.IsInstall`), so upgrade-only template branches are exercised. |
| `--kube-version=VERSION` | `---->` | Kubernetes version reported via `Capabilities.KubeVersion`, e.g. `1.29`. Useful to render for a specific cluster version offline. |
| `--output-dir=DIR` | `---->` | Write each rendered template to a separate file under this directory instead of printing everything to stdout. |
| `-s`, `--show-only=TEMPLATE` | `---->` | Restrict the output to the manifests produced by the given template path(s), e.g. `templates/deployment.yaml`. Repeatable. |
| `--skip-tests` | `---->` | Omit test manifests (resources annotated as Helm tests) from the output. |
| `--validate` | `---->` | Validate the rendered manifests against the cluster you currently point at. Requires cluster access and turns templating into a cluster-aware operation. |
| `--dry-run` | `---->` | Simulate an install during rendering, affecting template branches that check the dry-run state. |
| `-g`, `--generate-name` | `---->` | Let Helm generate the release name; use instead of `--name`. |
| `-l`, `--labels=KEY=VALUE` | `---->` | Add a label to the (simulated) release metadata. Repeatable. |
| `--create-namespace` | `---->` | Mark the namespace for creation in the rendered output, mirroring the install-time flag. |

## Value options

| Option | Marker | Description |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | Override a value inline, e.g. `--set replicas=3`. Types are inferred. Repeatable; later `--set` wins and overrides `-f` files. |
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
| `--pass-credentials` | `---->` | Send the repository credentials to all domains, not just the chart's host. Trusted repositories only. |
| `--ca-file=FILE` | `---->` | CA bundle used to verify the TLS certificate of the HTTPS chart server. |
| `--cert-file=FILE` | `---->` | Client TLS certificate for mutual TLS against the chart server. |
| `--key-file=FILE` | `---->` | Client TLS private key matching `--cert-file`. |
| `--insecure-skip-tls-verify` | `---->` | Skip TLS verification when downloading the chart. Insecure — avoid in production. |
| `--keyring=FILE` | `---->` | Keyring of public keys used to verify a signed chart (with `--verify`). Default `~/.gnupg/pubring.gpg`. |
| `--verify` | `---->` | Verify the chart's provenance signature before use; fails if missing or invalid. |
| `--version=VERSION` | `---->` | SemVer constraint selecting the chart version. Defaults to the latest stable version. |
| `--devel` | `---->` | Also consider pre-release versions (equivalent to `>0.0.0-0`). |
| `--dependency-update` | `---->` | Download/update missing chart dependencies before templating. |

## Rendering options

| Option | Marker | Description |
|---|---|---|
| `--no-hooks` | `---->` | Skip all chart lifecycle hooks during rendering. |
| `--disable-openapi-validation` | `---->` | Do not validate the rendered manifests against the OpenAPI schema. |
| `--name-template=TEMPLATE` | `---->` | Go template used to compute the release name, as an alternative to a fixed `--name`. |
| `--render-subchart-notes` | `---->` | Also render the NOTES.txt of subcharts. |
| `--skip-crds` | `---->` | Do not render the CRDs that ship with the chart (the opposite of `--include-crds`). |
| `--post-renderer=PATH` | `---->` | Executable run on the rendered manifests, enabling Kustomize-style post-processing. |
| `--post-renderer-args=ARG` | `---->` | An argument passed to the `--post-renderer` executable. Repeatable. |
| `--timeout=DURATION` | `---->` | Maximum time to wait for any single Kubernetes operation, as a Go duration (default `5m0s`). Mostly relevant with `--validate`. |

## Helm global options

| Option | Marker | Description |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | Namespace assumed while rendering (affects namespaced resources and `.Release.Namespace`). Defaults to the current kube-context's namespace. |
| `--kube-context=CONTEXT` | `---->` | Context inside your kubeconfig to use (relevant with `--validate`). |
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
# Print the rendered manifests for release "my-app"
kube-kts template /path/to/repository --name my-app

# Only a single template, including CRDs, with an inline override
kube-kts template . --name my-app -s templates/deployment.yaml --include-crds --set replicas=3

# Render for a specific cluster version into a directory
kube-kts template ./helm --name my-app --kube-version 1.29 --output-dir ./manifests
```
