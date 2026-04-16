package org.pcsoft.framework.kube.kts.api.chart.types

class KubeVersionBuilder {
    private val items = mutableListOf<ItemBuilder>()

    fun minInclusive(version: String) {
        add(version) {
            equality = KubeVersion.ItemEquality.GREATER_EQUAL
        }
    }

    fun minExclusive(version: String) {
        add(version) {
            equality = KubeVersion.ItemEquality.GREATER
        }
    }

    fun maxInclusive(version: String) {
        add(version) {
            equality = KubeVersion.ItemEquality.LESS_EQUAL
        }
    }

    fun maxExclusive(version: String) {
        add(version) {
            equality = KubeVersion.ItemEquality.LESS
        }
    }

    fun add(version: String, prepare: ItemBuilder.() -> Unit) {
        items.add(ItemBuilder(version).apply(prepare))
    }

    fun build() = KubeVersion(items.map { it.build() })

    class ItemBuilder(private val version: String) {
        var equality: KubeVersion.ItemEquality = KubeVersion.ItemEquality.EQUAL

        fun build() = KubeVersion.Item(version, equality)
    }
}