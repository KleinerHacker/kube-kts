# Kotlin スクリプト (KTS)

Kube KTS リポジトリは、Helm の Go テンプレート化された YAML の代わりに **Kotlin スクリプト** で
Kubernetes リソースを記述します。各スクリプトは純粋な Kotlin であり、`values` に対して評価され、
通常の Helm chart に変換されて Helm に渡されます。結果として得られるのは、すでに慣れ親しんだ
Helm ワークフローそのものですが、型安全性、null 安全性、完全な IDE サポート、そしてデバッグ可能で
ロジックを含まない YAML 出力が加わります。

このページでは、これらのスクリプトをどのように理解すべきか、そして **safe と unsafe** の違いに
ついて説明します。個々のリソース（Chart、Service、Deployment など）は、このセクション内の専用
ページで説明されています。

---

## KTS リポジトリの理解の仕方

リポジトリは Helm chart を反映したディレクトリツリーにすぎません。Kube KTS はそれをスキャンし、
`*.kts` ファイルをレンダリングされた YAML に置き換え、それ以外はそのままコピーします。

```
─ helm
  ├── Chart.spec.kts        → Chart.yaml
  ├── values.yaml           （そのままコピー、スクリプトにも提供される）
  └── templates
      ├── helpers.lib.kts   （ヘルパー関数 —— レンダリングされない）
      ├── deployment.spec.kts → deployment.yaml
      └── service.spec.kts    → service.yaml
```

### ファイルタイプ

| 拡張子 | 意味 | YAML へレンダリング |
| :--- | :--- | :--- |
| `*.spec.kts` | DSL を通じて Kubernetes リソースをちょうど 1 つ定義する（`deployment { … }`、`service { … }` など）。 | はい |
| `*.lib.kts` | 再利用可能なヘルパー関数/定数を定義し、**すべての** spec ファイルで利用可能。[ライブラリファイル](lib.md) を参照。 | いいえ |
| `*.kts` | レガシー —— spec ファイルとして扱われる。 | はい |
| `*.yaml` / `*.yml` | 通常の Helm YAML。完全な後方互換性のために保持される。 | そのままコピー |

### スクリプトとは実際には何か

各 `*.spec.kts` ファイルは通常の Kotlin スクリプトです。リソースを構築する単一のトップレベル DSL
関数を呼び出し、その過程で chart の values を読み取ることができます：

```kotlin
// templates/deployment.spec.kts
deployment {
    metadata("backend") { }
    spec {
        replicas = valueOrNull<Int>("spec.replicas") ?: 1
        template {
            spec {
                container("backend") {
                    image = value<String>("spec.image")
                }
            }
        }
    }
}
```

これは実行前にコンパイルされる本物の Kotlin であるため、型の誤り、プロパティ名のタイプミス、
必須値の欠落といった間違いは、デプロイ時に壊れた YAML を生成するのではなく、**コンパイル時**に
表面化します。値アクセス（`value`、`valueOrNull`、`array`、`map` など）については
[値の取り扱い](values.md) で説明しています。

---

## Safe と Unsafe

レンダリングを予測可能に保ち、スクリプトがマシン上でできることを制限するため、Kube KTS は
デフォルトで **safe モード** でスクリプトをコンパイルします。safe モードはスクリプトを Kube KTS
DSL と厳選された事前インポート型の範囲に制限します。unsafe モードはこれらの制限を解除し、
スクリプトが任意の JVM コードを実行できるようにします。

### safe モードが行うこと

スクリプトがコンパイルされる前に、そのソースがチェックされます（文字列リテラルとコメントは先に
取り除かれるため、ルールは実際のコードにのみ適用されます）。2 つのものが拒否されます：

| safe モードで拒否 | 例 | 理由 |
| :--- | :--- | :--- |
| `import` 文 | `import java.io.File` | 任意のクラスの取り込みを防ぐ。 |
| 完全修飾クラス名 | `java.lang.Runtime.getRuntime()` | import なしで任意の JVM API に到達するのを防ぐ。 |

いずれかが見つかると、コンパイルは説明メッセージとともに直ちに失敗します —— YAML は生成されません。

ただし safe スクリプトは「空」ではありません。完全な Kube KTS DSL に加えて一般的な標準ライブラリ型を
含む、豊富な型のセットが **事前インポート** されており、`import` なしで使用できます：

| 事前インポート | 含むもの |
| :--- | :--- |
| Kube KTS API | すべての `*Spec` / `*SpecBuilder` 型、`ValueAccess`、単位拡張（`250.mCpu`、`1.giBytes`、`25.percent` など） |
| `java.net` | `URL`、`URI` |
| `java.time` | `Duration`、`Instant`、`LocalDate` など |
| `kotlin.time` | `Duration`、`Duration.Companion.*` |

大多数の chart にとってこれが必要なすべてなので、safe モードのままにしておくべきです。

### unsafe モードが行うこと

unsafe モードは上記の import/FQN チェックを単に**スキップ**します。スクリプトはその後 `import` 文と
完全修飾クラス名を使用できるようになり、JVM クラスパス上の任意のクラスを呼び出せます —— ファイルの
読み取り、プロセスの起動、ネットワーク接続のオープンなどです。

その力はリスクでもあります：スクリプトはもはや Kubernetes リソースの記述に限定されず、レンダリングを
実行するマシン上で任意のコードを実行できます。完全に信頼しレビュー済みのリポジトリに対してのみ
unsafe モードを有効にしてください。

!!! warning "unsafe モードは任意のコードを実行する"
    unsafe モードが有効な場合、サンドボックスはありません：悪意のある、またはバグのあるスクリプトは
    現在のユーザーができることは何でもできます。有効にすることは信頼できないコードを実行することと
    同じだと考えてください。

---

## コマンドライン演算子

safe と unsafe は CLI のグローバル **`--unsafe`** フラグで制御されます。これを付けない場合、すべての
スクリプトは safe モードでコンパイルされます。付けると、その実行内のすべてのスクリプトについて
import/FQN 制限が解除されます。

```bash
# safe（デフォルト）—— import と FQN は拒否される
kube-kts render ./helm ./out

# unsafe —— import と完全修飾クラス名が許可される
kube-kts --unsafe render ./helm ./out
```

`--unsafe` は**グローバル**オプションなので、コマンドの前に置かれ、スクリプトをコンパイルする
あらゆるコマンド（`validate`、`compile`、`render`、`lint`、`template`、`install` など）に適用
されます。`--help` 出力とコマンドページでは `!!!` マーカーが付いており、セキュリティ関連の
オプションであることを示します。グローバルオプションの完全な一覧は
[CLI 概要](../cli/index.md#global-options) を参照してください。
