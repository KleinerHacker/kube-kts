package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.resources.types.BackendSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RulesSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.TlsSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class IngressSpec(
    val ingressClassName: String?,
    val defaultBackend: BackendSpec?,
    val tls: List<TlsSpec>?,
    val rules: List<RulesSpec>?
) : ResourceSpec {

    companion object {
        const val KIND = "Ingress"
        const val API_VERSION = "networking.k8s.io/v1"
    }

}