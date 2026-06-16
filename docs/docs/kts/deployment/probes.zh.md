# 探针

探针定义在容器上。它们帮助 Kubernetes 评估应用程序的状态。

| 探针 | 用途 |
| :--- | :--- |
| `startupProbe` | 检查应用程序是否已启动。在它成功之前，其他探针不会运行。 |
| `readinessProbe` | 检查容器是否可以接收流量。失败会将 Pod 从 Service 端点中移除。 |
| `livenessProbe` | 检查容器是否仍然正常工作。失败可能触发重启。 |

## HTTP GET

```kotlin
container("app", "registry.example.com/demo:1.0.0") {
    readinessProbe {
        httpGet(8080) {
            path = "/actuator/health/readiness"
            scheme = ProtocolScheme.HTTP
            httpHeaders {
                httpHeader("X-Probe", "readiness")
            }
        }
        initialDelaySeconds = 10.seconds.toJavaDuration()
        periodSeconds = 10.seconds.toJavaDuration()
        timeoutSeconds = 2.seconds.toJavaDuration()
        failureThreshold = 3
    }
}
```

## TCP Socket

```kotlin
container("app", "registry.example.com/demo:1.0.0") {
    livenessProbe {
        tcpSocket(8080)
        initialDelaySeconds = 30.seconds.toJavaDuration()
        periodSeconds = 20.seconds.toJavaDuration()
    }
}
```

## Exec

```kotlin
container("worker", "registry.example.com/worker:1.0.0") {
    livenessProbe {
        exec {
            command("/bin/sh", "-c", "test -f /tmp/healthy")
        }
        periodSeconds = 15.seconds.toJavaDuration()
    }
}
```

## gRPC

```kotlin
container("grpc-api", "registry.example.com/grpc-api:1.0.0") {
    readinessProbe {
        grpc(9090) {
            service = "demo.Health"
        }
        periodSeconds = 10.seconds.toJavaDuration()
    }
}
```

## 时间选项

| 属性 | 说明 |
| :--- | :--- |
| `initialDelaySeconds` | 容器启动后到第一次检查之间的等待时间。 |
| `periodSeconds` | 两次检查之间的间隔。 |
| `timeoutSeconds` | 单次检查的最长持续时间。 |
| `successThreshold` | 切换到成功状态所需的连续成功次数。 |
| `failureThreshold` | 在开始失败处理之前的连续失败次数。 |
| `terminationGracePeriodSeconds` | 由探针触发的终止的宽限期。 |
