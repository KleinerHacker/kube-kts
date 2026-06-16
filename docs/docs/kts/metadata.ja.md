# Metadata DSL

`metadata` DSL は、Kubernetes リソースのメタデータを定義するために使用します。メタデータはほぼすべての Kubernetes リソース（Service、Ingress、Deployment など）に存在し、それらを識別・整理するために役立ちます。

## 基本的な使い方

Kube KTS では、`metadata` ブロックはリソース内（例: `service`、`ingress`）で定義します。リソースの**名前**は `metadata` 関数の最初の引数として渡します。

### 最小構成

最小構成では、リソースの名前のみが必要です。

```kotlin
service {
    metadata("my-service") {
        // No further properties required
    }
    // ... remaining service configuration
}
```

## 完全な構成

以下は、`metadata` ブロック内で利用可能なすべての設定オプションを示す例です。

```kotlin
ingress {
    metadata("full-metadata-example") {
        namespace = "production"
        generateName = "app-prefix-"
        
        labels {
            label("app", "my-app")
            label("env", "prod")
        }
        
        annotations {
            annotation("cert-manager.io/cluster-issuer", "letsencrypt-prod")
            annotation("description", "Managed by Kube KTS")
        }
        
        finalizers {
            finalizer("kubernetes.io/pvc-protection")
        }
        
        ownerReferences {
            ownerReference("apps/v1", "Deployment", "parent-deploy", UUID.fromString("550e8400-e29b-11d4-a716-446655440000")) {
                controller = true
                blockOwnerDeletion = true
            }
        }
    }
    // ... remaining ingress configuration
}
```

## 設定リファレンス

### プロパティ

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | `String` | リソースの名前（`metadata` 関数の最初の引数として渡される）。名前空間内で一意である必要があります。 |
| `namespace` | `String?` | リソースを作成する名前空間。デフォルトは `default`。 |
| `generateName` | `String?` | 名前のプレフィックス。Kubernetes はサフィックスを付加して一意の名前を生成します。 |

### メソッド

| メソッド | 説明 |
| :--- | :--- |
| `labels { label(key, value) }` | 整理と選択のためのラベルを追加します。（代替: `addLabel`） |
| `annotations { annotation(key, value) }` | ツールが利用できる非識別メタデータを追加します。（代替: `addAnnotation`） |
| `finalizers { finalizer(name) }` | リソースの削除ライフサイクルを制御するファイナライザーを追加します。（代替: `addFinalizer`、`addFinalizers`） |
| `ownerReferences { ownerReference(...) { ... } }` | 他のリソースへの依存関係（所有者関係）を定義します。（代替: `addOwnerReference`） |

### 所有者参照（`ownerReference`）

`ownerReferences` 内では以下のフィールドを設定できます。

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `apiVersion` | `String` | 所有者オブジェクトの API バージョン。 |
| `kind` | `String` | 所有者オブジェクトの種類。 |
| `name` | `String` | 所有者オブジェクトの名前。 |
| `uid` | `UUID` | 所有者オブジェクトの一意の ID（UID）。 |
| `controller` | `Boolean?` | この参照がオブジェクトを管理するコントローラーを指すかどうか。 |
| `blockOwnerDeletion` | `Boolean?` | このオブジェクトが存在する間、所有者の削除をブロックするかどうか。 |

## メタデータについて

メタデータは次の理由で Kubernetes にとって不可欠です。

1. **リソースを識別する**: `name` と `namespace` を通じて。
2. **リソースをグループ化する**: `labels` により、リソースを効率的にフィルタリング・選択できます（例: Service や NetworkPolicy による）。
3. **拡張性を可能にする**: `annotations` により、コントローラーや外部ツール向けに追加情報を格納できます。
4. **ライフサイクルを管理する**: `finalizers` と `ownerReferences` により、リソースは正しい順序でクリーンに削除されます。

Kube KTS では、有効な Kubernetes リソースを生成するために、すべてのテンプレート（`service`、`ingress` など）でメタデータを設定する必要があります。
