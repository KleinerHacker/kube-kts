/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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
 * Represents the ingress rules specification.
 *
 * @property host The host for which the rules apply.
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
     * The list of HTTP paths and their backends.
     */
    @get:JsonIgnore
    val http: List<HttpPathConfig> by httpConfig::paths

    @NoArgs
    private data class HttpConfig(
        val paths: List<HttpPathConfig>
    )

    /**
     * Represents an HTTP path configuration.
     *
     * @property path The path pattern.
     * @property pathType The type of path matching.
     * @property backend The backend for this path.
     */
    @NoArgs
    data class HttpPathConfig(
        val path: String?,
        val pathType: PathType,
        val backend: BackendSpec
    ) {
        /**
         * Represents the type of path matching.
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