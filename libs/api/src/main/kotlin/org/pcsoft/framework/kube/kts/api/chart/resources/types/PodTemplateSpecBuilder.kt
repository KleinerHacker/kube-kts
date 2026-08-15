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

import org.pcsoft.framework.kube.kts.api.chart.types.MetadataPodSpecBuilder

/**
 * Builder for constructing instances of [PodTemplateSpec].
 *
 * This class is designed to facilitate the creation and configuration of pod template specifications,
 * primarily used in Kubernetes deployments. It allows users to configure metadata and specifications
 * of the pod template using a fluent API.
 *
 * Responsibilities:
 * - Enables the definition of metadata using the [MetadataPodSpecBuilder] class.
 * - Supports configuration of the pod specification using the [PodSpecBuilder] class.
 * - Produces a finalized [PodTemplateSpec] through the [build] method.
 *
 * Usage:
 * This builder provides methods to define metadata and pod specifications, ensuring that the required
 * components are configured before building the resulting [PodTemplateSpec]. The `spec` property
 * is mandatory and must be defined before invoking the `build` method.
 *
 * Error Handling:
 * The [build] method will throw an [IllegalStateException] if the `spec` property is not defined before
 * invocation.
 */
class PodTemplateSpecBuilder internal constructor() {
    private var metadata: MetadataPodSpecBuilder? = null
    private var spec: PodSpecBuilder? = null

    /**
     * Configures the metadata for the pod template specification.
     *
     * This method allows you to define metadata using a lambda block that operates on an instance
     * of [MetadataPodSpecBuilder]. The provided block is applied to a new instance of the builder,
     * enabling the configuration of metadata properties, such as labels and annotations, for the pod.
     *
     * @param prepare A lambda block used to configure an instance of [MetadataPodSpecBuilder].
     * The block provides a fluent API to set metadata properties, such as labels and annotations.
     */
    fun metadata(prepare: MetadataPodSpecBuilder.() -> Unit) {
        metadata = MetadataPodSpecBuilder().apply(prepare)
    }

    /**
     * Configures the specification for the pod template.
     *
     * This method allows you to define the pod specification using a lambda block that operates on an instance
     * of [PodSpecBuilder]. The provided block is applied to a new instance of the builder, enabling the configuration
     * of properties such as containers, volumes, and other pod characteristics.
     *
     * @param prepare A lambda block used to configure an instance of [PodSpecBuilder].
     * The block provides a fluent API to set properties for the pod specification.
     */
    fun spec(prepare: PodSpecBuilder.() -> Unit) {
        spec = PodSpecBuilder().apply(prepare)
    }

    /**
     * Builds and returns a fully configured instance of [PodTemplateSpec].
     *
     * This method ensures that the required `spec` property is set before constructing
     * the [PodTemplateSpec] object. If `spec` is null, an [IllegalStateException] is thrown.
     *
     * @return A new [PodTemplateSpec] instance containing the configured metadata and pod specification.
     * @throws IllegalStateException If the `spec` property is null.
     */
    internal fun build(): PodTemplateSpec {
        require(spec != null) { "Spec must be set!" }

        return PodTemplateSpec(metadata?.build(), spec!!.build())
    }
}
