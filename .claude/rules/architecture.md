---
name: Architecture
---

# Architecture

The project consists of several modules:
* `libs/api` - Contains the API for all supported Helm templates as Kotlin Scripts
* `libs/core` - Contains the logic to parse and run the Kotlin Scripts in repositories
* `libs/definition` - Contains the classes required for IntelliJ to interpret Kotlin Script references correctly
* `libs/logging` - Contains the symbols required for logging
* `apps/cli` - CLI application to run Kube KTS like Helm with CLI commands, based on picocli

## Package Structure

* Root package `org.pcsoft.framework.kube.kts`
  * Each module has its own sub-package named after the module
