package org.pcsoft.framework.kube.kts.api.chart.resources.types

sealed class BackendSpecBuilder {
    internal abstract fun build(): BackendSpec
}

class ServiceBackendSpecBuilder internal constructor(private val name: String) : BackendSpecBuilder() {
    private var port: PortSpec? = null

    fun port(name: String) {
        port = PortSpecBuilder(name).build()
    }

    fun port(number: Int) {
        port = PortSpecBuilder(number).build()
    }

    override fun build(): BackendSpec {
        require(name.isNotBlank()) { "Service name is required for service backend" }
        require(port != null) { "Port is required for service backend" }

        return ServiceBackendSpec(name, port!!)
    }
}

class ResourceBackendSpecBuilder internal constructor(private val name: String, private val kind: String) :
    BackendSpecBuilder() {
    val apiGroup: String? = null

    override fun build(): BackendSpec {
        require(name.isNotBlank()) { "Resource name is required for resource backend" }
        require(kind.isNotBlank()) { "Resource kind is required for resource backend" }

        return ResourceBackendSpec(name, kind, apiGroup)
    }
}