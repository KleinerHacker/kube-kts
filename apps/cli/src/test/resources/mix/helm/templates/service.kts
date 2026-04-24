import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

service {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
    }

    spec {
        type = ServiceSpec.Type.LoadBalancer

        addPort("port") {
            port = 9999
            targetPort = 8888
            nodePort = 7777
            protocol = PortMappingSpec.Protocol.SCTP
            appProtocol = "https"
        }

        addClusterIP("clusterIP")

        addIpFamily(ServiceSpec.IPFamily.IPv4)
        addIpFamily(ServiceSpec.IPFamily.IPv6)
        ipFamilyPolicy = ServiceSpec.FamilyPolicy.RequireDualStack

        addExternalIP("externalIP")
        externalName = "externalName"

        externalTrafficPolicy = ServiceSpec.TrafficPolicy.Local
        internalTrafficPolicy = ServiceSpec.TrafficPolicy.Local

        allocateLoadBalancerNodePorts = false
        loadBalancerIP = "loadBalancerIP"
        loadBalancerClass = "loadBalancerClass"
        addLoadBalancerSourceRange("loadBalancerSourceRange")

        sessionAffinity = ServiceSpec.SessionAffinity.None
        sessionAffinityClientTimeout = 30.seconds.toJavaDuration()

        publishNotReadyAddresses = true
        healthCheckNodePort = 3000
        trafficDistribution = ServiceSpec.TrafficDistribution.PreferClose
    }
}