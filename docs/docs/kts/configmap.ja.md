# ConfigMap DSL

`configMap` DSL は、Kubernetes ConfigMap リソースを構成するために使用します。ConfigMap は
機密でない構成データをキーと値のペアとして保存し、Pod は環境変数、コマンドライン引数、または
ボリュームでマウントしたファイルとしてそれを利用できます。

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名
    （例: `java.lang.Runtime`）を**許可しません**。事前構成されたデフォルトインポートで
    提供される型のみ使用できます。

    `--unsafe` フラグを使うとこれらの制限を解除できます。

!!! note "ConfigMap は機密でないデータ用です"
    ConfigMap は暗号化されないため、パスワードやトークンなどの機密情報を保存してはいけません。
    機密データには Secret を使用してください。

## 基本的な使い方

最小限の ConfigMap 構成には、`metadata` と `spec` 内の少なくとも 1 つのエントリが必要です。

```kotlin
configMap {
    metadata("my-config") {
        namespace = "default"
    }

    spec {
        addData("application.name", "demo")
        addData("log.level", "INFO")
    }
}
```

## 詳細な例

以下の例は、文字列データ、バイナリデータ、および不変性を示しています。

```kotlin
configMap {
    metadata("full-config") {
        namespace = "production"
        labels {
            label("app", "demo")
        }
        annotations {
            annotation("description", "Application configuration")
        }
    }

    spec {
        // String data — rendered under `data:`
        data {
            entry("log.level", "DEBUG")
            entry(
                "application.yaml",
                """
                server:
                  port: 8080
                """.trimIndent()
            )
        }

        // Binary data — rendered base64-encoded under `binaryData:`
        binaryData {
            entry("logo.png", logoBytes)
        }

        // Once created, the ConfigMap can no longer be changed
        immutable = true
    }
}
```

## 設定リファレンス

### メタデータ（`metadata`）

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | `String` | ConfigMap の名前（最初の引数として渡される）。 |
| `namespace` | `String?` | リソースの名前空間。 |
| `generateName` | `String?` | 一意の名前を生成するための任意のプレフィックス。 |
| `labels { label(key, value) }` | `Map<String, String>` | ConfigMap リソースのラベル。 |
| `annotations { annotation(key, value) }` | `Map<String, String>` | ConfigMap リソースのアノテーション。 |

### ConfigMap の仕様（`spec`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `addData(key, value)` | `data` に単一の文字列エントリを追加します。 |
| `data { entry(key, value) }` | ブロックを使って `data` に複数の文字列エントリを追加します。 |
| `addBinaryData(key, value)` | `binaryData` に単一のバイナリ（`ByteArray`）エントリを追加します。 |
| `binaryData { entry(key, value) }` | ブロックを使って `binaryData` に複数のバイナリエントリを追加します。 |
| `immutable` | `true` の場合、ConfigMap は作成後に更新できません。 |

!!! note "`data` と `binaryData`"
    `data` は UTF-8 文字列値に使用します。これらはレンダリングされた YAML の `data:` 配下に直接表示されます。
    `binaryData` は任意のバイト内容に使用します。値は base64 エンコードされて `binaryData:` 配下に表示されます。
    キーは両方のマップ全体で一意である必要があります。
