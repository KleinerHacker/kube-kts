package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class PortSpec(
    val name: String?,
    val number: Int?
)
