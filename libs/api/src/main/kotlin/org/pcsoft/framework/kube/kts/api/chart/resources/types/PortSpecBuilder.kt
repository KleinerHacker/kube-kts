package org.pcsoft.framework.kube.kts.api.chart.resources.types

class PortSpecBuilder internal constructor(private val name: String?, private val number: Int?) {
    constructor(name: String) : this(name, null)
    constructor(number: Int) : this(null, number)

    internal fun build(): PortSpec = PortSpec(name, number)
}