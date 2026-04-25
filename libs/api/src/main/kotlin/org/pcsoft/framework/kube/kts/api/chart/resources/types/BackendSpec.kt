package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.BackendSpecDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.BackendSpecSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

@NoArgs
@JsonSerialize(using = BackendSpecSerializer::class)
@JsonDeserialize(using = BackendSpecDeserializer::class)
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