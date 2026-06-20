# Job DSL

`job` DSL は、Kubernetes **Job** リソースを構成するために使用します。Job は 1 つ以上の Pod を作成し、
完了するまで実行します。[Deployment](deployment.md) が管理する長時間稼働サービスとは異なり、バッチや
完了実行型のワークロードに適しています。

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名（例: `java.lang.Runtime`）を
    **許可しません**。事前構成されたデフォルトインポートで提供される型のみ使用できます。

    `--unsafe` フラグを使用すると、これらの制限を解除できます。

## 基本的な使い方

最小限の Job には `metadata` と `spec` 内の Pod `template` が必要です。Pod の `restartPolicy` は
`Never` または `OnFailure` である必要があります。

```kotlin
job {
    metadata("my-job") {
        namespace = "default"
    }

    spec {
        template {
            spec {
                containers {
                    container("worker", "busybox") { }
                }
                restartPolicy = PodSpec.RestartPolicy.Never
            }
        }
    }
}
```

## 詳細な例

以下は、並列度、完了数、リトライ処理、自動クリーンアップ、明示的なセレクター、Pod 障害ポリシー、成功ポリシーを
示す包括的な例です。

```kotlin
job {
    metadata("full-job") {
        namespace = "batch"
    }

    spec {
        parallelism = 2
        completions = 5
        completionMode = JobSpec.CompletionMode.Indexed
        backoffLimit = 4
        backoffLimitPerIndex = 2
        maxFailedIndexes = 3
        activeDeadlineSeconds = 600.seconds.toJavaDuration()
        ttlSecondsAfterFinished = 3600.seconds.toJavaDuration()
        suspend = false
        manualSelector = true
        podReplacementPolicy = JobSpec.PodReplacementPolicy.Failed

        // 共有のラベルセレクター DSL を再利用（Deployment と同じ）
        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        // 特定の Pod 障害に対応
        podFailurePolicy {
            rule(PodFailurePolicySpec.Action.FailJob) {
                onExitCodes(PodFailurePolicySpec.OnExitCodes.Operator.In) {
                    containerName = "worker"
                    values(1, 42)
                }
            }
            rule(PodFailurePolicySpec.Action.Ignore) {
                onPodCondition("DisruptionTarget", "True")
            }
        }

        // インデックス付き Job を早期に成功と宣言
        successPolicy {
            rule {
                succeededIndexes = "0-2"
                succeededCount = 2
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
                    container("worker", "busybox") { }
                }
                restartPolicy = PodSpec.RestartPolicy.OnFailure
            }
        }
    }
}
```

## 構成リファレンス

### メタデータ（`metadata`）

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | `String` | Job リソースの名前（最初の引数として渡されます）。 |
| `namespace` | `String?` | リソースの名前空間。 |
| `generateName` | `String?` | 一意な名前を生成するための任意のプレフィックス。 |

### Job 仕様（`spec`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `parallelism` | 並列実行すべき Pod の最大数。 |
| `completions` | 正常に完了すべき Pod の数。 |
| `completionMode` | `NonIndexed` または `Indexed`。 |
| `backoffLimit` | Job が失敗とマークされるまでのリトライ回数。 |
| `backoffLimitPerIndex` | インデックスごとのリトライ回数（インデックス付き Job のみ）。 |
| `maxFailedIndexes` | Job が失敗するまでに許容される失敗インデックスの最大数（インデックス付き Job のみ）。 |
| `activeDeadlineSeconds` | Job が終了されるまでにアクティブでいられる最大時間。 |
| `ttlSecondsAfterFinished` | Job 完了後の TTL。これを超えるとクリーンアップ対象になります。 |
| `suspend` | true の場合、Job コントローラーは Pod を作成しません。 |
| `manualSelector` | true の場合、`selector` はシステムではなくユーザーが管理します。 |
| `podReplacementPolicy` | `TerminatingOrFailed` または `Failed`。 |
| `selector { ... }` | ラベルセレクター（共有セレクター DSL を再利用、[セレクター](deployment/selector.md) を参照）。 |
| `podFailurePolicy { rule(action) { ... } }` | Job が Pod 障害にどう対応するかを制御するルール。 |
| `successPolicy { rule { ... } }` | インデックス付き Job をいつ成功と宣言するかを定義するルール。 |
| `template { ... }` | Pod テンプレート（[Pod テンプレート](deployment/template.md) を参照）。 |

### Pod 障害ポリシー（`podFailurePolicy`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `rule(action) { ... }` | ルールを追加します。`action` は `FailJob`、`Ignore`、`Count`、`FailIndex`。 |
| `onExitCodes(operator) { containerName; values(...) }` | コンテナの終了コードに一致（`operator`: `In`/`NotIn`）。 |
| `onPodCondition(type, status)` | Pod の条件に一致（例: `"DisruptionTarget", "True"`）。 |

### 成功ポリシー（`successPolicy`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `rule { ... }` | ルールを追加します。 |
| `succeededIndexes` | 成功する必要があるインデックスの集合（例: `"0-2"`）。 |
| `succeededCount` | 成功したインデックスの最小数。 |

!!! note "restartPolicy"
    Deployment とは異なり、Job の Pod テンプレートでは `restartPolicy` を `Never` または `OnFailure` に
    設定する**必要があります**。Job では `Always` は使用できません。
