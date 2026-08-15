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
 * Builder for a [ProjectedSourceSpec], merging several projections into one volume.
 */
class ProjectedSourceSpecBuilder internal constructor() : SourceSpecBuilder<ProjectedSourceSpec> {
    private val sources = mutableListOf<ProjectedSourceEntrySpecBuilder>()

    /**
     * The POSIX permissions applied to created files unless overridden per item.
     */
    var defaultMode: Int? = null

    /**
     * Adds a single projection to the volume.
     *
     * Example:
     * ```kotlin
     * addSource {
     *     configMap { name = "app-config" }
     * }
     * ```
     *
     * @param prepare Configures the [ProjectedSourceEntrySpecBuilder].
     */
    fun addSource(prepare: ProjectedSourceEntrySpecBuilder.() -> Unit) {
        sources += ProjectedSourceEntrySpecBuilder().apply(prepare)
    }

    /**
     * Adds several projections in one block.
     *
     * @param prepare Configures the [SourceListBuilder].
     */
    fun sources(prepare: SourceListBuilder.() -> Unit) =
        SourceListBuilder().apply(prepare)

    /**
     * Adds a ConfigMap projection.
     *
     * @param prepare Configures the [ConfigMapSourceSpecBuilder].
     */
    fun addConfigMap(prepare: ConfigMapSourceSpecBuilder.() -> Unit) = addSource { configMap(prepare) }

    /**
     * Adds a Secret projection.
     *
     * @param prepare Configures the [SecretSourceSpecBuilder].
     */
    fun addSecret(prepare: SecretSourceSpecBuilder.() -> Unit) = addSource { secret(prepare) }

    /**
     * Adds a downward API projection.
     *
     * @param prepare Configures the [DownwardApiSourceSpecBuilder].
     */
    fun addDownwardApi(prepare: DownwardApiSourceSpecBuilder.() -> Unit) = addSource { downwardApi(prepare) }

    /**
     * Adds a ServiceAccount token projection.
     *
     * @param path              The relative path the token is written to.
     * @param audience          The intended audience of the token.
     * @param expirationSeconds The requested validity of the token in seconds.
     */
    fun addServiceAccountToken(path: String, audience: String? = null, expirationSeconds: Long? = null) =
        addSource {
            serviceAccountToken(path) {
                this.audience = audience
                this.expirationSeconds = expirationSeconds
            }
        }

    /**
     * Builds the configured projected source.
     *
     * @return A [ProjectedSourceSpec] carrying the configured values.
     */
    override fun build(): ProjectedSourceSpec =
        ProjectedSourceSpec(sources.map { it.build() }, defaultMode)

    /**
     * Collects several projections of a projected volume.
     */
    inner class SourceListBuilder internal constructor() {
        /**
         * Adds a single projection to the volume.
         *
         * @param prepare Configures the [ProjectedSourceEntrySpecBuilder].
         */
        fun source(prepare: ProjectedSourceEntrySpecBuilder.() -> Unit) = addSource(prepare)
    }
}
