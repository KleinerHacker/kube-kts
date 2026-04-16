package org.pcsoft.framework.kube.kts.api.chart.template

import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.template.types.MetadataSpecBuilder

class TemplateSpecBuilder<S, B> internal constructor(
    private val apiVersion: String,
    private val kind: String,
    private val specBuilder: B
) where S : ResourceSpec, B : ResourceSpecBuilder<S> {
    private var metadataBuilder: MetadataSpecBuilder? = null

    fun metadata(name: String, prepare: MetadataSpecBuilder.() -> Unit) {
        metadataBuilder = MetadataSpecBuilder(name).apply(prepare)
    }

    fun spec(prepare: B.() -> Unit) {
        specBuilder.apply(prepare)
    }

    internal fun build(): TemplateSpec<S> {
        require(metadataBuilder != null) { "Metadata is required" }

        return TemplateSpec(apiVersion, kind, metadataBuilder!!.build(), specBuilder.build())
    }
}