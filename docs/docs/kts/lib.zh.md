# 库文件（`*.lib.kts`）

库文件允许你定义可重用的辅助函数和常量，它们会自动在 Helm 仓库中的所有 spec 文件里可用。

---

## 概览

| 属性 | 值 |
| :--- | :--- |
| 文件扩展名 | `*.lib.kts` |
| 位置 | `helm/` 目录树中的任意位置 |
| 渲染为 YAML | 否 |
| 在 spec 文件中可用 | 是 —— 所有 spec 文件自动可用 |
| 在其他库文件中可用 | 否 |

---

## 创建库文件

在 `helm/` 目录中的任意位置创建一个带有 `.lib.kts` 扩展名的文件。
一种常见的约定是将共享的辅助函数放在 `templates/helpers.lib.kts` 中。

```
─ helm
  ├── Chart.spec.kts
  ├── values.yaml
  └── templates
      ├── helpers.lib.kts       ← library file
      ├── deployment.spec.kts
      └── service.spec.kts
```

在文件中，定义你需要的任何 Kotlin 函数、常量或扩展函数：

```kotlin
// templates/helpers.lib.kts

fun appLabels(name: String): Map<String, String> = mapOf(
    "app.kubernetes.io/name" to name,
    "app.kubernetes.io/managed-by" to "kube-kts"
)

fun fullName(release: String, component: String) = "$release-$component"

const val DEFAULT_REGISTRY = "registry.example.com"
```

---

## 在 spec 文件中使用库函数

任意 `*.lib.kts` 文件中定义的所有函数和常量都可以直接在每个 `*.spec.kts` 文件中调用 —— 无需导入。

```kotlin
// templates/deployment.spec.kts

deployment {
    metadata(fullName("my-release", "backend")) {
        appLabels("backend").forEach { label(it.key, it.value) }
    }
    spec {
        template {
            spec {
                container("backend") {
                    image = "$DEFAULT_REGISTRY/backend:latest"
                }
            }
        }
    }
}
```

---

## 默认导入

库文件可以访问与 spec 文件相同的默认导入，因此你无需显式的 import 语句即可使用所有
Kube KTS DSL 类型、Java 标准库类型和 Kotlin 时间扩展。

| 导入 | 包含 |
| :--- | :--- |
| Kube KTS API | 所有 `*Spec` 和 `*SpecBuilder` 类型、`ValueAccess` |
| `java.net` | `URL`、`URI` |
| `java.time` | `Duration`、`Instant`、`LocalDate`、… |
| `kotlin.time` | `Duration`、`Duration.Companion.*` |

---

## 约束

!!! warning "安全性：导入限制"
    默认情况下，KTS 脚本**不允许**使用 `import` 语句或完全限定的类名
    （例如 `java.lang.Runtime`）。只能使用上面列出的预配置默认导入中的类型。

    使用 `--unsafe` 标志可解除这些限制。

!!! warning "库文件不能访问其他库文件"
    在一个 `*.lib.kts` 文件中定义的函数**不能**在其他 `*.lib.kts` 文件中使用。
    只有 `*.spec.kts` 文件才能调用库函数。请相应地组织你的库，并避免跨库依赖。

!!! note "库文件不会被渲染"
    `*.lib.kts` 文件永远不会被渲染为 YAML 输出文件，即使它们包含顶层语句。
    只有 `*.spec.kts` 文件才会产生输出。

---

## 多个库文件

你可以拥有任意数量的 `*.lib.kts` 文件。Kube KTS 会在 `helm/` 目录树中
递归地发现所有这些文件，并将它们全部提供给每个 spec 文件。

```
─ helm
  └── templates
      ├── helpers.lib.kts         ← general helpers
      ├── labels.lib.kts          ← label utilities
      ├── resources.lib.kts       ← resource limit constants
      └── deployment.spec.kts     ← can call functions from all three lib files
```
