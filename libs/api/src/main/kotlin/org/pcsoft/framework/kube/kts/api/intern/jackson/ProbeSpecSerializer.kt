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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.ProbeSpec
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.*
import java.time.Duration

/**
 * Serializer for the ProbeSpec class, used to convert ProbeSpec objects into JSON format.
 *
 * This serializer handles the serialization of various ProbeSpec properties, including:
 * - The specific probe action (e.g., httpGet, exec, tcpSocket, grpc), represented by the `action` property.
 * - Optional timing-related properties, which are serialized if provided:
 *   - `initialDelaySeconds`
 *   - `periodSeconds`
 *   - `timeoutSeconds`
 *   - `failureThreshold`
 *   - `successThreshold`
 *   - `terminationGracePeriodSeconds`
 *
 * If the ProbeSpec value is null, the serializer writes a null value to the JSON output.
 */
internal class ProbeSpecSerializer : ValueSerializer<ProbeSpec>() {
    override fun serialize(
        value: ProbeSpec?,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeObject {
            val name = when (value.action) {
                is ProbeSpec.HttpGetAction -> "httpGet"
                is ProbeSpec.ExecAction -> "exec"
                is ProbeSpec.TCPSocketAction -> "tcpSocket"
                is ProbeSpec.GRPCAction -> "grpc"
            }
            gen.writePOJOProperty(name, value.action)

            value.initialDelaySeconds?.let { gen.writeNumberProperty("initialDelaySeconds", it.toSeconds()) }
            value.periodSeconds?.let { gen.writeNumberProperty("periodSeconds", it.toSeconds()) }
            value.timeoutSeconds?.let { gen.writeNumberProperty("timeoutSeconds", it.toSeconds()) }
            value.failureThreshold?.let { gen.writeNumberProperty("failureThreshold", it) }
            value.successThreshold?.let { gen.writeNumberProperty("successThreshold", it) }
            value.terminationGracePeriodSeconds?.let {
                gen.writeNumberProperty("terminationGracePeriodSeconds", it.toSeconds())
            }
        }
    }
}

/**
 * A custom deserializer for the `ProbeSpec` class, which is used to parse JSON into a `ProbeSpec` object.
 *
 * This deserializer interprets various types of probe actions and their associated fields from a JSON structure,
 * allowing flexible configuration based on the specific action type: `httpGet`, `exec`, `tcpSocket`, or `grpc`.
 *
 * The `ProbeSpecDeserializer` processes the following fields from the JSON input:
 * - `httpGet`: Deserializes the HTTP GET action configuration into a `ProbeSpec.HttpGetAction` instance.
 * - `exec`: Deserializes the exec action configuration into a `ProbeSpec.ExecAction` instance.
 * - `tcpSocket`: Deserializes the TCP socket action configuration into a `ProbeSpec.TCPSocketAction` instance.
 * - `grpc`: Deserializes the gRPC action configuration into a `ProbeSpec.GRPCAction` instance.
 *
 * Additionally, the deserializer supports optional fields for configuring the probe's timing and thresholds:
 * - `initialDelaySeconds`: The duration to wait before initializing the probe.
 * - `periodSeconds`: The interval between successive probes.
 * - `timeoutSeconds`: The timeout duration for each probe.
 * - `failureThreshold`: The number of consecutive probe failures required to determine failure.
 * - `successThreshold`: The number of consecutive probe successes required to determine success.
 * - `terminationGracePeriodSeconds`: The duration to wait before terminating the probe on failure.
 *
 * Throws an `IllegalArgumentException` if no valid probe action is found in the input JSON.
 */
internal class ProbeSpecDeserializer : ValueDeserializer<ProbeSpec>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ProbeSpec {
        val node: JsonNode = p.readValueAsTree()

        return ProbeSpec(
            action = when {
                node.has("httpGet") -> ctxt.readTreeAsValue(node.get("httpGet"), ProbeSpec.HttpGetAction::class.java)
                node.has("exec") -> ctxt.readTreeAsValue(node.get("exec"), ProbeSpec.ExecAction::class.java)
                node.has("tcpSocket") -> ctxt.readTreeAsValue(node.get("tcpSocket"), ProbeSpec.TCPSocketAction::class.java)
                node.has("grpc") -> ctxt.readTreeAsValue(node.get("grpc"), ProbeSpec.GRPCAction::class.java)
                else -> throw IllegalArgumentException("No valid probe action found")
            },
            initialDelaySeconds = node.get("initialDelaySeconds")?.asLong()?.let { Duration.ofSeconds(it) },
            periodSeconds = node.get("periodSeconds")?.asLong()?.let { Duration.ofSeconds(it) },
            timeoutSeconds = node.get("timeoutSeconds")?.asLong()?.let { Duration.ofSeconds(it) },
            failureThreshold = node.get("failureThreshold")?.asInt(),
            successThreshold = node.get("successThreshold")?.asInt(),
            terminationGracePeriodSeconds =
                node.get("terminationGracePeriodSeconds")?.asLong()?.let { Duration.ofSeconds(it) }
        )
    }
}