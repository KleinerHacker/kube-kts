package org.pcsoft.framework.kube.kts.api.chart.template

import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.template.types.MetadataSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents a Kubernetes resource template.
 *
 * @property apiVersion The API version of the resource.
 * @property kind The kind of the resource.
 * @property metadata The metadata for the resource.
 * @property spec The specification for the resource.
 */
@NoArgs
data class TemplateSpec<S>(
    val apiVersion: String,
    val kind: String,
    val metadata: MetadataSpec,
    val spec: S
) : KubeSpec where S : ResourceSpec