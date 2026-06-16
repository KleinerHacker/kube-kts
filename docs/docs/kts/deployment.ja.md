# Deployment DSL

`deployment` DSL は、Kubernetes Deployment リソースを構成するために使用します。Deployment はアプリケーションの望ましい状態を記述し、ReplicaSet を通じて関連する Pod を管理し、ロールアウト、ロールバック、スケーリングを制御します。

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名
    （例: `java.lang.Runtime`）を**許可しません**。事前構成されたデフォルトインポートで
    提供される型のみ使用できます。

    `--unsafe` フラグを使うとこれらの制限を解除できます。

## 基本的な使い方

最小限の Deployment 構成には、`metadata`、`selector`、および少なくとも 1 つのコンテナを含む `template` が必要です。

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

!!! warning "Selector と Template のラベル"
    `selector` は Pod テンプレートのラベルと一致する必要があります。Kubernetes はこの関係を使って Deployment が管理する Pod を見つけます。

## 詳細な例

以下の例は、現実的な Deployment 構成の最も重要な部分を示しています。個々のフラグメントはサブページで詳しく説明します。

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

## 設定リファレンス

### メタデータ（`metadata`）

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | `String` | Deployment の名前（最初の引数として渡される）。 |
| `namespace` | `String?` | リソースの名前空間。 |
| `generateName` | `String?` | 一意の名前を生成するための任意のプレフィックス。 |
| `labels { label(key, value) }` | `Map<String, String>` | Deployment リソースのラベル。 |
| `annotations { annotation(key, value) }` | `Map<String, String>` | Deployment リソースのアノテーション。 |

### Deployment の仕様（`spec`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `replicas` | 望ましい Pod レプリカ数。 |
| `selector { ... }` | 管理対象の Pod を選択するための必須ブロック。 |
| `template { ... }` | Pod テンプレートの必須ブロック。 |
| `strategy { ... }` | ロールアウト戦略（`Recreate` または `RollingUpdate`）。 |
| `minReadySeconds` | 新しい Pod が利用可能とみなされる前に Ready 状態を維持しなければならない最小時間。 |
| `revisionHistoryLimit` | ロールバックのために保持する古い ReplicaSet の数。 |
| `paused` | Deployment コントローラーによるロールアウト処理を一時停止します。 |
| `progressDeadlineSeconds` | ロールアウトが失敗とみなされるまでに許容される最大時間。 |

## フラグメント

- [Selector](deployment/selector.md)
- [Pod テンプレート](deployment/template.md)
- [Pod Spec](deployment/pod-spec.md)
- [コンテナ](deployment/containers.md)
- [プローブ](deployment/probes.md)
- [ボリューム](deployment/volumes.md)
- [Deployment ストラテジー](deployment/strategy.md)
