package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec

/**
 * Builder class for constructing instances of [PortMappingSpec]. This class provides a set of
 * configurable properties to define the port mapping for a Kubernetes Service.
 *
 * @constructor Creates an instance of the builder with the specified port name.
 * @param name The name of the port mapping. This value must not be blank.
 */
class PortMappingSpecBuilder internal constructor(private val name: String) {
    /**
     * The port that the service will serve on.
     */
    var port: Int = 0

    /**
     * The port on the pods that the service should forward traffic to.
     */
    var targetPort: Int? = null

    /**
     * The IP protocol for this port.
     */
    var protocol: PortMappingSpec.Protocol? = null

    /**
     * The application protocol for this port.
     */
    var appProtocol: String? = null

    /**
     * The port on each node on which this service is exposed.
     */
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