# Pod Template

The `template` describes the Pods created by the Deployment. It consists of optional Pod metadata and a required `spec`.

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

## Metadata

Metadata in the Pod template applies to the generated Pods, not to the Deployment resource itself.

| Block | Description |
| :--- | :--- |
| `labels { label(key, value) }` | Labels for the generated Pods. Important for selectors, Services, and scheduling rules. |
| `annotations { annotation(key, value) }` | Annotations for the generated Pods, for example for monitoring or sidecar systems. |

## Pod Spec

The `spec` in the template contains the actual Pod configuration. At least one container is required.

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

For more details about Pod configuration, see [Pod Spec](pod-spec.md).
