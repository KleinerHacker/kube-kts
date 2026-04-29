# Overview

Kube KTS is a Helm wrapper for Kubernetes, 100% compatible with legacy Helm.
Instead to use YAML files and Go templates you can use Kotlin Script files to
describe your Kubernetes resources.

This make it able to use Kotlin features like type safety, null safety, 
and functional programming constructs. Additionally, you can use debugging
features and have full IDE support by IntelliJ IDEA.

# Getting Started

After you install Kube KTS and add it to PATH you can start developing your
Kubernetes resources. Basically, you must create a classic helm repository. 
To use IDE support you must add all KTS files in folder `helm`. You can use
the known structure from legacy Helm repository.

## Project Structure

The project structure is the same as legacy Helm repository:

```
─ helm
  ├── Chart.kts
  ├── values.yaml
  └── templates
      ├── deployment.kts
      ├── service.kts
      └── ...
```

The resulting repository used with helm looks like this:

```
─ helm
  ├── Chart.yaml
  ├── values.yaml
  └── templates
      ├── deployment.yaml
      ├── service.yaml
      └── ...
```

## Legacy Support

Kube KTS is fully compatible with legacy Helm. This means that you can use
existing legacy Helm files (YAML) together with KTS files. The legaxy files 
will be copied in final structure without any changes.

## Value Files

Kube KTS supports value files. You can use them to override values in your
KTS files. The overlay support works the same way as in legacy Helm.

## Run Kube KTS

Kube KTS is a command line tool. You can run it with `kube-kts` command. To
handle repository you must define the path to the repository. See more in 
part of "How to use Kube KTS CLI".  