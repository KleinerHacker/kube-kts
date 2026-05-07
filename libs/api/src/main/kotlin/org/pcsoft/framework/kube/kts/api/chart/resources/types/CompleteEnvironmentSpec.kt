package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.CompleteEnvironmentSpecDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.CompleteEnvironmentSpecSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Represents the complete specification for environment configuration, including a prefix and a source reference.
 * This data class is designed to serialize and deserialize environment specifications to and from JSON representations.
 *
 * The `CompleteEnvironmentSpec` is primarily used to define environment variable inputs for Kubernetes manifests,
 * supporting configurations stored in ConfigMaps or Secrets.
 *
 * An optional `prefix` can be specified, which is appended to environment variable names loaded from the source.
 *
 * This class is equipped with custom serializers and deserializers to properly handle its JSON structure, ensuring
 * compatibility with common Kubernetes resource definitions.
 *
 * @property prefix The prefix appended to the environment variables loaded from the source. Can be null if no prefix is needed.
 * @property source The source of the environment variables, which can refer to either a ConfigMap or a Secret.
 * @see CompleteEnvironmentSpecSerializer
 * @see CompleteEnvironmentSpecDeserializer
 */
@JsonSerialize(using = CompleteEnvironmentSpecSerializer::class)
@JsonDeserialize(using = CompleteEnvironmentSpecDeserializer::class)
@NoArgs
data class CompleteEnvironmentSpec(
    val prefix: String?,
    val source: Source,
) {
    /**
     * Defines the source of environment variables used in Kubernetes manifest specifications.
     *
     * This class represents a reference to a Kubernetes ConfigMap or Secret, enabling the retrieval
     * of environment variable values from these resources. The `Source` specifies the type of the
     * resource, its name, and whether the resource is optional.
     *
     * @property type The type of the resource, indicating whether it is a ConfigMap or Secret.
     * @property name The name of the resource (ConfigMap or Secret) to be referenced.
     * @property optional Indicates whether the referenced resource is optional. If true, the application
     * can function without the resource being present.
     */
    @NoArgs
    data class Source(
        val type: SourceType,
        val name: String,
        val optional: Boolean?
    )

    /**
     * Represents the type of source used to provide configuration or secret data.
     *
     * This enum is typically utilized within the context of Kubernetes resource specifications
     * to define whether the data originates from a Kubernetes ConfigMap or a Kubernetes Secret.
     *
     * - `ConfigMap`: Indicates that the data source is a Kubernetes ConfigMap, which is used
     *   to store configuration data as key-value pairs.
     * - `Secret`: Indicates that the data source is a Kubernetes Secret, which is used to
     *   store sensitive information such as credentials or TLS certificates.
     */
    @Suppress("unused")
    enum class SourceType {
        ConfigMap,
        Secret
    }
}
