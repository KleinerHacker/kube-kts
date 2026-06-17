# compile

```bash
kube-kts compile <REPOSITORY> [--unsafe]
```

Compiles the KTS repository into object instances. This goes a step further than
[`validate`](validate.md): it actually executes the Kotlin scripts and builds the in-memory object
model of the chart (charts, deployments, services, …) that the renderer later turns into YAML. It is
the right command to confirm that your scripts **compile and evaluate** correctly — including type
errors, null-safety violations and runtime exceptions thrown inside the scripts — without yet writing
any output.

Because compilation runs your scripts, it is also where script-level problems surface first: an
invalid value lookup, a failed `require(...)`, or a script that references a class it is not allowed
to import.

!!! warning "`--unsafe` runs arbitrary code"
    By default the scripts run in a restricted sandbox that forbids `import` statements and fully
    qualified class names. If your scripts need them, add `--unsafe` — but only for repositories you
    trust, because it lets the scripts execute arbitrary JVM code.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `REPOSITORY` | yes | Path to the repository to compile. If omitted, the current working directory is used. A non-existent path fails immediately. |

## Options

In addition to the [global options](index.md#global-options), the [`--unsafe`](index.md#global-options)
flag is particularly relevant here, since compilation is the stage that executes the scripts:

| Option | Marker | Description |
|---|---|---|
| `--unsafe` | `!!!` | Allow `import` statements and fully qualified class names in the scripts so they can use additional JVM classes. Enables arbitrary code execution — use only with trusted repositories. |

## Examples

```bash
# Compile the current directory
kube-kts compile .

# Compile a repository whose scripts use imports
kube-kts compile /path/to/repository --unsafe

# Compile with a full stack trace if a script throws
kube-kts compile ./helm --exception
```
