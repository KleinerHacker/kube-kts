package org.pcsoft.framework.kube.kts.api

import org.pcsoft.framework.kube.kts.api.types.MetadataSpec
import org.pcsoft.framework.kube.kts.api.types.MetadataSpecBuilder

data class ResourceApi<S>(
    val apiVersion: String,
    val kind: String,
    val metadata: MetadataSpec,
    val spec: S
) where S : ResourceSpec

class ResourceApiBuilder<S, B>(
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

    fun build(): ResourceApi<S> {
        return ResourceApi(apiVersion, kind, metadataBuilder.build(), specBuilder.build())
    }
}