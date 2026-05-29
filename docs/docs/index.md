# Overview

Kube KTS is a Helm wrapper for Kubernetes that is **fully compatible with legacy Helm**.  
Instead of using YAML files with Go templates, you define your Kubernetes resources using **Kotlin Script (KTS)**.

This enables the use of modern language features such as:

- Type safety
- Null safety
- Functional programming constructs

In addition, you benefit from:

- Full IDE support (e.g., IntelliJ IDEA, VS Code)
- Debugging capabilities
- Improved developer ergonomics compared to template-based approaches

---

# Getting Started

After installing Kube KTS and adding it to your `PATH`, you can start developing Kubernetes resources immediately.

Kube KTS operates on a **standard Helm repository structure**.  
For proper IDE support, all `.kts` files must be located inside the `helm` directory.

---

## Project Structure

The project structure mirrors a classic Helm repository:

```
─ helm
  ├── Chart.spec.kts
  ├── values.yaml
  └── templates
      ├── deployment.spec.kts
      ├── service.spec.kts
      ├── helpers.lib.kts
      └── ...
```

During processing, Kube KTS generates a Helm-compatible output:

```
─ helm
  ├── Chart.yaml
  ├── values.yaml
  └── templates
      ├── deployment.yaml
      ├── service.yaml
      └── ...
```

!!! note "Library files are not rendered"
    Files with the `.lib.kts` extension are not rendered to YAML. Their content is
    automatically made available in all spec files at compile time.

---

## KTS File Types

Kube KTS distinguishes between two types of Kotlin Script files:

| Extension | Purpose |
| :--- | :--- |
| `*.spec.kts` | Defines a Kubernetes resource (rendered to YAML) |
| `*.lib.kts` | Defines helper functions available in all spec files |

### Spec Files (`*.spec.kts`)

Spec files define Kubernetes resources using the KTS DSL. Each spec file produces one YAML
output file. `Chart.spec.kts` is a special spec file that produces `Chart.yaml`.

```kotlin
// templates/deployment.spec.kts
deployment {
    metadata("my-app") { }
    spec { /* ... */ }
}
```

### Library Files (`*.lib.kts`)

Library files define reusable helper functions that are automatically available in all spec files
within the same repository. Library files are **not** rendered to YAML output and are **not**
accessible from other library files.

```kotlin
// templates/helpers.lib.kts
fun appLabels(name: String) = mapOf("app" to name, "managed-by" to "kube-kts")
```

```kotlin
// templates/deployment.spec.kts — can call appLabels() directly
deployment {
    metadata("my-app") {
        labels { appLabels("my-app").forEach { label(it.key, it.value) } }
    }
}
```

---

## Legacy Support

Kube KTS is fully compatible with legacy Helm setups.

- Existing YAML files can be used alongside `.spec.kts` files
- Plain `.kts` files (without the `.spec.` qualifier) are also supported as legacy spec files
- Legacy files are **copied unchanged** into the final output
- Mixed environments (YAML + KTS) are fully supported

---

## Values Files

Kube KTS supports Helm-style values files.

- Values can be defined in `values.yaml`
- Overrides behave identically to standard Helm
- Values are accessible within KTS scripts

---

## Running Kube KTS

Kube KTS is a command-line tool that processes your Helm repository.

Run it via:
`kube-kts`
To process a repository, provide the path to your Helm project.  
See **"How to Use Kube KTS CLI"** for detailed usage instructions.