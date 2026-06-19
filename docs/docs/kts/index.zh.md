# Kotlin 脚本 (KTS)

Kube KTS 仓库使用 **Kotlin 脚本** 来描述 Kubernetes 资源，而不是 Helm 基于 Go 模板的 YAML。
每个脚本都是纯 Kotlin，针对你的 `values` 求值，并生成一个普通的 Helm chart，再交给 Helm 处理。
最终得到的还是你熟悉的 Helm 工作流 —— 但带来了类型安全、空安全、完整的 IDE 支持，以及可调试、
不含逻辑的 YAML 输出。

本页说明这些脚本应如何理解，以及 **safe 与 unsafe** 的区别。各个资源（Chart、Service、
Deployment 等）在本节中各有专门的页面。

---

## 如何理解一个 KTS 仓库

仓库就是一个镜像 Helm chart 的目录树。Kube KTS 会扫描它，把 `*.kts` 文件替换为渲染后的 YAML，
其余文件原样复制。

```
─ helm
  ├── Chart.spec.kts        → Chart.yaml
  ├── values.yaml           （原样复制，同时提供给脚本）
  └── templates
      ├── helpers.lib.kts   （辅助函数 —— 永不渲染）
      ├── deployment.spec.kts → deployment.yaml
      └── service.spec.kts    → service.yaml
```

### 文件类型

| 扩展名 | 含义 | 渲染为 YAML |
| :--- | :--- | :--- |
| `*.spec.kts` | 通过 DSL 恰好定义一个 Kubernetes 资源（`deployment { … }`、`service { … }`…）。 | 是 |
| `*.lib.kts` | 定义可复用的辅助函数/常量，在**所有** spec 文件中可用。参见 [库文件](lib.md)。 | 否 |
| `*.kts` | 遗留 —— 当作 spec 文件处理。 | 是 |
| `*.yaml` / `*.yml` | 普通 Helm YAML，保留以实现完全向后兼容。 | 原样复制 |

### 脚本到底是什么

每个 `*.spec.kts` 文件都是一个普通的 Kotlin 脚本。它调用单个顶层 DSL 函数来构建资源，并可在过程中
读取 chart 的 values：

```kotlin
// templates/deployment.spec.kts
deployment {
    metadata("backend") { }
    spec {
        replicas = valueOrNull<Int>("spec.replicas") ?: 1
        template {
            spec {
                container("backend") {
                    image = value<String>("spec.image")
                }
            }
        }
    }
}
```

由于这是真正的 Kotlin，会在运行前编译，因此诸如类型错误、属性名拼写错误或缺失必需值等错误会在
**编译期**暴露，而不是在部署时产生损坏的 YAML。值访问（`value`、`valueOrNull`、`array`、`map`…）
在 [值处理](values.md) 中说明。

---

## Safe 与 Unsafe

为了让渲染保持可预测，并限制脚本在你的机器上能做什么，Kube KTS 默认以 **safe 模式** 编译脚本。
safe 模式将脚本限制在 Kube KTS DSL 和一组精选的预导入类型之内；unsafe 模式则解除这些限制，
允许脚本运行任意 JVM 代码。

### safe 模式做了什么

在脚本编译之前，会检查其源码（先剥离字符串字面量和注释，因此规则只作用于真正的代码）。有两类内容
会被拒绝：

| safe 模式中被拒绝 | 示例 | 原因 |
| :--- | :--- | :--- |
| `import` 语句 | `import java.io.File` | 防止引入任意类。 |
| 完全限定类名 | `java.lang.Runtime.getRuntime()` | 防止在不导入的情况下访问任意 JVM API。 |

一旦发现其中任意一项，编译会立即失败并给出说明性消息 —— 不会产生任何 YAML。

不过 safe 脚本并非“空空如也”：大量类型已被**预导入**，无需任何 `import` 即可使用，包括完整的
Kube KTS DSL 以及常用的标准库类型：

| 预导入 | 包含 |
| :--- | :--- |
| Kube KTS API | 所有 `*Spec` / `*SpecBuilder` 类型、`ValueAccess`、单位扩展（`250.mCpu`、`1.giBytes`、`25.percent`…） |
| `java.net` | `URL`、`URI` |
| `java.time` | `Duration`、`Instant`、`LocalDate`… |
| `kotlin.time` | `Duration`、`Duration.Companion.*` |

对于绝大多数 chart，这已经是你所需的一切，因此你应当保持在 safe 模式。

### unsafe 模式做了什么

unsafe 模式只是**跳过**上述 import/FQN 检查。脚本随后即可使用 `import` 语句和完全限定类名，
这意味着它们可以调用 JVM classpath 上的任意类 —— 读取文件、启动进程、打开网络连接等等。

这种能力同时也是风险所在：脚本不再局限于描述 Kubernetes 资源，它可以在运行渲染的机器上执行任意代码。
仅对你完全信任并已审查过的仓库启用 unsafe 模式。

!!! warning "unsafe 模式运行任意代码"
    启用 unsafe 模式后没有沙箱：恶意或有缺陷的脚本能做当前用户能做的任何事。请把启用它视为运行
    不受信任的代码。

---

## 命令行操作符

safe 与 unsafe 由 CLI 的全局 **`--unsafe`** 标志控制。不带它时，每个脚本都以 safe 模式编译；
带上它时，本次运行中所有脚本的 import/FQN 限制都会被解除。

```bash
# safe（默认）—— import 和 FQN 被拒绝
kube-kts render ./helm ./out

# unsafe —— 允许 import 和完全限定类名
kube-kts --unsafe render ./helm ./out
```

`--unsafe` 是**全局**选项，因此放在命令之前，并应用于任何会编译脚本的命令（`validate`、`compile`、
`render`、`lint`、`template`、`install`…）。在 `--help` 输出和命令页面中，它带有 `!!!` 标记，
表示这是一个安全相关选项。完整的全局选项列表参见 [CLI 概览](../cli/index.md#global-options)。
