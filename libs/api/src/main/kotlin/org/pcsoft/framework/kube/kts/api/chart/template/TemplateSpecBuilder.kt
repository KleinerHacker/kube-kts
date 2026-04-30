package org.pcsoft.framework.kube.kts.api.chart.template

import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.template.types.MetadataSpecBuilder

/**
 * Builder class for creating instances of [TemplateSpec]. This class provides methods to
 * configure the metadata and specification of a Kubernetes resource template while ensuring
 * that all necessary components are set before the final build.
 *
 * @param S The type of the resource specification contained within the template. Must extend [ResourceSpec].
 * @param B The type of the builder used to configure the resource specification. Must implement [ResourceSpecBuilder].
 * @constructor Creates an instance of the builder with the specified API version, kind, and specification builder.
 * @param apiVersion The API version of the resource being configured.
 * @param kind The kind of the resource being configured (e.g., ConfigMap, Pod, Service).
 * @param specBuilder An instance of the builder responsible for creating the resource specification.
 */
class TemplateSpecBuilder<S, B> internal constructor(
    private val apiVersion: String,
    private val kind: String,
    private val specBuilder: B
) where S : ResourceSpec, B : ResourceSpecBuilder<S> {
    private var metadataBuilder: MetadataSpecBuilder? = null

    /**
     * Configures the metadata for the Kubernetes resource template.
     *
     * Example:
     * ```kotlin
     * metadata("my-config") {
     *     labels = mapOf("app" to "myapp", "env" to "production")
     *     annotations = mapOf("description" to "Sample configuration")
     * }
     * ```
     *
     * @param name The name of the resource being configured.
     * @param prepare A lambda with a receiver of [MetadataSpecBuilder] to further customize the metadata.
     */
    fun metadata(name: String, prepare: MetadataSpecBuilder.() -> Unit) {
        metadataBuilder = MetadataSpecBuilder(name).apply(prepare)
    }

    /**
     * Configures the specification part of the Kubernetes resource template.
     *
     * Example:
     * ```kotlin
     * spec {
     *     // Configure resource-specific properties
     *     // The exact properties depend on the resource type (ConfigMap, Service, etc.)
     * }
     * ```
     *
     * @param prepare A lambda with a receiver of type [B], allowing configuration of the resource specification.
     */
    fun spec(prepare: B.() -> Unit) {
        specBuilder.apply(prepare)
    }

    internal fun build(): TemplateSpec<S> {
        require(metadataBuilder != null) { "Metadata is required" }

        return TemplateSpec(apiVersion, kind, metadataBuilder!!.build(), specBuilder.build())
    }
}