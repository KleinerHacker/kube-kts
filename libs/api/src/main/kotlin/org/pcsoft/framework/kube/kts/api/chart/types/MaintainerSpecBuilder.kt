package org.pcsoft.framework.kube.kts.api.chart.types

import java.net.URI

class MaintainerSpecBuilder internal constructor(private val name: String) {
    var email: String? = null
    var url: URI? = null

    internal fun build(): MaintainerSpec = MaintainerSpec(name, email, url)
}