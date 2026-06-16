# 概要

Kube KTS は Kubernetes 向けの Helm ラッパーであり、**従来の Helm と完全に互換性があります**。
Go テンプレートを含む YAML ファイルの代わりに、**Kotlin Script (KTS)** を使って Kubernetes リソースを定義します。

これにより、次のようなモダンな言語機能を利用できます。

- 型安全性
- Null 安全性
- 関数型プログラミングの構文

さらに、次のような利点もあります。

- 完全な IDE サポート（例: IntelliJ IDEA、VS Code）
- デバッグ機能
- テンプレートベースの手法と比べて優れた開発者体験

---

# はじめに

Kube KTS をインストールして `PATH` に追加すれば、すぐに Kubernetes リソースの開発を始められます。

Kube KTS は**標準的な Helm リポジトリ構造**で動作します。
適切な IDE サポートを得るため、すべての `.kts` ファイルは `helm` ディレクトリ内に配置する必要があります。

---

## プロジェクト構造

プロジェクト構造は従来の Helm リポジトリと同じです。

```
─ helm
  ├── Chart.spec.kts
  ├── values.yaml
  └── templates
      ├── deployment.spec.kts
      ├── service.spec.kts
      ├── helpers.lib.kts
      └── ...
```

処理中、Kube KTS は Helm 互換の出力を生成します。

```
─ helm
  ├── Chart.yaml
  ├── values.yaml
  └── templates
      ├── deployment.yaml
      ├── service.yaml
      └── ...
```

!!! note "ライブラリファイルはレンダリングされません"
    `.lib.kts` 拡張子のファイルは YAML にレンダリングされません。その内容は
    コンパイル時にすべての spec ファイルで自動的に利用可能になります。

---

## KTS ファイルの種類

Kube KTS は 2 種類の Kotlin Script ファイルを区別します。

| 拡張子 | 用途 |
| :--- | :--- |
| `*.spec.kts` | Kubernetes リソースを定義する（YAML にレンダリングされる） |
| `*.lib.kts` | すべての spec ファイルで利用できるヘルパー関数を定義する |

### Spec ファイル（`*.spec.kts`）

Spec ファイルは KTS DSL を使って Kubernetes リソースを定義します。各 spec ファイルは
1 つの YAML 出力ファイルを生成します。`Chart.spec.kts` は `Chart.yaml` を生成する特別な
spec ファイルです。

```kotlin
// templates/deployment.spec.kts
deployment {
    metadata("my-app") { }
    spec { /* ... */ }
}
```

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名
    （例: `java.lang.Runtime`）を**許可しません**。事前構成されたデフォルトインポートで
    提供される型のみ使用できます —— 完全な一覧は [デフォルトインポート](kts/lib.md#default-imports) を参照してください。

    `--unsafe` フラグを使うとこれらの制限を解除できます。

### ライブラリファイル（`*.lib.kts`）

ライブラリファイルは再利用可能なヘルパー関数を定義し、それらは同じリポジトリ内の
すべての spec ファイルで自動的に利用可能になります。ライブラリファイルは YAML 出力に
**レンダリングされず**、他のライブラリファイルからは**アクセスできません**。

```kotlin
// templates/helpers.lib.kts
fun appLabels(name: String) = mapOf("app" to name, "managed-by" to "kube-kts")
```

```kotlin
// templates/deployment.spec.kts —— appLabels() を直接呼び出せます
deployment {
    metadata("my-app") {
        labels { appLabels("my-app").forEach { label(it.key, it.value) } }
    }
}
```

---

## 従来との互換性

Kube KTS は従来の Helm 構成と完全に互換性があります。

- 既存の YAML ファイルを `.spec.kts` ファイルと併用できます
- プレーンな `.kts` ファイル（`.spec.` 修飾子なし）も従来の spec ファイルとしてサポートされます
- 従来のファイルは最終出力に**変更されずにコピー**されます
- 混在環境（YAML + KTS）も完全にサポートされます

---

## 値ファイル

Kube KTS は Helm スタイルの値ファイルをサポートします。

- 値は `values.yaml` で定義できます
- 上書きの挙動は標準の Helm と同一です
- 値は KTS スクリプト内からアクセスできます

---

## Kube KTS の実行

Kube KTS は Helm リポジトリを処理するコマンドラインツールです。

次のように実行します。
`kube-kts`
リポジトリを処理するには、Helm プロジェクトのパスを指定します。
詳しい使い方は **「Kube KTS CLI の使い方」** を参照してください。
