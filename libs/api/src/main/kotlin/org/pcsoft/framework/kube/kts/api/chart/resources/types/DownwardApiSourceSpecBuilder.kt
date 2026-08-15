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
 * Builder for a [DownwardApiSourceSpec], exposing Pod metadata as files.
 */
class DownwardApiSourceSpecBuilder internal constructor() : SourceSpecBuilder<DownwardApiSourceSpec> {
    private val items = mutableListOf<DownwardApiItemSpecBuilder>()

    /**
     * The POSIX permissions applied to created files unless overridden per item.
     */
    var defaultMode: Int? = null

    /**
     * Adds a single file exposing either a pod field or a container resource value.
     *
     * Example:
     * ```kotlin
     * addItem("labels") {
     *     fieldRef("metadata.labels")
     * }
     * ```
     *
     * @param path    The relative path of the created file.
     * @param prepare Configures the [DownwardApiItemSpecBuilder].
     */
    fun addItem(path: String, prepare: DownwardApiItemSpecBuilder.() -> Unit) {
        items += DownwardApiItemSpecBuilder(path).apply(prepare)
    }

    /**
     * Adds several files in one block.
     *
     * @param prepare Configures the [ItemListBuilder].
     */
    fun items(prepare: ItemListBuilder.() -> Unit) =
        ItemListBuilder().apply(prepare)

    /**
     * Writes a field of the Pod itself into a file.
     *
     * @param path       The relative path of the created file.
     * @param fieldPath  The selected field, for example `metadata.name`.
     * @param apiVersion The API version the field path is interpreted against.
     * @param mode       The POSIX permissions of the created file.
     */
    fun addFieldRef(path: String, fieldPath: String, apiVersion: String? = null, mode: Int? = null) =
        addItem(path) {
            this.mode = mode
            fieldRef(fieldPath) { this.apiVersion = apiVersion }
        }

    /**
     * Writes a resource request or limit of a container into a file.
     *
     * @param path          The relative path of the created file.
     * @param resource      The selected resource, for example `limits.cpu`.
     * @param containerName The container the resource is read from.
     * @param divisor       The unit the value is divided by, for example `1Mi`.
     * @param mode          The POSIX permissions of the created file.
     */
    fun addResourceFieldRef(
        path: String,
        resource: String,
        containerName: String,
        divisor: String? = null,
        mode: Int? = null
    ) = addItem(path) {
        this.mode = mode
        resourceFieldRef(resource) {
            this.containerName = containerName
            this.divisor = divisor
        }
    }

    /**
     * Builds the configured downward API source.
     *
     * @return A [DownwardApiSourceSpec] carrying the configured values.
     */
    override fun build(): DownwardApiSourceSpec =
        DownwardApiSourceSpec(items.map { it.build() }, defaultMode)

    /**
     * Collects several downward API items.
     */
    inner class ItemListBuilder internal constructor() {
        /**
         * Adds a single file exposing either a pod field or a container resource value.
         *
         * @param path    The relative path of the created file.
         * @param prepare Configures the [DownwardApiItemSpecBuilder].
         */
        fun item(path: String, prepare: DownwardApiItemSpecBuilder.() -> Unit) =
            addItem(path, prepare)
    }
}
