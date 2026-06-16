# 概览

Kube KTS 是一个用于 Kubernetes 的 Helm 封装器，并且**完全兼容传统 Helm**。
你无需再使用带有 Go 模板的 YAML 文件，而是使用 **Kotlin Script (KTS)** 来定义 Kubernetes 资源。

这使你能够使用现代语言特性，例如：

- 类型安全
- 空安全
- 函数式编程结构

此外，你还将受益于：

- 完整的 IDE 支持（例如 IntelliJ IDEA、VS Code）
- 调试能力
- 相较于基于模板的方式更佳的开发体验

---

# 快速开始

在安装 Kube KTS 并将其加入 `PATH` 之后，你即可立即开始开发 Kubernetes 资源。

Kube KTS 基于**标准的 Helm 仓库结构**运行。
为获得正确的 IDE 支持，所有 `.kts` 文件都必须位于 `helm` 目录内。

---

## 项目结构

项目结构与传统的 Helm 仓库一致：

```
─ helm
  ├── Chart.spec.kts
  ├── values.yaml
  └── templates
      ├── deployment.spec.kts
      ├── service.spec.kts
      ├── helpers.lib.kts
      └── ...
```

在处理过程中，Kube KTS 会生成与 Helm 兼容的输出：

```
─ helm
  ├── Chart.yaml
  ├── values.yaml
  └── templates
      ├── deployment.yaml
      ├── service.yaml
      └── ...
```

!!! note "库文件不会被渲染"
    带有 `.lib.kts` 扩展名的文件不会被渲染为 YAML。它们的内容会在编译时
    自动提供给所有 spec 文件使用。

---

## KTS 文件类型

Kube KTS 区分两种类型的 Kotlin Script 文件：

| 扩展名 | 用途 |
| :--- | :--- |
| `*.spec.kts` | 定义一个 Kubernetes 资源（渲染为 YAML） |
| `*.lib.kts` | 定义可在所有 spec 文件中使用的辅助函数 |

### Spec 文件（`*.spec.kts`）

Spec 文件使用 KTS DSL 定义 Kubernetes 资源。每个 spec 文件生成一个 YAML
输出文件。`Chart.spec.kts` 是一个特殊的 spec 文件，它生成 `Chart.yaml`。

```kotlin
// templates/deployment.spec.kts
deployment {
    metadata("my-app") { }
    spec { /* ... */ }
}
```

!!! warning "安全性：导入限制"
    默认情况下，KTS 脚本**不允许**使用 `import` 语句或完全限定的类名
    （例如 `java.lang.Runtime`）。只能使用通过预配置默认导入提供的类型
    —— 完整列表参见 [默认导入](kts/lib.md#default-imports)。

    使用 `--unsafe` 标志可解除这些限制。

### 库文件（`*.lib.kts`）

库文件定义可重用的辅助函数，这些函数会自动在同一仓库内的所有 spec 文件中可用。
库文件**不会**被渲染为 YAML 输出，也**不能**被其他库文件访问。

```kotlin
// templates/helpers.lib.kts
fun appLabels(name: String) = mapOf("app" to name, "managed-by" to "kube-kts")
```

```kotlin
// templates/deployment.spec.kts —— 可以直接调用 appLabels()
deployment {
    metadata("my-app") {
        labels { appLabels("my-app").forEach { label(it.key, it.value) } }
    }
}
```

---

## 传统兼容性

Kube KTS 完全兼容传统的 Helm 配置。

- 现有的 YAML 文件可以与 `.spec.kts` 文件一起使用
- 纯 `.kts` 文件（不带 `.spec.` 限定符）也作为传统 spec 文件被支持
- 传统文件会被**原样复制**到最终输出中
- 完全支持混合环境（YAML + KTS）

---

## 值文件

Kube KTS 支持 Helm 风格的值文件。

- 值可以在 `values.yaml` 中定义
- 覆盖行为与标准 Helm 完全一致
- 值可在 KTS 脚本中访问

---

## 运行 Kube KTS

Kube KTS 是一个用于处理 Helm 仓库的命令行工具。

通过以下方式运行：
`kube-kts`
要处理一个仓库，请提供你的 Helm 项目路径。
详细用法说明请参见 **“如何使用 Kube KTS CLI”**。
