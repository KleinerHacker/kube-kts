package org.pcsoft.framework.kube.kts.api.chart.template

import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.template.types.MetadataSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class TemplateSpec<S>(
    val apiVersion: String,
    val kind: String,
    val metadata: MetadataSpec,
    val spec: S
) where S : ResourceSpec