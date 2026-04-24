package org.pcsoft.framework.kube.kts.api.chart.resources.types

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class RulesSpec(
    val host: String?,
    @field:JsonProperty("http.paths")
    val http: List<HttpPathConfig>
) {
    @NoArgs
    data class HttpPathConfig(
        val path: String?,
        val pathType: PathType,
        val backend: BackendSpec
    ) {
        @Suppress("unused")
        enum class PathType {
            Prefix, Exact, ImplementationSpecific
        }
    }
}