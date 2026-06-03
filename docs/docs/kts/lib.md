# Library Files (`*.lib.kts`)

Library files allow you to define reusable helper functions and constants that are automatically
available across all spec files in your Helm repository.

---

## Overview

| Property | Value |
| :--- | :--- |
| File extension | `*.lib.kts` |
| Location | Anywhere inside the `helm/` directory tree |
| Rendered to YAML | No |
| Available in spec files | Yes — all spec files automatically |
| Available in other lib files | No |

---

## Creating a Library File

Create a file with the `.lib.kts` extension anywhere inside your `helm/` directory.
A common convention is to place shared helpers in `templates/helpers.lib.kts`.

```
─ helm
  ├── Chart.spec.kts
  ├── values.yaml
  └── templates
      ├── helpers.lib.kts       ← library file
      ├── deployment.spec.kts
      └── service.spec.kts
```

Inside the file, define any Kotlin functions, constants, or extension functions you need:

```kotlin
// templates/helpers.lib.kts

fun appLabels(name: String): Map<String, String> = mapOf(
    "app.kubernetes.io/name" to name,
    "app.kubernetes.io/managed-by" to "kube-kts"
)

fun fullName(release: String, component: String) = "$release-$component"

const val DEFAULT_REGISTRY = "registry.example.com"
```

---

## Using Library Functions in Spec Files

All functions and constants defined in any `*.lib.kts` file are directly callable in every
`*.spec.kts` file — no import required.

```kotlin
// templates/deployment.spec.kts

deployment {
    metadata(fullName("my-release", "backend")) {
        appLabels("backend").forEach { label(it.key, it.value) }
    }
    spec {
        template {
            spec {
                container("backend") {
                    image = "$DEFAULT_REGISTRY/backend:latest"
                }
            }
        }
    }
}
```

---

## Default Imports

Library files have access to the same default imports as spec files, so you can use all
Kube KTS DSL types, Java standard library types, and Kotlin time extensions without explicit
import statements.

| Import | Includes |
| :--- | :--- |
| Kube KTS API | All `*Spec` and `*SpecBuilder` types, `ValueAccess` |
| `java.net` | `URL`, `URI` |
| `java.time` | `Duration`, `Instant`, `LocalDate`, … |
| `kotlin.time` | `Duration`, `Duration.Companion.*` |

---

## Constraints

!!! warning "Lib files cannot access other lib files"
    Functions defined in one `*.lib.kts` file are **not** available in other `*.lib.kts` files.
    Only `*.spec.kts` files can call lib functions. Structure your libraries accordingly and
    avoid cross-lib dependencies.

!!! note "Library files are not rendered"
    `*.lib.kts` files are never rendered to a YAML output file, even if they contain top-level
    statements. Only `*.spec.kts` files produce output.

---

## Multiple Library Files

You can have as many `*.lib.kts` files as you like. Kube KTS discovers all of them
recursively within the `helm/` directory tree and makes them all available to every spec file.

```
─ helm
  └── templates
      ├── helpers.lib.kts         ← general helpers
      ├── labels.lib.kts          ← label utilities
      ├── resources.lib.kts       ← resource limit constants
      └── deployment.spec.kts     ← can call functions from all three lib files
```
