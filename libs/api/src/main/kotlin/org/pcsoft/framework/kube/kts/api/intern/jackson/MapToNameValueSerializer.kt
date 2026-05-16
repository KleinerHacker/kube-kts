/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
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

import org.pcsoft.framework.kube.kts.api.intern.utils.writeArray
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer

/**
 * Custom serializer for a `Map<String, String>` object that converts the map into a JSON array of objects.
 * Each entry in the map is serialized as an object with two fields: "name" and "value".
 *
 * - The "name" field corresponds to the key of the map entry.
 * - The "value" field corresponds to the value of the map entry.
 *
 * If the map is null, this serializer writes a null value to the output.
 *
 * The serialization process:
 * 1. Starts writing a JSON array.
 * 2. Iterates through each map entry and writes an object containing "name" and "value" fields.
 * 3. Ensures that the array and objects are properly closed regardless of any exceptions during the process.
 */
internal class MapToNameValueSerializer : ValueSerializer<Map<String, String>>() {
    override fun serialize(
        value: Map<String, String>?,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeArray {
            value.forEach { (key, value) ->
                gen.writeObject {
                    gen.writeStringProperty("name", key)
                    gen.writeStringProperty("value", value)
                }
            }
        }
    }
}

/**
 * A custom deserializer that converts a JSON array of objects with "name" and "value" fields into a `Map<String, String>`.
 *
 * This deserializer performs the following steps:
 * - Reads the JSON input as a tree structure.
 * - If the node is null, returns null.
 * - If the node is not an array, throws an `IllegalArgumentException`.
 * - Iterates over each element in the array, extracting the "name" and "value" fields.
 * - Constructs a map where each "name" becomes a key and the corresponding "value" becomes the value.
 *
 * The input JSON is expected to be an array of objects, where each object contains:
 * - `name`: The key associated with the value.
 * - `value`: The value associated with the key.
 *
 * Throws an `IllegalArgumentException` if the input JSON does not conform to the expected array structure.
 */
internal class MapToNameValueDeserializer : ValueDeserializer<Map<String, String>>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Map<String, String>? {
        val node: JsonNode = p.readValueAsTree()
        if (node.isNull)
            return null
        if (!node.isArray)
            throw IllegalArgumentException("Expected array")

        val result = mutableMapOf<String, String>()
        node.forEach { entry ->
            result[entry["name"].asString()] = entry["value"].asString()
        }
        return result
    }

}