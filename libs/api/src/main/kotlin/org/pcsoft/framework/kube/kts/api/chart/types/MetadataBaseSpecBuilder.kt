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

package org.pcsoft.framework.kube.kts.api.chart.types

import java.util.UUID

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

/**
 * Class responsible for building a `MetadataTemplateSpec`, which represents metadata configuration
 * for Kubernetes resource templates. This builder allows the specification of attributes such as
 * name, namespace, labels, annotations, finalizers, and owner references.
 *
 * This class is part of a framework for defining Kubernetes resource templates programmatically.
 *
 * @constructor Creates an instance of `MetadataTemplateSpecBuilder` with the specified resource name.
 * Note: This class is intended for internal use within the framework.
 */
class MetadataTemplateSpecBuilder internal constructor(private val name: String) : MetadataBaseSpecBuilder<MetadataTemplateSpec>() {
    private var finalizers: MutableList<String>? = null
    private var ownerReferences: MutableList<OwnerReferenceSpecBuilder>? = null

    /**
     * A prefix to use when generating a unique name for the resource.
     * When set, Kubernetes will automatically append a random suffix to this prefix to create a unique name.
     */
    var generateName: String? = null

    /**
     * The namespace where the Kubernetes resource should be created.
     *
     * This property specifies the namespace scope for the resource. If null,
     * the resource will be created in the default namespace or as specified by
     * cluster-wide configuration.
     */
    var namespace: String? = null

    /**
     * The name of the cluster where the resource resides.
     *
     * @Deprecated Cluster name is deprecated. Kubernetes resources are scoped to a single cluster.
     * Cluster identification should be handled at the infrastructure level.
     */
    @Deprecated(
        message = "Cluster name is deprecated. Kubernetes resources are scoped to a single cluster. " +
                "Cluster identification should be handled at the infrastructure level.",
        replaceWith = ReplaceWith(""),
        level = DeprecationLevel.WARNING
    )
    var clusterName: String? = null

    /**
     * Adds a finalizer to the metadata specification.
     *
     * Finalizers are used to control the deletion process of Kubernetes resources. When a resource has finalizers, its
     * deletion will be delayed until all finalizers have been processed.
     *
     * @param value The name of the finalizer to add.
     */
    fun addFinalizer(value: String) {
        if (finalizers == null) {
            finalizers = mutableListOf()
        }
        finalizers!!.add(value)
    }

    /**
     * Adds multiple finalizers to the metadata specification.
     *
     * Finalizers are used to control the deletion process of Kubernetes resources. When a resource has finalizers,
     * its deletion will be delayed until all finalizers have been processed.
     *
     * @param values The names of the finalizers to add.
     */
    fun addFinalizers(vararg values: String) {
        if (finalizers == null) {
            finalizers = mutableListOf()
        }
        finalizers!!.addAll(values.toList())
    }

    /**
     * Configures finalizers for the Kubernetes resource metadata using a builder pattern.
     *
     * This method provides a fluent interface to add multiple finalizers to the resource's metadata. Finalizers are
     * used to control the deletion process of Kubernetes resources, ensuring proper cleanup and preventing accidental deletion until certain conditions are met.
     *
     * Example:
     * ```kotlin
     *     finalizers {
     *         finalizer("finalizers.example.com/cleanup")
     *         finalizer("finalizers.example.com/backup")
     *     }
     * ```
     *
     * @param prepare A lambda function that configures the [FinalizerListBuilder] instance. The receiver of this lambda
     * is an instance of [FinalizerListBuilder], allowing you to call its methods (e.g., `finalizer`) directly.
     */
    fun finalizers(prepare: FinalizerListBuilder.() -> Unit) =
        FinalizerListBuilder().prepare()

    /**
     * Adds an owner reference to the metadata specification.
     *
     * This method creates and configures an [OwnerReferenceSpec] that represents a reference to another Kubernetes resource,
     * indicating ownership or control relationship. The owner reference is used by Kubernetes for garbage collection and
     * dependency management, allowing resources to be automatically deleted when their owner is deleted (if configured).
     *
     * @param apiVersion The API version of the referenced object (e.g., "v1").
     * @param kind The kind of the referenced object (e.g., "Pod", "Deployment").
     * @param name The name of the referenced object.
     * @param uid The unique identifier (UID) of the referenced object.
     * @param prepare A lambda function that configures the [OwnerReferenceSpecBuilder] instance. The receiver of this lambda
     *                is an instance of [OwnerReferenceSpecBuilder], allowing you to set optional properties like `controller`
     *                and `blockOwnerDeletion` directly within the lambda.
     */
    fun addOwnerReference(
        apiVersion: String,
        kind: String,
        name: String,
        uid: UUID,
        prepare: OwnerReferenceSpecBuilder.() -> Unit = {}
    ) {
        if (ownerReferences == null) {
            ownerReferences = mutableListOf()
        }
        ownerReferences!!.add(OwnerReferenceSpecBuilder(apiVersion, kind, name, uid).apply(prepare))
    }

    /**
     * Configures owner references for the Kubernetes resource metadata using a builder pattern.
     *
     * This method provides a fluent interface to add multiple owner references to the resource's metadata. Owner references
     * define relationships where one resource "owns" another, which is used by Kubernetes for garbage collection and dependency
     * management. When an owner reference is set with `blockOwnerDeletion` enabled, the referenced resource will be deleted when
     * its owner is deleted.
     *
     * Example:
     * ```kotlin
     *     ownerReferences {
     *         ownerReference("apps/v1", "Deployment", "my-deployment", UUID.randomUUID()) {
     *             controller = true
     *             blockOwnerDeletion = true
     *         }
     *     }
     * ```
     *
     * @param prepare A lambda function that configures the [OwnerReferenceListBuilder] instance. The receiver of this lambda
     *                is an instance of [OwnerReferenceListBuilder], allowing you to call its methods (e.g., `ownerReference`)
     *                directly within the lambda.
     */
    fun ownerReferences(prepare: OwnerReferenceListBuilder.() -> Unit = {}) =
        OwnerReferenceListBuilder().prepare()

    /**
     * Builds and returns a fully constructed [MetadataTemplateSpec] object.
     *
     * This method validates the required fields to ensure they meet the expected constraints before
     * creating the [MetadataTemplateSpec] instance. Any missing or empty required field will result
     * in an exception being thrown.
     *
     * @return A [MetadataTemplateSpec] instance populated with the data provided in the builder.
     *         The returned object includes values for name, generateName, namespace, labels,
     *         annotations, finalizers, ownerReferences, and clusterName (if applicable).
     *         If owner references are present, they are deeply built using their respective builders.
     */
    override fun build(): MetadataTemplateSpec {
        require(name.isNotBlank()) { "Name is required" }
        namespace?.let { require(it.isNotBlank()) { "Namespace is empty" } }
        generateName?.let { require(it.isNotBlank()) { "Generated name is empty" } }
        clusterName?.let { require(it.isNotBlank()) { "Cluster name is empty" } }

        return MetadataTemplateSpec(
            name,
            generateName,
            namespace,
            labels,
            annotations,
            finalizers,
            ownerReferences?.map { it.build() },
            clusterName
        )
    }

    /**
     * Builder for creating a list of finalizers in Kubernetes metadata.
     *
     * This class provides methods to add finalizers that control the deletion process of Kubernetes resources.
     * Finalizers are used to ensure proper cleanup and prevent accidental deletion until certain conditions are met.
     */
    inner class FinalizerListBuilder internal constructor() {
        /**
         * Adds a finalizer to the list of finalizers in Kubernetes metadata.
         *
         * Finalizers are used to ensure proper cleanup and prevent accidental deletion until certain conditions are met.
         * This method delegates to [addFinalizer] to add the specified value to the internal list of finalizers.
         *
         * @param value The finalizer string to be added. This should typically follow a naming convention like
         *              "finalizers.example.com/resource" to avoid conflicts with other resources.
         */
        fun finalizer(value: String) {
            addFinalizer(value)
        }
    }

    /**
     * Builder for creating a list of [OwnerReferenceSpec] instances.
     *
     * This builder allows configuration of multiple owner references that can be attached to Kubernetes resources.
     * Each owner reference defines a relationship where one resource "owns" another, which is used for garbage
     * collection and dependency management in Kubernetes. The builder supports setting the API version, kind,
     * name, UID, and optional flags like controller and blockOwnerDeletion for each reference.
     */
    inner class OwnerReferenceListBuilder internal constructor() {
        /**
         * Adds an owner reference to the metadata of a Kubernetes resource.
         *
         * This method creates and configures an [OwnerReferenceSpec] using the provided parameters and additional settings
         * defined in the [prepare] lambda. The owner reference establishes a relationship where one resource "owns" another,
         * which is used for garbage collection and dependency management in Kubernetes.
         *
         * @param apiVersion The API version of the referenced object (e.g., "v1").
         * @param kind The kind of the referenced object (e.g., "Pod", "Deployment").
         * @param name The name of the referenced object.
         * @param uid The unique identifier (UID) of the referenced object.
         * @param prepare A lambda that configures additional properties of the owner reference using [OwnerReferenceSpecBuilder].
         */
        fun ownerReference(
            apiVersion: String,
            kind: String,
            name: String,
            uid: UUID,
            prepare: OwnerReferenceSpecBuilder.() -> Unit
        ) {
            addOwnerReference(apiVersion, kind, name, uid, prepare)
        }
    }
}

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
class MetadataPodSpecBuilder internal constructor(): MetadataBaseSpecBuilder<MetadataPodSpec>() {
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