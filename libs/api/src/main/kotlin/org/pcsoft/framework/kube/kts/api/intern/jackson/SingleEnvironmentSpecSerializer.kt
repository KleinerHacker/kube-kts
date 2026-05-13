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

package org.pcsoft.framework.kube.kts.api.intern.jackson

import org.pcsoft.framework.kube.kts.api.chart.resources.types.SingleEnvironmentSpec
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObjectProperty
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer

/**
 * A custom serializer for the `EnvironmentSpec` class.
 *
 * This serializer is responsible for converting an instance of `EnvironmentSpec`
 * into a structured JSON representation. It handles various types of sources defined
 * within `EnvironmentSpec`, including static values, field references, and Kubernetes
 * resource references such as ConfigMaps and Secrets.
 *
 * The serialized JSON structure can vary depending on the type of `source` used within
 * the `EnvironmentSpec`. It ensures that each source type is correctly represented
 * with appropriate properties in the JSON output.
 *
 * Key serialization behaviors:
 * - If the `EnvironmentSpec` instance is null, a JSON null value is written.
 * - For a `ValueSource`, a simple string value is serialized.
 * - For other source types (such as field references, ConfigMap keys, or Secret keys),
 *   nested objects are serialized to represent the structure of the source.
 *
 * This class extends `ValueSerializer` and overrides the `serialize` method to provide
 * custom serialization logic specific to the `EnvironmentSpec` class.
 */
internal class SingleEnvironmentSpecSerializer : ValueSerializer<SingleEnvironmentSpec>() {
    override fun serialize(
        value: SingleEnvironmentSpec?,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeObject {
            gen.writeStringProperty("name", value.name)
            when (value.source) {
                is SingleEnvironmentSpec.ValueSource -> gen.writeStringProperty("value", value.source.value)
                is SingleEnvironmentSpec.FieldReferenceSource -> gen.writeObjectProperty("valueFrom") {
                    gen.writeObjectProperty("fieldRef") {
                        gen.writeStringProperty("fieldPath", value.source.fieldPath)
                    }
                }

                is SingleEnvironmentSpec.ResourceFieldReferenceSource -> gen.writeObjectProperty("valueFrom") {
                    gen.writeObjectProperty("resourceFieldRef") {
                        gen.writeStringProperty("resource", value.source.resource)
                    }
                }

                is SingleEnvironmentSpec.ConfigMapKeyReferenceSource -> gen.writeObjectProperty("valueFrom") {
                    gen.writeObjectProperty("configMapKeyRef") {
                        gen.writeStringProperty("name", value.source.name)
                        gen.writeStringProperty("key", value.source.key)
                    }
                }

                is SingleEnvironmentSpec.SecretKeyReferenceSource -> gen.writeObjectProperty("valueFrom") {
                    gen.writeObjectProperty("secretKeyRef") {
                        gen.writeStringProperty("name", value.source.name)
                        gen.writeStringProperty("key", value.source.key)
                    }
                }
            }
        }
    }
}

/**
 * A custom deserializer for the `EnvironmentSpec` class.
 *
 * The `EnvironmentSpecDeserializer` allows for the deserialization of JSON
 * input into instances of `EnvironmentSpec`. It supports parsing environment
 * variable configurations defined either as static values or dynamically sourced
 * from Kubernetes resources such as ConfigMaps and Secrets.
 *
 * This deserializer reads JSON nodes and determines the appropriate type of
 * `EnvironmentSpec.Source` subclass based on the presence of specific fields.
 *
 * Supported fields:
 * - `value`: Indicates a static value for the environment variable.
 * - `valueFrom`: Defines the source of a dynamic value, with support for the
 *   following subfields:
 *   - `fieldRef`: References a specific field within a Kubernetes resource.
 *   - `resourceFieldRef`: References a resource field.
 *   - `configMapKeyRef`: References a key in a ConfigMap.
 *   - `secretKeyRef`: References a key in a Secret.
 *
 * Exceptions:
 * - Throws `IllegalArgumentException` if neither `value` nor `valueFrom` is found in the input.
 * - Throws `IllegalArgumentException` if an unknown source type is specified within `valueFrom`.
 */
internal class SingleEnvironmentSpecDeserializer : ValueDeserializer<SingleEnvironmentSpec>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): SingleEnvironmentSpec {
        val node = p.readValueAsTree<JsonNode>()
        val name = node.get("name").asString()

        val source = when {
            node.has("value") -> {
                SingleEnvironmentSpec.ValueSource(node.get("value").asString())
            }

            node.has("valueFrom") -> {
                val valueFrom = node.get("valueFrom")
                when {
                    valueFrom.has("fieldRef") -> {
                        val fieldRef = valueFrom.get("fieldRef")
                        SingleEnvironmentSpec.FieldReferenceSource(fieldRef.get("fieldPath").asString())
                    }

                    valueFrom.has("resourceFieldRef") -> {
                        val resourceFieldRef = valueFrom.get("resourceFieldRef")
                        SingleEnvironmentSpec.ResourceFieldReferenceSource(resourceFieldRef.get("resource").asString())
                    }

                    valueFrom.has("configMapKeyRef") -> {
                        val configMapKeyRef = valueFrom.get("configMapKeyRef")
                        SingleEnvironmentSpec.ConfigMapKeyReferenceSource(
                            configMapKeyRef.get("name").asString(),
                            configMapKeyRef.get("key").asString()
                        )
                    }

                    valueFrom.has("secretKeyRef") -> {
                        val secretKeyRef = valueFrom.get("secretKeyRef")
                        SingleEnvironmentSpec.SecretKeyReferenceSource(
                            secretKeyRef.get("name").asString(),
                            secretKeyRef.get("key").asString()
                        )
                    }

                    else -> throw IllegalArgumentException("Unknown valueFrom source")
                }
            }

            else -> throw IllegalArgumentException("Either value or valueFrom must be present")
        }

        return SingleEnvironmentSpec(name, source)
    }
}
