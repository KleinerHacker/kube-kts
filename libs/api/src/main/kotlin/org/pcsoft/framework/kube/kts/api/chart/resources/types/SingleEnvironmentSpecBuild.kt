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

package org.pcsoft.framework.kube.kts.api.chart.resources.types


/**
 * Builder for creating a `SingleEnvironmentSpec` for one named environment variable.
 *
 * The builder stores the environment variable name supplied by the surrounding DSL and
 * requires exactly one value source to be configured before the final specification can
 * be built. Supported sources include static values, Kubernetes field references,
 * resource field references, ConfigMap keys, and Secret keys.
 *
 * @constructor Creates a builder for the given environment variable name. This constructor is
 * internally controlled by the DSL and is not intended to be called directly by users.
 * @param name The name of the environment variable represented by the resulting specification.
 */
class SingleEnvironmentSpecBuild internal constructor(private val name: String) {
    private var source: SingleEnvironmentSpec.Source? = null

    /**
     * Configures the `source` field of an environment specification with a static value.
     * 
     * This method creates a `ValueSource` using the provided value and assigns it to the
     * `source` field, allowing the environment variable to use a static, predefined value.
     *
     * Example:
     * ```kotlin
     *     fromValue("my-static-value")
     * ```
     *
     * @param value The static value to be used as the environment variable's value.
     */
    fun fromValue(value: String) {
        source = SingleEnvironmentSpec.ValueSource(value)
    }

    /**
     * Configures the `source` field of an environment specification using a field reference.
     *
     * This method assigns a `FieldReferenceSource` to the `source` field, allowing the environment
     * variable to be dynamically populated based on the value of a specific field within a Kubernetes resource.
     *
     * Example:
     * ```kotlin
     *     fromFieldReference("metadata.name")
     * ```
     *
     * @param fieldPath The path to the field within the resource from which the environment variable's value is sourced.
     */
    fun fromFieldReference(fieldPath: String) {
        source = SingleEnvironmentSpec.FieldReferenceSource(fieldPath)
    }

    /**
     * Configures the `source` field of an environment specification using a resource field reference.
     *
     * This method assigns a `ResourceFieldReferenceSource` to the `source` field, allowing the environment
     * variable to be dynamically populated based on the value of a specific field within a Kubernetes resource.
     *
     * Example:
     * ```kotlin
     *     fromResourceFieldReference("limits.cpu")
     * ```
     *
     * @param fieldPath The path to the field within the resource from which the environment variable's value is sourced.
     */
    fun fromResourceFieldReference(fieldPath: String) {
        source = SingleEnvironmentSpec.ResourceFieldReferenceSource(fieldPath)
    }

    /**
     * Configures the `source` field of an environment specification using a ConfigMap key reference.
     *
     * This method assigns a `ConfigMapKeyReferenceSource` to the `source` field, enabling the 
     * environment variable to retrieve its value from a specific key within a Kubernetes ConfigMap.
     *
     * Example:
     * ```kotlin
     *     fromConfigMapKeyReference("my-config", "app.properties")
     * ```
     *
     * @param name The name of the ConfigMap being referenced.
     * @param key The key within the ConfigMap whose value is being referenced.
     */
    fun fromConfigMapKeyReference(name: String, key: String) {
        source = SingleEnvironmentSpec.ConfigMapKeyReferenceSource(name, key)
    }

    /**
     * Configures the `source` field of an environment specification using a Secret key reference.
     *
     * This method assigns a `SecretKeyReferenceSource` to the `source` field, allowing the environment
     * variable to retrieve its value from a specific key within a Kubernetes Secret.
     *
     * Example:
     * ```kotlin
     *     fromSecretKeyReference("db-secret", "password")
     * ```
     *
     * @param name The name of the Secret being referenced.
     * @param key The key within the Secret whose value is being referenced.
     */
    fun fromSecretKeyReference(name: String, key: String) {
        source = SingleEnvironmentSpec.SecretKeyReferenceSource(name, key)
    }

    /**
     * Configures the `source` field of the environment specification using a builder.
     *
     * This method utilizes a lambda function with a `FromBuilder` receiver, allowing you to specify 
     * how the `source` field should be configured. The builder provides methods for creating various 
     * types of environment variable sources, such as static values, field references, or resource-specific references.
     *
     * Example:
     * ```kotlin
     *     from {
     *         // add source configuration here
     *     }
     * ```
     *
     * @param prepare A lambda function with `FromBuilder` as the receiver, used to configure the `source` field.
     */
    fun from(prepare: FromBuilder.() -> Unit) {
        FromBuilder().apply(prepare)
    }
    
    internal fun build(): SingleEnvironmentSpec {
        require(source != null) { "Source must be set" }
        
        return SingleEnvironmentSpec(name, source!!)
    }

    /**
     * A builder class responsible for configuring the `source` field of an environment specification.
     *
     * Provides methods to define various types of sources for environment variables, such as static values,
     * references to Kubernetes resource fields, ConfigMaps, and Secrets.
     */
    inner class FromBuilder {
        /**
         * Configures the `source` field of an environment specification with a static value.
         *
         * This method assigns the provided static value to the environment variable's source.
         *
         * Example:
         * ```kotlin
         *     from {
         *         value("my-static-value")
         *     }
         * ```
         *
         * @param value The static value to be used as the environment variable's value.
         */
        fun value(value: String) = fromValue(value)

        /**
         * Configures the `source` field of an environment specification using a field reference.
         *
         * This method allows you to set up a dynamic reference to a specific field within a Kubernetes resource,
         * enabling the environment variable to derive its value from the specified field.
         *
         * Example:
         * ```kotlin
         *     from {
         *         fieldReference("metadata.name")
         *     }
         * ```
         *
         * @param fieldPath The path to the field within the Kubernetes resource from which the environment variable's value is sourced.
         */
        fun fieldReference(fieldPath: String) = fromFieldReference(fieldPath)

        /**
         * Configures the environment variable's source field with a resource field reference.
         *
         * This method allows you to dynamically populate the environment variable's value
         * based on a specific field within a Kubernetes resource.
         *
         * Example:
         * ```kotlin
         *     from {
         *         resourceFieldReference("limits.memory")
         *     }
         * ```
         *
         * @param fieldPath The path to the field within the Kubernetes resource
         *                  from which the environment variable's value is sourced.
         */
        fun resourceFieldReference(fieldPath: String) = fromResourceFieldReference(fieldPath)

        /**
         * Configures the environment variable's source field using a ConfigMap key reference.
         *
         * This method sets up a reference to a specific key within a Kubernetes ConfigMap,
         * enabling the environment variable to derive its value from the referenced key.
         *
         * Example:
         * ```kotlin
         *     from {
         *         configMapKeyReference("my-config", "app.properties")
         *     }
         * ```
         *
         * @param name The name of the ConfigMap that contains the desired key.
         * @param key The specific key within the ConfigMap whose value is being referenced.
         */
        fun configMapKeyReference(name: String, key: String) = fromConfigMapKeyReference(name, key)

        /**
         * Configures the environment variable's source field using a Secret key reference.
         *
         * This method sets up a reference to a specific key within a Kubernetes Secret, enabling
         * the environment variable to derive its value from the designated key in the Secret.
         *
         * Example:
         * ```kotlin
         *     from {
         *         secretKeyReference("db-secret", "password")
         *     }
         * ```
         *
         * @param name The name of the Kubernetes Secret being referenced.
         * @param key The specific key within the Secret whose value is being referenced.
         */
        fun secretKeyReference(name: String, key: String) = fromSecretKeyReference(name, key)
    }
}
