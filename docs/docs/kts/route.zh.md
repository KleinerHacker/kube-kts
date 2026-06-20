# Route DSL

`route` DSL 用于配置 **OpenShift Route** 资源，它通过外部主机名暴露服务，并可在 OpenShift 路由器上终止 TLS。

!!! danger "并非标准 Kubernetes 资源"
    `Route`（`apiVersion: route.openshift.io/v1`）**不是**标准 Kubernetes 的一部分。它是
    **OpenShift / OKD**（或附带 OpenShift 路由器的兼容发行版）专有的资源，无法在原生 Kubernetes 集群上工作。

    在标准 Kubernetes 上请改用 [Ingress DSL](ingress.md)。

!!! warning "安全：导入限制"
    默认情况下，KTS 脚本**不允许** `import` 语句或完全限定的类名（例如 `java.lang.Runtime`）。
    只能使用通过预配置的默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

## 基本用法

最小的 Route 需要 `metadata` 以及 `spec` 中的后端（`to`）。

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

## 详细示例

以下是一个综合示例，展示基于路径的路由、通过备用后端进行的加权流量分配以及 edge TLS 终止。

```kotlin
route {
    metadata("full-route") {
        namespace = "production"
    }

    spec {
        host = "www.example.com"
        path = "/app"

        // 主后端
        to("main-service") {
            weight = 100
        }

        // 加权流量分配（例如金丝雀发布）
        alternateBackends {
            backend("canary-service") {
                weight = 20
            }
        }

        // 后端服务上的目标端口（数字或名称）
        port(8080)

        // Edge TLS 终止，将不安全流量重定向到 HTTPS
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

## 配置参考

### 元数据（`metadata`）

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `name` | `String` | Route 资源的名称（作为第一个参数传入）。 |
| `namespace` | `String?` | 资源的命名空间。 |
| `generateName` | `String?` | 用于生成唯一名称的可选前缀。 |

### Route 规约（`spec`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `host` | Route 发布所用的外部可达主机名。 |
| `path` | 基于路径路由的可选路径。 |
| `to(name) { ... }` | Route 引导流量的主后端（一个 Service）。 |
| `addAlternateBackend(name) { ... }` | 添加单个备用后端以进行加权分配。 |
| `alternateBackends { backend(name) { ... } }` | 添加多个备用后端。 |
| `port(Int)` / `port(String)` | 后端服务上的目标端口（数字或名称）。 |
| `tls(termination) { ... }` | 配置 TLS 终止。 |
| `wildcardPolicy` | `None` 或 `Subdomain`。 |

### 后端（`to` / `backend`）

| 属性 | 说明 |
| :--- | :--- |
| `kind` | 被引用对象的种类。默认为 `Service`。 |
| `weight` | 用于加权流量分配的可选相对权重（0-256）。 |

### TLS 配置（`tls`）

| 属性 | 说明 |
| :--- | :--- |
| `termination` | TLS 终止类型：`Edge`、`Passthrough` 或 `Reencrypt`。 |
| `insecureEdgeTerminationPolicy` | 不安全 HTTP 流量的行为：`None`、`Allow` 或 `Redirect`。 |
| `key` | PEM 编码的私钥（edge/reencrypt）。 |
| `certificate` | PEM 编码的证书（edge/reencrypt）。 |
| `caCertificate` | PEM 编码的 CA 证书链（edge/reencrypt）。 |
| `destinationCACertificate` | 用于验证后端的 PEM 编码 CA 证书（仅 reencrypt）。 |
