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

package org.pcsoft.framework.kube.kts.api.chart.resources.types

/**
 * The shared configuration of the ConfigMap and Secret volume sources.
 *
 * @param T The concrete [FileSourceSpec] produced by the implementing builder.
 */
sealed class FileSourceSpecBuilder<T : FileSourceSpec> : SourceSpecBuilder<T> {
    protected var items: MutableList<KeyToPathSpecBuilder>? = null; private set

    /**
     * The name of the referenced ConfigMap or Secret.
     */
    var name: String? = null

    /**
     * If true, the volume mounts empty instead of failing when the referenced object is missing.
     */
    var optional: Boolean? = null

    /**
     * The POSIX permissions applied to created files unless overridden per item.
     */
    var defaultMode: Int? = null

    /**
     * Projects a single key under a specific relative path instead of projecting all keys.
     *
     * @param key     The key to project.
     * @param path    The relative path the key's value is written to.
     * @param prepare Configures the [KeyToPathSpecBuilder].
     */
    fun addItem(key: String, path: String, prepare: KeyToPathSpecBuilder.() -> Unit = {}) {
        if (items == null) {
            items = mutableListOf()
        }
        items!!.add(KeyToPathSpecBuilder(key, path).apply(prepare))
    }

    /**
     * Projects several keys under specific relative paths.
     *
     * @param prepare Configures the [ItemListBuilder].
     */
    fun items(prepare: ItemListBuilder.() -> Unit) =
        ItemListBuilder().apply(prepare)

    /**
     * Collects several key mappings for a file-based volume source.
     */
    inner class ItemListBuilder internal constructor() {
        /**
         * Projects a single key under a specific relative path.
         *
         * @param key     The key to project.
         * @param path    The relative path the key's value is written to.
         * @param prepare Configures the [KeyToPathSpecBuilder].
         */
        fun item(key: String, path: String, prepare: KeyToPathSpecBuilder.() -> Unit = {}) =
            addItem(key, path, prepare)
    }
}
