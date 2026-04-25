package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec.*
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortMappingSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpecBuilder
import java.time.Duration

class ServiceSpecBuilder internal constructor() : ResourceSpecBuilder<ServiceSpec> {
    private val ports = mutableListOf<PortMappingSpecBuilder>()
    private val selector: Map<String, String>? = null //TODO: replace with reference
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

    fun addPort(name: String, prepare: PortMappingSpecBuilder.() -> Unit) {
        val portSpec = PortMappingSpecBuilder(name).apply(prepare)
        ports.add(portSpec)
    }

    fun addClusterIP(clusterIP: String) {
        if (this.clusterIPs == null) {
            this.clusterIPs = mutableListOf()
        }
        this.clusterIPs!!.add(clusterIP)
        this.clusterIP = this.clusterIPs!!.first()
    }

    fun addClusterIPs(vararg clusterIPs: String) {
        if (this.clusterIPs == null) {
            this.clusterIPs = mutableListOf()
        }
        this.clusterIPs!!.addAll(clusterIPs.toList())
    }

    fun addIpFamily(ipFamily: IPFamily) {
        if (this.ipFamilies == null) {
            this.ipFamilies = mutableSetOf()
        }
        this.ipFamilies!!.add(ipFamily)
    }

    fun addIpFamilies(vararg ipFamilies: IPFamily) {
        if (this.ipFamilies == null) {
            this.ipFamilies = mutableSetOf()
        }
        this.ipFamilies!!.addAll(ipFamilies.toSet())
    }

    fun addAllIpFamilies() {
        if (this.ipFamilies == null) {
            this.ipFamilies = mutableSetOf()
        }
        this.ipFamilies!!.addAll(IPFamily.entries.toSet())
    }

    fun addExternalIP(externalIP: String) {
        if (this.externalIPs == null) {
            this.externalIPs = mutableListOf()
        }
        this.externalIPs!!.add(externalIP)
    }

    fun addExternalIPs(vararg externalIPs: String) {
        if (this.externalIPs == null) {
            this.externalIPs = mutableListOf()
        }
        this.externalIPs!!.addAll(externalIPs.toList())
    }

    fun addLoadBalancerSourceRange(loadBalancerSourceRange: String) {
        if (this.loadBalancerSourceRanges == null) {
            this.loadBalancerSourceRanges = mutableListOf()
        }
        this.loadBalancerSourceRanges!!.add(loadBalancerSourceRange)
    }

    fun addLoadBalancerSourceRanges(vararg loadBalancerSourceRanges: String) {
        if (this.loadBalancerSourceRanges == null) {
            this.loadBalancerSourceRanges = mutableListOf()
        }
        this.loadBalancerSourceRanges!!.addAll(loadBalancerSourceRanges.toList())
    }

    @Suppress("DEPRECATION")
    override fun build(): ServiceSpec {
        require(ports.isNotEmpty()) { "At least one port is required at ${ServiceSpec::class.simpleName}" }

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

fun service(prepare: TemplateSpecBuilder<ServiceSpec, ServiceSpecBuilder>.() -> Unit): TemplateSpec<ServiceSpec> =
    TemplateSpecBuilder(ServiceSpec.API_VERSION, ServiceSpec.KIND, ServiceSpecBuilder())
        .apply(prepare)
        .build()