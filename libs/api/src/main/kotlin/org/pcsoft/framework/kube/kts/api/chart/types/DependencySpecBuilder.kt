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

import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec.ImportValue
import java.net.URI

/**
 * Builds and configures a specification for defining a Helm chart dependency.
 *
 * @constructor Creates a DependencySpecBuilder instance with the specified chart name and version.
 * @property name The name of the chart you want to depend on.
 * @property version The version of the chart you want to depend on.
 *
 * @see DependencySpec
 */
class DependencySpecBuilder internal constructor(private val name: String, private val version: String) {
    private var importValues: MutableList<ImportValue>? = null
    private var tags: MutableSet<String>? = null

    /**
     * The repository URL or alias for the chart.
     */
    var repository: URI? = null

    /**
     * The alias for the chart (if you want to use a different name).
     */
    var alias: String? = null

    /**
     * A yaml path that resolves to a boolean, used to enable/disable the chart.
     */
    var condition: String? = null

    /**
     * Adds a path import value to the list of import values for the dependency.
     *
     * @param path The path to import from the sub-chart.
     */
    fun addPathImportValue(path: String) {
        if (importValues == null) {
            importValues = mutableListOf()
        }
        importValues!!.add(DependencySpec.PathImportValue(path))
    }

    /**
     * Adds multiple path import values to the dependency's list of import values.
     *
     * @param paths The paths to import from the sub-chart.
     */
    fun addPathImportValues(vararg paths: String) {
        if (importValues == null) {
            importValues = mutableListOf()
        }
        importValues!!.addAll(paths.map { DependencySpec.PathImportValue(it) })
    }

    /**
     * Configures a list of path import values for the dependency.
     *
     * This method allows specifying multiple path-based import values using a builder pattern. 
     * Path import values define specific paths in sub-charts whose values should be imported 
     * into the parent chart. The provided `prepare` block is used to define these import values.
     *
     * Example:
     * ```kotlin
     * pathImportValues {
     *     pathImportValue("exports.data")
     *     pathImportValue("exports.config")
     * }
     * ```
     *
     * @param prepare A lambda with receiver of `PathImportValueListBuilder` used to configure 
     * the list of path import values.
     */
    fun pathImportValues(prepare: PathImportValueListBuilder.() -> Unit) {
        PathImportValueListBuilder().apply(prepare)
    }

    /**
     * Adds a mapping import value to the dependency's list of import values.
     *
     * A mapping import value defines a key-value relationship where `key` represents 
     * a path in the sub-chart (child) and `value` represents a path in the parent chart.
     *
     * @param key The path in the sub-chart to map.
     * @param value The path in the parent chart to map.
     */
    fun addMappingImportValue(key: String, value: String) {
        if (importValues == null) {
            importValues = mutableListOf()
        }
        importValues!!.add(DependencySpec.MappingImportValue(key, value))
    }

    /**
     * Configures a list of mapping import values for the dependency.
     *
     * This method uses a builder pattern to define key-value mappings, where each mapping
     * specifies the relationship between a path in the sub-chart (child) and a corresponding
     * path in the parent chart. The provided `prepare` block is used to build these mappings.
     *
     * Example:
     * ```kotlin
     * mappingImportValues {
     *     mappingImportValue("child.exports.data", "parent.imports.data")
     *     mappingImportValue("child.exports.config", "parent.imports.config")
     * }
     * ```
     *
     * @param prepare A lambda with receiver of `MappingImportValueListBuilder` used to configure
     * the list of mapping import values.
     */
    fun mappingImportValues(prepare: MappingImportValueListBuilder.() -> Unit) {
        MappingImportValueListBuilder().apply(prepare)
    }

    /**
     * Adds a tag to the dependency's set of tags.
     *
     * If the set of tags is null, it initializes a mutable set to store the tags.
     *
     * @param tag The tag to be added.
     */
    fun addTag(tag: String) {
        if (tags == null) {
            tags = mutableSetOf()
        }
        tags!!.add(tag)
    }

    /**
     * Adds multiple tags to the dependency's set of tags.
     *
     * If the set of tags is null, it initializes a mutable set to store the tags.
     *
     * @param tags The tags to be added.
     */
    fun addTags(vararg tags: String) {
        if (this.tags == null) {
            this.tags = mutableSetOf()
        }
        this.tags!!.addAll(tags.toSet())
    }

    /**
     * Configures a list of tags for the dependency.
     *
     * This method uses a builder pattern to allow defining multiple tags 
     * in a concise and fluent manner. Tags can be used for categorizing 
     * or labeling dependencies, enabling better organization and filtering 
     * within configuration management.
     *
     * Example:
     * ```kotlin
     * tags {
     *     tag("frontend")
     *     tag("backend")
     *     tag("database")
     * }
     * ```
     *
     * @param prepare A lambda with receiver of `TagListBuilder` used to configure 
     * the list of tags.
     */
    fun tags(prepare: TagListBuilder.() -> Unit) = TagListBuilder().apply(prepare)

    internal fun build(): DependencySpec =
        DependencySpec(
            name = name,
            version = version,
            repository = repository,
            alias = alias,
            condition = condition,
            importValues = importValues ?: emptyList(),
            tags = tags ?: emptySet()
        )

    /**
     * Builder for creating and managing a list of path import values for a dependency.
     *
     * This inner class provides functionality to add path-based import values to 
     * a dependency's import values list. A path import value specifies a particular
     * path in a sub-chart whose values should be imported into the parent chart.
     *
     * To append a new path import value, the method internally utilizes the 
     * `addPathImportValues` utility of the containing class.
     */
    inner class PathImportValueListBuilder internal constructor() {
        /**
         * Adds a path-based import value to the dependency's import values list.
         *
         * This method specifies a particular path in a sub-chart, indicating that the values defined at 
         * the given path should be imported into the parent chart.
         *
         * @param path The path in the sub-chart whose values should be imported.
         */
        fun pathImportValue(path: String) = addPathImportValues(path)
    }

    /**
     * A builder class for configuring a list of mapping import values.
     *
     * This builder simplifies the process of defining key-value mappings 
     * for dependency import values. A mapping import value specifies a 
     * relationship where a `key` represents a path in the sub-chart (child), 
     * and a `value` represents a path in the parent chart.
     *
     * Instances of this class should only be created and used 
     * within the containing `DependencySpecBuilder` context.
     */
    inner class MappingImportValueListBuilder internal constructor() {
        /**
         * Defines a key-value mapping for importing values between sub-chart and parent chart paths.
         *
         * A key represents the path in the sub-chart (child) and the value represents
         * the corresponding path in the parent chart.
         *
         * @param key The path in the sub-chart used as a key in the mapping.
         * @param value The path in the parent chart used as a value in the mapping.
         */
        fun mappingImportValue(key: String, value: String) = addMappingImportValue(key, value)
    }

    /**
     * Builder class for managing a list of tags within a dependency specification.
     *
     * This class is designed to be used internally by `DependencySpecBuilder` 
     * to enable a fluent and concise way of adding tags to a dependency.
     * Tags can be used to categorize or label dependencies for better organization 
     * and easier identification within a configuration.
     */
    inner class TagListBuilder internal constructor() {
        /**
         * Adds a tag to the list of tags.
         *
         * This method enables adding a tag to the underlying dependency specification.
         * Tags can help categorize or label dependencies for improved organization and filtering.
         *
         * @param tag The tag to be added. Must be a non-empty string.
         */
        fun tag(tag: String) = addTag(tag)
    }
}