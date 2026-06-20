# StatefulSet DSL

`statefulSet` DSL は Kubernetes の **StatefulSet** リソースを構成するために使用します。StatefulSet は、
安定して一意なネットワーク識別子と、Pod ごとの安定した永続ストレージを持つ一連の Pod を管理します。
[Deployment](deployment.md) が管理する交換可能な Pod とは異なり、データベースやメッセージブローカーなどの
ステートフルなアプリケーションに適しています。

!!! warning "セキュリティ：インポート制限"
    既定では、KTS スクリプトは `import` 文や完全修飾クラス名（例: `java.lang.Runtime`）を**許可しません**。
    事前構成された既定インポートで提供される型のみ使用できます。

    これらの制限を解除するには `--unsafe` フラグを使用してください。

## 基本的な使い方

最小構成の StatefulSet には、`metadata`、`serviceName`（ネットワークドメインを制御するヘッドレス
Service）、`selector`、そして `spec` 内の Pod `template` が必要です。

```kotlin
statefulSet {
    metadata("my-database") {
        namespace = "default"
    }

    spec {
        serviceName = "my-database"

        selector {
            matchLabels {
                label("app", "my-database")
            }
        }

        template {
            metadata {
                labels {
                    label("app", "my-database")
                }
            }

            spec {
                containers {
                    container("db", "postgres") { }
                }
            }
        }
    }
}
```

## 詳細な例

以下は、レプリカ数、制御 Service、Pod 管理ポリシー、更新戦略、`volumeClaimTemplates` による Pod ごとの
永続ストレージ、PVC 保持ポリシーを示す包括的な例です。

```kotlin
statefulSet {
    metadata("full-database") {
        namespace = "data"
    }

    spec {
        replicas = 3
        serviceName = "full-database"
        podManagementPolicy = StatefulSetSpec.PodManagementPolicy.Parallel
        revisionHistoryLimit = 5
        minReadySeconds = 10.seconds.toJavaDuration()
        ordinals(1)

        // 共有のラベルセレクター DSL を再利用（Deployment と同じ）
        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        // テンプレートの更新をどのように展開するかを制御
        updateStrategy {
            type = StatefulSetUpdateStrategySpec.Type.RollingUpdate
            rollingUpdate {
                partition = 1
                maxUnavailable = 1.absolute
            }
        }

        // 安定した Pod ごとのストレージ：テンプレートごとに Pod 1 つにつき PVC を 1 つプロビジョニング
        volumeClaimTemplates {
            claim("data") {
                accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
                storageClassName = "standard"
                volumeMode = VolumeClaimTemplateSpec.VolumeMode.Filesystem
                requests {
                    storage = 1.giBytes
                }
            }
        }

        // 削除 / スケールダウン時の PVC の扱いを決定
        persistentVolumeClaimRetentionPolicy {
            whenDeleted = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Delete
            whenScaled = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Retain
        }

        template {
            metadata {
                labels {
                    label("app", "demo")
                }
            }

            spec {
                containers {
                    container("db", "postgres") { }
                }
            }
        }
    }
}
```

## 構成リファレンス

### メタデータ（`metadata`）

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | `String` | StatefulSet リソースの名前（第 1 引数として渡します）。 |
| `namespace` | `String?` | リソースの名前空間。 |
| `generateName` | `String?` | 一意な名前を生成するための任意のプレフィックス。 |

### StatefulSet 仕様（`spec`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `replicas` | 望ましいレプリカ（Pod インスタンス）数。既定は 1。 |
| `serviceName` | ネットワークドメインを制御する（通常ヘッドレスの）Service 名。**必須。** |
| `podManagementPolicy` | `OrderedReady`（既定）または `Parallel`。 |
| `revisionHistoryLimit` | ロールバック用に保持するリビジョンの最大数。 |
| `minReadySeconds` | 新しい Pod が利用可能とみなされるために Ready を維持する最小秒数。 |
| `ordinals(start)` | 最初のレプリカインデックスを表す番号（既定は 0）。 |
| `selector { ... }` | ラベルセレクター（共有のセレクター DSL を再利用、[セレクター](deployment/selector.md) を参照）。**必須。** |
| `updateStrategy { ... }` | テンプレートの更新の展開方法を制御します。 |
| `volumeClaimTemplates { claim(name) { ... } }` | 安定した Pod ごとのストレージを提供する PVC テンプレート。 |
| `persistentVolumeClaimRetentionPolicy { ... }` | プロビジョニングされた PVC のライフサイクルを制御します。 |
| `template { ... }` | Pod テンプレート（[Pod テンプレート](deployment/template.md) を参照）。**必須。** |

### 更新戦略（`updateStrategy`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `type` | `RollingUpdate`（既定）または `OnDelete`。 |
| `rollingUpdate { partition; maxUnavailable }` | ローリングアップデートのパラメーター。 |
| `partition` | この序数以上の Pod を更新します（段階的 / カナリアロールアウト）。 |
| `maxUnavailable` | 更新中に利用不可となる Pod の最大数（絶対値または割合、例: `1.absolute` / `25.percent`）。 |

### ボリュームクレームテンプレート（`volumeClaimTemplates`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `claim(name) { ... }` | PVC テンプレートを追加します。`name` は Pod コンテナの `volumeMount` と一致する必要があります。 |
| `accessModes(...)` | アクセスモード（例: `ReadWriteOnce`、`ReadOnlyMany`、`ReadWriteMany`、`ReadWriteOncePod`）。 |
| `storageClassName` | ボリュームのプロビジョニングに使用する StorageClass。 |
| `volumeMode` | `Filesystem`（既定）または `Block`。 |
| `requests { storage = ... }` | 要求する最小ストレージ（例: `1.giBytes`）。 |
| `limits { storage = ... }` | 許容される最大ストレージ。 |
| `label(key, value)` / `labels { ... }` | クレームメタデータのラベル。 |
| `annotation(key, value)` / `annotations { ... }` | クレームメタデータのアノテーション。 |

### PVC 保持ポリシー（`persistentVolumeClaimRetentionPolicy`）

| プロパティ | 説明 |
| :--- | :--- |
| `whenDeleted` | `Retain`（既定）または `Delete` —— StatefulSet が削除されたときに適用。 |
| `whenScaled` | `Retain`（既定）または `Delete` —— StatefulSet がスケールダウンされたときに適用。 |

!!! note "安定したストレージ"
    `volumeClaimTemplates` の各エントリは、**Pod ごとに** 1 つの PersistentVolumeClaim をプロビジョニング
    します。クレームは再スケジューリングをまたいで同一性を保持し、各レプリカに専用の永続ストレージを
    提供します。
