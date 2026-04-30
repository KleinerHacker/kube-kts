# Service

The following page describes how to use KTS to configure Service.

## Example

This is a maximal example of Service configuration:

```kotlin
service {
    metadata("metadata") {
        ...
    }

    spec {
        type = Type.LoadBalancer

        addPort("port") {
            port = 9999
            targetPort = 8888
            nodePort = 7777
            protocol = Protocol.SCTP
            appProtocol = "https"
        }

        addClusterIP("clusterIP")

        addIpFamily(IPFamily.IPv4)
        addIpFamily(IPFamily.IPv6)
        ipFamilyPolicy = FamilyPolicy.RequireDualStack

        addExternalIP("externalIP")
        externalName = "externalName"

        externalTrafficPolicy = TrafficPolicy.Local
        internalTrafficPolicy = TrafficPolicy.Local

        allocateLoadBalancerNodePorts = false
        loadBalancerIP = "loadBalancerIP"
        loadBalancerClass = "loadBalancerClass"
        addLoadBalancerSourceRange("loadBalancerSourceRange")

        sessionAffinity = SessionAffinity.None
        sessionAffinityClientTimeout = 30.seconds.toJavaDuration()

        publishNotReadyAddresses = true
        healthCheckNodePort = 3000
        trafficDistribution = TrafficDistribution.PreferClose
    }
}
```

### Special Types

- `Protocol`: Protocol to use for the port. Allows `TCP`, `UDP`, `SCTP`.
- `Type`: Type of the Service. Allows `Cluster`, `NodePort`, `LoadBalancer`, `ExternalName`.
- `IPFamily`: IP family for the Service. Allows `IPv4`, `IPv6`.
- `FamilyPolicy`: IP family policy for the Service. Allows `SingleStack`, `PreferDualStack`, `RequireDualStack`.
- `TrafficPolicy`: Traffic policy for the Service. Allows `Cluster`, `Local`.
- `SessionAffinity`: Session affinity policy for the Service. Allows `None`, `ClientIP`.
- `TrafficDistribution`: Traffic distribution policy for the Service. Allows `None`, `SourceIP`, `PreferClose`.