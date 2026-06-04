# Ingress DSL

The `ingress` DSL is used to configure Kubernetes Ingress resources, which manage external access to services in a cluster, typically over HTTP.

!!! warning "Security: Import Restrictions"
    By default, KTS scripts **do not allow** `import` statements or fully qualified class names
    (e.g. `java.lang.Runtime`). Only types provided via the pre-configured default imports may
    be used.

    Use the `--unsafe` flag to lift these restrictions.

## Basic Usage

A minimal Ingress configuration requires `metadata` and at least one rule in `spec`.

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

## Detailed Example

The following is a comprehensive example showing various configuration options, including TLS and multiple rules.

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

## Configuration Reference

### Metadata (`metadata`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the Ingress resource (passed as the first argument). |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix for generating a unique name. |

### Ingress Specification (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `ingressClassName` | Name of the IngressClass cluster resource. |
| `defaultServiceBackend(name) { ... }` | Sets the default backend for a service. |
| `defaultResourceBackend(name, kind) { ... }` | Sets the default backend for a custom resource. |
| `tlsList { tls { ... } }` | Adds a TLS configuration block. (Alternative: `addTls`) |
| `rules { rule { ... } }` | Adds an Ingress rule. (Alternative: `addRule`) |

### TLS Configuration (`tls`)

| Property / Method | Description |
| :--- | :--- |
| `secretName` | The name of the Secret containing the TLS certificate and key. |
| `hosts { host(String) }` | Adds a host to include in the TLS certificate. (Alternative: `addHost`) |

### Rules (`rule`)

| Property / Method | Description |
| :--- | :--- |
| `host` | The fully qualified domain name of a network host. |
| `httpPaths { httpPath(type) { ... } }` | Adds an HTTP path to the rule. (Alternative: `addHttpPath`) |

### HTTP Path (`httpPath`)

| Property / Method | Description |
| :--- | :--- |
| `path` | The path matched against the path of an incoming request. |
| `type` | The type of path matching: `Exact`, `Prefix`, or `ImplementationSpecific`. |
| `serviceBackend(name) { ... }` | References a Service backend. |
| `resourceBackend(name, kind) { ... }` | References a custom resource backend. |

### Backend Configuration

When using `serviceBackend`, the port must be specified:
- `port(Int)`: Uses a numeric port.
- `port(String)`: Uses a named port.
