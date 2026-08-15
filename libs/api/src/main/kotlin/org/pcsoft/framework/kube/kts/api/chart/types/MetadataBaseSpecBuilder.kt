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
 * Base builder class for constructing metadata specifications with labels and annotations.
 *
 * @param T The type of metadata specification that this builder constructs. It must inherit from [MetadataBaseSpec].
 *
 * This sealed class provides common methods and structures required to configure and build
 * metadata specifications that include labels and annotations. It offers builder patterns for
 * fluently defining key-value pairs for organizing (labels) and storing custom information (annotations).
 *
 * Subclasses should implement the [build] method to return an instance of the specific metadata specification type.
 */
sealed class MetadataBaseSpecBuilder<T : MetadataBaseSpec> {
    protected var labels: MutableMap<String, String>? = null; private set
    protected var annotations: MutableMap<String, String>? = null; private set


    /**
     * Adds a label to the metadata specification.
     *
     * @param key The key of the label to add.
     * @param value The value of the label to add.
     */
    fun addLabel(key: String, value: String) {
        if (labels == null) {
            labels = mutableMapOf()
        }
        labels!![key] = value
    }

    /**
     * Configures labels for the Kubernetes resource metadata using a builder pattern.
     *
     * This method provides a fluent interface to add multiple key-value pairs as labels to the resource's metadata.
     * Labels are used to organize and select resources based on custom attributes.
     *
     * Example:
     * ```kotlin
     *     labels {
     *         label("app", "my-application")
     *         label("environment", "production")
     *         label("version", "1.0.0")
     *     }
     * ```
     *
     * @param prepare A lambda function that configures the [LabelListBuilder] instance. The receiver of this lambda
     *                is an instance of [LabelListBuilder], allowing you to call its methods (e.g., `label`) directly.
     */
    fun labels(prepare: LabelListBuilder.() -> Unit) =
        LabelListBuilder().prepare()

    /**
     * Adds an annotation to the metadata specification.
     *
     * @param key The key of the annotation to add.
     * @param value The value of the annotation to add.
     */
    fun addAnnotation(key: String, value: String) {
        if (annotations == null) {
            annotations = mutableMapOf()
        }
        annotations!![key] = value
    }

    /**
     * Configures annotations for the Kubernetes resource metadata using a builder pattern.
     *
     * This method provides a fluent interface to add multiple key-value pairs as annotations to the resource's metadata.
     * Annotations are non-identifying metadata that can be attached to Kubernetes resources and are often used by tools
     * or systems for various purposes like scheduling, network policies, or custom resource definitions.
     *
     * Example:
     * ```kotlin
     *     annotations {
     *         annotation("description", "This is a sample resource")
     *         annotation("example.com/managed-by", "kube-kts")
     *     }
     * ```
     *
     * @param prepare A lambda function that configures the [AnnotationListBuilder] instance. The receiver of this lambda
     *                is an instance of [AnnotationListBuilder], allowing you to call its methods (e.g., `annotation`) directly.
     */
    fun annotations(prepare: AnnotationListBuilder.() -> Unit) =
        AnnotationListBuilder().prepare()

    /**
     * Constructs and returns an instance of the metadata specification.
     *
     * This method serves as the final step in the builder pattern, aggregating all configured labels,
     * annotations, and other metadata properties to generate the desired metadata object.
     *
     * @return The constructed instance of the metadata specification of type [T].
     */
    internal abstract fun build(): T

    /**
     * Builder for creating label entries in Kubernetes metadata.
     *
     * This inner class provides a fluent interface for adding key-value pairs as labels to Kubernetes resources.
     * Labels are used to organize and select resources based on custom attributes.
     */
    inner class LabelListBuilder internal constructor() {
        /**
         * Adds a label to the Kubernetes resource metadata.
         *
         * This method creates a key-value pair that will be included in the labels section of the resource's metadata.
         * Labels are used for identifying and organizing resources based on custom attributes.
         *
         * @param key The label key (must not be empty).
         * @param value The label value to associate with the key.
         */
        fun label(key: String, value: String) {
            addLabel(key, value)
        }
    }

    /**
     * Builder for creating annotation lists within Kubernetes metadata specifications.
     *
     * This inner class provides a fluent interface for adding key-value pairs as annotations to the parent [MetadataBaseSpecBuilder].
     * Annotations are additional metadata that can be attached to Kubernetes resources and are often used by tools or systems
     * for various purposes like scheduling, network policies, or custom resource definitions.
     */
    inner class AnnotationListBuilder internal constructor() {
        /**
         * Adds an annotation to the Kubernetes metadata specification.
         *
         * @param key The key of the annotation. This is typically a string identifier for the annotation.
         * @param value The value of the annotation. This can be any string that provides additional metadata about the resource.
         */
        fun annotation(key: String, value: String) {
            addAnnotation(key, value)
        }
    }
}
