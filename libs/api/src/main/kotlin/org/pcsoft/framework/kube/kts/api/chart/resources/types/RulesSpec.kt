package org.pcsoft.framework.kube.kts.api.chart.resources.types

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
class RulesSpec private constructor(
    val host: String?,
    @field:JsonProperty("http")
    private val httpConfig: HttpConfig
) {
    constructor(
        host: String?,
        http: List<HttpPathConfig>
    ) : this(host, HttpConfig(http))

    @get:JsonIgnore
    val http: List<HttpPathConfig> by httpConfig::paths

    @NoArgs
    private data class HttpConfig(
        val paths: List<HttpPathConfig>
    )

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