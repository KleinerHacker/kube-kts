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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeSpec
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer

/**
 * A custom serializer for the [VolumeSpec] class that converts VolumeSpec instances
 * into a JSON representation based on their specific source type.
 *
 * This serializer checks the type of the [VolumeSpec.source] property and serializes it 
 * accordingly into one of the predefined source types: secret, configMap, persistentVolumeClaim, 
 * hostPath, or emptyDir. If the value is null, it writes a JSON null value.
 *
 * This implementation ensures a dynamic handling of [VolumeSpec] objects during serialization
 * by delegating the source-specific serialization to their respective POJO properties.
 *
 * Throws exceptions if the internal object writer (JsonGenerator) encounters errors during serialization
 * or if the data structure being serialized is invalid.
 */
class VolumeSpecSerializer : ValueSerializer<VolumeSpec>() {
    override fun serialize(
        value: VolumeSpec?,
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
                is VolumeSpec.SecretSourceSpec -> gen.writePOJOProperty("secret", value.source)
                is VolumeSpec.ConfigMapSourceSpec -> gen.writePOJOProperty("configMap", value.source)
                is VolumeSpec.PersistentVolumeClaimSourceSpec -> gen.writePOJOProperty("persistentVolumeClaim", value.source)
                is VolumeSpec.HostPathSourceSpec -> gen.writePOJOProperty("hostPath", value.source)
                is VolumeSpec.EmptyDirSourceSpec -> gen.writePOJOProperty("emptyDir", value.source)
            }
        }
    }
}

/**
 * A custom deserializer for the [VolumeSpec] class that converts JSON representations
 * back into VolumeSpec instances based on their specific source type.
 *
 * This deserializer reads the JSON object and determines the source type based on which
 * source property is present (secret, configMap, persistentVolumeClaim, hostPath, or emptyDir).
 * It then deserializes the source-specific data into the appropriate VolumeSpec.SourceSpec subtype.
 *
 * This implementation ensures proper reconstruction of [VolumeSpec] objects during deserialization
 * by handling each source type dynamically.
 *
 * Throws exceptions if the JSON structure is invalid or if required fields are missing.
 */
class VolumeSpecDeserializer : ValueDeserializer<VolumeSpec>() {
    override fun deserialize(
        p: tools.jackson.core.JsonParser,
        ctxt: tools.jackson.databind.DeserializationContext
    ): VolumeSpec {
        val node: JsonNode = p.readValueAsTree()
        val name = node.get("name").asString()

        val source = when {
            node.has("secret") -> ctxt.readTreeAsValue(node.get("secret"), VolumeSpec.SecretSourceSpec::class.java)
            node.has("configMap") -> ctxt.readTreeAsValue(
                node.get("configMap"),
                VolumeSpec.ConfigMapSourceSpec::class.java
            )

            node.has("persistentVolumeClaim") -> ctxt.readTreeAsValue(
                node.get("persistentVolumeClaim"),
                VolumeSpec.PersistentVolumeClaimSourceSpec::class.java
            )

            node.has("hostPath") -> ctxt.readTreeAsValue(node.get("hostPath"), VolumeSpec.HostPathSourceSpec::class.java)
            node.has("emptyDir") -> ctxt.readTreeAsValue(node.get("emptyDir"), VolumeSpec.EmptyDirSourceSpec::class.java)
            else -> throw IllegalArgumentException("Unknown volume source type")
        }

        return VolumeSpec(name, source)
    }
}