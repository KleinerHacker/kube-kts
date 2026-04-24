package org.pcsoft.framework.kube.kts.api.chart.types

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.*
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.net.URI

@NoArgs
data class DependencySpec(
    val name: String,
    val version: String,
    val repository: URI?,
    val alias: String?,
    val condition: String?,
    val tags: Set<String>?,
    @field:JsonProperty("import-values")
    val importValues: List<ImportValue>?,
) {
    @JsonSerialize(using = ImportValueSerializer::class)
    @JsonDeserialize(using = ImportValueDeserializer::class)
    sealed interface ImportValue

    @NoArgs
    data class PathImportValue(val path: String) : ImportValue

    @NoArgs
    data class MappingImportValue(val child: String, val parent: String) : ImportValue
}

internal class ImportValueSerializer : ValueSerializer<DependencySpec.ImportValue>() {
    override fun serialize(
        value: DependencySpec.ImportValue?,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        if (value == null) {
            return
        }

        when (value) {
            is DependencySpec.PathImportValue -> gen.writeString(value.path)
            is DependencySpec.MappingImportValue -> {
                gen.writeStartObject()
                gen.writeStringProperty("child", value.child)
                gen.writeStringProperty("parent", value.parent)
                gen.writeEndObject()
            }
        }
    }
}

internal class ImportValueDeserializer : ValueDeserializer<DependencySpec.ImportValue>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): DependencySpec.ImportValue? {
        val tree = p.readValueAsTree<JsonNode>()
        if (tree.isString) {
            return DependencySpec.PathImportValue(tree.stringValue())
        } else if (tree.isObject) {
            return DependencySpec.MappingImportValue(tree["child"].stringValue(), tree["parent"].stringValue())
        } else if (tree.isNull) {
            return null
        }

        throw NotImplementedError()
    }

}
