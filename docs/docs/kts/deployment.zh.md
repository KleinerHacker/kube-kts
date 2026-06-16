# Deployment DSL

`deployment` DSL 用于配置 Kubernetes Deployment 资源。Deployment 描述应用程序的期望状态，通过 ReplicaSet 管理相关的 Pod，并控制发布、回滚和扩缩容。

!!! warning "安全性：导入限制"
    默认情况下，KTS 脚本**不允许**使用 `import` 语句或完全限定的类名
    （例如 `java.lang.Runtime`）。只能使用通过预配置默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

## 基本用法

最小的 Deployment 配置需要 `metadata`、一个 `selector` 以及一个至少包含一个容器的 `template`。

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

!!! warning "Selector 与 Template 标签"
    `selector` 必须与 Pod 模板中的标签匹配。Kubernetes 通过这种关系来查找由 Deployment 管理的 Pod。

## 详细示例

下面的示例展示了一个真实 Deployment 配置中最重要的部分。各个片段会在子页面中详细说明。

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

## 配置参考

### 元数据（`metadata`）

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `name` | `String` | Deployment 的名称，作为第一个参数传递。 |
| `namespace` | `String?` | 资源的命名空间。 |
| `generateName` | `String?` | 用于生成唯一名称的可选前缀。 |
| `labels { label(key, value) }` | `Map<String, String>` | Deployment 资源上的标签。 |
| `annotations { annotation(key, value) }` | `Map<String, String>` | Deployment 资源上的注解。 |

### Deployment 规约（`spec`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `replicas` | 期望的 Pod 副本数量。 |
| `selector { ... }` | 用于选择被管理 Pod 的必填块。 |
| `template { ... }` | Pod 模板的必填块。 |
| `strategy { ... }` | 发布策略（`Recreate` 或 `RollingUpdate`）。 |
| `minReadySeconds` | 新 Pod 在被视为可用前必须保持就绪的最短时间。 |
| `revisionHistoryLimit` | 为回滚保留的旧 ReplicaSet 数量。 |
| `paused` | 暂停 Deployment 控制器对发布的处理。 |
| `progressDeadlineSeconds` | 发布被视为失败前所允许的最长时间。 |

## 片段

- [Selector](deployment/selector.md)
- [Pod 模板](deployment/template.md)
- [Pod Spec](deployment/pod-spec.md)
- [容器](deployment/containers.md)
- [探针](deployment/probes.md)
- [卷](deployment/volumes.md)
- [Deployment 策略](deployment/strategy.md)
