# ConfigMap DSL

`configMap` DSL 用于配置 Kubernetes ConfigMap 资源。ConfigMap 以键值对的形式存储非机密
配置数据，Pod 可以将其作为环境变量、命令行参数，或通过卷挂载的文件来使用。

!!! warning "安全性：导入限制"
    默认情况下，KTS 脚本**不允许**使用 `import` 语句或完全限定的类名
    （例如 `java.lang.Runtime`）。只能使用通过预配置默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

!!! note "ConfigMap 用于非机密数据"
    ConfigMap 不会被加密，不得用于存储密码或令牌等敏感信息。
    机密数据请使用 Secret。

## 基本用法

最小的 ConfigMap 配置需要 `metadata` 以及 `spec` 中至少一个条目。

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

## 详细示例

下面的示例展示了字符串数据、二进制数据和不可变性。

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

## 配置参考

### 元数据（`metadata`）

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `name` | `String` | ConfigMap 的名称（作为第一个参数传递）。 |
| `namespace` | `String?` | 资源的命名空间。 |
| `generateName` | `String?` | 用于生成唯一名称的可选前缀。 |
| `labels { label(key, value) }` | `Map<String, String>` | ConfigMap 资源上的标签。 |
| `annotations { annotation(key, value) }` | `Map<String, String>` | ConfigMap 资源上的注解。 |

### ConfigMap 规约（`spec`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `addData(key, value)` | 向 `data` 添加单个字符串条目。 |
| `data { entry(key, value) }` | 通过块向 `data` 添加多个字符串条目。 |
| `addBinaryData(key, value)` | 向 `binaryData` 添加单个二进制（`ByteArray`）条目。 |
| `binaryData { entry(key, value) }` | 通过块向 `binaryData` 添加多个二进制条目。 |
| `immutable` | 若为 `true`，则 ConfigMap 创建后无法被更新。 |

!!! note "`data` 与 `binaryData`"
    `data` 用于 UTF-8 字符串值；它们会直接出现在渲染后 YAML 的 `data:` 下。
    `binaryData` 用于任意字节内容；其值会以 base64 编码出现在 `binaryData:` 下。
    键在这两个 map 之间必须唯一。
