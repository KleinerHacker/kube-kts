package org.pcsoft.framework.kube.kts.api.chart.resources.types

/**
 * Builder class for creating instances of `PortSpec`.
 *
 * This builder allows for specifying a port either by its name or by its number,
 * but not both simultaneously.
 */
class PortSpecBuilder private constructor(private val name: String?, private val number: Int?) {
    internal constructor(name: String) : this(name, null)

    internal constructor(number: Int) : this(null, number)

    internal fun build(): PortSpec = PortSpec(name, number)
}