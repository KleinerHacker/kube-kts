package org.pcsoft.framework.kube.kts.api.chart

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.chart.types.MaintainerSpec

@NoArgs
data class ChartSpec(
    val apiVersion: String,
    val name: String,
    val version: String,
    val kubeVersion: KubeVersion?,
    val description: String?,
    val type: Type?,
    val keywords: Set<String>?,
    val home: String?,
    val sources: List<String>?,
    val dependencies: List<DependencySpec>?,
    val maintainers: List<MaintainerSpec>?,
    val icon: String?,
    val appVersion: String?,
    val deprecated: Boolean?,
    val annotations: Map<String, String>?
) : KubeSpec {
    companion object {
        const val API_VERSION = "v2"
    }

    @Suppress("unused")
    enum class Type {
        Application, Library
    }
}
