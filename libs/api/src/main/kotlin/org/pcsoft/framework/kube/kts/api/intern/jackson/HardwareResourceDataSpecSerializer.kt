package org.pcsoft.framework.kube.kts.api.intern.jackson

import org.pcsoft.framework.kube.kts.api.chart.resources.types.HardwareResourceSpec
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import org.pcsoft.framework.kube.kts.api.types.CpuValue
import org.pcsoft.framework.kube.kts.api.types.MemoryValue
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.*

/**
 * Custom serializer for the `HardwareResourceSpec.Data` class, which represents resource data
 * such as CPU, memory, ephemeral storage, and extended resources. This serializer is
 * responsible for converting `HardwareResourceSpec.Data` instances into their JSON representation.
 *
 * The serialized JSON object may include the following properties:
 * - `cpu`: CPU resource requirement or limit, if specified.
 * - `memory`: Memory resource requirement or limit, if specified.
 * - `ephemeral-storage`: Ephemeral storage resource requirement or limit, if specified.
 * - Additional extended resources as key-value pairs, if any.
 *
 * This serializer handles null values gracefully by writing a `null` JSON token
 * when the input value is null.
 *
 * Serialization is performed using the `JsonGenerator` provided, ensuring that the
 * object structure is properly opened and closed using the `writeObject` function.
 */
internal class HardwareResourceDataSpecSerializer : ValueSerializer<HardwareResourceSpec.Data>() {
    override fun serialize(
        value: HardwareResourceSpec.Data?,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeObject {
            value.cpu?.let { gen.writePOJOProperty("cpu", it) }
            value.memory?.let { gen.writePOJOProperty("memory", it) }
            value.ephemeralStorage?.let { gen.writePOJOProperty("ephemeral-storage", it) }
            value.extendedResources?.forEach { (key, value) ->
                gen.writeStringProperty(key, value)
            }
        }
    }
}

/**
 * A custom deserializer for `HardwareResourceSpec.Data` objects, extending the `ValueDeserializer` base class.
 * This deserializer is responsible for converting JSON input into instances of `HardwareResourceSpec.Data`
 * by extracting and parsing CPU, memory, ephemeral storage, and extended resource attributes.
 *
 * The input JSON is expected to be a structured object. If the node is not an object or is null, the
 * method will return null. The deserialization logic parses the following attributes:
 *
 * - `cpu`: Extracted as a string and parsed into a `CpuValue` using the `CpuValue.parse()` function.
 * - `memory`: Extracted as a string and parsed into a `MemoryValue` using the `MemoryValue.parse()` function.
 * - `ephemeral-storage`: Extracted as a string and parsed into a `MemoryValue` using the `MemoryValue.parse()` function.
 * - `extendedResources`: A map of key-value pairs derived from the remaining attributes in the JSON object,
 *   excluding the reserved keys `cpu`, `memory`, and `ephemeral-storage`.
 *
 * This deserializer enables the flexible handling of dynamic resources beyond the core CPU and memory,
 * as specified by extended resources.
 */
internal class HardwareResourceDataSpecDeserializer : ValueDeserializer<HardwareResourceSpec.Data>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): HardwareResourceSpec.Data? {
        val node = p.readValueAsTree<JsonNode>()
        if (node == null || !node.isObject) {
            return null
        }

        return HardwareResourceSpec.Data(
            cpu = node.get("cpu")?.asString()?.let { CpuValue.parse(it) },
            memory = node.get("memory")?.asString()?.let { MemoryValue.parse(it) },
            ephemeralStorage = node.get("ephemeral-storage")?.asString()?.let { MemoryValue.parse(it) },
            extendedResources = node.properties()
                .filter { !it.key.equals("cpu", true) }
                .filter { !it.key.equals("memory", true) }
                .filter { !it.key.equals("ephemeral-storage", true) }
                .associate { it.key to it.value.asString() }
        )
    }
}

