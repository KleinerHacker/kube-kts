package org.pcsoft.framework.kube.kts.api.chart

import org.pcsoft.framework.kube.kts.api.chart.ChartSpec.Type
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersionBuilder
import org.pcsoft.framework.kube.kts.api.chart.types.MaintainerSpecBuilder

class ChartSpecBuilder internal constructor(private val name: String, private val version: String) {
    private var keywords: MutableSet<String>? = null
    private var sources: MutableList<String>? = null
    private var dependencies: MutableList<DependencySpecBuilder>? = null
    private var maintainers: MutableList<MaintainerSpecBuilder>? = null
    private var annotations: MutableMap<String, String>? = null
    private var kubeVersion: KubeVersionBuilder? = null

    var description: String? = null
    var type: Type? = null
    var home: String? = null
    var icon: String? = null
    var appVersion: String? = null
    var deprecated: Boolean? = null

    fun addKeyword(keyword: String) {
        if (keywords == null) {
            keywords = mutableSetOf()
        }
        keywords!!.add(keyword)
    }

    fun addKeywords(vararg keywords: String) {
        if (this.keywords == null) {
            this.keywords = mutableSetOf()
        }
        this.keywords!!.addAll(keywords.toSet())
    }

    fun addSource(source: String) {
        if (sources == null) {
            sources = mutableListOf()
        }
        sources!!.add(source)
    }

    fun addSources(vararg sources: String) {
        if (this.sources == null) {
            this.sources = mutableListOf()
        }
        this.sources!!.addAll(sources.toList())
    }

    fun addDependency(name: String, version: String, prepare: DependencySpecBuilder.() -> Unit) {
        if (dependencies == null) {
            dependencies = mutableListOf()
        }
        dependencies!!.add(DependencySpecBuilder(name, version).apply(prepare))
    }

    fun addMaintainer(name: String, prepare: MaintainerSpecBuilder.() -> Unit) {
        if (maintainers == null) {
            maintainers = mutableListOf()
        }
        maintainers!!.add(MaintainerSpecBuilder(name).apply(prepare))
    }

    fun addAnnotation(key: String, value: String) {
        if (annotations == null) {
            annotations = mutableMapOf()
        }
        annotations!![key] = value
    }

    fun kubeVersion(version: String) {
        kubeVersion = KubeVersionBuilder().apply {
            add(version) {
                equality = KubeVersion.ItemEquality.EQUAL
            }
        }
    }

    fun kubeVersion(prepare: KubeVersionBuilder.() -> Unit) {
        kubeVersion = KubeVersionBuilder().apply(prepare)
    }

    internal fun build() : ChartSpec =
        ChartSpec(
            apiVersion = ChartSpec.API_VERSION,
            name = name,
            version = version,
            description = description,
            keywords = keywords,
            sources = sources,
            dependencies = dependencies?.map { it.build() },
            maintainers = maintainers?.map { it.build() },
            kubeVersion = kubeVersion?.build(),
            type = type,
            home = home,
            icon = icon,
            appVersion = appVersion,
            deprecated = deprecated,
            annotations = annotations
        )
}