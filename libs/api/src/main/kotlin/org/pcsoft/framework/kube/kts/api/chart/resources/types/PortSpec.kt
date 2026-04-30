package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents a port specification, either by name or by number.
 *
 * @property name The name of the port.
 * @property number The number of the port.
 */
@NoArgs
data class PortSpec(
    val name: String?,
    val number: Int?
)
