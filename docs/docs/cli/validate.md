# validate

```bash
kube-kts validate <REPOSITORY>
```

Validates a KTS repository. The command scans the repository, discovers every `*.spec.kts`,
`*.lib.kts` and legacy YAML/template file, and checks that the repository is structurally
well-formed and that all required files (such as the chart definition) are present. It does **not**
compile or evaluate the scripts and produces no output artifacts — it is purely a fast structural
check.

Use it as the first, cheapest gate in a CI pipeline: it catches missing or misplaced files long
before the more expensive compile/render steps run, and it never touches a cluster or Helm.

!!! tip "Where validate fits"
    `validate` only checks the *structure*. To additionally verify that the Kotlin scripts compile
    and evaluate, use [`compile`](compile.md); to also produce Helm YAML, use [`render`](render.md).

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the repository to validate. If omitted, the current working directory is used. A non-existent path fails the command immediately. |

## Options

This command only accepts the [global options](index.md#global-options). The most useful ones here
are `--verbose` (to see exactly which files are discovered) and `--exception` (to get a stack trace
if validation fails for a non-obvious reason).

## Examples

```bash
# Validate the current directory
kube-kts validate .

# Validate a specific repository and show every discovered file
kube-kts validate /path/to/repository --verbose

# Validate in CI and print a full stack trace on failure
kube-kts validate ./helm --exception
```
