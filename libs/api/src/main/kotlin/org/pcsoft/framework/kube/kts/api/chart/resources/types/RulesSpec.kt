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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Defines specifications for ingress rules, including host and HTTP path configurations.
 *
 * This class encapsulates the rules for routing external traffic to specific services
 * or backends based on the defined paths and hostnames.
 *
 * @property host The hostname for the ingress rule. Can be null to allow all hostnames.
 * @constructor Creates an instance of `RulesSpec` by either directly providing the host
 *              and HTTP path configuration or by using a private configuration structure.
 */
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

    /**
     * Provides the HTTP path configurations for the ingress rule.
     *
     * This property contains a list of HTTP path configurations (`HttpPathConfig`) which define
     * how traffic is routed to the appropriate backends based on specified path patterns and matching types.
     */
    @get:JsonIgnore
    val http: List<HttpPathConfig> by httpConfig::paths

    @NoArgs
    private data class HttpConfig(
        val paths: List<HttpPathConfig>
    )

    /**
     * Defines the configuration for an HTTP path within ingress rules.
     *
     * This data class is utilized to map incoming HTTP requests to specific backend services
     * or resources by specifying path patterns and path-matching types.
     *
     * @property path The URL path pattern to match. Can be null to match all paths.
     * @property pathType The type of path matching to apply, as defined by `PathType`.
     * @property backend The backend configuration to route the matching requests.
     */
    @NoArgs
    data class HttpPathConfig(
        val path: String?,
        val pathType: PathType,
        val backend: BackendSpec
    ) {
        /**
         * Defines the available types for matching HTTP request paths within an ingress configuration.
         *
         * This enum is used to specify how the path attribute in ingress rules should be interpreted.
         * It provides flexibility for handling different path matching strategies based on specific use cases.
         */
        @Suppress("unused")
        enum class PathType {
            /**
             * Matches based on a URL path prefix.
             */
            Prefix,

            /**
             * Matches the URL path exactly.
             */
            Exact,

            /**
             * Matching depends on the IngressClass.
             */
            ImplementationSpecific
        }
    }
}