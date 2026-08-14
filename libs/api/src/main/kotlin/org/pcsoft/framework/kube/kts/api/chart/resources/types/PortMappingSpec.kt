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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.types.PortValue

/**
 * Maps a port exposed by a Service onto a port of the Pods behind it.
 *
 * @property name        The name of this port. Optional on single-port Services, but required as soon as
 *                       a Service exposes more than one port. Must be a valid IANA service name.
 * @property port        The port the Service itself exposes.
 * @property targetPort  The port on the backing Pods traffic is forwarded to, either a number or the
 *                       name of a container port. Defaults to [port] when unset.
 * @property protocol    The transport protocol. Defaults to [Protocol.TCP] when unset.
 * @property appProtocol A hint at the application protocol spoken on this port, for example `http` or
 *                       `kubernetes.io/h2c`.
 * @property nodePort    The port allocated on every node for Services of type `NodePort` or
 *                       `LoadBalancer`. Assigned by the system when unset.
 */
@NoArgs
data class PortMappingSpec(
    val name: String?,
    val port: Int,
    val targetPort: PortValue<*>?,
    val protocol: Protocol?,
    val appProtocol: String?,
    val nodePort: Int?
) {
    /**
     * Validates the port numbers and the optional port name.
     */
    init {
        require(port in 1..65535) { "Port must be between 1 and 65535, but was $port" }
        nodePort?.let { require(it in 1..65535) { "Node port must be between 1 and 65535, but was $it" } }
        name?.let {
            require(it.isNotBlank()) { "Port name must not be blank" }
            require(it.length <= 15) { "Port name must not exceed 15 characters, but was '$it'" }
        }
    }
}
