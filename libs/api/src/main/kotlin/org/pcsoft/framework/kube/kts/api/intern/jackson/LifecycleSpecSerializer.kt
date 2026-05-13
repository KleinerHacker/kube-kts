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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.LifecycleSpec
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer

/**
 * A custom serializer for the `LifecycleSpec.Action` type, which serializes different implementations
 * of the `Action` sealed interface into their respective JSON representations.
 *
 * This serializer handles three specific subtypes of `LifecycleSpec.Action`:
 * - `ExecAction`: Serialized as a property named "exec" containing the command details.
 * - `HttpGetAction`: Serialized as a property named "httpGet" with HTTP request details.
 * - `SleepAction`: Serialized as a property named "sleep" representing the delay duration.
 *
 * When an action is null, the serializer writes a JSON null value. For valid instances, the output
 * is wrapped in a JSON object, and the appropriate property is populated based on the specific
 * `Action` implementation.
 *
 * This class is typically used in conjunction with `LifecycleSpec` to serialize lifecycle events
 * like `postStart` and `preStop` into appropriate JSON structures.
 */
internal class LifecycleSpecActionSerializer : ValueSerializer<LifecycleSpec.Action>() {
    override fun serialize(
        value: LifecycleSpec.Action?, gen: JsonGenerator, ctxt: SerializationContext
    ) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeObject {
            when (value) {
                is LifecycleSpec.ExecAction -> gen.writePOJOProperty("exec", value)
                is LifecycleSpec.HttpGetAction -> gen.writePOJOProperty("httpGet", value)
                is LifecycleSpec.SleepAction -> gen.writePOJOProperty("sleep", value)
            }
        }
    }
}

/**
 * A custom deserializer for the `LifecycleSpec.Action` type, which deserializes JSON representations
 * into their respective implementations of the `Action` sealed interface.
 *
 * This deserializer handles three specific subtypes of `LifecycleSpec.Action`:
 * - `ExecAction`: Deserialized from a property named "exec" containing the command details.
 * - `HttpGetAction`: Deserialized from a property named "httpGet" with HTTP request details.
 * - `SleepAction`: Deserialized from a property named "sleep" representing the delay duration.
 *
 * When the JSON input is null, the deserializer returns null. For valid JSON objects, it examines
 * the available properties and instantiates the appropriate `Action` implementation based on which
 * property is present.
 *
 * This class is typically used in conjunction with `LifecycleSpec` to deserialize lifecycle events
 * like `postStart` and `preStop` from JSON structures.
 */
internal class LifecycleSpecActionDeserializer : ValueDeserializer<LifecycleSpec.Action>() {
    override fun deserialize(
        p: JsonParser, ctxt: DeserializationContext
    ): LifecycleSpec.Action? {
        val node: JsonNode = p.readValueAsTree()

        if (node.isNull) {
            return null
        }

        return when {
            node.has("exec") -> {
                ctxt.readTreeAsValue(node.get("exec"), LifecycleSpec.ExecAction::class.java)
            }

            node.has("httpGet") -> {
                ctxt.readTreeAsValue(node.get("httpGet"), LifecycleSpec.HttpGetAction::class.java)
            }

            node.has("sleep") -> {
                ctxt.readTreeAsValue(node.get("sleep"), LifecycleSpec.SleepAction::class.java)
            }

            else -> throw IllegalArgumentException("Unknown lifecycle action type")
        }
    }
}