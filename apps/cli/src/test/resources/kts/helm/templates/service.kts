/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

service {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
    }

    spec {
        type = Type.LoadBalancer

        ports {
            port("port", 9999) {
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