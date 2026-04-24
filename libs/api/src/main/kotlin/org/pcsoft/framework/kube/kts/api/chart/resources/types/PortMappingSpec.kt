package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class PortMappingSpec(
    val name: String,
    val port: Int,
    val targetPort: Int?,
    val protocol: Protocol?,
    val appProtocol: String?,
    val nodePort: Int?
) {
    @Suppress("unused")
    enum class Protocol {
        TCP, UDP, SCTP
    }
}