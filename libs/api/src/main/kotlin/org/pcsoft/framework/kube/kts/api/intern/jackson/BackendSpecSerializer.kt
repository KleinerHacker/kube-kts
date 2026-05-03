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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.BackendSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.ResourceBackendSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.ServiceBackendSpec
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer

/**
 * Serializer implementation for the `BackendSpec` sealed class.
 *
 * This serializer transforms different types of `BackendSpec` objects into a JSON representation.
 * The specific serialization logic depends on whether the `BackendSpec` is a `ServiceBackendSpec`
 * or a `ResourceBackendSpec`. The serialized JSON structure includes type-specific properties while
 * adhering to a consistent schema for backend types.
 *
 * Supported `BackendSpec` types:
 * - `ServiceBackendSpec`: Serialized with a `service` object containing service-specific properties.
 * - `ResourceBackendSpec`: Serialized with a `resource` object containing resource-specific properties.
 *
 * If the serialized value is `null`, a `null` JSON value is written instead.
 *
 * This serializer is typically paired with a corresponding deserializer for seamless (de)serialization
 * of `BackendSpec` objects in the context of JSON processing libraries.
 */
internal class BackendSpecSerializer : ValueSerializer<BackendSpec>() {
    override fun serialize(value: BackendSpec?, gen: JsonGenerator, ctxt: SerializationContext) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeStartObject()

        when (value) {
            is ServiceBackendSpec -> {
                gen.writeObjectPropertyStart("service")
                gen.writeStringProperty("name", value.name)
                gen.writeName("port")
                ctxt.findValueSerializer(value.port::class.java).serialize(value.port, gen, ctxt)
                gen.writeEndObject()
            }

            is ResourceBackendSpec -> {
                gen.writeObjectPropertyStart("resource")
                gen.writeStringProperty("name", value.name)
                gen.writeStringProperty("kind", value.kind)
                if (value.apiGroup == null) {
                    gen.writeNullProperty("apiGroup")
                } else {
                    gen.writeStringProperty("apiGroup", value.apiGroup)
                }
                gen.writeEndObject()
            }
        }

        gen.writeEndObject()
    }
}

/**
 * Custom deserializer for `BackendSpec`, responsible for converting JSON structures into specific
 * backend specification types, such as `ServiceBackendSpec` or `ResourceBackendSpec`.
 *
 * This deserializer identifies the type of backend based on the presence of specific properties
 * in the JSON structure ("service" or "resource") and constructs an instance of the corresponding
 * subclass of `BackendSpec`.
 *
 * Behavior:
 * - For a "service" backend, it extracts the `name` of the service and its associated `port` information,
 *   creating an instance of `ServiceBackendSpec`.
 * - For a "resource" backend, it extracts the `name`, `kind`, and optionally the `apiGroup` of the resource,
 *   creating an instance of `ResourceBackendSpec`.
 * - Throws an `IllegalArgumentException` if the JSON structure does not match recognized types.
 *
 * Expected JSON structure for "service" backend:
 * ```
 * {
 *   "service": {
 *     "name": "<service-name>",
 *     "port": { ... } // PortSpec structure
 *   }
 * }
 * ```
 *
 * Expected JSON structure for "resource" backend:
 * ```
 * {
 *   "resource": {
 *     "name": "<resource-name>",
 *     "kind": "<resource-kind>",
 *     "apiGroup": "<api-group>" // Optional
 *   }
 * }
 * ```
 */
internal class BackendSpecDeserializer : ValueDeserializer<BackendSpec>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): BackendSpec {
        val node: JsonNode = p.readValueAsTree()

        return when {
            node.has("service") -> {
                node.get("service").let { serviceNode ->
                    val name = serviceNode.get("name").asString()
                    val port = serviceNode.get("port").let { portNode ->
                        ctxt.readTreeAsValue<PortSpec>(
                            portNode,
                            ctxt.typeFactory.constructType(PortSpec::class.java)
                        )
                    }
                    ServiceBackendSpec(name, port)
                }
            }

            node.has("resource") -> {
                node.get("resource").let { resourceNode ->
                    val name = resourceNode.get("name").asString()
                    val kind = resourceNode.get("kind").asString()
                    val apiGroup = if (resourceNode.has("apiGroup") && !resourceNode.get("apiGroup").isNull) {
                        resourceNode.get("apiGroup").asString()
                    } else {
                        null
                    }
                    ResourceBackendSpec(name, kind, apiGroup)
                }
            }

            else -> throw IllegalArgumentException("Unknown backend spec type")
        }
    }
}