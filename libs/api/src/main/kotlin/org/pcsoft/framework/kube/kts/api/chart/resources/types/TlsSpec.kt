package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents the TLS configuration for an Ingress.
 *
 * @property hosts The list of hosts included in the TLS certificate.
 * @property secretName The name of the secret that contains the TLS certificate and private key.
 */
@NoArgs
data class TlsSpec(
    val hosts: List<String>?,
    val secretName: String?
)