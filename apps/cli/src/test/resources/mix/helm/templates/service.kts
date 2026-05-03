service {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
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

        if (valueOrNull<Boolean>("service.ip.useIpV6") == true) {
            addIpFamily(IPFamily.IPv6)
        } else {
            addIpFamily(IPFamily.IPv4)
        }
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