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
  ├── Chart.kts
  ├── values.yaml
  └── templates
      ├── deployment.kts
      ├── service.kts
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


---

## Legacy Support

Kube KTS is fully compatible with legacy Helm setups.

- Existing YAML files can be used alongside `.kts` files
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