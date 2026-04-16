package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec

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