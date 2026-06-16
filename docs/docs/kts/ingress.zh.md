# Ingress DSL

`ingress` DSL 用于配置 Kubernetes Ingress 资源，它管理对集群中服务的外部访问，通常通过 HTTP 进行。

!!! warning "安全性：导入限制"
    默认情况下，KTS 脚本**不允许**使用 `import` 语句或完全限定的类名
    （例如 `java.lang.Runtime`）。只能使用通过预配置默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

## 基本用法

最小的 Ingress 配置需要 `metadata` 以及 `spec` 中至少一条规则。

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

## 详细示例

下面是一个完整示例，展示了各种配置选项，包括 TLS 和多条规则。

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

## 配置参考

### 元数据（`metadata`）

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `name` | `String` | Ingress 资源的名称（作为第一个参数传递）。 |
| `namespace` | `String?` | 资源的命名空间。 |
| `generateName` | `String?` | 用于生成唯一名称的可选前缀。 |

### Ingress 规约（`spec`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `ingressClassName` | IngressClass 集群资源的名称。 |
| `defaultServiceBackend(name) { ... }` | 设置某个 Service 的默认后端。 |
| `defaultResourceBackend(name, kind) { ... }` | 设置某个自定义资源的默认后端。 |
| `tlsList { tls { ... } }` | 添加一个 TLS 配置块。（替代方法：`addTls`） |
| `rules { rule { ... } }` | 添加一条 Ingress 规则。（替代方法：`addRule`） |

### TLS 配置（`tls`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `secretName` | 包含 TLS 证书和密钥的 Secret 名称。 |
| `hosts { host(String) }` | 添加要包含在 TLS 证书中的主机。（替代方法：`addHost`） |

### 规则（`rule`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `host` | 网络主机的完全限定域名。 |
| `httpPaths { httpPath(type) { ... } }` | 向规则添加一个 HTTP 路径。（替代方法：`addHttpPath`） |

### HTTP 路径（`httpPath`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `path` | 用于与传入请求路径匹配的路径。 |
| `type` | 路径匹配类型：`Exact`、`Prefix` 或 `ImplementationSpecific`。 |
| `serviceBackend(name) { ... }` | 引用一个 Service 后端。 |
| `resourceBackend(name, kind) { ... }` | 引用一个自定义资源后端。 |

### 后端配置

使用 `serviceBackend` 时，必须指定端口：
- `port(Int)`：使用数字端口。
- `port(String)`：使用命名端口。
