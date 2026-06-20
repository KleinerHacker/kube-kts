# Job DSL

`job` DSL 用于配置 Kubernetes **Job** 资源。Job 会创建一个或多个 Pod 并将其运行至完成 —— 适用于批处理和
运行至完成的工作负载，而非由 [Deployment](deployment.md) 管理的长期运行服务。

!!! warning "安全：导入限制"
    默认情况下，KTS 脚本**不允许** `import` 语句或完全限定的类名（例如 `java.lang.Runtime`）。
    只能使用通过预配置的默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

## 基本用法

最小的 Job 需要 `metadata` 以及 `spec` 中的 Pod `template`。Pod 的 `restartPolicy` 必须为
`Never` 或 `OnFailure`。

```kotlin
job {
    metadata("my-job") {
        namespace = "default"
    }

    spec {
        template {
            spec {
                containers {
                    container("worker", "busybox") { }
                }
                restartPolicy = PodSpec.RestartPolicy.Never
            }
        }
    }
}
```

## 详细示例

以下是一个综合示例，展示并行度、完成数、重试处理、自动清理、显式选择器、Pod 失败策略和成功策略。

```kotlin
job {
    metadata("full-job") {
        namespace = "batch"
    }

    spec {
        parallelism = 2
        completions = 5
        completionMode = JobSpec.CompletionMode.Indexed
        backoffLimit = 4
        backoffLimitPerIndex = 2
        maxFailedIndexes = 3
        activeDeadlineSeconds = 600.seconds.toJavaDuration()
        ttlSecondsAfterFinished = 3600.seconds.toJavaDuration()
        suspend = false
        manualSelector = true
        podReplacementPolicy = JobSpec.PodReplacementPolicy.Failed

        // 复用共享的标签选择器 DSL（与 Deployment 相同）
        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        // 对特定的 Pod 失败做出响应
        podFailurePolicy {
            rule(PodFailurePolicySpec.Action.FailJob) {
                onExitCodes(PodFailurePolicySpec.OnExitCodes.Operator.In) {
                    containerName = "worker"
                    values(1, 42)
                }
            }
            rule(PodFailurePolicySpec.Action.Ignore) {
                onPodCondition("DisruptionTarget", "True")
            }
        }

        // 提前声明索引 Job 成功
        successPolicy {
            rule {
                succeededIndexes = "0-2"
                succeededCount = 2
            }
        }

        template {
            metadata {
                labels {
                    label("app", "demo")
                }
            }

            spec {
                containers {
                    container("worker", "busybox") { }
                }
                restartPolicy = PodSpec.RestartPolicy.OnFailure
            }
        }
    }
}
```

## 配置参考

### 元数据（`metadata`）

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `name` | `String` | Job 资源的名称（作为第一个参数传入）。 |
| `namespace` | `String?` | 资源的命名空间。 |
| `generateName` | `String?` | 用于生成唯一名称的可选前缀。 |

### Job 规约（`spec`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `parallelism` | 应并行运行的最大 Pod 数量。 |
| `completions` | 期望成功完成的 Pod 数量。 |
| `completionMode` | `NonIndexed` 或 `Indexed`。 |
| `backoffLimit` | Job 标记为失败前的重试次数。 |
| `backoffLimitPerIndex` | 每个索引的重试次数（仅索引 Job）。 |
| `maxFailedIndexes` | Job 失败前允许的最大失败索引数（仅索引 Job）。 |
| `activeDeadlineSeconds` | Job 在被终止前可处于活动状态的最长持续时间。 |
| `ttlSecondsAfterFinished` | Job 完成后的 TTL，超过后可被清理。 |
| `suspend` | 若为 true，Job 控制器不会创建任何 Pod。 |
| `manualSelector` | 若为 true，`selector` 由用户而非系统管理。 |
| `podReplacementPolicy` | `TerminatingOrFailed` 或 `Failed`。 |
| `selector { ... }` | 标签选择器（复用共享的选择器 DSL，参见 [选择器](deployment/selector.md)）。 |
| `podFailurePolicy { rule(action) { ... } }` | 控制 Job 如何响应 Pod 失败的规则。 |
| `successPolicy { rule { ... } }` | 定义索引 Job 何时被声明为成功的规则。 |
| `template { ... }` | Pod 模板（参见 [Pod 模板](deployment/template.md)）。 |

### Pod 失败策略（`podFailurePolicy`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `rule(action) { ... }` | 添加规则。`action` 为 `FailJob`、`Ignore`、`Count` 或 `FailIndex`。 |
| `onExitCodes(operator) { containerName; values(...) }` | 匹配容器退出码（`operator`：`In`/`NotIn`）。 |
| `onPodCondition(type, status)` | 匹配 Pod 条件（例如 `"DisruptionTarget", "True"`）。 |

### 成功策略（`successPolicy`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `rule { ... }` | 添加规则。 |
| `succeededIndexes` | 必须成功的索引集合（例如 `"0-2"`）。 |
| `succeededCount` | 成功索引的最小数量。 |

!!! note "restartPolicy"
    与 Deployment 不同，Job 的 Pod 模板**必须**将 `restartPolicy` 设置为 `Never` 或 `OnFailure`。
    Job 不允许使用 `Always`。
