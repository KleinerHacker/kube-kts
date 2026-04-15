package org.pcsoft.framework.kube.kts.api.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class MetadataSpec(val name: String, val generatedName: String?, val namespace: String)

class MetadataSpecBuilder {
    var name: String = ""
    var generatedName: String? = null
    var namespace: String = ""

    fun build(): MetadataSpec {
        require(name.isNotBlank()) { "Name is required" }
        require(namespace.isNotBlank()) { "Namespace is required" }

        return MetadataSpec(name, generatedName, namespace)
    }
}