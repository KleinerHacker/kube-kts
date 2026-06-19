# Secret DSL

`secret` DSL 用于配置 Kubernetes Secret 资源。Secret 用于存储少量敏感数据，例如密码、
令牌或密钥，Pod 可以将其作为环境变量，或通过卷挂载的文件来使用。

!!! warning "安全性：导入限制"
    默认情况下，KTS 脚本**不允许**使用 `import` 语句或完全限定的类名
    （例如 `java.lang.Runtime`）。只能使用通过预配置默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

!!! note "Secret 仅经过 base64 编码，并非加密"
    Secret 中的值以 base64 编码存储，**并非**加密。任何对 Secret（或 etcd）拥有读取权限
    的人都能解码它们。请勿将渲染后的 Secret 提交到版本控制。若需要可安全提交到版本控制的
    替代方案，请使用 [SealedSecret](sealedsecret.md)。

## 基本用法

最小的 Secret 配置需要 `metadata` 以及 `spec` 中至少一个数据条目。

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

## 详细示例

以下示例展示了 Secret 类型、二进制数据、字符串数据以及不可变性。

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
        // 内置的 Secret 类型
        type = SecretSpec.Type.Opaque

        // 二进制数据 —— 在 `data:` 下以 base64 编码渲染
        data {
            entry("token", tokenBytes)
        }

        // 纯字符串数据 —— 在 `stringData:` 下渲染
        stringData {
            entry("username", "admin")
            entry("password", "s3cr3t")
        }

        // 创建后 Secret 将无法再被修改
        immutable = true
    }
}
```

## 配置参考

### 元数据 (`metadata`)

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `name` | `String` | Secret 的名称，作为第一个参数传入。 |
| `namespace` | `String?` | 资源所在的命名空间。 |
| `generateName` | `String?` | 用于生成唯一名称的可选前缀。 |
| `labels { label(key, value) }` | `Map<String, String>` | Secret 资源上的标签。 |
| `annotations { annotation(key, value) }` | `Map<String, String>` | Secret 资源上的注解。 |

### Secret 规约 (`spec`)

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `type` | Secret 类型（参见 [Secret 类型](#secret)）。省略时默认为 `Opaque`。 |
| `addData(key, value)` | 向 `data` 添加单个二进制 (`ByteArray`) 条目。 |
| `data { entry(key, value) }` | 通过代码块向 `data` 添加多个二进制条目。 |
| `addStringData(key, value)` | 向 `stringData` 添加单个纯字符串条目。 |
| `stringData { entry(key, value) }` | 通过代码块向 `stringData` 添加多个纯字符串条目。 |
| `immutable` | 若为 `true`，Secret 创建后将无法更新。 |

!!! note "`data` 与 `stringData`"
    `data` 用于原始字节内容；其值在渲染的 YAML 中以 base64 编码出现在 `data:` 下。
    `stringData` 用于 UTF-8 字符串值；它们以明文形式出现在 `stringData:` 下，并由 Kubernetes
    合并到 `data` 中。键在两个映射中必须唯一。

### Secret 类型

| DSL 值 | 渲染类型 |
| :--- | :--- |
| `SecretSpec.Type.Opaque` | `Opaque` |
| `SecretSpec.Type.ServiceAccountToken` | `kubernetes.io/service-account-token` |
| `SecretSpec.Type.DockerCfg` | `kubernetes.io/dockercfg` |
| `SecretSpec.Type.DockerConfigJson` | `kubernetes.io/dockerconfigjson` |
| `SecretSpec.Type.BasicAuth` | `kubernetes.io/basic-auth` |
| `SecretSpec.Type.SshAuth` | `kubernetes.io/ssh-auth` |
| `SecretSpec.Type.Tls` | `kubernetes.io/tls` |
| `SecretSpec.Type.BootstrapToken` | `bootstrap.kubernetes.io/token` |
