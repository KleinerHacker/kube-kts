# SealedSecret DSL

`sealedSecret` DSL 用于配置 [Bitnami Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets)
（`bitnami.com/v1alpha1`）。SealedSecret 保存**已加密**的数据，只有集群内的控制器才能将其
解密为常规的 Kubernetes [Secret](secret.md)。由于这些值是加密的，SealedSecret 可以安全地
提交到版本控制。

!!! warning "安全性：导入限制"
    默认情况下，KTS 脚本**不允许**使用 `import` 语句或完全限定的类名
    （例如 `java.lang.Runtime`）。只能使用通过预配置默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

!!! note "必须安装 SealedSecrets 控制器"
    SealedSecret 只能由运行在目标集群中的 SealedSecrets 控制器解密。加密后的值由 `kubeseal`
    针对该控制器的公钥生成。

## 基本用法

最小的 SealedSecret 配置需要 `metadata` 以及 `encryptedData` 中至少一个条目。

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

## 详细示例

以下示例展示了多个加密条目以及描述控制器应生成的 Secret 的 `template`。

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

        // 描述控制器生成的 Secret
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

## 配置参考

### 元数据 (`metadata`)

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `name` | `String` | SealedSecret 的名称，作为第一个参数传入。 |
| `namespace` | `String?` | 资源所在的命名空间。 |
| `generateName` | `String?` | 用于生成唯一名称的可选前缀。 |
| `labels { label(key, value) }` | `Map<String, String>` | SealedSecret 资源上的标签。 |
| `annotations { annotation(key, value) }` | `Map<String, String>` | SealedSecret 资源上的注解。 |

### SealedSecret 规约 (`spec`)

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `addEncryptedData(key, value)` | 向 `encryptedData` 添加单个加密条目。至少需要一个条目。 |
| `encryptedData { entry(key, value) }` | 通过代码块向 `encryptedData` 添加多个加密条目。 |
| `template { ... }` | 描述所生成 Secret 的可选模板（参见 [模板](#template)）。 |

### 模板 (`template`)

`template` 代码块描述控制器解密后生成的 Secret。

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `type` | 所生成 Secret 的类型（与 [Secret DSL](secret.md#secret) 的取值相同）。 |
| `immutable` | 若为 `true`，所生成的 Secret 创建后将无法更新。 |
| `metadata { labels { ... }; annotations { ... } }` | 应用于所生成 Secret 的标签和注解。 |
