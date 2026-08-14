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

/**
 * Represents a network port exposed by a container.
 *
 * A container port declares which port the containerized application listens on. Naming a port allows
 * other resources - Services, probes or Ingress backends - to reference it by name instead of by
 * number.
 *
 * @property name          An optional name for this port. Must be unique within the Pod and a valid IANA
 *                         service name (at most 15 characters). Required if the port is to be referenced by name.
 * @property containerPort The port number the container listens on. Must be between 1 and 65535.
 * @property hostPort      An optional port on the host node this container port is mapped to. Most workloads
 *                         should leave this unset, as it constrains scheduling to nodes with the port free.
 * @property hostIP        An optional host IP address to bind the [hostPort] to. Only meaningful together
 *                         with [hostPort].
 * @property protocol      The transport protocol for this port. Defaults to [Protocol.TCP] if not set.
 */
@NoArgs
data class ContainerPortSpec(
    val name: String?,
    val containerPort: Int,
    val hostPort: Int?,
    val hostIP: String?,
    val protocol: Protocol?
) {
    /**
     * Validates the port numbers and the optional port name.
     */
    init {
        require(containerPort in 1..65535) {
            "Container port must be between 1 and 65535, but was $containerPort"
        }
        hostPort?.let {
            require(it in 1..65535) { "Host port must be between 1 and 65535, but was $it" }
        }
        name?.let {
            require(it.isNotBlank()) { "Port name must not be blank" }
            require(it.length <= 15) { "Port name must not exceed 15 characters, but was '$it'" }
        }
        hostIP?.let { require(it.isNotBlank()) { "Host IP must not be blank" } }
    }
}
