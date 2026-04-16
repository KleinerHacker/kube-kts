package org.pcsoft.framework.kube.kts.api.types

import org.pcsoft.framework.kube.kts.api.ServiceSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class PortSpec(
    val name: String,
    val port: Int,
    val targetPort: Int?,
    val protocol: Protocol?,
    val appProtocol: String?,
    val nodePort: Int?
) {
    @Suppress("unused")
    enum class Protocol {
        TCP, UDP, SCTP
    }
}

class PortSpecBuilder(private val name: String) {
    var port: Int = 0
    var targetPort: Int? = null
    var protocol: PortSpec.Protocol? = null
    var appProtocol: String? = null
    var nodePort: Int? = null

    fun build(type: ServiceSpec.Type?): PortSpec {
        require(name.isNotBlank()) { "Name is required" }
        require(port > 0) { "Port must be positive" }
        if (type == ServiceSpec.Type.NodePort) {
            require(nodePort != null) { "NodePort is required for type $type" }
        }

        return PortSpec(name, port, targetPort, protocol, appProtocol, nodePort)
    }
}