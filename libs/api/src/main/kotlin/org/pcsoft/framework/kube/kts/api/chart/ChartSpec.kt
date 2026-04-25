package org.pcsoft.framework.kube.kts.api.chart

import com.fasterxml.jackson.annotation.JsonValue
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.chart.types.MaintainerSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import java.net.URI

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
    val sources: List<URI>?,
    val dependencies: List<DependencySpec>?,
    val maintainers: List<MaintainerSpec>?,
    val icon: URI?,
    val appVersion: String?,
    val deprecated: Boolean?,
    val annotations: Map<String, String>?
) : KubeSpec {
    companion object {
        const val API_VERSION = "v2"
    }

    @Suppress("unused")
    enum class Type {
        Application, Library;

        @JsonValue
        fun toJson(): String {
            return name.lowercase()
        }
    }
}
