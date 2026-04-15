package org.pcsoft.framework.kube.kts.api.types

open class MetadataSpec(val name: String, val generatedName: String?, val namespace: String)

abstract class MetadataSpecBuilder<M : MetadataSpec> {
    var name: String = ""
    var generatedName: String? = null
    var namespace: String = ""

    abstract fun build(): M
}

class DefaultMetadataSpecBuilder : MetadataSpecBuilder<MetadataSpec>() {
    override fun build(): MetadataSpec {
        require(name.isNotBlank()) { "Name is required" }
        require(namespace.isNotBlank()) { "Namespace is required" }

        return MetadataSpec(name, generatedName, namespace)
    }
}