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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.NodeSelectorTermSpec
import org.pcsoft.framework.kube.kts.api.intern.utils.writeArrayProperty
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer

/**
 * Custom serializer for the `NodeSelector` class, responsible for serializing a `NodeSelector` instance
 * as a JSON object with a single property "nodeSelectorTerms" containing an array of `NodeSelectorTermSpec` objects.
 */
class NodeSelectorSerializer : ValueSerializer<List<NodeSelectorTermSpec>>() {
    override fun serialize(
        value: List<NodeSelectorTermSpec>?,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeObject {
            gen.writeArrayProperty("nodeSelectorTerms") {
                value.forEach { gen.writePOJO(it) }
            }
        }
    }
}

/**
 * Custom deserializer for the `NodeSelector` class, responsible for deserializing a JSON object
 * with a single property "nodeSelectorTerms" containing an array of `NodeSelectorTermSpec` objects.
 */
class NodeSelectorDeserializer : ValueDeserializer<List<NodeSelectorTermSpec>>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): List<NodeSelectorTermSpec>? {
        val node: JsonNode = p.readValueAsTree()

        if (node.isNull)
            return null
        if (!node.isObject)
            throw IllegalArgumentException("Expected object")

        val nodeSelectorTermsNode = node.get("nodeSelectorTerms")
            ?: throw IllegalArgumentException("Missing nodeSelectorTerms")

        if (!nodeSelectorTermsNode.isArray)
            throw IllegalArgumentException("Expected array")

        return nodeSelectorTermsNode.asArray().toList().map { termNode ->
            ctxt.readTreeAsValue(termNode, NodeSelectorTermSpec::class.java)
        }
    }
}

