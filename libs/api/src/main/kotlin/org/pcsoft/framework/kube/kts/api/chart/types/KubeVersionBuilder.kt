package org.pcsoft.framework.kube.kts.api.chart.types

/**
 * Builder for [KubeVersion].
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