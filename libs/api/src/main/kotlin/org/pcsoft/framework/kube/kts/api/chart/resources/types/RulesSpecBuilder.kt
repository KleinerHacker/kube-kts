package org.pcsoft.framework.kube.kts.api.chart.resources.types

class RulesSpecBuilder internal constructor() {
    private var http: MutableList<HttpPathConfigBuilder>? = null

    var host: String? = null

    fun addHttpPath(type: RulesSpec.HttpPathConfig.PathType, prepare: HttpPathConfigBuilder.() -> Unit) {
        if (http == null) {
            http = mutableListOf()
        }
        http!!.add(HttpPathConfigBuilder(type).apply(prepare))
    }

    class HttpPathConfigBuilder internal constructor(private val type: RulesSpec.HttpPathConfig.PathType) {
        private var backend: BackendSpecBuilder? = null

        var path: String? = null

        fun serviceBackend(name: String, prepare: ServiceBackendSpecBuilder.() -> Unit) {
            backend = ServiceBackendSpecBuilder(name).apply(prepare)
        }

        fun resourceBackend(name: String, kind: String, prepare: ResourceBackendSpecBuilder.() -> Unit) {
            backend = ResourceBackendSpecBuilder(name, kind).apply(prepare)
        }

        fun build(): RulesSpec.HttpPathConfig {
            require(backend != null) { "Backend is required for http path in rules" }
            require(path == null || path!!.isNotBlank()) { "Path should not be empty in rules" }

            return RulesSpec.HttpPathConfig(
                path, type, backend!!.build()
            )
        }
    }

    internal fun build(): RulesSpec {
        require(http != null) { "Http is required for rules" }
        require(host == null || host!!.isNotBlank()) { "Host should not be empty in rules" }

        return RulesSpec(host, http!!.map { it.build() })
    }
}