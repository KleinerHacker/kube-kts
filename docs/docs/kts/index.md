# Kotlin Scripts (KTS)

A Kube KTS repository describes Kubernetes resources with **Kotlin Scripts** instead of Helm's
Go-templated YAML. Each script is plain Kotlin, evaluated against your `values`, and turned into a
normal Helm chart that Helm then consumes. The result is the same Helm workflow you already know —
but with type safety, null safety, full IDE support and a debuggable, logic-free YAML output.

This page explains how the scripts are meant to be understood and what the **safe vs. unsafe**
distinction is about. The individual resources (Chart, Service, Deployment, …) are documented on
their own pages in this section.

---

## How a KTS repository is understood

A repository is just a directory tree that mirrors a Helm chart. Kube KTS scans it, replaces the
`*.kts` files with their rendered YAML and copies everything else through unchanged.

```
─ helm
  ├── Chart.spec.kts        → Chart.yaml
  ├── values.yaml           (copied as-is, also fed to the scripts)
  └── templates
      ├── helpers.lib.kts   (helper functions — never rendered)
      ├── deployment.spec.kts → deployment.yaml
      └── service.spec.kts    → service.yaml
```

### File types

| Extension | Meaning | Rendered to YAML |
| :--- | :--- | :--- |
| `*.spec.kts` | Defines exactly one Kubernetes resource via the DSL (`deployment { … }`, `service { … }`, …). | Yes |
| `*.lib.kts` | Defines reusable helper functions/constants available in **all** spec files. See [Library Files](lib.md). | No |
| `*.kts` | Legacy — treated like a spec file. | Yes |
| `*.yaml` / `*.yml` | Plain Helm YAML, kept for full backward compatibility. | Copied unchanged |

### What a script actually is

Every `*.spec.kts` file is an ordinary Kotlin script. It calls a single top-level DSL function that
builds the resource, and it can read the chart's values along the way:

```kotlin
// templates/deployment.spec.kts
deployment {
    metadata("backend") { }
    spec {
        replicas = valueOrNull<Int>("spec.replicas") ?: 1
        template {
            spec {
                container("backend") {
                    image = value<String>("spec.image")
                }
            }
        }
    }
}
```

Because this is real Kotlin compiled before it runs, mistakes such as a wrong type, a typo in a
property name or a missing required value surface **at compile time** instead of producing broken
YAML at deploy time. Value access (`value`, `valueOrNull`, `array`, `map`, …) is described under
[Values Handling](values.md).

---

## Safe vs. Unsafe

To keep rendering predictable and to limit what a script can do on your machine, Kube KTS compiles
scripts in **safe mode by default**. Safe mode constrains the scripts to the Kube KTS DSL and a
curated set of pre-imported types; unsafe mode lifts those constraints and lets a script run
arbitrary JVM code.

### What safe mode does

Before a script is compiled, its source is checked (string literals and comments are stripped first,
so the rules only apply to actual code). Two things are rejected:

| Rejected in safe mode | Example | Why |
| :--- | :--- | :--- |
| `import` statements | `import java.io.File` | Prevents pulling in arbitrary classes. |
| Fully qualified class names | `java.lang.Runtime.getRuntime()` | Prevents reaching arbitrary JVM APIs without an import. |

If either is found, compilation fails immediately with an explanatory message — no YAML is produced.

Safe scripts are not "empty", though: a generous set of types is **pre-imported** and usable without
any `import`, including the complete Kube KTS DSL plus common standard-library types:

| Pre-imported | Includes |
| :--- | :--- |
| Kube KTS API | All `*Spec` / `*SpecBuilder` types, `ValueAccess`, the unit extensions (`250.mCpu`, `1.giBytes`, `25.percent`, …) |
| `java.net` | `URL`, `URI` |
| `java.time` | `Duration`, `Instant`, `LocalDate`, … |
| `kotlin.time` | `Duration`, `Duration.Companion.*` |

For the vast majority of charts this is everything you need, so you should stay in safe mode.

### What unsafe mode does

Unsafe mode simply **skips** the import/FQN checks above. Scripts may then use `import` statements
and fully qualified class names, which means they can call any class on the JVM classpath — read
files, start processes, open network connections, and so on.

That power is also the risk: a script is no longer limited to describing Kubernetes resources, it can
execute arbitrary code on the machine that runs the render. Only enable unsafe mode for repositories
you fully trust and have reviewed.

!!! warning "Unsafe mode runs arbitrary code"
    With unsafe mode active there is no sandbox: a malicious or buggy script can do anything the
    current user can do. Treat enabling it as running untrusted code.

---

## The command-line operator

Safe vs. unsafe is controlled by the global **`--unsafe`** flag of the CLI. Without it, every script
is compiled in safe mode; with it, the import/FQN restrictions are lifted for all scripts in the run.

```bash
# safe (default) — imports and FQNs are rejected
kube-kts render ./helm ./out

# unsafe — imports and fully qualified class names are allowed
kube-kts --unsafe render ./helm ./out
```

`--unsafe` is a **global** option, so it is placed before the command and applies to any command that
compiles scripts (`validate`, `compile`, `render`, `lint`, `template`, `install`, …). In the
`--help` output and the command pages it carries the `!!!` marker, which flags it as a
security-relevant option. See the [CLI overview](../cli/index.md#global-options) for the full list of
global options.
