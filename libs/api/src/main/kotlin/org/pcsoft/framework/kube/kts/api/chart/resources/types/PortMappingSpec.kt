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