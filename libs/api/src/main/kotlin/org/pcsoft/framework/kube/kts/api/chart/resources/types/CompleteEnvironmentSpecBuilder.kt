package org.pcsoft.framework.kube.kts.api.chart.resources.types

/**
 * A builder class used to construct instances of `CompleteEnvironmentSpec`. This class provides a fluent API
 * to define the complete specification for environment variables by setting a prefix and referencing a source.
 * The source can either be a ConfigMap or a Secret.
 *
 * This builder is designed to simplify the creation of `CompleteEnvironmentSpec` instances while allowing
 * for customization and configuration of the source via a functional block.
 *
 * Some values are required.
 *
 * @constructor This class is internally constructed and is not intended to be instantiated directly by users.
 */
class CompleteEnvironmentSpecBuilder internal constructor() {
    private var source: SourceBuilder? = null

    /**
     * Specifies the prefix to be applied to environment variable keys when mapping
     * keys from a ConfigMap or Secret to environment variables.
     *
     * This property is used to avoid naming conflicts by adding a uniform prefix
     * to all environment variables sourced using this builder. If no prefix is defined,
     * the keys retain their original name.
     *
     * The prefix can be any valid string or null, where null indicates that no prefix
     * will be applied.
     */
    var prefix: String? = null

    /**
     * References a Kubernetes ConfigMap to provide environment variable configuration.
     *
     * This method configures the `source` property of the builder to use a `ConfigMap` as the source
     * of environment variables. It accepts the name of the ConfigMap to reference, and an optional
     * configuration block to further customize the source properties.
     *
     * Example usage:
     * ```kotlin
     * // Basic usage
     * configMapRef("my-config-map")
     *
     * // With optional configuration
     * configMapRef("my-config-map") {
     *     optional = true
     * }
     * ```
     *
     * @param name The name of the Kubernetes ConfigMap to reference.
     * @param prepare Optional configuration block to customize the source, such as setting additional properties.
     */
    fun configMapRef(name: String, prepare: SourceBuilder.() -> Unit = {}) {
        source = SourceBuilder(CompleteEnvironmentSpec.SourceType.ConfigMap, name).apply(prepare)
    }

    /**
     * References a Kubernetes Secret to provide environment variable configuration.
     *
     * This method configures the `source` property of the builder to use a `Secret` as the source
     * of environment variables. It accepts the name of the Secret to reference and an optional
     * configuration block to further customize the source properties.
     *
     * Example usage:
     * ```kotlin
     * // Basic usage
     * secretRef("my-secret")
     *
     * // With optional configuration
     * secretRef("my-secret") {
     *     optional = true
     * }
     * ```
     *
     * @param name The name of the Kubernetes Secret to reference.
     * @param prepare Optional configuration block to customize the source, such as setting additional properties.
     */
    fun secretRef(name: String, prepare: SourceBuilder.() -> Unit = {}) {
        source = SourceBuilder(CompleteEnvironmentSpec.SourceType.Secret, name).apply(prepare)
    }

    /**
     * Builds and returns a `CompleteEnvironmentSpec` instance based on the current state of the builder.
     *
     * The method ensures that the `source` property is set before constructing the `CompleteEnvironmentSpec`.
     * If `source` is not set, an `IllegalArgumentException` will be thrown. The returned object includes
     * the configured `prefix` and the result of the `source.build()` operation.
     *
     * @return A `CompleteEnvironmentSpec` instance containing the configured `prefix` and `source` properties.
     * @throws IllegalArgumentException If the `source` property is null.
     */
    internal fun build(): CompleteEnvironmentSpec {
        require(source != null) { "Source must be set" }

        return CompleteEnvironmentSpec(
            prefix = prefix,
            source = source!!.build()
        )
    }

    /**
     * A builder for configuring and constructing instances of `CompleteEnvironmentSpec.Source`.
     *
     * This class is used internally to help define the details of a Kubernetes ConfigMap or Secret
     * being referenced within environment variable configuration. It allows setting properties such
     * as the type of the resource, its name, and whether it is optional.
     *
     * @constructor Creates an instance of `SourceBuilder` with the specified resource type and name.
     * @param type The type of the source, indicating whether it is a `ConfigMap` or `Secret`.
     * @param name The name of the source (ConfigMap or Secret) to be referenced.
     */
    class SourceBuilder internal constructor(
        private val type: CompleteEnvironmentSpec.SourceType,
        private val name: String
    ) {
        /**
         * Indicates whether the referenced Kubernetes ConfigMap or Secret is optional.
         *
         * If set to `true`, the absence of the referenced resource will not result
         * in errors, allowing the application to proceed without the configuration
         * defined in the missing resource. If set to `false`, the absence will cause
         * an error. If `null`, the behavior depends on the default settings or
         * implementation-defined behavior.
         */
        var optional: Boolean? = null

        /**
         * Constructs a new instance of `CompleteEnvironmentSpec.Source` based on the properties set
         * in the builder. This method finalizes the configurations provided through the builder
         * pattern and creates an immutable `Source` object.
         *
         * @return A `CompleteEnvironmentSpec.Source` instance containing the configured type, name,
         * and optional properties.
         */
        internal fun build(): CompleteEnvironmentSpec.Source {
            return CompleteEnvironmentSpec.Source(
                type = type,
                name = name,
                optional = optional
            )
        }
    }
}