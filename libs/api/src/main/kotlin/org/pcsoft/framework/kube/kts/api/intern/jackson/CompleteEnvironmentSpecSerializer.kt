package org.pcsoft.framework.kube.kts.api.intern.jackson

import org.pcsoft.framework.kube.kts.api.chart.resources.types.CompleteEnvironmentSpec
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer


class CompleteEnvironmentSpecSerializer : ValueSerializer<CompleteEnvironmentSpec>() {
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

class CompleteEnvironmentSpecDeserializer : ValueDeserializer<CompleteEnvironmentSpec>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): CompleteEnvironmentSpec? {
        val node = p.readValueAsTree<tools.jackson.databind.JsonNode>()
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

