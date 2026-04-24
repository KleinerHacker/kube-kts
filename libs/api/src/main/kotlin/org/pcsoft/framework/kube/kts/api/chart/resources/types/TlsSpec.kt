package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class TlsSpec(
    val hosts: List<String>?,
    val secretName: String?
)