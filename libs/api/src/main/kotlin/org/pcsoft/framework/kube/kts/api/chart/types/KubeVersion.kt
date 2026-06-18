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

package org.pcsoft.framework.kube.kts.api.chart.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.*
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Represents a Kubernetes version consisting of multiple version items with associated equality operators.
 *
 * The class provides functionality for serializing and deserializing Kubernetes versions,
 * as well as parsing version strings into structured [KubeVersion] objects. Each version
 * item is represented by the nested [Item] class, which includes a version string and an
 * associated equality operator from the [ItemEquality] enum.
 *
 * The [KubeVersion] class also supports converting the version back to its string
 * representation using the [toString] method.
 *
 * @constructor Creates a new instance with a list of [Item] objects.
 *
 * @property items The list of version items in this Kubernetes version.
 */
@NoArgs
@JsonSerialize(using = KubeVersionSerializer::class)
@JsonDeserialize(using = KubeVersionDeserializer::class)
data class KubeVersion(val items: List<Item>) {

    companion object {
        /**
         * Parses a version string and converts it into a KubeVersion object.
         *
         * @param version The version string to be parsed.
         *                The version string should contain space-separated items that represent version constraints.
         * @return A KubeVersion object containing the parsed version constraints as items.
         */
        fun parse(version: String): KubeVersion {
            val items = version.split(" ")
                .filter { it.isNotBlank() }
                .map { Item.parse(it) }
            return KubeVersion(items)
        }
    }

    /**
     * Returns a string representation of the object. This object can parse back to a KubeVersion object using [parse].
     *
     * @return A string consisting of all items joined by a space.
     */
    override fun toString(): String {
        return items.joinToString(" ")
    }

    /**
     * Represents a single version item with an associated equality operator,
     * used for version comparison in the context of a [KubeVersion].
     *
     * @property version The version string associated with this item.
     * @property equality The equality operator used for version comparison.
     */
    data class Item(val version: String, val equality: ItemEquality) {
        companion object {
            /**
             * Parses a version string to create an Item object representing the version and its associated equality operator.
             *
             * @param version The version string that may include an equality operator (e.g., ">=1.0").
             * @return An Item object containing the parsed version string and the associated equality operator.
             */
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

        /**
         * Converts the version item to its string representation.
         * The result is a combination of the equality operator and the version string.
         * The result can parse back to an Item object using [parse].
         *
         * @return A string representation of the version item, formatted as "{equality operator}{version}".
         */
        override fun toString(): String = "${equality.operator}$version"
    }

    /**
     * Represents the equality operator for a version item in the context of version comparisons.
     *
     * @property operator The string representation of the equality operator.
     */
    enum class ItemEquality(val operator: String) {
        /**
         * Represents the equality operator `=` in the context of version comparisons.
         * 
         * Example: `=1.21.0` matches exactly version 1.21.0
         */
        EQUAL("="),

        /**
         * Represents the greater-than operator (>) in the context of version comparisons.
         * 
         * Example: `>1.21.0` matches versions greater than 1.21.0
         */
        GREATER(">"),

        /**
         * Represents the less-than operator (<) in the context of version comparisons.
         * 
         * Example: `<1.21.0` matches versions less than 1.21.0
         */
        LESS("<"),

        /**
         * Represents the not-equal operator (!=) in the context of version comparisons.
         * 
         * Example: `!=1.21.0` matches any version except 1.21.0
         */
        NOT_EQUAL("!="),

        /**
         * Represents the greater-than-or-equal-to operator (>=) in the context of version comparisons.
         * 
         * Example: `>=1.21.0` matches version 1.21.0 and any version greater than 1.21.0
         */
        GREATER_EQUAL(">="),

        /**
         * Represents the less-than-or-equal-to operator (<=) in the context of version comparisons.
         * 
         * Example: `<=1.21.0` matches version 1.21.0 and any version less than 1.21.0
         */
        LESS_EQUAL("<=")
    }
}

/**
 * A custom serializer for the [KubeVersion] class.
 *
 * This serializer is responsible for converting a [KubeVersion] object into its string representation
 * during the serialization process. The string representation is obtained by invoking the `toString`
 * method of the [KubeVersion] object, which produces a format that can be parsed back into a [KubeVersion] instance.
 *
 * If the provided [KubeVersion] object is `null`, this serializer does nothing.
 */
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

/**
 * A custom deserializer for parsing Kubernetes version strings into [KubeVersion] objects.
 *
 * This deserializer is responsible for converting JSON representations of Kubernetes versions
 * into instances of the [KubeVersion] class. The input JSON is expected to be a string that can
 * be parsed by the [KubeVersion.parse] method. If the input JSON is null, the deserializer
 * returns `null`.
 *
 * The [KubeVersion] object encapsulates a version string, consisting of version constraints
 * and equality operators used to specify Kubernetes version requirements.
 *
 * This deserializer extends `ValueDeserializer` to leverage the deserialization mechanism
 * provided by the framework for constructing structured objects from JSON trees.
 */
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
