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

/**
 * A builder class for creating and configuring instances of [KubeVersion].
 *
 * Allows setting version constraints, such as minimum or maximum versions
 * (inclusive or exclusive), and building a [KubeVersion] from the specified constraints.
 */
class KubeVersionBuilder {
    private val items = mutableListOf<ItemBuilder>()

    /**
     * Sets a minimum inclusive version (>=).
     */
    fun minInclusive(version: String) {
        add(version) {
            equality = KubeVersion.ItemEquality.GREATER_EQUAL
        }
    }

    /**
     * Sets a minimum exclusive version (>).
     */
    fun minExclusive(version: String) {
        add(version) {
            equality = KubeVersion.ItemEquality.GREATER
        }
    }

    /**
     * Sets a maximum inclusive version (<=).
     */
    fun maxInclusive(version: String) {
        add(version) {
            equality = KubeVersion.ItemEquality.LESS_EQUAL
        }
    }

    /**
     * Sets a maximum exclusive version (<).
     */
    fun maxExclusive(version: String) {
        add(version) {
            equality = KubeVersion.ItemEquality.LESS
        }
    }

    /**
     * Adds an item to the list using the specified version and preparation logic.
     *
     * Example:
     * ```
     * add("1.20.0") {
     *     equality = KubeVersion.ItemEquality.GREATER_EQUAL
     * }
     * ```
     *
     * @param version The version string associated with the item.
     * @param prepare A lambda that applies additional configuration to the [ItemBuilder].
     */
    fun add(version: String, prepare: ItemBuilder.() -> Unit) {
        items.add(ItemBuilder(version).apply(prepare))
    }

    fun build() = KubeVersion(items.map { it.build() })

    /**
     * Builder for [KubeVersion.Item].
     */
    class ItemBuilder(private val version: String) {
        /**
         * The equality operator for this version item.
         */
        var equality: KubeVersion.ItemEquality = KubeVersion.ItemEquality.EQUAL

        fun build() = KubeVersion.Item(version, equality)
    }
}