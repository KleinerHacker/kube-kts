# install

```bash
kube-kts install <REPOSITORY> [TARGET] --name <NAME> [OPTIONS]
```

Renders the repository to a Helm chart and then runs `helm install` to deploy it into a Kubernetes
cluster as a new release. This is the command that actually changes cluster state, so it talks to the
API server using your kube-context (or the overrides below) and forwards every supported Helm install
flag.

## How it works

1. The repository is scanned, compiled and rendered into a (temporary or explicit) chart directory,
   with your `-f`/`--set*` values applied.
2. `helm install <NAME> .` is executed in that directory with all forwarded options appended.
3. Helm creates the release; the command's exit code mirrors Helm's result. On failure nothing is
   rolled back unless you pass `--atomic`.

!!! note "Release name is an option, namespace is `-n`"
    The release name is passed with `--name` and forwarded to Helm as the positional `NAME`. The `-n`
    shorthand is intentionally **not** the name — it is reserved for `--namespace`, to stay in sync
    with Helm. Omit `--name` only together with `--generate-name`.

!!! tip "Safer rollouts"
    Combine `--atomic` (roll back on failure) with `--wait` / `--wait-for-jobs` and a sensible
    `--timeout` so a failed install does not leave half-created resources behind.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the repository to install. If omitted, the current working directory is used. |
| `TARGET` | no | Directory the chart is rendered into before installing. If omitted, a temporary directory is used. |

## Install options

| Option | Marker | Description |
|---|---|---|
| `--name=NAME` | | Name of the release to create. Forwarded to Helm as the positional `NAME`. Required unless `--generate-name` is used. |
| `--atomic` | `---->` | If the install fails, automatically delete everything that was created, leaving the cluster as it was. Implies `--wait`. Strongly recommended for unattended/CI installs. |
| `--create-namespace` | `---->` | Create the target namespace (from `-n`/`--namespace`) if it does not already exist, instead of failing. |
| `--dry-run` | `---->` | Simulate the install and show what would happen without changing the cluster. Great for previewing a release. |
| `--enable-dns` | `---->` | Allow templates to perform DNS lookups during rendering (Helm's `getHostByName` and friends). Off by default for reproducibility. |
| `--force` | `---->` | Force resource updates by replacing them, used to recover from otherwise stuck states. Can be disruptive — it may delete and recreate resources. |
| `-g`, `--generate-name` | `---->` | Let Helm generate the release name automatically; use instead of `--name`. |
| `-l`, `--labels=KEY=VALUE` | `---->` | Add a label to the release metadata (the Helm release object, not the rendered resources). Repeatable. |
| `--description=TEXT` | `---->` | Attach a custom, human-readable description to this release revision. |
| `-o`, `--output=FORMAT` | `---->` | Format of the command's result output: `table` (default), `json` or `yaml`. Use `json`/`yaml` when scripting. |
| `--replace` | `---->` | Re-use the name of a previously deleted release that still exists in history. Not recommended for production; prefer a clean uninstall/install. |
| `--wait` | `---->` | Block until all created resources (Pods, PVCs, Services, Deployments, …) report ready before marking the release successful. Respects `--timeout`. |
| `--wait-for-jobs` | `---->` | In addition to `--wait`, also block until all Jobs created by the release have completed. |

## Value options

| Option | Marker | Description |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | Override a value inline, e.g. `--set image.tag=1.2.3`. Types are inferred. Repeatable; later `--set` wins and overrides `-f` files. |
| `--set-string=KEY=VALUE` | `---->` | Like `--set` but always keeps the value as a string (e.g. to preserve `"01"`). Repeatable. |
| `--set-file=KEY=PATH` | `---->` | Use the entire content of the file at `PATH` as the value of `KEY` (certificates, scripts, …). Repeatable. |
| `--set-json=KEY=JSON` | `---->` | Set a value from a JSON expression, enabling objects/arrays, e.g. `--set-json 'ports=[80,443]'`. Repeatable. |
| `--set-literal=KEY=VALUE` | `---->` | Set the value exactly as written, with no type conversion at all. Repeatable. |
| `-f`, `--values=FILE` | `---->` | YAML values file used for rendering and forwarded to Helm. Repeatable and layered in order; `--set*` overrides these. |

## Chart source & verification options

| Option | Marker | Description |
|---|---|---|
| `--repo=URL` | `---->` | Pull the chart from this repository URL instead of a local path. |
| `--username=USER` | `---->` | Username for the chart repository (used with `--password`). |
| `--password=PASSWORD` | `---->` | Password for the chart repository. Prefer secrets/environment over passing it on the command line. |
| `--pass-credentials` | `---->` | Send the repository credentials to all domains, not just the chart's host. Use only with trusted repositories. |
| `--ca-file=FILE` | `---->` | CA bundle used to verify the TLS certificate of the HTTPS chart server. |
| `--cert-file=FILE` | `---->` | Client TLS certificate for mutual TLS against the chart server. |
| `--key-file=FILE` | `---->` | Client TLS private key matching `--cert-file`. |
| `--insecure-skip-tls-verify` | `---->` | Skip TLS verification when downloading the chart. Insecure — avoid in production. |
| `--keyring=FILE` | `---->` | Keyring of public keys used to verify a signed chart (with `--verify`). Default `~/.gnupg/pubring.gpg`. |
| `--verify` | `---->` | Verify the chart's provenance signature before installing; fails if it is missing or invalid. |
| `--version=VERSION` | `---->` | SemVer constraint selecting the chart version, e.g. `1.2.3` or `^1.2`. Defaults to the latest stable version. |
| `--devel` | `---->` | Also consider pre-release versions (equivalent to `>0.0.0-0`). |
| `--dependency-update` | `---->` | Download/update missing chart dependencies before installing (like `helm dependency update`). |

## Rendering options

| Option | Marker | Description |
|---|---|---|
| `--no-hooks` | `---->` | Skip all chart lifecycle hooks (pre/post-install, etc.) during the install. |
| `--disable-openapi-validation` | `---->` | Do not validate the rendered manifests against the cluster's OpenAPI schema. Faster, but skips a safety net. |
| `--name-template=TEMPLATE` | `---->` | Go template used to compute the release name, as an alternative to a fixed `--name`. |
| `--render-subchart-notes` | `---->` | Also render the NOTES.txt of subcharts, not just the parent chart. |
| `--skip-crds` | `---->` | Do not install the CRDs that ship with the chart (use when CRDs are managed separately). |
| `--post-renderer=PATH` | `---->` | Executable run on the rendered manifests before they are applied, enabling Kustomize-style post-processing. |
| `--post-renderer-args=ARG` | `---->` | An argument passed to the `--post-renderer` executable. Repeatable. |
| `--timeout=DURATION` | `---->` | Maximum time to wait for any single Kubernetes operation, as a Go duration (`5m0s`, `90s`, …). Default `5m0s`. Most relevant together with `--wait`. |

## Helm global options

| Option | Marker | Description |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | Namespace the release is installed into. If it does not exist, combine with `--create-namespace`. Defaults to the current kube-context's namespace. |
| `--kube-context=CONTEXT` | `---->` | Context inside your kubeconfig to use, targeting a specific cluster/user without switching your active context. |
| `--kubeconfig=FILE` | `---->` | Path to the kubeconfig file to use instead of `$KUBECONFIG` / `~/.kube/config`. |
| `--kube-apiserver=ADDRESS` | `---->` | Override the API server address and port from the kubeconfig. |
| `--kube-as-user=USER` | `---->` | Impersonate this user (RBAC user impersonation); requires impersonation rights. |
| `--kube-as-group=GROUP` | `---->` | Impersonate this group. Repeatable for multiple groups. |
| `--kube-ca-file=FILE` | `---->` | CA certificate file used to verify the API server's TLS certificate. |
| `--kube-token=TOKEN` | `---->` | Bearer token used to authenticate instead of the kubeconfig credentials. |
| `--kube-tls-server-name=NAME` | `---->` | Server name used when validating the API server certificate, if it differs from the URL host. |
| `--kube-insecure-skip-tls-verify` | `---->` | Skip verification of the API server certificate. Insecure — trusted/test environments only. |
| `--burst-limit=INT` | `---->` | Client-side throttling burst limit for API requests (default `100`); raise for very large charts. |
| `--qps=QPS` | `---->` | Client-side queries-per-second limit toward the API server; accepts fractional values. |
| `--registry-config=FILE` | `---->` | Path to the OCI registry configuration (credentials) file. |
| `--repository-cache=DIR` | `---->` | Directory of cached repository indexes used to resolve dependencies. |
| `--repository-config=FILE` | `---->` | Path to the file mapping repository names to URLs (`repositories.yaml`). |

## Global options

All [global options](index.md#global-options) (e.g. `--debug`, `--unsafe`) are available as well.

## Examples

```bash
# Install release "my-app" into the "prod" namespace and wait for readiness
kube-kts install /path/to/repository --name my-app -n prod --create-namespace --wait

# Atomic install with value overrides and a longer timeout
kube-kts install . --name my-app --atomic --timeout 10m --set image.tag=1.2.3 -f prod.yaml

# Preview what would be installed without touching the cluster
kube-kts install ./helm --name my-app --dry-run
```
