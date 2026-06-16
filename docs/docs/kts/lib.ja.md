# ライブラリファイル（`*.lib.kts`）

ライブラリファイルを使うと、再利用可能なヘルパー関数や定数を定義でき、それらは Helm リポジトリ内のすべての spec ファイルで自動的に利用可能になります。

---

## 概要

| プロパティ | 値 |
| :--- | :--- |
| ファイル拡張子 | `*.lib.kts` |
| 場所 | `helm/` ディレクトリツリー内の任意の場所 |
| YAML へのレンダリング | いいえ |
| spec ファイルで利用可能 | はい —— すべての spec ファイルで自動的に |
| 他のライブラリファイルで利用可能 | いいえ |

---

## ライブラリファイルの作成

`helm/` ディレクトリ内の任意の場所に `.lib.kts` 拡張子のファイルを作成します。
共有ヘルパーを `templates/helpers.lib.kts` に置くのが一般的な慣習です。

```
─ helm
  ├── Chart.spec.kts
  ├── values.yaml
  └── templates
      ├── helpers.lib.kts       ← library file
      ├── deployment.spec.kts
      └── service.spec.kts
```

ファイル内では、必要な Kotlin の関数、定数、拡張関数を定義します。

```kotlin
// templates/helpers.lib.kts

fun appLabels(name: String): Map<String, String> = mapOf(
    "app.kubernetes.io/name" to name,
    "app.kubernetes.io/managed-by" to "kube-kts"
)

fun fullName(release: String, component: String) = "$release-$component"

const val DEFAULT_REGISTRY = "registry.example.com"
```

---

## spec ファイルでのライブラリ関数の使用

任意の `*.lib.kts` ファイルで定義されたすべての関数と定数は、すべての `*.spec.kts` ファイルから直接呼び出せます —— インポートは不要です。

```kotlin
// templates/deployment.spec.kts

deployment {
    metadata(fullName("my-release", "backend")) {
        appLabels("backend").forEach { label(it.key, it.value) }
    }
    spec {
        template {
            spec {
                container("backend") {
                    image = "$DEFAULT_REGISTRY/backend:latest"
                }
            }
        }
    }
}
```

---

## デフォルトインポート

ライブラリファイルは spec ファイルと同じデフォルトインポートにアクセスできるため、明示的な
import 文なしで、すべての Kube KTS DSL 型、Java 標準ライブラリ型、Kotlin の時間拡張を使用できます。

| インポート | 含まれるもの |
| :--- | :--- |
| Kube KTS API | すべての `*Spec` および `*SpecBuilder` 型、`ValueAccess` |
| `java.net` | `URL`、`URI` |
| `java.time` | `Duration`、`Instant`、`LocalDate`、… |
| `kotlin.time` | `Duration`、`Duration.Companion.*` |

---

## 制約

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名
    （例: `java.lang.Runtime`）を**許可しません**。上記の事前構成されたデフォルトインポートの型のみ使用できます。

    `--unsafe` フラグを使うとこれらの制限を解除できます。

!!! warning "ライブラリファイルは他のライブラリファイルにアクセスできません"
    1 つの `*.lib.kts` ファイルで定義された関数は、他の `*.lib.kts` ファイルでは**利用できません**。
    ライブラリ関数を呼び出せるのは `*.spec.kts` ファイルだけです。それに応じてライブラリを構成し、ライブラリ間の依存を避けてください。

!!! note "ライブラリファイルはレンダリングされません"
    `*.lib.kts` ファイルは、トップレベルのステートメントを含んでいても、YAML 出力ファイルにレンダリングされることはありません。
    出力を生成するのは `*.spec.kts` ファイルだけです。

---

## 複数のライブラリファイル

`*.lib.kts` ファイルは好きなだけ作成できます。Kube KTS は `helm/` ディレクトリツリー内の
それらすべてを再帰的に検出し、すべての spec ファイルで利用可能にします。

```
─ helm
  └── templates
      ├── helpers.lib.kts         ← general helpers
      ├── labels.lib.kts          ← label utilities
      ├── resources.lib.kts       ← resource limit constants
      └── deployment.spec.kts     ← can call functions from all three lib files
```
