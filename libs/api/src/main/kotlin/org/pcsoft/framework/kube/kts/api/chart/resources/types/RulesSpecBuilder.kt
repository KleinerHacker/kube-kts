/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart.resources.types

/**
 * Builder class for constructing ingress rules specifications.
 *
 * This class provides DSL-based methods to configure rules, including host and HTTP path
 * configurations, for defining ingress behaviors and routing mechanisms.
 */
class RulesSpecBuilder internal constructor() {
    private var http: MutableList<HttpPathConfigBuilder>? = null

    /**
     * The host for which the rules apply.
     */
    var host: String? = null

    /**
     * Adds an HTTP path configuration for ingress rules.
     *
     * This method enables defining an HTTP path configuration using a DSL-based builder.
     * The configuration specifies path matching rules and the corresponding backend service or resource.
     *
     * Example:
     * ```kotlin
     * addHttpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
     *     path = "/api"
     *     serviceBackend("my-service") {
     *         port = 8080
     *     }
     * }
     * ```
     *
     * @param type The type of path matching to be applied (e.g., Prefix, Exact, or ImplementationSpecific).
     * @param prepare A lambda function for configuring the HTTP path. This lambda receives
     *                an instance of [HttpPathConfigBuilder] to define the path properties and backend details.
     */
    fun addHttpPath(type: RulesSpec.HttpPathConfig.PathType, prepare: HttpPathConfigBuilder.() -> Unit) {
        if (http == null) {
            http = mutableListOf()
        }
        http!!.add(HttpPathConfigBuilder(type).apply(prepare))
    }

    /**
     * Configures a list of HTTP paths for ingress rules.
     *
     * This method allows defining multiple HTTP path configurations by using
     * a DSL-based builder. Each path configuration can specify routing details,
     * such as path patterns and corresponding backend services.
     *
     * Example:
     * ```kotlin
     * httpPaths {
     *     httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
     *         path = "/api"
     *         serviceBackend("api-service") {
     *             port = 8080
     *         }
     *     }
     *     httpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
     *         path = "/admin"
     *         serviceBackend("admin-service") {
     *             port = 9090
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda function for configuring the list of HTTP paths.
     *                This lambda receives an instance of [HttpPathListBuilder] to
     *                define individual path configurations.
     */
    fun httpPaths(prepare: HttpPathListBuilder.() -> Unit) {
        HttpPathListBuilder().apply(prepare)
    }

    /**
     * A builder class for configuring HTTP path rules within ingress specifications.
     *
     * This class allows defining HTTP path configurations such as path patterns
     * and the corresponding backend services or resources. It provides methods
     * to specify service or resource backends, which determine where the incoming
     * requests are routed based on the provided path and matching type.
     *
     * @constructor Creates an instance of `HttpPathConfigBuilder` with the specified path matching type.
     * @param type The type of path matching to be applied (e.g., Prefix, Exact, or ImplementationSpecific).
     */
    class HttpPathConfigBuilder internal constructor(private val type: RulesSpec.HttpPathConfig.PathType) {
        private var backend: BackendSpecBuilder? = null

        /**
         * Represents the HTTP path pattern for the configuration.
         *
         * This property specifies the URL path that incoming requests should match in order
         * to route traffic to the appropriate backend. A null value implies that all paths
         * should be matched.
         */
        var path: String? = null

        /**
         * Configures a service backend by specifying its name and additional configuration.
         *
         * Example:
         * ```kotlin
         * serviceBackend("my-service") {
         *     port = 8080
         * }
         * ```
         *
         * @param name The name of the service to be used as the backend.
         * @param prepare A lambda block to configure the service backend using the [ServiceBackendSpecBuilder].
         */
        fun serviceBackend(name: String, prepare: ServiceBackendSpecBuilder.() -> Unit) {
            backend = ServiceBackendSpecBuilder(name).apply(prepare)
        }

        /**
         * Configures a resource backend by specifying its name, kind, and additional configuration.
         *
         * Example:
         * ```kotlin
         * resourceBackend("my-resource", "StorageBucket") {
         *     apiGroup = "storage.example.com"
         * }
         * ```
         *
         * @param name The name of the resource to be used as the backend.
         * @param kind The kind of the resource to be used as the backend.
         * @param prepare A lambda block to configure the resource backend using the [ResourceBackendSpecBuilder].
         */
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

    /**
     * Builder for managing a list of HTTP path configurations within ingress rules.
     *
     * This class provides utilities to define and add multiple HTTP path-based routing
     * rules to the underlying rules configuration. It allows specifying the type of
     * path matching and preparing additional configuration for each path.
     *
     * @constructor Creates an instance of [HttpPathListBuilder]. This is typically used
     * internally within a parent builder to manage HTTP path definitions.
     */
    inner class HttpPathListBuilder internal constructor() {
        /**
         * Defines an HTTP path configuration for ingress rules.
         *
         * Example:
         * ```kotlin
         * httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
         *     path = "/api"
         *     serviceBackend("api-service") {
         *         port = 8080
         *     }
         * }
         * ```
         *
         * @param type The type of path matching to be used (e.g., `Prefix`, `Exact`, `ImplementationSpecific`).
         * @param prepare A lambda function to configure additional settings for the HTTP path, including path pattern
         *                and backend configuration.
         */
        fun httpPath(type: RulesSpec.HttpPathConfig.PathType, prepare: HttpPathConfigBuilder.() -> Unit) =
            addHttpPath(type, prepare)
    }
}