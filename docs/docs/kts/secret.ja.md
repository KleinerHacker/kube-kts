# Secret DSL

`secret` DSL は Kubernetes Secret リソースを構成するために使用します。Secret はパスワード、
トークン、キーなどの少量の機密データを保存し、Pod は環境変数として、またはボリュームを通じて
マウントされたファイルとして利用できます。

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名
    （例: `java.lang.Runtime`）の使用を**許可しません**。事前構成されたデフォルトインポート
    で提供される型のみが使用できます。

    `--unsafe` フラグを使用すると、これらの制限を解除できます。

!!! note "Secret は base64 エンコードされるだけで暗号化されません"
    Secret の値は base64 エンコードで保存され、**暗号化はされません**。Secret（または etcd）
    への読み取りアクセスを持つ者は誰でもデコードできます。レンダリングされた Secret を
    バージョン管理にコミットしないでください。バージョン管理に安全な代替手段としては、
    [SealedSecret](sealedsecret.md) を使用してください。

## 基本的な使い方

最小限の Secret 構成には `metadata` と `spec` 内の少なくとも 1 つのデータエントリが必要です。

```kotlin
secret {
    metadata("my-secret") {
        namespace = "default"
    }

    spec {
        addStringData("username", "admin")
        addStringData("password", "s3cr3t")
    }
}
```

## 詳細な例

以下の例は、Secret のタイプ、バイナリデータ、文字列データ、不変性を示しています。

```kotlin
secret {
    metadata("full-secret") {
        namespace = "production"
        labels {
            label("app", "demo")
        }
        annotations {
            annotation("description", "Application credentials")
        }
    }

    spec {
        // 組み込みの Secret タイプ
        type = SecretSpec.Type.Opaque

        // バイナリデータ —— `data:` の下に base64 エンコードでレンダリングされます
        data {
            entry("token", tokenBytes)
        }

        // プレーンな文字列データ —— `stringData:` の下にレンダリングされます
        stringData {
            entry("username", "admin")
            entry("password", "s3cr3t")
        }

        // 作成後、Secret は変更できなくなります
        immutable = true
    }
}
```

## 構成リファレンス

### メタデータ (`metadata`)

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | `String` | Secret の名前。最初の引数として渡します。 |
| `namespace` | `String?` | リソースの名前空間。 |
| `generateName` | `String?` | 一意の名前を生成するための任意のプレフィックス。 |
| `labels { label(key, value) }` | `Map<String, String>` | Secret リソースのラベル。 |
| `annotations { annotation(key, value) }` | `Map<String, String>` | Secret リソースのアノテーション。 |

### Secret 仕様 (`spec`)

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `type` | Secret のタイプ（[Secret タイプ](#secret)を参照）。省略時のデフォルトは `Opaque`。 |
| `addData(key, value)` | `data` に単一のバイナリ (`ByteArray`) エントリを追加します。 |
| `data { entry(key, value) }` | ブロックで `data` に複数のバイナリエントリを追加します。 |
| `addStringData(key, value)` | `stringData` に単一のプレーン文字列エントリを追加します。 |
| `stringData { entry(key, value) }` | ブロックで `stringData` に複数のプレーン文字列エントリを追加します。 |
| `immutable` | `true` の場合、Secret は作成後に更新できません。 |

!!! note "`data` と `stringData`"
    `data` は生のバイトコンテンツに使用します。値はレンダリングされた YAML の `data:` の下に
    base64 エンコードで出力されます。`stringData` は UTF-8 文字列値に使用します。これらは
    `stringData:` の下にプレーン形式で出力され、Kubernetes によって `data` にマージされます。
    キーは両方のマップ間で一意である必要があります。

### Secret タイプ

| DSL 値 | レンダリングされるタイプ |
| :--- | :--- |
| `SecretSpec.Type.Opaque` | `Opaque` |
| `SecretSpec.Type.ServiceAccountToken` | `kubernetes.io/service-account-token` |
| `SecretSpec.Type.DockerCfg` | `kubernetes.io/dockercfg` |
| `SecretSpec.Type.DockerConfigJson` | `kubernetes.io/dockerconfigjson` |
| `SecretSpec.Type.BasicAuth` | `kubernetes.io/basic-auth` |
| `SecretSpec.Type.SshAuth` | `kubernetes.io/ssh-auth` |
| `SecretSpec.Type.Tls` | `kubernetes.io/tls` |
| `SecretSpec.Type.BootstrapToken` | `bootstrap.kubernetes.io/token` |
