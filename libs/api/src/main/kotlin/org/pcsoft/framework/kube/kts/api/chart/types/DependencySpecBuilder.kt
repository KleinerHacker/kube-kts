package org.pcsoft.framework.kube.kts.api.chart.types

import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec.ImportValue
import java.net.URI

class DependencySpecBuilder internal constructor(private val name: String, private val version: String) {
    private var importValues: MutableList<ImportValue>? = null
    private var tags: MutableSet<String>? = null

    var repository: URI? = null
    var alias: String? = null
    var condition: String? = null

    fun addPathImportValue(path: String) {
        if (importValues == null) {
            importValues = mutableListOf()
        }
        importValues!!.add(DependencySpec.PathImportValue(path))
    }

    fun addPathImportValues(vararg paths: String) {
        if (importValues == null) {
            importValues = mutableListOf()
        }
        importValues!!.addAll(paths.map { DependencySpec.PathImportValue(it) })
    }

    fun addMappingImportValue(key: String, value: String) {
        if (importValues == null) {
            importValues = mutableListOf()
        }
        importValues!!.add(DependencySpec.MappingImportValue(key, value))
    }

    fun addTag(tag: String) {
        if (tags == null) {
            tags = mutableSetOf()
        }
        tags!!.add(tag)
    }

    fun addTags(vararg tags: String) {
        if (this.tags == null) {
            this.tags = mutableSetOf()
        }
        this.tags!!.addAll(tags.toSet())
    }

    internal fun build(): DependencySpec =
        DependencySpec(
            name = name,
            version = version,
            repository = repository,
            alias = alias,
            condition = condition,
            importValues = importValues ?: emptyList(),
            tags = tags ?: emptySet()
        )
}