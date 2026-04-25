package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec

class PortMappingSpecBuilder internal constructor(private val name: String) {
    var port: Int = 0
    var targetPort: Int? = null
    var protocol: PortMappingSpec.Protocol? = null
    var appProtocol: String? = null
    var nodePort: Int? = null

    internal fun build(type: ServiceSpec.Type?): PortMappingSpec {
        require(name.isNotBlank()) { "Name is required" }
        require(port > 0) { "Port must be positive" }
        if (type == ServiceSpec.Type.NodePort) {
            require(nodePort != null) { "NodePort is required for type $type" }
        }

        return PortMappingSpec(name, port, targetPort, protocol, appProtocol, nodePort)
    }
}