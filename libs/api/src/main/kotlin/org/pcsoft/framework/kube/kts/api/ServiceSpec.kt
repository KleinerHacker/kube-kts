package org.pcsoft.framework.kube.kts.api

import org.pcsoft.framework.kube.kts.api.types.DefaultMetadataSpecBuilder
import org.pcsoft.framework.kube.kts.api.types.MetadataSpec
import org.pcsoft.framework.kube.kts.api.types.PortSpec
import org.pcsoft.framework.kube.kts.api.types.PortSpecBuilder

@ResourceSpecHeader("v1", "Service")
class ServiceSpec(metadata: MetadataSpec, type: Type, ports: List<PortSpec>) : ResourceSpec<MetadataSpec>(metadata) {
    val spec = Spec(type, ports)

    data class Spec(val type: Type, val ports: List<PortSpec>)

    @Suppress("unused")
    enum class Type {
        ClusterIP, NodePort, LoadBalancer, ExternalName
    }
}

class ServiceSpecBuilder {
    private lateinit var metadataSpec: MetadataSpec
    private val ports = mutableListOf<PortSpec>()

    var type: ServiceSpec.Type = ServiceSpec.Type.ClusterIP

    fun metadata(prepare: DefaultMetadataSpecBuilder.() -> Unit) : MetadataSpec {
        metadataSpec = DefaultMetadataSpecBuilder().apply(prepare).build()
        return metadataSpec
    }

    fun addPort(name: String, prepare: PortSpecBuilder.() -> Unit) : PortSpec {
        val portSpec = PortSpecBuilder(name).apply(prepare).build()
        ports.add(portSpec)
        return portSpec
    }

    internal fun build(): ServiceSpec {
        require(::metadataSpec.isInitialized) { "Metadata is required" }
        require(ports.isNotEmpty()) { "At least one port is required" }

        return ServiceSpec(metadataSpec, type, ports)
    }
}

fun serviceSpec(prepare: ServiceSpecBuilder.() -> Unit) : ServiceSpec =
    ServiceSpecBuilder().apply(prepare).build()