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
 * Represents a dependency specification for a software module or component.
 *
 * @property name The name of the dependency.
 * @property version The version of the dependency.
 * @property repository The URI of the repository hosting the dependency, or null if unspecified.
 * @property alias An optional alias for the dependency, allowing it to be referred to by a different name.
 * @property condition An optional condition that determines when the dependency should be included.
 * @property tags A set of optional tags categorizing or describing the dependency.
 * @property importValues A list of values to import from a sub-chart, allowing custom configuration mappings.
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
     * Represents a value to be imported from a sub-chart in a dependency specification.
     *
     * This is a sealed interface with two concrete implementations:
     * - `PathImportValue`: Specifies a path to be imported.
     * - `MappingImportValue`: Specifies a mapping between a child path in the sub-chart and a parent path.
     *
     * This interface supports custom serialization and deserialization through the
     * `ImportValueSerializer` and `ImportValueDeserializer` classes, allowing the instances
     * to be represented as either strings or objects in JSON, depending on the implementation.
     */
    @JsonSerialize(using = ImportValueSerializer::class)
    @JsonDeserialize(using = ImportValueDeserializer::class)
    sealed interface ImportValue

    /**
     * Represents a specific import value in the form of a path within a dependency specification.
     *
     * A `PathImportValue` defines a single path to be imported from a sub-chart in a dependency setup.
     * It is a concrete implementation of the `ImportValue` interface and is utilized for scenarios
     * where importing a specific path suffices without requiring additional mapping.
     *
     * This class is marked with the `@NoArgs` annotation, ensuring it can be instantiated
     * without arguments, which may be required by frameworks or serialization mechanisms.
     *
     * @property path Defines the import path as a string. This is used to locate the target
     * resource in the context of a sub-chart or dependency.
     */
    @NoArgs
    data class PathImportValue(val path: String) : ImportValue

    /**
     * Represents a mapping between a child path and a parent path in an import value.
     *
     * This class is a concrete implementation of the `ImportValue` interface and is primarily
     * used within dependency specifications to define how a value from a child chart is mapped
     * to a location within the parent chart.
     *
     * @property child The path or key in the sub-chart that is to be referenced.
     * @property parent The path or key in the parent where the value from the child should be applied.
     */
    @NoArgs
    data class MappingImportValue(val child: String, val parent: String) : ImportValue
}

/**
 * A serializer for serializing instances of `DependencySpec.ImportValue` into JSON.
 *
 * This serializer handles the different concrete types of the `ImportValue` sealed interface:
 * - For `PathImportValue`, it writes the path value as a JSON string.
 * - For `MappingImportValue`, it writes the child and parent values as a JSON object with properties `child` and `parent`.
 *
 * If the provided value is null, no action is performed.
 */
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

/**
 * A custom deserializer for converting JSON input into instances of `DependencySpec.ImportValue`.
 *
 * This deserializer handles different JSON structures to create the appropriate implementation
 * of the `DependencySpec.ImportValue` sealed interface. The following JSON structures are supported:
 *
 * 1. A string value is deserialized into a `PathImportValue`.
 * 2. An object with `child` and `parent` fields is deserialized into a `MappingImportValue`.
 * 3. A null value is deserialized as `null`.
 *
 * If the input JSON does not conform to any of these structures, a `NotImplementedError` is thrown.
 */
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
