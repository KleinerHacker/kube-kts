# Kube KTS

Kube KTS is a powerful wrapper for Helm that allows you to transition from traditional Go-templating to **Kotlin Scripts (KTS)**.

---

**"Work in Progress"**
    
This project is a work in progress. Comprehensive documentation is available via [MK Docs](https://kleinerhacker.github.io/kube-kts/).

---

## Overview

### Motivation

Traditional Helm Go-Templates often break the YAML structure, making them difficult to read, maintain, and debug. By leveraging Kotlin Scripts (KTS), similar to Gradle, you benefit from a declarative "look and feel" while retaining the full programmatic power of Kotlin. 

Key advantages include:
- **Type Safety:** Catch errors during compilation rather than at runtime.
- **Validation:** Built-in validation during rendering.
- **Readability:** Maintain clean YAML structures without template logic interference.

### Structure

Kube KTS integrates seamlessly with your existing Helm workflows. You maintain a standard `helm` directory, but instead of writing `.yaml` templates, you use Kotlin Script files. The tool compiles and renders these into 100% Helm-compatible YAML files.

#### Legacy Support

Kube KTS fully supports classic Helm Go-templates. Files with `.yaml` or `.yml` extensions are processed as traditional templates, and all other file types are preserved and copied to the output.

### Values

The `values.yaml` file remains the central place for configuration. Multiple value files can be combined into a single map, just as in Helm.

In KTS, the root `values` key is handled automatically. For complex objects, lambda functions allow you to easily scope and access nested configuration nodes.

---

For more details, visit the [official documentation](https://kleinerhacker.github.io/kube-kts/) or [API Doc](https://kleinerhacker.github.io/kube-kts/dokka/html).

For licence information see [here](https://kleinerhacker.github.io/kube-kts/licences).