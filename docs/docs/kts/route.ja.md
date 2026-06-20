# Route DSL

`route` DSL は、サービスを外部ホスト名で公開し、必要に応じて OpenShift ルーターで TLS を終端する
**OpenShift Route** リソースを構成するために使用します。

!!! danger "標準 Kubernetes リソースではありません"
    `Route`（`apiVersion: route.openshift.io/v1`）は標準 Kubernetes の一部では**ありません**。これは
    **OpenShift / OKD**（または OpenShift ルーターを同梱する互換ディストリビューション）に固有のリソースであり、
    バニラ Kubernetes クラスターでは動作しません。

    標準 Kubernetes では代わりに [Ingress DSL](ingress.md) を使用してください。

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名（例: `java.lang.Runtime`）を
    **許可しません**。事前構成されたデフォルトインポートで提供される型のみ使用できます。

    `--unsafe` フラグを使用すると、これらの制限を解除できます。

## 基本的な使い方

最小限の Route には `metadata` と `spec` 内のバックエンド（`to`）が必要です。

```kotlin
route {
    metadata("my-route") {
        namespace = "default"
    }

    spec {
        host = "www.example.com"
        to("my-service") {
            weight = 100
        }
        port(8080)
    }
}
```

## 詳細な例

以下は、パスベースのルーティング、代替バックエンドによる重み付きトラフィック分割、および edge TLS 終端を
示す包括的な例です。

```kotlin
route {
    metadata("full-route") {
        namespace = "production"
    }

    spec {
        host = "www.example.com"
        path = "/app"

        // プライマリバックエンド
        to("main-service") {
            weight = 100
        }

        // 重み付きトラフィック分割（例: カナリアデプロイ）
        alternateBackends {
            backend("canary-service") {
                weight = 20
            }
        }

        // バックエンドサービス上のターゲットポート（数値または名前）
        port(8080)

        // Edge TLS 終端、安全でないトラフィックを HTTPS にリダイレクト
        tls(RouteTlsSpec.Termination.Edge) {
            insecureEdgeTerminationPolicy = RouteTlsSpec.InsecureEdgeTerminationPolicy.Redirect
            key = "<PEM private key>"
            certificate = "<PEM certificate>"
            caCertificate = "<PEM CA certificate>"
        }

        wildcardPolicy = RouteSpec.WildcardPolicy.None
    }
}
```

## 構成リファレンス

### メタデータ（`metadata`）

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | `String` | Route リソースの名前（最初の引数として渡されます）。 |
| `namespace` | `String?` | リソースの名前空間。 |
| `generateName` | `String?` | 一意な名前を生成するための任意のプレフィックス。 |

### Route 仕様（`spec`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `host` | Route が公開される外部到達可能なホスト名。 |
| `path` | パスベースのルーティング用の任意のパス。 |
| `to(name) { ... }` | Route がトラフィックを誘導するプライマリバックエンド（Service）。 |
| `addAlternateBackend(name) { ... }` | 重み付き分割用に単一の代替バックエンドを追加します。 |
| `alternateBackends { backend(name) { ... } }` | 複数の代替バックエンドを追加します。 |
| `port(Int)` / `port(String)` | バックエンドサービス上のターゲットポート（数値または名前）。 |
| `tls(termination) { ... }` | TLS 終端を構成します。 |
| `wildcardPolicy` | `None` または `Subdomain`。 |

### バックエンド（`to` / `backend`）

| プロパティ | 説明 |
| :--- | :--- |
| `kind` | 参照されるオブジェクトの種類。デフォルトは `Service`。 |
| `weight` | 重み付きトラフィック分割用の任意の相対重み（0-256）。 |

### TLS 構成（`tls`）

| プロパティ | 説明 |
| :--- | :--- |
| `termination` | TLS 終端タイプ: `Edge`、`Passthrough`、`Reencrypt`。 |
| `insecureEdgeTerminationPolicy` | 安全でない HTTP トラフィックの動作: `None`、`Allow`、`Redirect`。 |
| `key` | PEM エンコードされた秘密鍵（edge/reencrypt）。 |
| `certificate` | PEM エンコードされた証明書（edge/reencrypt）。 |
| `caCertificate` | PEM エンコードされた CA 証明書チェーン（edge/reencrypt）。 |
| `destinationCACertificate` | バックエンドを検証する PEM エンコードされた CA 証明書（reencrypt のみ）。 |
