package org.pcsoft.framework.kube.kts.api.chart.resources.types

class PortSpecBuilder internal constructor(private val name: String?, private val port: Int?) {
    constructor(name: String) : this(name, null)
    constructor(port: Int) : this(null, port)

    internal fun build(): PortSpec = PortSpec(name, port)
}