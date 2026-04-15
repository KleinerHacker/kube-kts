package org.pcsoft.framework.kube.kts.api

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.types.PortSpec
import kotlin.time.Duration

@NoArgs
class ServiceSpec(
    val type: Type?,
    val selector: Map<String, String>?, //TODO: replace with reference
    val ports: List<PortSpec>,
    val clusterIP: String?,
    val clusterIPs: List<String>?,
    val ipFamilies: Set<IPFamily>?,
    val ipFamilyPolicy: FamilyPolicy?,
    val externalIPs: List<String>?,
    val externalName: String?,
    val externalTrafficPolicy: TrafficPolicy?,
    val internalTrafficPolicy: TrafficPolicy?,
    val allocateLoadBalancerNodePorts: Boolean?,
    @Deprecated("This field was under-specified and its meaning varies across implementations. Using it is non-portable and it may not support dual-stack. Users are encouraged to use implementation-specific annotations when available.")
    val loadBalancerIP: String?,
    val loadBalancerClass: String?,
    val loadBalancerSourceRanges: List<String>?,
    val sessionAffinity: SessionAffinity?,
    val sessionAffinityConfig: SessionAffinityConfig?,
    val publishNotReadyAddresses: Boolean?,
    val healthCheckNodePort: Int?,
    val trafficDistribution: TrafficDistribution?
) : ResourceSpec {
    companion object {
        const val KIND = "Service"
        const val API_VERSION = "v1"
    }

    @Suppress("unused")
    enum class Type {
        ClusterIP, NodePort, LoadBalancer, ExternalName
    }

    @Suppress("unused")
    enum class FamilyPolicy {
        SingleStack, PreferDualStack, RequireDualStack
    }

    @Suppress("unused")
    enum class TrafficPolicy {
        Cluster, Local
    }

    @Suppress("unused")
    enum class IPFamily {
        IPv4, IPv6
    }

    @Suppress("unused")
    enum class SessionAffinity {
        None, ClientIP
    }

    @Suppress("unused")
    enum class TrafficDistribution {
        PreferClose
    }

    data class SessionAffinityConfig(val clientIP: ClientIPConfig)

    data class ClientIPConfig(val timeoutSeconds: Duration)
}