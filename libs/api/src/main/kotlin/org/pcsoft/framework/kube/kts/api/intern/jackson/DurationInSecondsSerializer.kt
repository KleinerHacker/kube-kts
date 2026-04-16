package org.pcsoft.framework.kube.kts.api.intern.jackson

import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import java.time.Duration

internal class DurationInSecondsSerializer : ValueSerializer<Duration>() {
    override fun serialize(
        value: Duration,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        gen.writeNumber(value.seconds)
    }
}

internal class DurationInSecondsDeserializer : ValueDeserializer<Duration>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Duration {
        return Duration.ofSeconds(p.longValue)
    }
}