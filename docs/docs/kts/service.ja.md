# Service DSL

`service` DSL は、Pod の論理的な集合とそれらにアクセスするためのポリシーを定義する Kubernetes Service リソースを構成するために使用します。

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名
    （例: `java.lang.Runtime`）を**許可しません**。事前構成されたデフォルトインポートで
    提供される型のみ使用できます。

    `--unsafe` フラグを使うとこれらの制限を解除できます。

## 基本的な使い方

最小限の Service 構成には、`metadata` と `spec` 内の少なくとも 1 つのポートが必要です。

```kotlin
service {
    metadata("my-service") {
        namespace = "default"
    }

    spec {
        type = ServiceSpec.Type.ClusterIP
        ports {
            port("http") {
                port = 80
                targetPort = 8080
            }
        }
    }
}
```

## 詳細な例

以下は、Service のさまざまな設定オプションを示す包括的な例です。

```kotlin
service {
    metadata("full-service") {
        namespace = "production"
    }

    spec {
        type = ServiceSpec.Type.LoadBalancer
        
        ports {
            port("https") {
                port = 443
                targetPort = 8443
                protocol = PortMappingSpec.Protocol.TCP
                appProtocol = "https"
            }
        }

        // IP configuration
        clusterIPs {
            clusterIP("10.0.0.1")
        }
        ipFamilies {
            ipFamily(ServiceSpec.IPFamily.IPv4)
        }
        ipFamilyPolicy = ServiceSpec.FamilyPolicy.SingleStack

        // External access
        externalIPs {
            externalIP("1.2.3.4")
        }
        externalTrafficPolicy = ServiceSpec.TrafficPolicy.Local
        
        // Load balancer settings
        allocateLoadBalancerNodePorts = true
        loadBalancerClass = "example.com/internal-lb"
        loadBalancerSourceRanges {
            loadBalancerSourceRange("192.168.0.0/24")
        }

        // Session affinity
        sessionAffinity = ServiceSpec.SessionAffinity.ClientIP
        sessionAffinityClientTimeout = 60.seconds.toJavaDuration()

        // Traffic management
        publishNotReadyAddresses = false
        trafficDistribution = ServiceSpec.TrafficDistribution.PreferClose
    }
}
```

## 設定リファレンス

### メタデータ（`metadata`）

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | `String` | Service の名前（最初の引数として渡される）。 |
| `namespace` | `String?` | リソースの名前空間。 |
| `generateName` | `String?` | 一意の名前を生成するための任意のプレフィックス。 |

### Service の仕様（`spec`）

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `type` | Service の種類（例: `ClusterIP`、`LoadBalancer`）。 |
| `ports { port(name) { ... } }` | ポートマッピング構成を追加します。（代替: `addPort`） |
| `clusterIPs { clusterIP(ip) }` | クラスター IP アドレスを設定します。（代替: `addClusterIP`、`addClusterIPs`） |
| `ipFamilies { ipFamily(family) }` | IP ファミリー（IPv4/IPv6）を追加します。（代替: `addIpFamily`、`addIpFamilies`） |
| `ipFamilyPolicy` | IP ファミリーポリシーを設定します。 |
| `externalIPs { externalIP(ip) }` | 外部 IP アドレスを追加します。（代替: `addExternalIP`、`addExternalIPs`） |
| `externalName` | `ExternalName` タイプの Service の外部名。 |
| `externalTrafficPolicy` | 外部トラフィックのトラフィックポリシー。 |
| `internalTrafficPolicy` | 内部トラフィックのトラフィックポリシー。 |
| `allocateLoadBalancerNodePorts` | LoadBalancer タイプの Service にノードポートを割り当てるかどうか。 |
| `loadBalancerIP` | **非推奨。** 代わりに実装固有のアノテーションを使用してください。 |
| `loadBalancerClass` | ロードバランサーのクラス。 |
| `loadBalancerSourceRanges { loadBalancerSourceRange(range) }` | ロードバランサーで許可される CIDR 範囲を追加します。（代替: `addLoadBalancerSourceRange`） |
| `sessionAffinity` | セッションアフィニティポリシー。 |
| `sessionAffinityClientTimeout` | クライアント IP ベースのセッションアフィニティのタイムアウト。 |
| `publishNotReadyAddresses` | 準備ができていない Pod のアドレスを公開するかどうか。 |
| `healthCheckNodePort` | ヘルスチェック用のポート（Type=LoadBalancer の場合）。 |
| `trafficDistribution` | トラフィック分散ポリシー。 |

### ポートマッピング（`port`）

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `port` | `Int` | Service が公開するポート。 |
| `targetPort` | `Int?` | Service がトラフィックを転送する Pod のポート。 |
| `nodePort` | `Int?` | 各ノードで Service が公開されるポート。 |
| `protocol` | `Protocol?` | IP プロトコル（TCP、UDP、SCTP）。 |
| `appProtocol` | `String?` | アプリケーションプロトコル（例: http、https）。 |

## 特殊な型

- `ServiceSpec.Type`: `ClusterIP`、`NodePort`、`LoadBalancer`、`ExternalName`。
- `PortMappingSpec.Protocol`: `TCP`、`UDP`、`SCTP`。
- `ServiceSpec.IPFamily`: `IPv4`、`IPv6`。
- `ServiceSpec.FamilyPolicy`: `SingleStack`、`PreferDualStack`、`RequireDualStack`。
- `ServiceSpec.TrafficPolicy`: `Cluster`、`Local`。
- `ServiceSpec.SessionAffinity`: `None`、`ClientIP`。
- `ServiceSpec.TrafficDistribution`: `PreferClose`。
