# 프로브

프로브는 컨테이너에 정의됩니다. Kubernetes가 애플리케이션의 상태를 평가하는 데 도움을 줍니다.

| 프로브 | 목적 |
| :--- | :--- |
| `startupProbe` | 애플리케이션이 시작되었는지 확인합니다. 성공할 때까지 다른 프로브는 실행되지 않습니다. |
| `readinessProbe` | 컨테이너가 트래픽을 받을 수 있는지 확인합니다. 실패하면 Pod가 Service 엔드포인트에서 제거됩니다. |
| `livenessProbe` | 컨테이너가 여전히 정상 작동하는지 확인합니다. 실패하면 재시작이 트리거될 수 있습니다. |

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

## 타이밍 옵션

| 속성 | 설명 |
| :--- | :--- |
| `initialDelaySeconds` | 컨테이너 시작 후 첫 번째 확인까지의 대기 시간. |
| `periodSeconds` | 두 확인 사이의 간격. |
| `timeoutSeconds` | 단일 확인의 최대 지속 시간. |
| `successThreshold` | 성공 상태로 전환하는 데 필요한 연속 성공 횟수. |
| `failureThreshold` | 실패 처리가 시작되기 전까지의 연속 실패 횟수. |
| `terminationGracePeriodSeconds` | 프로브에 의해 트리거된 종료를 위한 유예 기간. |
