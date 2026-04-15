package org.pcsoft.framework.kube.kts.api

import org.pcsoft.framework.kube.kts.api.types.PortSpec

class ServiceSpec(val type: Type, val ports: List<PortSpec>) : ResourceSpec {
    companion object {
        const val KIND = "Service"
        const val API_VERSION = "v1"
    }

    @Suppress("unused")
    enum class Type {
        ClusterIP, NodePort, LoadBalancer, ExternalName
    }
}