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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.RoutePortSpec
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer

/**
 * Serializer for [RoutePortSpec].
 *
 * Emits the OpenShift Route port structure with a single `targetPort` value, which is either a
 * numeric port or a named port:
 * ```
 * port:
 *   targetPort: 8080      # or: targetPort: http
 * ```
 */
internal class RoutePortSpecSerializer : ValueSerializer<RoutePortSpec>() {
    override fun serialize(value: RoutePortSpec?, gen: JsonGenerator, ctxt: SerializationContext) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeStartObject()
        when {
            value.targetPortNumber != null -> gen.writeNumberProperty("targetPort", value.targetPortNumber)
            value.targetPortName != null -> gen.writeStringProperty("targetPort", value.targetPortName)
        }
        gen.writeEndObject()
    }
}

/**
 * Deserializer for [RoutePortSpec], reading back the single `targetPort` value as either a numeric
 * or a named target port.
 */
internal class RoutePortSpecDeserializer : ValueDeserializer<RoutePortSpec>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): RoutePortSpec {
        val node: JsonNode = p.readValueAsTree()
        val targetPort = node.get("targetPort")

        return when {
            targetPort == null || targetPort.isNull -> RoutePortSpec(null, null)
            targetPort.isNumber -> RoutePortSpec(null, targetPort.asInt())
            else -> RoutePortSpec(targetPort.asString(), null)
        }
    }
}
