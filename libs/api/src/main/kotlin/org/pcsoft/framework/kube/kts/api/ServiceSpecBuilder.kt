package org.pcsoft.framework.kube.kts.api

import org.pcsoft.framework.kube.kts.api.ServiceSpec.*
import org.pcsoft.framework.kube.kts.api.types.PortSpecBuilder
import kotlin.time.Duration

class ServiceSpecBuilder : ResourceSpecBuilder<ServiceSpec> {
    private val ports = mutableListOf<PortSpecBuilder>()
    private val selector = mutableMapOf<String, String>() //TODO: replace with reference
    private var clusterIP: String? = null
    private var clusterIPs: MutableList<String>? = null
    private var ipFamilies: MutableSet<IPFamily>? = null
    private var externalIPs: MutableList<String>? = null
    private var loadBalancerSourceRanges: MutableList<String>? = null

    var type: Type? = null
    var ipFamilyPolicy: FamilyPolicy? = null
    var externalName: String? = null
    var externalTrafficPolicy: TrafficPolicy? = null
    var internalTrafficPolicy: TrafficPolicy? = null
    var allocateLoadBalancerNodePorts: Boolean? = null
    @Deprecated("This field was under-specified and its meaning varies across implementations. Using it is non-portable and it may not support dual-stack. Users are encouraged to use implementation-specific annotations when available.")
    var loadBalancerIP: String? = null
    var loadBalancerClass: String? = null
    var sessionAffinity: SessionAffinity? = null
    var sessionAffinityClientTimeout: Duration? = null
    var publishNotReadyAddresses: Boolean? = null
    var healthCheckNodePort: Int? = null
    var trafficDistribution: TrafficDistribution? = null

    fun addPort(name: String, prepare: PortSpecBuilder.() -> Unit) {
        val portSpec = PortSpecBuilder(name).apply(prepare)
        ports.add(portSpec)
    }

    fun addClusterIP(clusterIP: String) {
        if (this.clusterIPs == null) {
            this.clusterIPs = mutableListOf()
        }
        this.clusterIPs!!.add(clusterIP)
        this.clusterIP = this.clusterIPs!!.first()
    }

    fun addIpFamily(ipFamily: IPFamily) {
        if (this.ipFamilies == null) {
            this.ipFamilies = mutableSetOf()
        }
        this.ipFamilies!!.add(ipFamily)
    }

    fun addExternalIP(externalIP: String) {
        if (this.externalIPs == null) {
            this.externalIPs = mutableListOf()
        }
        this.externalIPs!!.add(externalIP)
    }

    fun addLoadBalancerSourceRange(loadBalancerSourceRange: String) {
        if (this.loadBalancerSourceRanges == null) {
            this.loadBalancerSourceRanges = mutableListOf()
        }
        this.loadBalancerSourceRanges!!.add(loadBalancerSourceRange)
    }

    @Suppress("DEPRECATION")
    override fun build(): ServiceSpec {
        require(ports.isNotEmpty()) { "At least one port is required" }

        return ServiceSpec(
            type,
            selector,
            ports.map { it.build(type) },
            clusterIP,
            clusterIPs,
            ipFamilies,
            ipFamilyPolicy,
            externalIPs,
            externalName,
            externalTrafficPolicy,
            internalTrafficPolicy,
            allocateLoadBalancerNodePorts,
            loadBalancerIP,
            loadBalancerClass,
            loadBalancerSourceRanges,
            sessionAffinity,
            sessionAffinityClientTimeout?.let {
                SessionAffinityConfig(ClientIPConfig(sessionAffinityClientTimeout!!))
            },
            publishNotReadyAddresses,
            healthCheckNodePort,
            trafficDistribution
        )
    }
}

fun serviceSpec(prepare: ResourceApiBuilder<ServiceSpec, ServiceSpecBuilder>.() -> Unit): ResourceApi<ServiceSpec> =
    ResourceApiBuilder(ServiceSpec.API_VERSION, ServiceSpec.KIND, ServiceSpecBuilder()).apply(prepare).build()