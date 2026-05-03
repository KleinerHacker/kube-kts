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

import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import java.time.Duration

/**
 * Custom serializer for `Duration` objects, responsible for serializing a `Duration` instance
 * as the total number of seconds.
 *
 * This serializer writes the `Duration` value as a single numeric value representing the
 * total seconds, which is derived from the `Duration.seconds` property.
 *
 * Key Features:
 * - Serializes the `Duration` object directly to a JSON number.
 * - Provides a human-readable representation for durations in terms of total seconds.
 *
 * Typical usage includes scenarios where duration values are represented consistently
 * in seconds within the JSON representation, facilitating straightforward processing
 * and readability.
 */
internal class DurationInSecondsSerializer : ValueSerializer<Duration>() {
    override fun serialize(
        value: Duration,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        gen.writeNumber(value.seconds)
    }
}

/**
 * Custom deserializer for `Duration` objects, designed to interpret a JSON numeric value as a duration in seconds.
 *
 * This deserializer expects the JSON value to be a long or integer representing the duration in seconds.
 * The value is converted into a `Duration` instance using `Duration.ofSeconds`.
 *
 * Behavior:
 * - If the JSON value is a numeric type, it is interpreted as seconds and directly converted to a `Duration`.
 * - If the JSON value is null or not a valid number, deserialization will throw an exception.
 *
 * This deserializer is typically used in contexts where duration values are represented as seconds in JSON
 * and need to be converted into a Kotlin or Java `Duration` type.
 */
internal class DurationInSecondsDeserializer : ValueDeserializer<Duration>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Duration {
        return Duration.ofSeconds(p.longValue)
    }
}