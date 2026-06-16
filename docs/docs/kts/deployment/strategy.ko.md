# Deployment 전략

`strategy`는 Deployment가 변경될 때 Kubernetes가 Pod를 어떻게 교체하는지 제어합니다.

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

`Recreate`에서는 Kubernetes가 새 Pod를 생성하기 전에 기존 Pod를 종료합니다.

```kotlin
strategy {
    type = DeploymentStrategySpec.Type.Recreate
}
```

이 전략은 단순하지만 다운타임을 유발할 수 있습니다. 이전 버전과 새 버전이 병렬로 실행되어서는 안 되는 애플리케이션에 적합합니다.

## RollingUpdate

`RollingUpdate`에서는 Kubernetes가 Pod를 점진적으로 교체합니다.

```kotlin
strategy {
    type = DeploymentStrategySpec.Type.RollingUpdate
    rollingUpdate {
        maxSurge = 1.absolute
        maxUnavailable = 0.absolute
    }
}
```

| 속성 | 설명 |
| :--- | :--- |
| `maxSurge` | `replicas`를 초과하여 추가할 수 있는 Pod의 최대 수. |
| `maxUnavailable` | 롤아웃 중 사용 불가능한 Pod의 최대 수. |

두 값 모두 절대값 또는 백분율로 지정할 수 있습니다.

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

## Deployment Spec의 롤아웃 옵션

| 속성 | 설명 |
| :--- | :--- |
| `minReadySeconds` | 새 Pod가 사용 가능한 것으로 간주되기 전에 Ready 상태를 유지해야 하는 최소 시간. |
| `revisionHistoryLimit` | 롤백을 위해 보관하는 이전 ReplicaSet의 수. |
| `paused` | `true`인 경우 Deployment 컨트롤러는 새로운 롤아웃 변경을 처리하지 않습니다. |
| `progressDeadlineSeconds` | 롤아웃 진행에 대한 시간 제한. |

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
