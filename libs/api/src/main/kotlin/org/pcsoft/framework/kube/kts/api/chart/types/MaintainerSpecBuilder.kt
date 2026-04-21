package org.pcsoft.framework.kube.kts.api.chart.types

import org.pcsoft.framework.kube.kts.api.types.MailAddress
import java.net.URI

class MaintainerSpecBuilder internal constructor(private val name: String) {
    var email: MailAddress? = null
    var url: URI? = null

    internal fun build(): MaintainerSpec = MaintainerSpec(name, email, url)
}