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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.SingleEnvironmentSpecDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.SingleEnvironmentSpecSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Represents a specification for a single environment variable, including its name
 * and the method by which its value is sourced.
 *
 * This data class serves as a configuration model to define environment variables
 * for applications, particularly in scenarios such as Kubernetes configuration
 * or other deployment contexts where environment variables are dynamically managed.
 *
 * @property name The name of the environment variable.
 * @property source The method or source from which the value of the environment variable is derived.
 *                  The type of source determines whether the value is static, field-based, or references
 *                  external objects like ConfigMaps or Secrets.
 */
@NoArgs
@JsonSerialize(using = SingleEnvironmentSpecSerializer::class)
@JsonDeserialize(using = SingleEnvironmentSpecDeserializer::class)
data class SingleEnvironmentSpec(
    val name: String,
    val source: Source
) {
    /**
     * Represents the source of an environment variable's value.
     *
     * Implementations of this interface define the specific way an environment
     * variable's value is sourced, which could originate from static values,
     * field references, ConfigMaps, Secrets, or other Kubernetes resources.
     *
     * This interface is used within the `EnvironmentSpec` class to provide flexible
     * and configurable mechanisms for defining environment variables in Kubernetes configurations.
     */
    sealed interface Source

    /**
     * Represents a static value source for an environment variable.
     *
     * This class provides a mechanism to define an environment variable
     * with a static, predefined value within the configuration. It acts
     * as one possible implementation of the `Source` interface, suitable
     * for scenarios where the value of the environment variable does not
     * need to be dynamically resolved or fetched from an external resource.
     *
     * Example:
     * ```
     * EnvironmentSpec(
     *     name = "DATABASE_URL",
     *     source = ValueSource(value = "postgresql://localhost:5432/mydb")
     * )
     * ```
     *
     * @property value The static value assigned to the environment variable.
     */
    @NoArgs
    data class ValueSource(val value: String) : Source

    /**
     * Represents a reference to a field-based value source.
     *
     * This class is primarily used to specify sourcing of environment variable
     * values from fields within Kubernetes resources. It provides a mechanism
     * to define detailed references to specific fields in resource objects.
     *
     * Example:
     * ```
     * EnvironmentSpec(
     *     name = "POD_NAME",
     *     source = FieldReferenceSource(fieldPath = "metadata.name")
     * )
     * ```
     *
     * @property fieldPath The path to the field within the resource from which the value is sourced.
     */
    @NoArgs
    data class FieldReferenceSource(val fieldPath: String) : Source

    /**
     * Represents a source of an environment variable that references a specific field within a resource.
     *
     * This data class is a concrete implementation of the `Source` interface and is used to define a 
     * field reference source that extracts the value of an environment variable from a particular field 
     * in a Kubernetes resource.
     *
     * Example:
     * ```
     * EnvironmentSpec(
     *     name = "MEMORY_LIMIT",
     *     source = ResourceFieldReferenceSource(resource = "limits.memory")
     * )
     * ```
     *
     * @property resource The name of the resource field being referenced.
     *                    This field is used to dynamically fetch the value at runtime.
     */
    @NoArgs
    data class ResourceFieldReferenceSource(val resource: String) : Source

    /**
     * Represents a reference to a specific key within a Kubernetes ConfigMap.
     *
     * This class is used as a source for environment variable configuration,
     * allowing the value of an environment variable to be dynamically derived
     * from a specific key in a specified ConfigMap.
     *
     * Example:
     * ```
     * EnvironmentSpec(
     *     name = "APP_CONFIG",
     *     source = ConfigMapKeyReferenceSource(name = "app-config", key = "database.url")
     * )
     * ```
     *
     * @property name The name of the ConfigMap being referenced.
     * @property key The key within the ConfigMap whose value is being referenced.
     */
    @NoArgs
    data class ConfigMapKeyReferenceSource(val name: String, val key: String) : Source

    /**
     * Represents a reference to a specific key within a Kubernetes Secret.
     *
     * This class is used as a source for environment variable configuration,
     * allowing the value of an environment variable to be dynamically derived
     * from a specific key in a specified Secret.
     *
     * Example:
     * ```
     * EnvironmentSpec(
     *     name = "DB_PASSWORD",
     *     source = SecretKeyReferenceSource(name = "db-credentials", key = "password")
     * )
     * ```
     *
     * @property name The name of the Secret being referenced.
     * @property key The key within the Secret whose value is being referenced.
     */
    @NoArgs
    data class SecretKeyReferenceSource(val name: String, val key: String) : Source
}
