# Deployment DSL

The `deployment` DSL is used to configure Kubernetes Deployment resources. A Deployment describes the desired state of an application, manages the related Pods through ReplicaSets, and controls rollouts, rollbacks, and scaling.

## Basic Usage

A minimal Deployment configuration requires `metadata`, a `selector`, and a `template` with at least one container.

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

!!! warning "Selector and Template Labels"
    The `selector` must match the labels in the Pod template. Kubernetes uses this relationship to find the Pods managed by the Deployment.

## Detailed Example

The following example shows the most important parts of a realistic Deployment configuration. The individual fragments are described in more detail on the subpages.

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

## Configuration Reference

### Metadata (`metadata`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the Deployment, passed as the first argument. |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix for generating a unique name. |
| `labels { label(key, value) }` | `Map<String, String>` | Labels on the Deployment resource. |
| `annotations { annotation(key, value) }` | `Map<String, String>` | Annotations on the Deployment resource. |

### Deployment Specification (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `replicas` | Desired number of Pod replicas. |
| `selector { ... }` | Required block for selecting the managed Pods. |
| `template { ... }` | Required block for the Pod template. |
| `strategy { ... }` | Rollout strategy (`Recreate` or `RollingUpdate`). |
| `minReadySeconds` | Minimum amount of time a new Pod must be ready before it is considered available. |
| `revisionHistoryLimit` | Number of old ReplicaSets to keep for rollbacks. |
| `paused` | Pauses rollout processing by the Deployment controller. |
| `progressDeadlineSeconds` | Maximum amount of time a rollout may take before it is considered failed. |

## Fragments

- [Selector](deployment/selector.md)
- [Pod Template](deployment/template.md)
- [Pod Spec](deployment/pod-spec.md)
- [Container](deployment/containers.md)
- [Probes](deployment/probes.md)
- [Volumes](deployment/volumes.md)
- [Deployment Strategy](deployment/strategy.md)
