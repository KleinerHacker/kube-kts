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

package org.pcsoft.framework.kube.kts.api.chart.types

/**
 * Builder for creating instances of [MetadataPodSpec].
 *
 * This class provides a mechanism to construct and configure metadata specifications
 * for pod templates, supporting the addition of labels and annotations.
 * Labels are required for constructing a valid [MetadataPodSpec] instance.
 *
 * This builder extends the [MetadataBaseSpecBuilder], inheriting methods for configuring
 * labels and annotations. The final constructed object represents metadata associated
 * with a pod template, commonly used in Kubernetes resource definitions.
 *
 * Responsibilities:
 * - Ensures that labels are provided before building the [MetadataPodSpec].
 * - Supports a fluent interface for adding labels and annotations.
 * - Constructs a finalized [MetadataPodSpec] object through the [build] method.
 *
 * The [build] method will throw an [IllegalStateException] if labels are not defined
 * or are empty at the time of invocation.
 */
class MetadataPodSpecBuilder internal constructor() : MetadataBaseSpecBuilder<MetadataPodSpec>() {
    /**
     * Builds and returns an instance of [MetadataPodSpec] with the current configuration.
     *
     * This method ensures that the `labels` property is defined and non-empty before constructing
     * the [MetadataPodSpec] object. If `labels` is null or empty, an [IllegalStateException]
     * is thrown.
     *
     * @return A newly constructed [MetadataPodSpec] containing the configured labels
     * and annotations.
     * @throws IllegalStateException If `labels` is null or empty.
     */
    override fun build(): MetadataPodSpec {
        require(labels != null && labels!!.isNotEmpty()) { "Labels is required" }

        return MetadataPodSpec(labels, annotations)
    }


}
