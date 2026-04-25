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