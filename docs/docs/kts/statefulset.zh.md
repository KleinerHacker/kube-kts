# StatefulSet DSL

`statefulSet` DSL 用于配置 Kubernetes **StatefulSet** 资源。StatefulSet 管理一组具有稳定且唯一的网络标识
以及稳定的每 Pod 持久化存储的 Pod —— 适用于数据库或消息代理等有状态应用，而非由
[Deployment](deployment.md) 管理的可互换 Pod。

!!! warning "安全：导入限制"
    默认情况下，KTS 脚本**不允许** `import` 语句或完全限定的类名（例如 `java.lang.Runtime`）。
    只能使用通过预配置的默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

## 基本用法

最小的 StatefulSet 需要 `metadata`、`serviceName`（控制其网络域的无头 Service）、`selector` 以及
`spec` 中的 Pod `template`。

```kotlin
statefulSet {
    metadata("my-database") {
        namespace = "default"
    }

    spec {
        serviceName = "my-database"

        selector {
            matchLabels {
                label("app", "my-database")
            }
        }

        template {
            metadata {
                labels {
                    label("app", "my-database")
                }
            }

            spec {
                containers {
                    container("db", "postgres") { }
                }
            }
        }
    }
}
```

## 详细示例

以下是一个综合示例，展示副本数、控制 Service、Pod 管理策略、更新策略、通过 `volumeClaimTemplates`
提供的每 Pod 持久化存储以及 PVC 保留策略。

```kotlin
statefulSet {
    metadata("full-database") {
        namespace = "data"
    }

    spec {
        replicas = 3
        serviceName = "full-database"
        podManagementPolicy = StatefulSetSpec.PodManagementPolicy.Parallel
        revisionHistoryLimit = 5
        minReadySeconds = 10.seconds.toJavaDuration()
        ordinals(1)

        // 复用共享的标签选择器 DSL（与 Deployment 相同）
        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        // 控制模板更新如何下发
        updateStrategy {
            type = StatefulSetUpdateStrategySpec.Type.RollingUpdate
            rollingUpdate {
                partition = 1
                maxUnavailable = 1.absolute
            }
        }

        // 稳定的每 Pod 存储：每个模板为每个 Pod 制备一个 PVC
        volumeClaimTemplates {
            claim("data") {
                accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
                storageClassName = "standard"
                volumeMode = VolumeClaimTemplateSpec.VolumeMode.Filesystem
                requests {
                    storage = 1.giBytes
                }
            }
        }

        // 决定删除 / 缩容时 PVC 的处理方式
        persistentVolumeClaimRetentionPolicy {
            whenDeleted = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Delete
            whenScaled = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Retain
        }

        template {
            metadata {
                labels {
                    label("app", "demo")
                }
            }

            spec {
                containers {
                    container("db", "postgres") { }
                }
            }
        }
    }
}
```

## 配置参考

### 元数据（`metadata`）

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `name` | `String` | StatefulSet 资源的名称（作为第一个参数传入）。 |
| `namespace` | `String?` | 资源的命名空间。 |
| `generateName` | `String?` | 用于生成唯一名称的可选前缀。 |

### StatefulSet 规约（`spec`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `replicas` | 期望的副本（Pod 实例）数量。默认为 1。 |
| `serviceName` | 控制其网络域的（通常为无头）Service 名称。**必填。** |
| `podManagementPolicy` | `OrderedReady`（默认）或 `Parallel`。 |
| `revisionHistoryLimit` | 为回滚保留的最大修订版本数量。 |
| `minReadySeconds` | 新 Pod 被视为可用前必须保持就绪的最少秒数。 |
| `ordinals(start)` | 表示第一个副本索引的编号（默认为 0）。 |
| `selector { ... }` | 标签选择器（复用共享的选择器 DSL，参见 [选择器](deployment/selector.md)）。**必填。** |
| `updateStrategy { ... }` | 控制模板更新如何下发。 |
| `volumeClaimTemplates { claim(name) { ... } }` | 提供稳定每 Pod 存储的 PVC 模板。 |
| `persistentVolumeClaimRetentionPolicy { ... }` | 控制所制备 PVC 的生命周期。 |
| `template { ... }` | Pod 模板（参见 [Pod 模板](deployment/template.md)）。**必填。** |

### 更新策略（`updateStrategy`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `type` | `RollingUpdate`（默认）或 `OnDelete`。 |
| `rollingUpdate { partition; maxUnavailable }` | 滚动更新参数。 |
| `partition` | 从此序号（含）起更新 Pod（分阶段 / 金丝雀发布）。 |
| `maxUnavailable` | 更新期间不可用 Pod 的最大数量（绝对值或百分比，例如 `1.absolute` / `25.percent`）。 |

### 卷申领模板（`volumeClaimTemplates`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `claim(name) { ... }` | 添加一个 PVC 模板。`name` 必须与 Pod 容器的 `volumeMount` 匹配。 |
| `accessModes(...)` | 访问模式，例如 `ReadWriteOnce`、`ReadOnlyMany`、`ReadWriteMany`、`ReadWriteOncePod`。 |
| `storageClassName` | 用于制备卷的 StorageClass。 |
| `volumeMode` | `Filesystem`（默认）或 `Block`。 |
| `requests { storage = ... }` | 申请的最小存储（例如 `1.giBytes`）。 |
| `limits { storage = ... }` | 允许的最大存储。 |
| `label(key, value)` / `labels { ... }` | 申领元数据的标签。 |
| `annotation(key, value)` / `annotations { ... }` | 申领元数据的注解。 |

### PVC 保留策略（`persistentVolumeClaimRetentionPolicy`）

| 属性 | 说明 |
| :--- | :--- |
| `whenDeleted` | `Retain`（默认）或 `Delete` —— 在 StatefulSet 被删除时应用。 |
| `whenScaled` | `Retain`（默认）或 `Delete` —— 在 StatefulSet 缩容时应用。 |

!!! note "稳定存储"
    `volumeClaimTemplates` 的每个条目都会为**每个 Pod** 制备一个 PersistentVolumeClaim。这些申领在重新
    调度后仍保持其身份，从而为每个副本提供独立的持久化存储。
