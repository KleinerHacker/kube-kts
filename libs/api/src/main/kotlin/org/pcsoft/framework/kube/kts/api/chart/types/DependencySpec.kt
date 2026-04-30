package org.pcsoft.framework.kube.kts.api.chart.types

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.*
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.net.URI

/**
 * Represents a dependency for a Helm chart.
 *
 * @property name The name of the chart you want to depend on.
 * @property version The version of the chart you want to depend on.
 * @property repository The repository URL or alias for the chart.
 * @property alias The alias for the chart (if you want to use a different name).
 * @property condition A yaml path that resolves to a boolean, used to enable/disable the chart.
 * @property tags A list of tags that can be used to enable/disable the chart.
 * @property importValues A list of values to import from the sub-chart.
 */
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
    /**
     * Represents a value to import from a sub-chart.
     */
    @JsonSerialize(using = ImportValueSerializer::class)
    @JsonDeserialize(using = ImportValueDeserializer::class)
    sealed interface ImportValue

    /**
     * Imports values by path.
     *
     * @property path The path to import.
     */
    @NoArgs
    data class PathImportValue(val path: String) : ImportValue

    /**
     * Imports values with a mapping between child and parent.
     *
     * @property child The path in the sub-chart.
     * @property parent The path in the parent chart.
     */
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
