# SealedSecret DSL

The `sealedSecret` DSL is used to configure [Bitnami Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets)
(`bitnami.com/v1alpha1`). A SealedSecret holds **encrypted** data that only the in-cluster controller
can decrypt into a regular Kubernetes [Secret](secret.md). Because the values are encrypted, a
SealedSecret is safe to store in version control.

!!! warning "Security: Import Restrictions"
    By default, KTS scripts **do not allow** `import` statements or fully qualified class names
    (e.g. `java.lang.Runtime`). Only types provided via the pre-configured default imports may
    be used.

    Use the `--unsafe` flag to lift these restrictions.

!!! note "The SealedSecrets controller must be installed"
    A SealedSecret can only be decrypted by the SealedSecrets controller running in the target
    cluster. The encrypted values are produced by `kubeseal` against that controller's public key.

## Basic Usage

A minimal SealedSecret configuration requires `metadata` and at least one entry in `encryptedData`.

```kotlin
sealedSecret {
    metadata("my-sealed-secret") {
        namespace = "default"
    }

    spec {
        addEncryptedData("password", "AgBy3i4OJSWK+PiTySYZZA9rO...")
    }
}
```

## Detailed Example

The following example shows multiple encrypted entries and a Secret `template` describing the
Secret the controller should produce.

```kotlin
sealedSecret {
    metadata("full-sealed-secret") {
        namespace = "production"
        labels {
            label("app", "demo")
        }
    }

    spec {
        encryptedData {
            entry("username", "AgAKv2H8x9Qm0pLrT3uVwX1yZ...")
            entry("password", "AgBy3i4OJSWK+PiTySYZZA9rO...")
        }

        // Describes the Secret produced by the controller
        template {
            type = SecretSpec.Type.Opaque
            immutable = true
            metadata {
                labels {
                    label("app", "demo")
                }
                annotations {
                    annotation("description", "Application credentials")
                }
            }
        }
    }
}
```

## Configuration Reference

### Metadata (`metadata`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the SealedSecret, passed as the first argument. |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix for generating a unique name. |
| `labels { label(key, value) }` | `Map<String, String>` | Labels on the SealedSecret resource. |
| `annotations { annotation(key, value) }` | `Map<String, String>` | Annotations on the SealedSecret resource. |

### SealedSecret Specification (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `addEncryptedData(key, value)` | Adds a single encrypted entry to `encryptedData`. At least one entry is required. |
| `encryptedData { entry(key, value) }` | Adds multiple encrypted entries to `encryptedData` via a block. |
| `template { ... }` | Optional template describing the produced Secret (see [Template](#template-template)). |

### Template (`template`)

The `template` block describes the Secret that the controller produces after decryption.

| Property / Method | Description |
| :--- | :--- |
| `type` | The type of the produced Secret (same values as the [Secret DSL](secret.md#secret-types)). |
| `immutable` | If `true`, the produced Secret cannot be updated once created. |
| `metadata { labels { ... }; annotations { ... } }` | Labels and annotations applied to the produced Secret. |
