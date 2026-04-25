package org.pcsoft.framework.kube.kts.api.chart.resources.types

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class RulesSpec(
    val host: String?,
    @get:JsonIgnore
    val http: List<HttpPathConfig>
) {
    /**
     * Only for JSON serialization/deserialization
     */
    @get:JsonProperty("http")
    private val httpWrapper: Map<String, List<HttpPathConfig>>
        get() = mapOf("paths" to http)

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