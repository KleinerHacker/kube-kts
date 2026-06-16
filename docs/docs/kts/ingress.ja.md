# Ingress DSL

`ingress` DSL は、通常 HTTP 経由でクラスター内のサービスへの外部アクセスを管理する Kubernetes Ingress リソースを構成するために使用します。

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名
    （例: `java.lang.Runtime`）を**許可しません**。事前構成されたデフォルトインポートで
    提供される型のみ使用できます。

    `--unsafe` フラグを使うとこれらの制限を解除できます。

## 基本的な使い方

最小限の Ingress 構成には、`metadata` と `spec` 内の少なくとも 1 つのルールが必要です。

```kotlin
ingress {
    metadata("my-ingress") {
        namespace = "default"
    }

    spec {
        rules {
            rule {
                host = "example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                        path = "/"
                        serviceBackend("my-service") {
                            port(80)
                        }
                    }
                }
            }
        }
    }
}
```

## 詳細な例

以下は、TLS や複数のルールを含む、さまざまな設定オプションを示す包括的な例です。

```kotlin
ingress {
    metadata("full-ingress") {
        namespace = "production"
    }

    spec {
        ingressClassName = "nginx"

        // Default backend when no rules match
        defaultServiceBackend("default-service") {
            port(8080)
        }

        // TLS configuration
        tlsList {
            tls {
                secretName = "example-tls-secret"
                hosts {
                    host("example.com")
                    host("api.example.com")
                }
            }
        }

        // Rule for the main website
        rules {
            rule {
                host = "example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
                        path = "/home"
                        serviceBackend("web-service") {
                            port("http") // Reference port by name
                        }
                    }
                }
            }
        }

        // Rule for API with multiple paths
        rules {
            rule {
                host = "api.example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                        path = "/v1"
                        serviceBackend("api-v1-service") {
                            port(8081)
                        }
                    }
                    httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                        path = "/v2"
                        serviceBackend("api-v2-service") {
                            port(8082)
                        }
                    }
                }
            }
        }
    }
}
```

## 設定リファレンス

### メタデータ（`metadata`）

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | `String` | Ingress リソースの名前（最初の引数として渡される）。 |
| `namespace` | `String?` | リソースの名前空間。 |
| `generateName` | `String?` | 一意の名前を生成するための任意のプレフィックス。 |

### Ingress の仕様（`spec`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `ingressClassName` | IngressClass クラスターリソースの名前。 |
| `defaultServiceBackend(name) { ... }` | サービスのデフォルトバックエンドを設定します。 |
| `defaultResourceBackend(name, kind) { ... }` | カスタムリソースのデフォルトバックエンドを設定します。 |
| `tlsList { tls { ... } }` | TLS 構成ブロックを追加します。（代替: `addTls`） |
| `rules { rule { ... } }` | Ingress ルールを追加します。（代替: `addRule`） |

### TLS 構成（`tls`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `secretName` | TLS 証明書と鍵を含む Secret の名前。 |
| `hosts { host(String) }` | TLS 証明書に含めるホストを追加します。（代替: `addHost`） |

### ルール（`rule`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `host` | ネットワークホストの完全修飾ドメイン名。 |
| `httpPaths { httpPath(type) { ... } }` | ルールに HTTP パスを追加します。（代替: `addHttpPath`） |

### HTTP パス（`httpPath`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `path` | 受信リクエストのパスと照合されるパス。 |
| `type` | パスの照合タイプ: `Exact`、`Prefix`、または `ImplementationSpecific`。 |
| `serviceBackend(name) { ... }` | Service バックエンドを参照します。 |
| `resourceBackend(name, kind) { ... }` | カスタムリソースバックエンドを参照します。 |

### バックエンド構成

`serviceBackend` を使用する場合、ポートを指定する必要があります。
- `port(Int)`: 数値ポートを使用します。
- `port(String)`: 名前付きポートを使用します。
