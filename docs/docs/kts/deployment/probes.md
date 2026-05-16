# Probes

Probes are defined on the container. They help Kubernetes evaluate the state of an application.

| Probe | Purpose |
| :--- | :--- |
| `startupProbe` | Checks whether the application has started. The other probes do not run until it succeeds. |
| `readinessProbe` | Checks whether the container can receive traffic. Failures remove the Pod from Service endpoints. |
| `livenessProbe` | Checks whether the container is still functional. Failures can trigger a restart. |

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

## Timing Options

| Property | Description |
| :--- | :--- |
| `initialDelaySeconds` | Wait time after container start before the first check. |
| `periodSeconds` | Interval between two checks. |
| `timeoutSeconds` | Maximum duration of a single check. |
| `successThreshold` | Number of consecutive successes required to switch to a successful status. |
| `failureThreshold` | Number of consecutive failures before failure handling starts. |
| `terminationGracePeriodSeconds` | Grace period for a termination triggered by the probe. |
