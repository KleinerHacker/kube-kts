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
 * Builder class for creating instances of [ServiceAccountTokenProjectionSpec].
 *
 * The projection writes a short-lived ServiceAccount token into the volume.
 *
 * @constructor Creates an instance of [ServiceAccountTokenProjectionSpecBuilder] for internal usage.
 * @param path The relative path the token is written to.
 */
class ServiceAccountTokenProjectionSpecBuilder internal constructor(private val path: String) {
    /**
     * The intended audience of the token. Defaults to the API server when unset.
     */
    var audience: String? = null

    /**
     * The requested validity of the token in seconds.
     */
    var expirationSeconds: Long? = null

    /**
     * Constructs and returns a [ServiceAccountTokenProjectionSpec] instance based on the configured values.
     *
     * @return A [ServiceAccountTokenProjectionSpec] carrying the configured values.
     */
    internal fun build(): ServiceAccountTokenProjectionSpec =
        ServiceAccountTokenProjectionSpec(path, audience, expirationSeconds)
}
