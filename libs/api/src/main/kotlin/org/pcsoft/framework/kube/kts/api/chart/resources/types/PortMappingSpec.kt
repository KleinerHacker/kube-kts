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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents a port mapping configuration for a Service.
 *
 * @property name The name of the port.
 * @property port The port that the service will serve on.
 * @property targetPort The port on the pods that the service should forward traffic to.
 * @property protocol The IP protocol for this port (TCP, UDP, SCTP).
 * @property appProtocol The application protocol for this port.
 * @property nodePort The port on each node on which this service is exposed (for NodePort type).
 */
@NoArgs
data class PortMappingSpec(
    val name: String,
    val port: Int,
    val targetPort: Int?,
    val protocol: Protocol?,
    val appProtocol: String?,
    val nodePort: Int?
) {
    /**
     * IP protocols.
     */
    @Suppress("unused")
    enum class Protocol {
        TCP, UDP, SCTP
    }
}