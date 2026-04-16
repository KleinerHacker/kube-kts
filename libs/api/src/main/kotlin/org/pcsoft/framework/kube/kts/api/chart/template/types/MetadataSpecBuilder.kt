package org.pcsoft.framework.kube.kts.api.chart.template.types

class MetadataSpecBuilder internal constructor(private val name: String) {
    var generateName: String? = null
    var namespace: String? = null

    fun build(): MetadataSpec {
        require(name.isNotBlank()) { "Name is required" }

        return MetadataSpec(name, generateName, namespace)
    }
}