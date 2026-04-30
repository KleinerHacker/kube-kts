# Service DSL

The `service` DSL is used to configure Kubernetes Service resources, which define a logical set of Pods and a policy by which to access them.

## Basic Usage

A minimal Service configuration requires `metadata` and at least one port in the `spec`.

```kotlin
service {
    metadata("my-service") {
        namespace = "default"
    }

    spec {
        type = ServiceSpec.Type.ClusterIP
        addPort("http") {
            port = 80
            targetPort = 8080
        }
    }
}
```

## Detailed Example

Below is a comprehensive example demonstrating various configuration options for a Service.

```kotlin
service {
    metadata("full-service") {
        namespace = "production"
    }

    spec {
        type = ServiceSpec.Type.LoadBalancer
        
        addPort("https") {
            port = 443
            targetPort = 8443
            protocol = PortMappingSpec.Protocol.TCP
            appProtocol = "https"
        }

        // IP Configuration
        addClusterIP("10.0.0.1")
        addIpFamily(ServiceSpec.IPFamily.IPv4)
        ipFamilyPolicy = ServiceSpec.FamilyPolicy.SingleStack

        // External Access
        addExternalIP("1.2.3.4")
        externalTrafficPolicy = ServiceSpec.TrafficPolicy.Local
        
        // Load Balancer Settings
        allocateLoadBalancerNodePorts = true
        loadBalancerClass = "example.com/internal-lb"
        addLoadBalancerSourceRange("192.168.0.0/24")

        // Session Affinity
        sessionAffinity = ServiceSpec.SessionAffinity.ClientIP
        sessionAffinityClientTimeout = 60.seconds.toJavaDuration()

        // Traffic Management
        publishNotReadyAddresses = false
        trafficDistribution = ServiceSpec.TrafficDistribution.PreferClose
    }
}
```

## Configuration Reference

### Metadata (`metadata`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the Service (passed as first argument). |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix to generate a unique name. |

### Service Spec (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `type` | The type of the Service (e.g., `ClusterIP`, `LoadBalancer`). |
| `addPort(name) { ... }` | Adds a port mapping configuration. |
| `addClusterIP(ip)` / `addClusterIPs(vararg ip)` | Sets cluster IP addresses. |
| `addIpFamily(family)` / `addIpFamilies(vararg family)` | Adds IP families (IPv4/IPv6). |
| `ipFamilyPolicy` | Sets the IP family policy. |
| `addExternalIP(ip)` / `addExternalIPs(vararg ip)` | Adds external IP addresses. |
| `externalName` | The external name for `ExternalName` type services. |
| `externalTrafficPolicy` | Traffic policy for external traffic. |
| `internalTrafficPolicy` | Traffic policy for internal traffic. |
| `allocateLoadBalancerNodePorts` | Whether to allocate node ports for LoadBalancer services. |
| `loadBalancerIP` | **Deprecated.** Use implementation-specific annotations instead. |
| `loadBalancerClass` | The class of the load balancer. |
| `addLoadBalancerSourceRange(range)` | Adds allowed CIDR ranges for the load balancer. |
| `sessionAffinity` | The session affinity policy. |
| `sessionAffinityClientTimeout` | Timeout for client IP session affinity. |
| `publishNotReadyAddresses` | Whether to publish addresses of pods that are not ready. |
| `healthCheckNodePort` | The port for health checks (for Type=LoadBalancer). |
| `trafficDistribution` | The traffic distribution policy. |

### Port Mapping (`addPort`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `port` | `Int` | The port that will be exposed by the service. |
| `targetPort` | `Int?` | The port on the pods that the service should forward traffic to. |
| `nodePort` | `Int?` | The port on each node where the service is exposed. |
| `protocol` | `Protocol?` | The IP protocol (TCP, UDP, SCTP). |
| `appProtocol` | `String?` | The application protocol (e.g., http, https). |

## Special Types

- `ServiceSpec.Type`: `ClusterIP`, `NodePort`, `LoadBalancer`, `ExternalName`.
- `PortMappingSpec.Protocol`: `TCP`, `UDP`, `SCTP`.
- `ServiceSpec.IPFamily`: `IPv4`, `IPv6`.
- `ServiceSpec.FamilyPolicy`: `SingleStack`, `PreferDualStack`, `RequireDualStack`.
- `ServiceSpec.TrafficPolicy`: `Cluster`, `Local`.
- `ServiceSpec.SessionAffinity`: `None`, `ClientIP`.
- `ServiceSpec.TrafficDistribution`: `PreferClose`.