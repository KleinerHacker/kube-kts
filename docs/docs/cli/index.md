# Kube KTS CLI

Kube KTS is a command-line tool that is executed with the `kube-kts` command. It acts as a
type-safe wrapper around Helm: instead of Go templates it uses Kotlin Scripts (KTS) to describe
Kubernetes resources, renders them to plain Helm charts and then delegates to Helm for the actual
cluster operations.

This section is the primary reference if the built-in `--help` output is not enough. The help text
is intentionally short so that it still fits on screen; the pages here go further and explain the
behaviour, defaults, units and interactions of every option in detail. Each command has its own
page that documents **all** of its options.

## How it relates to Helm

Kube KTS never replaces Helm — it prepares the input for it. A typical command runs in two phases:

1. **Render phase (Kube KTS):** the repository is scanned, the Kotlin scripts are compiled and
   evaluated, and the result is written as a normal Helm chart into a target directory.
2. **Helm phase (delegation):** for the Helm-backed commands (`lint`, `template`, `install`,
   `upgrade`, `uninstall`) Kube KTS then invokes the real `helm` binary against that rendered chart and forwards
   all Helm-specific options unchanged. A few Helm-backed commands (`status`) skip the render phase
   entirely and call Helm directly, because they operate on an existing release.

Because of this, the Helm-backed commands accept both Kube KTS options (which influence rendering)
and Helm options (which are forwarded). Options that end up at Helm are marked accordingly — see the
[legend](#option-marker-legend) below.

!!! note "Helm must be installed"
    The `helm` executable must be available on your `PATH` for `lint`, `template`, `install`,
    `upgrade`, `uninstall` and `status`. `validate`, `compile` and `render` work without Helm.

## Usage

```bash
kube-kts [GLOBAL OPTIONS] <command> <REPOSITORY> [TARGET] [COMMAND OPTIONS]
```

### Repository and target paths

- **`REPOSITORY`** is the first positional argument and points to the Kube KTS repository (the
  directory that contains your `*.spec.kts`, `*.lib.kts`, `values.yaml`, etc.). If you omit it, the
  current working directory is used. If the path does not exist the command fails immediately,
  before Helm is ever invoked.
- **`TARGET`** is the optional second positional argument used by the rendering commands
  (`render`, `lint`, `template`, `install`, `upgrade`, `uninstall`). It is the directory the chart is rendered
  into. If you omit it, a temporary directory is created and cleaned up by the operating system; pass
  an explicit target when you want to inspect or reuse the generated chart.

### Exit codes

The CLI returns `0` on success and a non-zero exit code on failure (for example an invalid
repository, a failed render, or a non-zero exit code coming back from Helm). Use `--exception` to see
the full stack trace when diagnosing a failure.

## Commands

| Command | Page | Purpose |
|---|---|---|
| `validate` | [validate](validate.md) | Validate a KTS repository without producing output. |
| `compile` | [compile](compile.md) | Compile the KTS repository into object instances. |
| `render` | [render](render.md) | Render the repository to a plain Helm chart on disk. |
| `lint` | [lint](lint.md) | Render and then lint the chart via `helm lint`. |
| `template` | [template](template.md) | Render and print the manifests via `helm template`. |
| `install` | [install](install.md) | Render and install the chart into a cluster via `helm install`. |
| `upgrade` | [upgrade](upgrade.md) | Render and upgrade (or install) a release via `helm upgrade`. |
| `uninstall` | [uninstall](uninstall.md) | Render and uninstall one or more releases via `helm uninstall`. |
| `status` | [status](status.md) | Show the status of a release via `helm status` (no rendering). |

## Do these commands need rendering? (KTS relevance)

Whether a command renders the KTS repository tells you whether your Kotlin scripts are relevant for
that command at all — and therefore whether you need to point it at a repository (KTS, plain YAML, or
mixed) in the first place:

- **Needs rendering → repository required (KTS relevant):** `validate`, `compile`, `render`, `lint`,
  `template`, `install` and `upgrade` all run the *scan → compile → render* pipeline and therefore
  depend on your KTS scripts. You must pass (or stand in) a repository for them.
- **Independent of rendering → no repository needed (KTS not relevant):** operations that act on an
  already installed release, the cluster, or a repository do not need a rendered chart, so the KTS
  scripts play no role for them. `status` is the first such command (it takes the release name as a
  positional and forwards it straight to Helm); future ones like `list` or `rollback` will follow the
  same pattern.

!!! note "`uninstall` is a special case"
    `uninstall` removes a release purely by name and does not technically need a rendered chart.
    For consistency it currently still renders before calling Helm, but functionally the KTS scripts
    are not relevant for it.

## Option marker legend

In the `--help` output a dedicated column shows the nature of each option. The same markers are used
in the option tables on the command pages so the documentation matches the help screen exactly:

| Marker | Meaning |
|---|---|
| `---->` | The option is forwarded to the underlying Helm CLI. Its effect is exactly Helm's effect. |
| `*` | The option is experimental and may change or be removed in future versions. Requires `--experimental`. |
| `!!!` | The option is dangerous / security relevant. Read its description carefully before using it. |

Options without a marker are used internally by Kube KTS only and never reach Helm.

## Global Options

These options are available for every command and are usually placed **before** the command name.

| Option | Marker | Description |
|---|---|---|
| `--debug` | `---->` | Print debug-level diagnostics, including which steps run and the exact arguments passed to Helm. It is also forwarded to Helm as `--debug`, so Helm itself becomes verbose too. Ideal as a first step when something behaves unexpectedly. |
| `--verbose` | | Print everything, including trace-level details (even more than `--debug`). Useful to follow the scan/compile/render pipeline step by step. Not forwarded to Helm. |
| `--show-log-level` | | Prefix every log line with its level (INFO/DEBUG/…). Implied automatically when `--debug` or `--verbose` is active. |
| `--exception` | | On error, print the full exception stack trace instead of a short, user-friendly message. Use it when reporting bugs or diagnosing a non-obvious failure. |
| `--experimental` | | Unlock the experimental options listed below. Without this flag, passing any experimental option makes the command fail with an explanatory error. |
| `--unsafe` | `!!!` | Allow `import` statements and fully qualified class names inside the Kotlin scripts. This lets scripts execute arbitrary JVM code, so only enable it for repositories you fully trust. |

## Experimental Features

Experimental features are not stable and may change in future versions. They require the
`--experimental` flag; otherwise the command fails before doing any work.

| Option | Marker | Description |
|---|---|---|
| `--yaml-merge=TYPE` | `*` | Selects the algorithm used to merge YAML documents (for example a base file with overlays). `HELM` (default) reproduces Helm's own merge semantics so the result matches what Helm would do. `INTERNAL` uses the custom Kube KTS algorithm, which additionally honours `--yaml-array-merge`. |
| `--yaml-array-merge=TYPE` | `*` | Controls how arrays are merged, and only takes effect with `--yaml-merge=INTERNAL`. `None` keeps the base array untouched, `Replace` (default) replaces it with the overlay array, `AddFirst` prepends the overlay items, and `AddLast` appends them. Arrays in plain Helm are always replaced, so this is the knob to change that behaviour. |

## Values

All rendering commands (`render`, `lint`, `template`, `install`, `upgrade`, `uninstall`) accept values files.
Values feed two stages: they are available to the Kotlin scripts during rendering **and** are passed
on to Helm.

| Option | Marker | Description |
|---|---|---|
| `-f`, `--values=FILE` | `---->` | A YAML values file. Used while executing the Kotlin scripts and additionally forwarded to Helm as `-f`. Repeatable: multiple files are layered in the order given, with later files overriding earlier ones. Inline `--set*` overrides (where available) take precedence over all `-f` files. The file must exist or the command fails. |
