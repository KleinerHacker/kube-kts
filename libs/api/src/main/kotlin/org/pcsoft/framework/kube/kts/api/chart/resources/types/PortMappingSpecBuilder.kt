/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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
    var protocol: Protocol? = null

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