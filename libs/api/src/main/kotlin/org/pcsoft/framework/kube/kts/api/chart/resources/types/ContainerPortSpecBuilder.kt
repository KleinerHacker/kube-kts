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

/**
 * Builder for a single [ContainerPortSpec].
 *
 * @constructor Creates a builder for the given container port.
 * @param containerPort The port number the container listens on.
 */
class ContainerPortSpecBuilder internal constructor(private val containerPort: Int) {
    /**
     * An optional name for this port, allowing other resources to reference it by name.
     *
     * Must be a valid IANA service name of at most 15 characters.
     */
    var name: String? = null

    /**
     * The transport protocol for this port. Defaults to [Protocol.TCP] when unset.
     */
    var protocol: Protocol? = null

    /**
     * An optional port on the host node this container port is mapped to.
     *
     * Setting it constrains scheduling to nodes where the port is free and should be avoided for
     * ordinary workloads.
     */
    var hostPort: Int? = null

    /**
     * An optional host IP address to bind [hostPort] to. Only meaningful together with [hostPort].
     */
    var hostIP: String? = null

    /**
     * Builds the configured container port.
     *
     * @return A [ContainerPortSpec] carrying the configured values.
     */
    internal fun build(): ContainerPortSpec =
        ContainerPortSpec(name, containerPort, hostPort, hostIP, protocol)
}
