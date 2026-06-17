# lint

```bash
kube-kts lint <REPOSITORY> [TARGET] [OPTIONS]
```

Renders the repository to a Helm chart and then runs `helm lint` against it. Linting examines the
rendered chart for problems and best-practice violations — missing required fields, malformed
`Chart.yaml`, templates that do not produce valid YAML, recommended-but-absent metadata, and so on —
and reports them as information, warnings or errors.

Because Kube KTS renders first, `lint` validates the *actual* chart that Helm would receive, with
your values already applied. It is the natural next step after [`compile`](compile.md): compile proves
the scripts run, lint proves the resulting chart is sound.

## How it works

1. The repository is scanned, compiled and rendered into a (temporary or explicit) chart directory.
2. `helm lint .` is executed in that directory, with all forwarded options appended.
3. The combined Kube KTS and Helm output is printed; the command's exit code reflects Helm's result.

!!! tip "Fail the build on warnings"
    By default `helm lint` only fails on errors. Add `--strict` to also fail on warnings, which is
    what you usually want in CI.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the repository to lint. If omitted, the current working directory is used. |
| `TARGET` | no | Directory the chart is rendered into before linting. If omitted, a temporary directory is used. |

## Lint options

| Option | Marker | Description |
|---|---|---|
| `--quiet` | `---->` | Suppress informational messages and print only warnings and errors. Useful for terse CI logs. |
| `--strict` | `---->` | Treat warnings as failures, so any lint warning produces a non-zero exit code. Recommended for pipelines that should not let style/best-practice issues through. |
| `--with-subcharts` | `---->` | Lint the chart's dependent subcharts in addition to the top-level chart, instead of only the parent. |

## Value options

These set or override chart values and are forwarded to Helm. They complement the
[`-f`/`--values`](index.md#values) files.

| Option | Marker | Description |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | Override a value inline, e.g. `--set image.tag=1.2.3`. Types are inferred. Repeatable; later `--set` wins and overrides `-f` files. |
| `--set-string=KEY=VALUE` | `---->` | Like `--set` but the value is always kept as a string (e.g. to preserve `"01"`). Repeatable. |
| `--set-file=KEY=PATH` | `---->` | Use the entire content of the file at `PATH` as the value of `KEY` (handy for certificates or scripts). Repeatable. |
| `--set-json=KEY=JSON` | `---->` | Set a value from a JSON expression, enabling objects and arrays, e.g. `--set-json 'ports=[80,443]'`. Repeatable. |
| `--set-literal=KEY=VALUE` | `---->` | Set the value exactly as written, with no type conversion at all. Repeatable. |
| `-f`, `--values=FILE` | `---->` | YAML values file used for rendering and forwarded to Helm. Repeatable and layered in order. |

## Helm global options

These configure the connection to the cluster and the Helm environment. They are forwarded to Helm
unchanged. (For `lint`, cluster-related options rarely matter because linting is offline, but they
are accepted for consistency.)

| Option | Marker | Description |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | Namespace scope assumed for the request. Defaults to the namespace of your current kube-context. |
| `--kube-context=CONTEXT` | `---->` | Context inside your kubeconfig to use, letting you target a specific cluster/user without switching your active context. |
| `--kubeconfig=FILE` | `---->` | Path to the kubeconfig file to use instead of `$KUBECONFIG` / `~/.kube/config`. |
| `--kube-apiserver=ADDRESS` | `---->` | Override the API server address and port from the kubeconfig. |
| `--kube-as-user=USER` | `---->` | Impersonate this user (RBAC user impersonation); requires impersonation rights. |
| `--kube-as-group=GROUP` | `---->` | Impersonate this group. Repeatable for multiple groups. |
| `--kube-ca-file=FILE` | `---->` | CA certificate file used to verify the API server's TLS certificate. |
| `--kube-token=TOKEN` | `---->` | Bearer token used to authenticate instead of the kubeconfig credentials. |
| `--kube-tls-server-name=NAME` | `---->` | Server name used when validating the API server certificate, if it differs from the URL host. |
| `--kube-insecure-skip-tls-verify` | `---->` | Skip verification of the API server certificate. Insecure — trusted/test environments only. |
| `--burst-limit=INT` | `---->` | Client-side throttling burst limit for API requests (default `100`). |
| `--qps=QPS` | `---->` | Client-side queries-per-second limit toward the API server; accepts fractional values. |
| `--registry-config=FILE` | `---->` | Path to the OCI registry configuration (credentials) file. |
| `--repository-cache=DIR` | `---->` | Directory of cached repository indexes used to resolve dependencies. |
| `--repository-config=FILE` | `---->` | Path to the file mapping repository names to URLs (`repositories.yaml`). |

## Global options

All [global options](index.md#global-options) (e.g. `--debug`, `--unsafe`) are available as well.

## Examples

```bash
# Lint the current directory
kube-kts lint .

# Strict lint with an extra values file and an inline override
kube-kts lint /path/to/repository ./out --strict -f prod.yaml --set image.tag=1.2.3

# Lint including subcharts, quietly
kube-kts lint ./helm --with-subcharts --quiet
```
