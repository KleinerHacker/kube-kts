package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
sealed class BackendSpec(
    val name: String
)

@NoArgs
class ServiceBackendSpec(
    name: String,
    val port: PortSpec
) : BackendSpec(name)

@NoArgs
class ResourceBackendSpec(
    name: String,
    val kind: String,
    val apiGroup: String?
) : BackendSpec(name)