# SealedSecret DSL

`sealedSecret` DSL は [Bitnami Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets)
（`bitnami.com/v1alpha1`）を構成するために使用します。SealedSecret は**暗号化された**データを
保持し、クラスター内のコントローラーのみがそれを通常の Kubernetes [Secret](secret.md) に
復号できます。値が暗号化されているため、SealedSecret はバージョン管理に安全に保存できます。

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名
    （例: `java.lang.Runtime`）の使用を**許可しません**。事前構成されたデフォルトインポート
    で提供される型のみが使用できます。

    `--unsafe` フラグを使用すると、これらの制限を解除できます。

!!! note "SealedSecrets コントローラーがインストールされている必要があります"
    SealedSecret は、対象クラスターで実行されている SealedSecrets コントローラーによってのみ
    復号できます。暗号化された値は、そのコントローラーの公開鍵に対して `kubeseal` により
    生成されます。

## 基本的な使い方

最小限の SealedSecret 構成には `metadata` と `encryptedData` 内の少なくとも 1 つのエントリが
必要です。

```kotlin
sealedSecret {
    metadata("my-sealed-secret") {
        namespace = "default"
    }

    spec {
        addEncryptedData("password", "AgBy3i4OJSWK+PiTySYZZA9rO...")
    }
}
```

## 詳細な例

以下の例は、複数の暗号化エントリと、コントローラーが生成すべき Secret を記述する `template`
を示しています。

```kotlin
sealedSecret {
    metadata("full-sealed-secret") {
        namespace = "production"
        labels {
            label("app", "demo")
        }
    }

    spec {
        encryptedData {
            entry("username", "AgAKv2H8x9Qm0pLrT3uVwX1yZ...")
            entry("password", "AgBy3i4OJSWK+PiTySYZZA9rO...")
        }

        // コントローラーが生成する Secret を記述します
        template {
            type = SecretSpec.Type.Opaque
            immutable = true
            metadata {
                labels {
                    label("app", "demo")
                }
                annotations {
                    annotation("description", "Application credentials")
                }
            }
        }
    }
}
```

## 構成リファレンス

### メタデータ (`metadata`)

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | `String` | SealedSecret の名前。最初の引数として渡します。 |
| `namespace` | `String?` | リソースの名前空間。 |
| `generateName` | `String?` | 一意の名前を生成するための任意のプレフィックス。 |
| `labels { label(key, value) }` | `Map<String, String>` | SealedSecret リソースのラベル。 |
| `annotations { annotation(key, value) }` | `Map<String, String>` | SealedSecret リソースのアノテーション。 |

### SealedSecret 仕様 (`spec`)

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `addEncryptedData(key, value)` | `encryptedData` に単一の暗号化エントリを追加します。少なくとも 1 つのエントリが必要です。 |
| `encryptedData { entry(key, value) }` | ブロックで `encryptedData` に複数の暗号化エントリを追加します。 |
| `template { ... }` | 生成される Secret を記述する任意のテンプレート（[テンプレート](#template)を参照）。 |

### テンプレート (`template`)

`template` ブロックは、コントローラーが復号後に生成する Secret を記述します。

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `type` | 生成される Secret のタイプ（[Secret DSL](secret.md#secret) と同じ値）。 |
| `immutable` | `true` の場合、生成される Secret は作成後に更新できません。 |
| `metadata { labels { ... }; annotations { ... } }` | 生成される Secret に適用されるラベルとアノテーション。 |
