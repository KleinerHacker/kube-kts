package org.pcsoft.framework.kube.kts.api.types

data class PortSpec(
    val name: String,
    val port: Int,
    val targetPort: Int,
    val protocol: Protocol,
    val appProtocol: String? = null,
    val nodePort: Int? = null
) {
    @Suppress("unused")
    enum class Protocol {
        TCP, UDP, SCTP
    }
}

class PortSpecBuilder(private val name: String) {
    var port: Int = 0
    var targetPort: Int = 0
    var protocol: PortSpec.Protocol = PortSpec.Protocol.TCP
    var appProtocol: String? = null
    var nodePort: Int? = null

    fun build(): PortSpec {
        require(name.isNotBlank()) { "Name is required" }
        require(port > 0) { "Port must be positive" }
        require(targetPort > 0) { "Target port must be positive" }

        return PortSpec(name, port, targetPort, protocol, appProtocol, nodePort)
    }
}