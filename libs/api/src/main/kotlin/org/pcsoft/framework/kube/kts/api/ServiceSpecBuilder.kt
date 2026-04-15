package org.pcsoft.framework.kube.kts.api

import org.pcsoft.framework.kube.kts.api.types.PortSpec
import org.pcsoft.framework.kube.kts.api.types.PortSpecBuilder

class ServiceSpecBuilder : ResourceSpecBuilder<ServiceSpec> {
    private val ports = mutableListOf<PortSpec>()

    var type: ServiceSpec.Type = ServiceSpec.Type.ClusterIP

    fun addPort(name: String, prepare: PortSpecBuilder.() -> Unit): PortSpec {
        val portSpec = PortSpecBuilder(name).apply(prepare).build()
        ports.add(portSpec)
        return portSpec
    }

    override fun build(): ServiceSpec {
        require(ports.isNotEmpty()) { "At least one port is required" }

        return ServiceSpec(type, ports)
    }
}

fun serviceSpec(prepare: ResourceApiBuilder<ServiceSpec, ServiceSpecBuilder>.() -> Unit): ResourceApi<ServiceSpec> =
    ResourceApiBuilder(ServiceSpec.API_VERSION, ServiceSpec.KIND, ServiceSpecBuilder()).apply(prepare).build()