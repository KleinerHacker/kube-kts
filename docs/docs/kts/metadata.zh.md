# Metadata DSL

`metadata` DSL 用于定义 Kubernetes 资源的元数据。几乎所有 Kubernetes 资源（如 Service、Ingress、Deployment）都包含元数据，用于标识和组织这些资源。

## 基本用法

在 Kube KTS 中，`metadata` 块定义在某个资源内部（例如 `service`、`ingress`）。资源的**名称**作为第一个参数传递给 `metadata` 函数。

### 最小配置

最小配置只需要资源的名称。

```kotlin
service {
    metadata("my-service") {
        // No further properties required
    }
    // ... remaining service configuration
}
```

## 完整配置

下面的示例展示了 `metadata` 块中所有可用的配置选项。

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

## 配置参考

### 属性

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `name` | `String` | 资源的名称（作为 `metadata` 函数的第一个参数传递）。在命名空间内必须唯一。 |
| `namespace` | `String?` | 资源应被创建到的命名空间。默认为 `default`。 |
| `generateName` | `String?` | 名称的前缀。Kubernetes 会通过追加后缀生成唯一的名称。 |

### 方法

| 方法 | 说明 |
| :--- | :--- |
| `labels { label(key, value) }` | 添加用于组织和选择的标签。（替代方法：`addLabel`） |
| `annotations { annotation(key, value) }` | 添加可供工具使用的非标识性元数据。（替代方法：`addAnnotation`） |
| `finalizers { finalizer(name) }` | 添加用于控制资源删除生命周期的 finalizer。（替代方法：`addFinalizer`、`addFinalizers`） |
| `ownerReferences { ownerReference(...) { ... } }` | 定义对其他资源的依赖关系（所有者关系）。（替代方法：`addOwnerReference`） |

### 所有者引用（`ownerReference`）

在 `ownerReferences` 中可以设置以下字段：

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `apiVersion` | `String` | 所有者对象的 API 版本。 |
| `kind` | `String` | 所有者对象的类型。 |
| `name` | `String` | 所有者对象的名称。 |
| `uid` | `UUID` | 所有者对象的唯一 ID（UID）。 |
| `controller` | `Boolean?` | 此引用是否指向管理该对象的控制器。 |
| `blockOwnerDeletion` | `Boolean?` | 在此对象存在期间是否应阻止删除所有者。 |

## 关于元数据

元数据对 Kubernetes 至关重要，因为它：

1. **标识资源**：通过 `name` 和 `namespace`。
2. **分组资源**：通过 `labels`，可以高效地筛选和选择资源（例如供 Service 或 NetworkPolicy 使用）。
3. **支持可扩展性**：通过 `annotations`，可以为控制器或外部工具存储额外信息。
4. **管理生命周期**：通过 `finalizers` 和 `ownerReferences`，资源会以正确的顺序被干净地删除。

在 Kube KTS 中，必须在每个模板（`service`、`ingress` 等）中设置元数据，才能生成有效的 Kubernetes 资源。
