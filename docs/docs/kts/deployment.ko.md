# Deployment DSL

`deployment` DSL은 Kubernetes Deployment 리소스를 구성하는 데 사용됩니다. Deployment는 애플리케이션의 원하는 상태를 기술하고, ReplicaSet을 통해 관련 Pod를 관리하며, 롤아웃, 롤백 및 스케일링을 제어합니다.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 완전 정규화된 클래스 이름
    (예: `java.lang.Runtime`)을 **허용하지 않습니다**. 사전 구성된 기본 임포트로
    제공되는 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

## 기본 사용법

최소한의 Deployment 구성에는 `metadata`, `selector`, 그리고 컨테이너가 최소 하나 포함된 `template`이 필요합니다.

```kotlin
deployment {
    metadata("my-deployment") {
        namespace = "default"
    }

    spec {
        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        template {
            metadata {
                labels {
                    label("app", "demo")
                }
            }

            spec {
                containers {
                    container("app", "nginx:1.27") {
                        ports {
                            port(8080) {
                                name = "http"
                                protocol = Protocol.TCP
                            }
                        }
                    }
                }
            }
        }
    }
}
```

!!! warning "Selector와 Template 레이블"
    `selector`는 Pod 템플릿의 레이블과 일치해야 합니다. Kubernetes는 이 관계를 사용하여 Deployment가 관리하는 Pod를 찾습니다.

## 상세 예제

다음 예제는 현실적인 Deployment 구성의 가장 중요한 부분을 보여줍니다. 각 조각은 하위 페이지에서 자세히 설명합니다.

```kotlin
deployment {
    metadata("full-deployment") {
        namespace = "production"
        labels {
            label("app", "demo")
        }
    }

    spec {
        replicas = 3

        selector {
            matchLabels {
                label("app", "demo")
            }
            matchExpressions {
                expression("tier", LabelSelectorRequirementSpec.Operator.In) {
                    values {
                        value("backend")
                    }
                }
            }
        }

        template {
            metadata {
                labels {
                    label("app", "demo")
                    label("tier", "backend")
                }
                annotations {
                    annotation("description", "Demo pod template")
                }
            }

            spec {
                serviceAccountName = "demo-service-account"
                restartPolicy = PodSpec.RestartPolicy.Always

                imagePullSecrets {
                    secret("registry-credentials")
                }

                containers {
                    container("app", "registry.example.com/demo:1.0.0") {
                        imagePullPolicy = ContainerSpec.ImagePullPolicy.IfNotPresent

                        ports {
                            port(8080) {
                                name = "http"
                                protocol = Protocol.TCP
                            }
                        }

                        env("SPRING_PROFILES_ACTIVE") {
                            fromValue("production")
                        }

                        resources {
                            requests {
                                cpu = 250.mCpu
                                memory = 256.miBytes
                            }
                            limits {
                                cpu = oneCpu
                                memory = 1.giBytes
                            }
                        }

                        readinessProbe {
                            httpGet(8080) {
                                path = "/actuator/health/readiness"
                                scheme = ProtocolScheme.HTTP
                            }
                            initialDelaySeconds = 10.seconds.toJavaDuration()
                            periodSeconds = 10.seconds.toJavaDuration()
                        }

                        livenessProbe {
                            httpGet(8080) {
                                path = "/actuator/health/liveness"
                            }
                            initialDelaySeconds = 30.seconds.toJavaDuration()
                            periodSeconds = 20.seconds.toJavaDuration()
                        }
                    }
                }

                volumes {
                    volume("config") {
                        from {
                            configMap {
                                name = "demo-config"
                            }
                        }
                    }
                }
            }
        }

        strategy {
            type = DeploymentStrategySpec.Type.RollingUpdate
            rollingUpdate {
                maxSurge = 25.percent
                maxUnavailable = 1.absolute
            }
        }

        minReadySeconds = 30.seconds.toJavaDuration()
        revisionHistoryLimit = 5
        progressDeadlineSeconds = 600.seconds.toJavaDuration()
    }
}
```

## 구성 참조

### 메타데이터(`metadata`)

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `name` | `String` | Deployment의 이름(첫 번째 인수로 전달됨). |
| `namespace` | `String?` | 리소스의 네임스페이스. |
| `generateName` | `String?` | 고유한 이름 생성을 위한 선택적 접두사. |
| `labels { label(key, value) }` | `Map<String, String>` | Deployment 리소스의 레이블. |
| `annotations { annotation(key, value) }` | `Map<String, String>` | Deployment 리소스의 어노테이션. |

### Deployment 명세(`spec`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `replicas` | 원하는 Pod 레플리카 수. |
| `selector { ... }` | 관리 대상 Pod를 선택하기 위한 필수 블록. |
| `template { ... }` | Pod 템플릿을 위한 필수 블록. |
| `strategy { ... }` | 롤아웃 전략(`Recreate` 또는 `RollingUpdate`). |
| `minReadySeconds` | 새 Pod가 사용 가능한 것으로 간주되기 전에 Ready 상태를 유지해야 하는 최소 시간. |
| `revisionHistoryLimit` | 롤백을 위해 보관하는 이전 ReplicaSet의 수. |
| `paused` | Deployment 컨트롤러의 롤아웃 처리를 일시 중지합니다. |
| `progressDeadlineSeconds` | 롤아웃이 실패로 간주되기 전 허용되는 최대 시간. |

## 조각(Fragments)

- [Selector](deployment/selector.md)
- [Pod 템플릿](deployment/template.md)
- [Pod Spec](deployment/pod-spec.md)
- [컨테이너](deployment/containers.md)
- [프로브](deployment/probes.md)
- [볼륨](deployment/volumes.md)
- [Deployment 전략](deployment/strategy.md)
