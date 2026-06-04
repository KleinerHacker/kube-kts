# Service DSL

The `service` DSL is used to configure Kubernetes Service resources, which define a logical set of Pods and a policy for accessing them.

!!! warning "Security: Import Restrictions"
    By default, KTS scripts **do not allow** `import` statements or fully qualified class names
    (e.g. `java.lang.Runtime`). Only types provided via the pre-configured default imports may
    be used.

    Use the `--unsafe` flag to lift these restrictions.

## Basic Usage

A minimal Service configuration requires `metadata` and at least one port in `spec`.

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

## Detailed Example

The following is a comprehensive example showing various configuration options for a Service.

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

## Configuration Reference

### Metadata (`metadata`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the Service (passed as the first argument). |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix for generating a unique name. |

### Service Specification (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `type` | The type of Service (e.g. `ClusterIP`, `LoadBalancer`). |
| `ports { port(name) { ... } }` | Adds a port mapping configuration. (Alternative: `addPort`) |
| `clusterIPs { clusterIP(ip) }` | Sets cluster IP addresses. (Alternative: `addClusterIP`, `addClusterIPs`) |
| `ipFamilies { ipFamily(family) }` | Adds IP families (IPv4/IPv6). (Alternative: `addIpFamily`, `addIpFamilies`) |
| `ipFamilyPolicy` | Sets the IP family policy. |
| `externalIPs { externalIP(ip) }` | Adds external IP addresses. (Alternative: `addExternalIP`, `addExternalIPs`) |
| `externalName` | The external name for Services of type `ExternalName`. |
| `externalTrafficPolicy` | Traffic policy for external traffic. |
| `internalTrafficPolicy` | Traffic policy for internal traffic. |
| `allocateLoadBalancerNodePorts` | Whether to allocate node ports for LoadBalancer Services. |
| `loadBalancerIP` | **Deprecated.** Use implementation-specific annotations instead. |
| `loadBalancerClass` | The class of the load balancer. |
| `loadBalancerSourceRanges { loadBalancerSourceRange(range) }` | Adds allowed CIDR ranges for the load balancer. (Alternative: `addLoadBalancerSourceRange`) |
| `sessionAffinity` | The session affinity policy. |
| `sessionAffinityClientTimeout` | Timeout for client-IP-based session affinity. |
| `publishNotReadyAddresses` | Whether to publish addresses of Pods that are not ready. |
| `healthCheckNodePort` | The port for health checks (for Type=LoadBalancer). |
| `trafficDistribution` | The traffic distribution policy. |

### Port Mapping (`port`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `port` | `Int` | The port exposed by the Service. |
| `targetPort` | `Int?` | The port on the Pods to which the Service forwards traffic. |
| `nodePort` | `Int?` | The port on each node at which the Service is exposed. |
| `protocol` | `Protocol?` | The IP protocol (TCP, UDP, SCTP). |
| `appProtocol` | `String?` | The application protocol (e.g. http, https). |

## Special Types

- `ServiceSpec.Type`: `ClusterIP`, `NodePort`, `LoadBalancer`, `ExternalName`.
- `PortMappingSpec.Protocol`: `TCP`, `UDP`, `SCTP`.
- `ServiceSpec.IPFamily`: `IPv4`, `IPv6`.
- `ServiceSpec.FamilyPolicy`: `SingleStack`, `PreferDualStack`, `RequireDualStack`.
- `ServiceSpec.TrafficPolicy`: `Cluster`, `Local`.
- `ServiceSpec.SessionAffinity`: `None`, `ClientIP`.
- `ServiceSpec.TrafficDistribution`: `PreferClose`.
