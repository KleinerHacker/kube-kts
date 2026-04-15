package org.pcsoft.framework.kube.kts.api

sealed class ResourceSpec<M : ResourceSpecMetadata>(val metadata: M) {
    val header: ResourceSpecHeader

    init {
        val header = this::class.annotations.first { it is ResourceSpecHeader } as? ResourceSpecHeader ?:
            throw IllegalStateException("${this::class.qualifiedName} must be annotated with ${ResourceSpecHeader::class.simpleName}")
        this.header = header
    }
}