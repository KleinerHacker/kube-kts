package org.pcsoft.framework.kube.kts.api.chart.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.types.MailAddress
import java.net.URI

@NoArgs
data class MaintainerSpec(
    val name: String,
    val email: MailAddress?,
    val url: URI?
)
