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
 * Builder class for creating instances of [ProjectedSourceEntrySpec].
 *
 * A single entry of a projected volume carries exactly one of the supported projections. Configuring
 * a second projection replaces the first one.
 *
 * @constructor Creates an instance of [ProjectedSourceEntrySpecBuilder] for internal usage.
 */
class ProjectedSourceEntrySpecBuilder internal constructor() {
    private var configMap: ConfigMapSourceSpecBuilder? = null
    private var secret: SecretSourceSpecBuilder? = null
    private var downwardApi: DownwardApiSourceSpecBuilder? = null
    private var serviceAccountToken: ServiceAccountTokenProjectionSpecBuilder? = null

    /**
     * Projects the entries of a ConfigMap.
     *
     * @param prepare Configures the [ConfigMapSourceSpecBuilder].
     */
    fun configMap(prepare: ConfigMapSourceSpecBuilder.() -> Unit) {
        clear()
        configMap = ConfigMapSourceSpecBuilder().apply(prepare)
    }

    /**
     * Projects the entries of a Secret.
     *
     * @param prepare Configures the [SecretSourceSpecBuilder].
     */
    fun secret(prepare: SecretSourceSpecBuilder.() -> Unit) {
        clear()
        secret = SecretSourceSpecBuilder().apply(prepare)
    }

    /**
     * Projects pod metadata and container resource values.
     *
     * @param prepare Configures the [DownwardApiSourceSpecBuilder].
     */
    fun downwardApi(prepare: DownwardApiSourceSpecBuilder.() -> Unit) {
        clear()
        downwardApi = DownwardApiSourceSpecBuilder().apply(prepare)
    }

    /**
     * Projects a short-lived ServiceAccount token.
     *
     * @param path    The relative path the token is written to.
     * @param prepare Configures the [ServiceAccountTokenProjectionSpecBuilder].
     */
    fun serviceAccountToken(
        path: String,
        prepare: ServiceAccountTokenProjectionSpecBuilder.() -> Unit = {}
    ) {
        clear()
        serviceAccountToken = ServiceAccountTokenProjectionSpecBuilder(path).apply(prepare)
    }

    private fun clear() {
        configMap = null
        secret = null
        downwardApi = null
        serviceAccountToken = null
    }

    /**
     * Constructs and returns a [ProjectedSourceEntrySpec] instance based on the configured projection.
     *
     * @return A [ProjectedSourceEntrySpec] carrying the configured projection.
     */
    internal fun build(): ProjectedSourceEntrySpec {
        require(configMap != null || secret != null || downwardApi != null || serviceAccountToken != null) {
            "A projected source entry must declare exactly one projection"
        }

        return ProjectedSourceEntrySpec(
            configMap?.build(),
            secret?.build(),
            downwardApi?.build(),
            serviceAccountToken?.build()
        )
    }
}
