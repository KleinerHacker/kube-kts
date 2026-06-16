# Pod 模板

`template` 描述由 Deployment 创建的 Pod。它由可选的 Pod 元数据和必需的 `spec` 组成。

```kotlin
template {
    metadata {
        labels {
            label("app", "demo")
        }
        annotations {
            annotation("description", "Demo pod template")
        }
    }

    spec {
        containers {
            container("app", "nginx:1.27") {}
        }
    }
}
```

## 元数据

Pod 模板中的元数据应用于生成的 Pod，而不是 Deployment 资源本身。

| 块 | 说明 |
| :--- | :--- |
| `labels { label(key, value) }` | 生成的 Pod 的标签。对 selector、Service 和调度规则非常重要。 |
| `annotations { annotation(key, value) }` | 生成的 Pod 的注解，例如用于监控或 sidecar 系统。 |

## Pod Spec

模板中的 `spec` 包含实际的 Pod 配置。至少需要一个容器。

```kotlin
template {
    spec {
        serviceAccountName = "demo-service-account"
        restartPolicy = PodSpec.RestartPolicy.Always

        containers {
            container("api", "registry.example.com/api:1.0.0") {
                ports {
                    port(8080) {
                        name = "http"
                    }
                }
            }
        }
    }
}
```

有关 Pod 配置的更多细节，请参见 [Pod Spec](pod-spec.md)。
