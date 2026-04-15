package org.pcsoft.framework.kube.kts.api

import org.pcsoft.framework.kube.kts.api.types.MetadataSpec

sealed class ResourceSpec<M : MetadataSpec>(val metadata: M) {
    val header: ResourceSpecHeader

    init {
        val header = this::class.annotations.first { it is ResourceSpecHeader } as? ResourceSpecHeader ?:
            throw IllegalStateException("${this::class.qualifiedName} must be annotated with ${ResourceSpecHeader::class.simpleName}")
        this.header = header
    }
}