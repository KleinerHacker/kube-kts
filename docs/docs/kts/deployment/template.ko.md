# Pod 템플릿

`template`은 Deployment가 생성하는 Pod를 기술합니다. 선택적 Pod 메타데이터와 필수 `spec`으로 구성됩니다.

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

## 메타데이터

Pod 템플릿의 메타데이터는 Deployment 리소스 자체가 아니라 생성되는 Pod에 적용됩니다.

| 블록 | 설명 |
| :--- | :--- |
| `labels { label(key, value) }` | 생성되는 Pod의 레이블. selector, Service 및 스케줄링 규칙에 중요합니다. |
| `annotations { annotation(key, value) }` | 생성되는 Pod의 어노테이션. 예: 모니터링 또는 사이드카 시스템용. |

## Pod Spec

템플릿 내 `spec`에는 실제 Pod 구성이 포함됩니다. 최소 하나의 컨테이너가 필요합니다.

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

Pod 구성에 대한 자세한 내용은 [Pod Spec](pod-spec.md)을 참조하세요.
