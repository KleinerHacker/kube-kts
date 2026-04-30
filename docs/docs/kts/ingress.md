# Ingress DSL

The `ingress` DSL is used to configure Kubernetes Ingress resources, which manage external access to services in a cluster, typically HTTP.

## Basic Usage

A minimal Ingress configuration requires `metadata` and at least one rule in the `spec`.

```kotlin
ingress {
    metadata("my-ingress") {
        namespace = "default"
    }

    spec {
        addRule {
            host = "example.com"
            addHttpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                path = "/"
                serviceBackend("my-service") {
                    port(80)
                }
            }
        }
    }
}
```

## Detailed Example

Below is a comprehensive example demonstrating various configuration options, including TLS and multiple rules.

```kotlin
ingress {
    metadata("full-ingress") {
        namespace = "production"
    }

    spec {
        ingressClassName = "nginx"

        // Default backend if no rules match
        defaultServiceBackend("default-service") {
            port(8080)
        }

        // TLS Configuration
        addTls {
            secretName = "example-tls-secret"
            addHost("example.com")
            addHost("api.example.com")
        }

        // Rule for main website
        addRule {
            host = "example.com"
            addHttpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
                path = "/home"
                serviceBackend("web-service") {
                    port("http") // Reference port by name
                }
            }
        }

        // Rule for API with multiple paths
        addRule {
            host = "api.example.com"
            addHttpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                path = "/v1"
                serviceBackend("api-v1-service") {
                    port(8081)
                }
            }
            addHttpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                path = "/v2"
                serviceBackend("api-v2-service") {
                    port(8082)
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
| `name` | `String` | The name of the Ingress resource (passed as first argument). |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix to generate a unique name. |

### Ingress Spec (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `ingressClassName` | Name of the IngressClass cluster resource. |
| `defaultServiceBackend(name) { ... }` | Sets the default backend for a service. |
| `defaultResourceBackend(name, kind) { ... }` | Sets the default backend for a custom resource. |
| `addTls { ... }` | Adds a TLS configuration block. |
| `addRule { ... }` | Adds an Ingress rule. |

### TLS Configuration (`addTls`)

| Property / Method | Description |
| :--- | :--- |
| `secretName` | The name of the secret containing the TLS certificate and key. |
| `addHost(String)` | Adds a host to be included in the TLS certificate. |

### Rules (`addRule`)

| Property / Method | Description |
| :--- | :--- |
| `host` | The fully qualified domain name of a network host. |
| `addHttpPath(type) { ... }` | Adds an HTTP path to the rule. |

### HTTP Path (`addHttpPath`)

| Property / Method | Description |
| :--- | :--- |
| `path` | The path which is matched against the path of an incoming request. |
| `type` | The type of path matching: `Exact`, `Prefix`, or `ImplementationSpecific`. |
| `serviceBackend(name) { ... }` | Points to a service backend. |
| `resourceBackend(name, kind) { ... }` | Points to a custom resource backend. |

### Backend Configuration

When using `serviceBackend`, you must specify the port:
- `port(Int)`: Use a numeric port.
- `port(String)`: Use a named port.