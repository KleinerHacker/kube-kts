# Deployment Strategy

The `strategy` controls how Kubernetes replaces Pods when the Deployment changes.

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

With `Recreate`, Kubernetes terminates the existing Pods before creating new Pods.

```kotlin
strategy {
    type = DeploymentStrategySpec.Type.Recreate
}
```

This strategy is simple, but it can cause downtime. It is suitable for applications where old and new versions must not run in parallel.

## RollingUpdate

With `RollingUpdate`, Kubernetes replaces Pods gradually.

```kotlin
strategy {
    type = DeploymentStrategySpec.Type.RollingUpdate
    rollingUpdate {
        maxSurge = 1.absolute
        maxUnavailable = 0.absolute
    }
}
```

| Property | Description |
| :--- | :--- |
| `maxSurge` | Maximum number of additional Pods above `replicas`. |
| `maxUnavailable` | Maximum number of unavailable Pods during the rollout. |

Both values can be specified as absolute values or percentages.

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

## Rollout Options in the Deployment Spec

| Property | Description |
| :--- | :--- |
| `minReadySeconds` | Minimum amount of time a new Pod must be ready before it is considered available. |
| `revisionHistoryLimit` | Number of old ReplicaSets kept for rollbacks. |
| `paused` | When `true`, the Deployment controller does not process new rollout changes. |
| `progressDeadlineSeconds` | Time limit for rollout progress. |

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
