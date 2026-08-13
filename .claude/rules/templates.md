---
name: templates
---

# Templates

* A Helm template represented as a Kotlin Script MUST be wrapped completely and deeply
* A template MUST be tested in a minimal and a maximal variant
* A template MUST be tested with different values

## Structure

* Required sub-classes for a template MUST be written in their own class files
  * Except: if the class is 1. only part of this template and 2. very small; ask the user in this case
* Each template has its own builder
  * Each builder MUST be written in its own class file
  * Each sub-builder MUST be written in its own class file
    * Except: if the class assigned to this builder is written as a nested class
