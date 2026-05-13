package org.pcsoft.framework.kube.kts.api.intern.jackson

import org.pcsoft.framework.kube.kts.api.chart.resources.types.CompleteEnvironmentSpec
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer

/**
 * Custom serializer for the `CompleteEnvironmentSpec` class, enabling its conversion to JSON format.
 *
 * This serializer is responsible for serializing `CompleteEnvironmentSpec` objects into their JSON representation,
 * adhering to a structure commonly used in Kubernetes resource definitions. It manages optional values and handles
 * the nuances of the nested `Source` object serialization.
 *
 * The output JSON includes the following properties:
 * - `prefix`: A string representing the prefix to be appended to environment variables, if specified.
 * - `source`: A nested object indicating the source of the environment variables. It includes:
 *   - The source type (`ConfigMap` or `Secret`) as the parent JSON object (e.g., `configMapRef` or `secretRef`).
 *   - `name`: The name of the ConfigMap or Secret.
 *   - `optional`: A boolean indicating whether the source is optional (included only if not null).
 *
 * The serializer ensures compatibility with the expected formats in Kubernetes manifests.
 */
internal class CompleteEnvironmentSpecSerializer : ValueSerializer<CompleteEnvironmentSpec>() {
    override fun serialize(
        value: CompleteEnvironmentSpec?,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeObject {
            gen.writeStringProperty("prefix", value.prefix)
            when (value.source.type) {
                CompleteEnvironmentSpec.SourceType.ConfigMap -> gen.writeObjectPropertyStart("configMapRef")
                CompleteEnvironmentSpec.SourceType.Secret -> gen.writeObjectPropertyStart("secretRef")
            }
            gen.writeStringProperty("name", value.source.name)
            if (value.source.optional != null) {
                gen.writeBooleanProperty("optional", value.source.optional)
            }
            gen.writeEndObject()
        }
    }
}

/**
 * A custom deserializer for the `CompleteEnvironmentSpec` class, responsible for converting JSON
 * representations of environment specifications into `CompleteEnvironmentSpec` objects.
 *
 * This deserializer handles the parsing of JSON nodes to identify and construct the appropriate
 * source type for environment variables, which can reference either a Kubernetes ConfigMap or a
 * Secret. The deserialization process also accommodates an optional `prefix` field for environment
 * variables.
 *
 * The deserialization process recognizes two primary source types:
 * - `configMapRef`: Refers to a ConfigMap-based environment specification, including its name and
 *   whether it is optional.
 * - `secretRef`: Refers to a Secret-based environment specification, including its name and whether
 *   it is optional.
 *
 * This implementation throws an `IllegalStateException` if neither `configMapRef` nor `secretRef`
 * are provided in the JSON node, as it cannot determine the appropriate source type.
 *
 * Inherits from the `ValueDeserializer` base class, providing the override for the `deserialize` method
 * used to perform the custom deserialization logic.
 *
 * @constructor Creates an instance of the `CompleteEnvironmentSpecDeserializer` class.
 *
 * @see CompleteEnvironmentSpec
 * @see CompleteEnvironmentSpec.Source
 * @see CompleteEnvironmentSpec.SourceType
 */
internal class CompleteEnvironmentSpecDeserializer : ValueDeserializer<CompleteEnvironmentSpec>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): CompleteEnvironmentSpec? {
        val node = p.readValueAsTree<JsonNode>()
        if (node == null || !node.isObject) {
            return null
        }

        val prefix = node.get("prefix")?.asString()

        return when {
            node.has("configMapRef") -> CompleteEnvironmentSpec(
                prefix,
                CompleteEnvironmentSpec.Source(
                    CompleteEnvironmentSpec.SourceType.ConfigMap,
                    node.get("configMapRef").get("name").asString(),
                    node.get("configMapRef").get("optional")?.asBoolean()
                )
            )
            node.has("secretRef") -> CompleteEnvironmentSpec(
                prefix,
                CompleteEnvironmentSpec.Source(
                    CompleteEnvironmentSpec.SourceType.Secret,
                    node.get("secretRef").get("name").asString(),
                    node.get("secretRef").get("optional")?.asBoolean()
                )
            )
            else -> throw IllegalStateException("Unknown source type")
        }
    }
}

