service {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
    }

    spec {
        type = Type.LoadBalancer

        exists("ports") {
            array("ports") {
                addPort(it.value<String>("name")) {
                    port = it.value<Int>("port")
                    targetPort = it.value<Int>("targetPort")
                    nodePort = it.valueOrNull<Int>("nodePort")
                    protocol = Protocol.SCTP
                    appProtocol = "https"
                }
            }
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