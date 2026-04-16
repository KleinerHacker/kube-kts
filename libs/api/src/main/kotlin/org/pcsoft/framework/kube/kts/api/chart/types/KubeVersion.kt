package org.pcsoft.framework.kube.kts.api.chart.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.*
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

@NoArgs
@JsonSerialize(using = KubeVersionSerializer::class)
@JsonDeserialize(using = KubeVersionDeserializer::class)
data class KubeVersion(val items: List<Item>) {

    companion object {
        fun parse(version: String): KubeVersion {
            val items = version.split(" ")
                .filter { it.isNotBlank() }
                .map { Item.parse(it) }
            return KubeVersion(items)
        }
    }

    override fun toString(): String {
        return items.joinToString(" ")
    }

    data class Item(val version: String, val equality: ItemEquality) {
        companion object {
            fun parse(version: String): Item {
                val equality = when {
                    version.startsWith(">") -> ItemEquality.GREATER
                    version.startsWith("<") -> ItemEquality.LESS
                    version.startsWith("!=") -> ItemEquality.NOT_EQUAL
                    version.startsWith(">=") -> ItemEquality.GREATER_EQUAL
                    version.startsWith("<=") -> ItemEquality.LESS_EQUAL
                    else -> ItemEquality.EQUAL
                }

                return Item(version.substring(equality.operator.length), equality)
            }
        }

        override fun toString(): String = "${equality.operator}$version"
    }

    enum class ItemEquality(val operator: String) {
        EQUAL("="), GREATER(">"), LESS("<"), NOT_EQUAL("!="), GREATER_EQUAL(">="), LESS_EQUAL("<=")
    }
}

internal class KubeVersionSerializer : ValueSerializer<KubeVersion>() {
    override fun serialize(
        value: KubeVersion?,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        if (value == null) {
            return
        }

        gen.writeString(value.toString())
    }
}

internal class KubeVersionDeserializer : ValueDeserializer<KubeVersion>() {
    override fun deserialize(
        gen: JsonParser,
        ctxt: DeserializationContext
    ): KubeVersion? {
        val tree = gen.readValueAsTree<JsonNode>()
        if (tree.isNull)
            return null

        val version = tree.stringValue()
        return KubeVersion.parse(version)
    }
}
