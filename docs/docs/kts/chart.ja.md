# Chart DSL

`chart` DSL は、従来の Helm チャートの `Chart.yaml` ファイルと同様に、Helm チャートのメタデータと依存関係を定義するために使用します。

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名
    （例: `java.lang.Runtime`）を**許可しません**。事前構成されたデフォルトインポートで
    提供される型のみ使用できます。

    `--unsafe` フラグを使うとこれらの制限を解除できます。

## 基本的な使い方

チャートを定義するには、チャートの**名前**と**バージョン**を主な引数として受け取る `chart` 関数を使用します。

```kotlin
chart("my-chart", "1.0.0") {
    description = "A short description of my chart"
    type = ChartSpec.Type.Application
}
```

## 詳細な例

以下は、`chart` ブロック内で利用可能なすべての設定オプションを示す包括的な例です。

```kotlin
chart("full-featured-chart", "1.2.3") {
    // Metadata
    description = "A comprehensive example of the Chart DSL"
    type = ChartSpec.Type.Library // Default is Application if not specified
    home = "https://github.com/example/kube-kts"
    icon = URI("https://example.com/icon.png")
    appVersion = "2.5.0"
    deprecated = false

    // Keywords and sources
    keywords {
        keyword("kubernetes")
        keyword("kotlin")
        keyword("dsl")
    }
    sources {
        source(URI("https://github.com/example/kube-kts/src"))
    }

    // Compatibility
    kubeVersion {
        minInclusive("1.20.0")
        maxExclusive("1.30.0")
    }

    // Dependencies
    dependencies {
        dependency("common-utils", "0.5.0") {
            repository = URI("https://charts.example.com")
            alias = "utils"
            condition = "utils.enabled"

            tags {
                tag("infrastructure")
            }
            pathImportValues {
                pathImportValue("exports.values")
            }
            mappingImportValues {
                mappingImportValue("source.key", "destination.key")
            }
        }
    }

    // Maintainers
    maintainers {
        maintainer("John Doe") {
            email = MailAddress.parse("john.doe@example.com")
            url = URI("https://johndoe.com")
        }
    }

    // Custom annotations
    annotations {
        annotation("custom-metadata-key", "some-value")
    }
}
```

## 設定リファレンス

### トップレベルのプロパティ

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `description` | `String?` | チャートの 1 行の説明。 |
| `type` | `ChartSpec.Type?` | チャートの種類: `Application` または `Library`。 |
| `home` | `String?` | プロジェクトのホームページの URL。 |
| `icon` | `URI?` | アイコンとして使用する SVG または PNG 画像の URL。 |
| `appVersion` | `String?` | このチャートに含まれるアプリケーションのバージョン（チャートのバージョンではない）。 |
| `deprecated` | `Boolean?` | このチャートが非推奨かどうか。 |

### メソッド

| メソッド | 説明 |
|:-----------------------------------------------------------------| :--- |
| `keywords { ... }` | このチャートを検索するためのキーワードを追加します。（代替: `addKeyword`、`addKeywords`） |
| `sources { ... }` | このプロジェクトのソースコードへの URL を追加します。（代替: `addSource`、`addSources`） |
| `kubeVersion { ... }` | 互換性のある Kubernetes バージョンの範囲を設定します。 |
| `dependencies { ... }` | チャートの依存関係を追加します。下記の [依存関係](#dependencies) を参照してください。（代替: `addDependency`） |
| `maintainers { ... }` | メンテナーの情報を追加します。（代替: `addMaintainer`） |
| `annotations { ... }` | カスタムアノテーションを追加します。（代替: `addAnnotation`） |


### 依存関係

`dependency` ブロックでは、きめ細かい制御が可能です。

- `repository`: チャートリポジトリの URL。
- `alias`: チャートのエイリアス（同じチャートを複数回使用する場合に便利）。
- `condition`: チャートをインストールするかどうかを判断するブール式。
- `tags { tag(String) }`: 依存関係をグループ化するためのタグを追加します。（代替: `addTag`）
- `pathImportValues { pathImportValue(path) }`: サブチャートから値をインポートします。（代替: `addPathImportValue`）
- `mappingImportValues { mappingImportValue(childKey, parentKey) }`: サブチャートの特定の値を親チャートにマッピングします。（代替: `addMappingImportValue`）

## 特殊な型

- `ChartSpec.Type`: `Application` と `Library` の値を持つ列挙型。
- `MailAddress`: メールアドレスの検証とフォーマットのためのヘルパー型。
- `URI`: Web リンクを表す標準の `java.net.URI`。
