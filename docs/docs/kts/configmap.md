# ConfigMap DSL

The `configMap` DSL is used to configure Kubernetes ConfigMap resources. A ConfigMap stores
non-confidential configuration data as key-value pairs that Pods can consume as environment
variables, command-line arguments, or files mounted through a volume.

!!! warning "Security: Import Restrictions"
    By default, KTS scripts **do not allow** `import` statements or fully qualified class names
    (e.g. `java.lang.Runtime`). Only types provided via the pre-configured default imports may
    be used.

    Use the `--unsafe` flag to lift these restrictions.

!!! note "ConfigMaps are for non-confidential data"
    ConfigMaps are not encrypted and must not be used to store sensitive information such as
    passwords or tokens. Use a Secret for confidential data.

## Basic Usage

A minimal ConfigMap configuration requires `metadata` and at least one entry in `spec`.

```kotlin
configMap {
    metadata("my-config") {
        namespace = "default"
    }

    spec {
        addData("application.name", "demo")
        addData("log.level", "INFO")
    }
}
```

## Detailed Example

The following example shows string data, binary data, and immutability.

```kotlin
configMap {
    metadata("full-config") {
        namespace = "production"
        labels {
            label("app", "demo")
        }
        annotations {
            annotation("description", "Application configuration")
        }
    }

    spec {
        // String data — rendered under `data:`
        data {
            entry("log.level", "DEBUG")
            entry(
                "application.yaml",
                """
                server:
                  port: 8080
                """.trimIndent()
            )
        }

        // Binary data — rendered base64-encoded under `binaryData:`
        binaryData {
            entry("logo.png", logoBytes)
        }

        // Once created, the ConfigMap can no longer be changed
        immutable = true
    }
}
```

## Configuration Reference

### Metadata (`metadata`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the ConfigMap, passed as the first argument. |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix for generating a unique name. |
| `labels { label(key, value) }` | `Map<String, String>` | Labels on the ConfigMap resource. |
| `annotations { annotation(key, value) }` | `Map<String, String>` | Annotations on the ConfigMap resource. |

### ConfigMap Specification (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `addData(key, value)` | Adds a single string entry to `data`. |
| `data { entry(key, value) }` | Adds multiple string entries to `data` via a block. |
| `addBinaryData(key, value)` | Adds a single binary (`ByteArray`) entry to `binaryData`. |
| `binaryData { entry(key, value) }` | Adds multiple binary entries to `binaryData` via a block. |
| `immutable` | If `true`, the ConfigMap cannot be updated once created. |

!!! note "`data` vs. `binaryData`"
    Use `data` for UTF-8 string values; they appear directly under `data:` in the rendered YAML.
    Use `binaryData` for arbitrary byte content; values are base64-encoded under `binaryData:`.
    A key must be unique across both maps.
