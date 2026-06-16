# Pod テンプレート

`template` は、Deployment によって作成される Pod を記述します。任意の Pod メタデータと必須の `spec` で構成されます。

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

## メタデータ

Pod テンプレートのメタデータは、Deployment リソース自体ではなく、生成される Pod に適用されます。

| ブロック | 説明 |
| :--- | :--- |
| `labels { label(key, value) }` | 生成される Pod のラベル。selector、Service、スケジューリングルールにとって重要です。 |
| `annotations { annotation(key, value) }` | 生成される Pod のアノテーション。例: モニタリングやサイドカーシステム向け。 |

## Pod Spec

テンプレート内の `spec` には実際の Pod 構成が含まれます。少なくとも 1 つのコンテナが必要です。

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

Pod 構成の詳細については、[Pod Spec](pod-spec.md) を参照してください。
