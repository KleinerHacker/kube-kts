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

package org.pcsoft.framework.kube.kts.api.chart.resources

/**
 * A builder class for constructing the [SealedSecretTemplateMetadataSpec] of a SealedSecret template.
 */
class SealedSecretTemplateMetadataSpecBuilder internal constructor() {
    private var labels: MutableMap<String, String>? = null
    private var annotations: MutableMap<String, String>? = null

    /**
     * Adds a single label to the produced Secret metadata.
     */
    fun addLabel(key: String, value: String) {
        if (labels == null) labels = mutableMapOf()
        labels!![key] = value
    }

    /**
     * Configures labels via a [LabelListBuilder].
     */
    fun labels(prepare: LabelListBuilder.() -> Unit) = LabelListBuilder().apply(prepare)

    /**
     * Adds a single annotation to the produced Secret metadata.
     */
    fun addAnnotation(key: String, value: String) {
        if (annotations == null) annotations = mutableMapOf()
        annotations!![key] = value
    }

    /**
     * Configures annotations via an [AnnotationListBuilder].
     */
    fun annotations(prepare: AnnotationListBuilder.() -> Unit) = AnnotationListBuilder().apply(prepare)

    internal fun build(): SealedSecretTemplateMetadataSpec = SealedSecretTemplateMetadataSpec(labels, annotations)

    /**
     * Builder for adding labels to the produced Secret metadata.
     */
    inner class LabelListBuilder internal constructor() {
        fun label(key: String, value: String) = addLabel(key, value)
    }

    /**
     * Builder for adding annotations to the produced Secret metadata.
     */
    inner class AnnotationListBuilder internal constructor() {
        fun annotation(key: String, value: String) = addAnnotation(key, value)
    }
}
