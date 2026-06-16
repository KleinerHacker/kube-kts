# Deployment 策略

`strategy` 控制当 Deployment 发生变化时 Kubernetes 如何替换 Pod。

```kotlin
strategy {
    type = DeploymentStrategySpec.Type.RollingUpdate
    rollingUpdate {
        maxSurge = 25.percent
        maxUnavailable = 1.absolute
    }
}
```

## Recreate

使用 `Recreate` 时，Kubernetes 会先终止现有的 Pod，然后再创建新的 Pod。

```kotlin
strategy {
    type = DeploymentStrategySpec.Type.Recreate
}
```

此策略简单，但可能导致停机。它适用于新旧版本不得并行运行的应用程序。

## RollingUpdate

使用 `RollingUpdate` 时，Kubernetes 会逐步替换 Pod。

```kotlin
strategy {
    type = DeploymentStrategySpec.Type.RollingUpdate
    rollingUpdate {
        maxSurge = 1.absolute
        maxUnavailable = 0.absolute
    }
}
```

| 属性 | 说明 |
| :--- | :--- |
| `maxSurge` | 超出 `replicas` 的额外 Pod 的最大数量。 |
| `maxUnavailable` | 发布期间不可用 Pod 的最大数量。 |

这两个值都可以指定为绝对值或百分比。

```kotlin
rollingUpdate {
    maxSurge = 25.percent
    maxUnavailable = 10.percent
}
```

```kotlin
rollingUpdate {
    maxSurge = 2.absolute
    maxUnavailable = 1.absolute
}
```

## Deployment Spec 中的发布选项

| 属性 | 说明 |
| :--- | :--- |
| `minReadySeconds` | 新 Pod 在被视为可用前必须保持就绪的最短时间。 |
| `revisionHistoryLimit` | 为回滚保留的旧 ReplicaSet 数量。 |
| `paused` | 为 `true` 时，Deployment 控制器不处理新的发布变更。 |
| `progressDeadlineSeconds` | 发布进度的时间限制。 |

```kotlin
spec {
    replicas = 3
    minReadySeconds = 30.seconds.toJavaDuration()
    revisionHistoryLimit = 5
    paused = false
    progressDeadlineSeconds = 600.seconds.toJavaDuration()

    strategy {
        type = DeploymentStrategySpec.Type.RollingUpdate
        rollingUpdate {
            maxSurge = 25.percent
            maxUnavailable = 1.absolute
        }
    }
}
```
