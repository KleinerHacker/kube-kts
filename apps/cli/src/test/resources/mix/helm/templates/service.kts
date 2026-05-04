service {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
    }

    spec {
        type = Type.LoadBalancer

        ports {
            port("port") {
                port = 9999
                targetPort = 8888
                nodePort = 7777
                protocol = Protocol.SCTP
                appProtocol = "https"
            }
        }

        clusterIPs {
            clusterIP("clusterIP")
        }

        ipFamilies {
            ipFamily(IPFamily.IPv4)
            ipFamily(IPFamily.IPv6)
        }
        ipFamilyPolicy = FamilyPolicy.RequireDualStack

        externalIPs {
            externalIP("externalIP")
        }
        externalName = "externalName"

        externalTrafficPolicy = TrafficPolicy.Local
        internalTrafficPolicy = TrafficPolicy.Local

        allocateLoadBalancerNodePorts = false
        loadBalancerIP = "loadBalancerIP"
        loadBalancerClass = "loadBalancerClass"
        loadBalancerSourceRanges {
            loadBalancerSourceRange("loadBalancerSourceRange")
        }

        sessionAffinity = SessionAffinity.None
        sessionAffinityClientTimeout = 30.seconds.toJavaDuration()

        publishNotReadyAddresses = true
        healthCheckNodePort = 3000
        trafficDistribution = TrafficDistribution.PreferClose
    }
}