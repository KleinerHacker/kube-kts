/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec

/**
 * Builder class for constructing instances of [PortMappingSpec]. This class provides a set of
 * configurable properties to define the port mapping for a Kubernetes Service.
 *
 * Some values are required.
 *
 * @constructor Creates an instance of the builder with the specified port name.
 * @param name The name of the port mapping. This value must not be blank.
 * @param port The port number for the port mapping. This value must be a positive integer.
 */
class PortMappingSpecBuilder internal constructor(private val name: String, private val port: Int) {

    /**
     * The port on the pods that the service should forward traffic to.
     */
    var targetPort: Int? = null

    /**
     * The IP protocol for this port.
     */
    var protocol: Protocol? = null

    /**
     * The application protocol for this port.
     */
    var appProtocol: String? = null

    /**
     * The port on each node on which this service is exposed.
     */
    var nodePort: Int? = null

    /**
     * Builds an instance of [PortMappingSpec] using the current configuration of the builder.
     *
     * @param type The type of the Kubernetes Service. If the service type is [ServiceSpec.Type.NodePort],
     *             the `nodePort` property must not be null.
     * @return A configured instance of [PortMappingSpec] based on the builder's properties.
     * @throws IllegalArgumentException If the `name` property is blank.
     * @throws IllegalArgumentException If the `port` property is not a positive value.
     * @throws IllegalArgumentException If the `nodePort` property is null when `type` is [ServiceSpec.Type.NodePort].
     */
    internal fun build(type: ServiceSpec.Type?): PortMappingSpec {
        require(name.isNotBlank()) { "Name is required" }
        require(port > 0) { "Port must be positive" }
        if (type == ServiceSpec.Type.NodePort) {
            require(nodePort != null) { "NodePort is required for type $type" }
        }

        return PortMappingSpec(name, port, targetPort, protocol, appProtocol, nodePort)
    }
}