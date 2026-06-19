# Secret DSL

The `secret` DSL is used to configure Kubernetes Secret resources. A Secret stores a small amount
of sensitive data such as passwords, tokens, or keys that Pods can consume as environment variables
or files mounted through a volume.

!!! warning "Security: Import Restrictions"
    By default, KTS scripts **do not allow** `import` statements or fully qualified class names
    (e.g. `java.lang.Runtime`). Only types provided via the pre-configured default imports may
    be used.

    Use the `--unsafe` flag to lift these restrictions.

!!! note "Secrets are only base64-encoded, not encrypted"
    The values in a Secret are stored base64-encoded, **not** encrypted. Anyone with read access to
    the Secret (or to etcd) can decode them. Do not commit rendered Secrets to version control. For
    a version-control-safe alternative, use a [SealedSecret](sealedsecret.md).

## Basic Usage

A minimal Secret configuration requires `metadata` and at least one data entry in `spec`.

```kotlin
secret {
    metadata("my-secret") {
        namespace = "default"
    }

    spec {
        addStringData("username", "admin")
        addStringData("password", "s3cr3t")
    }
}
```

## Detailed Example

The following example shows the secret type, binary data, string data, and immutability.

```kotlin
secret {
    metadata("full-secret") {
        namespace = "production"
        labels {
            label("app", "demo")
        }
        annotations {
            annotation("description", "Application credentials")
        }
    }

    spec {
        // The built-in Secret type
        type = SecretSpec.Type.Opaque

        // Binary data — rendered base64-encoded under `data:`
        data {
            entry("token", tokenBytes)
        }

        // Plain string data — rendered under `stringData:`
        stringData {
            entry("username", "admin")
            entry("password", "s3cr3t")
        }

        // Once created, the Secret can no longer be changed
        immutable = true
    }
}
```

## Configuration Reference

### Metadata (`metadata`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the Secret, passed as the first argument. |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix for generating a unique name. |
| `labels { label(key, value) }` | `Map<String, String>` | Labels on the Secret resource. |
| `annotations { annotation(key, value) }` | `Map<String, String>` | Annotations on the Secret resource. |

### Secret Specification (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `type` | The Secret type (see [Secret types](#secret-types)). Defaults to `Opaque` when omitted. |
| `addData(key, value)` | Adds a single binary (`ByteArray`) entry to `data`. |
| `data { entry(key, value) }` | Adds multiple binary entries to `data` via a block. |
| `addStringData(key, value)` | Adds a single plain string entry to `stringData`. |
| `stringData { entry(key, value) }` | Adds multiple plain string entries to `stringData` via a block. |
| `immutable` | If `true`, the Secret cannot be updated once created. |

!!! note "`data` vs. `stringData`"
    Use `data` for raw byte content; values are base64-encoded under `data:` in the rendered YAML.
    Use `stringData` for UTF-8 string values; they appear in plain form under `stringData:` and are
    merged into `data` by Kubernetes. A key must be unique across both maps.

### Secret types

| DSL value | Rendered type |
| :--- | :--- |
| `SecretSpec.Type.Opaque` | `Opaque` |
| `SecretSpec.Type.ServiceAccountToken` | `kubernetes.io/service-account-token` |
| `SecretSpec.Type.DockerCfg` | `kubernetes.io/dockercfg` |
| `SecretSpec.Type.DockerConfigJson` | `kubernetes.io/dockerconfigjson` |
| `SecretSpec.Type.BasicAuth` | `kubernetes.io/basic-auth` |
| `SecretSpec.Type.SshAuth` | `kubernetes.io/ssh-auth` |
| `SecretSpec.Type.Tls` | `kubernetes.io/tls` |
| `SecretSpec.Type.BootstrapToken` | `bootstrap.kubernetes.io/token` |
