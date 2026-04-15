package org.pcsoft.framework.kube.kts.api

open class ResourceSpecMetadata(val name: String, val generatedName: String, val namespace: String)

abstract class ResourceSpecMetadataBuilder<M : ResourceSpecMetadata> {
    var name: String = ""
    var generatedName: String = ""
    var namespace: String = ""

    abstract fun build(): M
}

class DefaultResourceSpecMetadataBuilder : ResourceSpecMetadataBuilder<ResourceSpecMetadata>() {
    override fun build(): ResourceSpecMetadata = ResourceSpecMetadata(name, generatedName, namespace)
}