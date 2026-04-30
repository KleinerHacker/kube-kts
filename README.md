# Kube KTS

Kube KTS is a solution and wrapper for Helm to change from classic Go-Templating
to Kotlin Scripts. 

---

IMPORTANT HINT: This is a work in progress. The complete documentation will follow
with the help of MK Docs in future times.

## Overview

### Motivation

The Helm Go-Templates destroy YAML structure and make it hard to read templates
and debug them. Like usage in Gradle with Kotlin Script (KTS) you can use the 
Vorteil of its declarative look and feel. At the same time you can use classic
programmatic structures known from Kotlin. Additionally, you get type safety and
validation while compiling and rendering.

### Structure

Like Helm create a normal `helm` directory with all known files. Instead, to
create YAML files, you can use Kotlin Script files. With this tool you can now
compile and render it to classic YAML files, 100% compatible with Helm.

#### Legacy Support

Kube KTS also supports the classic Helm Go-Templates. All files with the `.yaml` or `.yml`
extension are used as classic Helm Go-Templates. Additionally, all other file types
are copied to the YAML repository, too.

### Values

The values.yaml file is usable like in classic Helm Go-Templates. Multiple values
would be combined into one map.

In KTS you do not need to set the root key `values`. This is done automatically. In
the case of complex objects, it is required to use lambda functions to use the new root
node from the values.yaml file at this position. 

---

For more information see the [documentation](https://kleinerhacker.github.io/kube-kts/).