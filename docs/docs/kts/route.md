# Route DSL

The `route` DSL is used to configure **OpenShift Route** resources, which expose a service under an
external host name and optionally terminate TLS at the OpenShift router.

!!! danger "Not a standard Kubernetes resource"
    `Route` (`apiVersion: route.openshift.io/v1`) is **not** part of standard Kubernetes. It is
    specific to **OpenShift / OKD** (or compatible distributions that ship the OpenShift router) and
    will not work on a vanilla Kubernetes cluster.

    On standard Kubernetes use the [Ingress DSL](ingress.md) instead.

!!! warning "Security: Import Restrictions"
    By default, KTS scripts **do not allow** `import` statements or fully qualified class names
    (e.g. `java.lang.Runtime`). Only types provided via the pre-configured default imports may
    be used.

    Use the `--unsafe` flag to lift these restrictions.

## Basic Usage

A minimal Route requires `metadata` and a backend (`to`) in `spec`.

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

## Detailed Example

The following is a comprehensive example showing path-based routing, weighted traffic splitting via
alternate backends, and edge TLS termination.

```kotlin
route {
    metadata("full-route") {
        namespace = "production"
    }

    spec {
        host = "www.example.com"
        path = "/app"

        // Primary backend
        to("main-service") {
            weight = 100
        }

        // Weighted traffic splitting (e.g. canary deployments)
        alternateBackends {
            backend("canary-service") {
                weight = 20
            }
        }

        // Target port on the backing service (numeric or named)
        port(8080)

        // Edge TLS termination, redirecting insecure traffic to HTTPS
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

## Configuration Reference

### Metadata (`metadata`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the Route resource (passed as the first argument). |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix for generating a unique name. |

### Route Specification (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `host` | The externally reachable host name the Route is published under. |
| `path` | An optional path for path-based routing. |
| `to(name) { ... }` | The primary backend (a Service) the Route directs traffic to. |
| `addAlternateBackend(name) { ... }` | Adds a single alternate backend for weighted splitting. |
| `alternateBackends { backend(name) { ... } }` | Adds multiple alternate backends. |
| `port(Int)` / `port(String)` | The target port on the backing service (numeric or named). |
| `tls(termination) { ... }` | Configures TLS termination. |
| `wildcardPolicy` | `None` or `Subdomain`. |

### Backend (`to` / `backend`)

| Property | Description |
| :--- | :--- |
| `kind` | The kind of the referenced object. Defaults to `Service`. |
| `weight` | An optional relative weight (0-256) for weighted traffic splitting. |

### TLS Configuration (`tls`)

| Property | Description |
| :--- | :--- |
| `termination` | The TLS termination type: `Edge`, `Passthrough` or `Reencrypt`. |
| `insecureEdgeTerminationPolicy` | Behaviour for insecure HTTP traffic: `None`, `Allow` or `Redirect`. |
| `key` | The PEM encoded private key (edge/reencrypt). |
| `certificate` | The PEM encoded certificate (edge/reencrypt). |
| `caCertificate` | The PEM encoded CA certificate chain (edge/reencrypt). |
| `destinationCACertificate` | The PEM encoded CA certificate validating the backend (reencrypt only). |
