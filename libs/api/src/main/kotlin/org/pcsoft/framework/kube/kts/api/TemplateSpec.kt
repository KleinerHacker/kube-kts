package org.pcsoft.framework.kube.kts.api

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.types.MetadataSpec
import org.pcsoft.framework.kube.kts.api.types.MetadataSpecBuilder

@NoArgs
data class TemplateSpec<S>(
    val apiVersion: String,
    val kind: String,
    val metadata: MetadataSpec,
    val spec: S
) where S : ResourceSpec

class TemplateSpecBuilder<S, B>(
    val apiVersion: String,
    val kind: String,
    private val specBuilder: B
) where S : ResourceSpec, B : ResourceSpecBuilder<S> {
    private val metadataBuilder = MetadataSpecBuilder()

    fun metadata(prepare: MetadataSpecBuilder.() -> Unit) {
        metadataBuilder.apply(prepare)
    }

    fun spec(prepare: B.() -> Unit) {
        specBuilder.apply(prepare)
    }

    fun build(): TemplateSpec<S> {
        return TemplateSpec(apiVersion, kind, metadataBuilder.build(), specBuilder.build())
    }
}