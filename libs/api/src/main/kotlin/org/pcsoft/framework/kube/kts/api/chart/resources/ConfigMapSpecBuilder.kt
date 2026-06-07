/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.template.FlatTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.FlatTemplateSpecBuilder

/**
 * A builder class for constructing `ConfigMapSpec` objects.
 *
 * Provides DSL-style methods for setting string data, binary data, and immutability.
 */
class ConfigMapSpecBuilder internal constructor() : ResourceSpecBuilder<ConfigMapSpec> {
    private var data: MutableMap<String, String>? = null
    private var binaryData: MutableMap<String, ByteArray>? = null

    /**
     * If true, ensures the ConfigMap cannot be updated once created.
     */
    var immutable: Boolean? = null

    /**
     * Adds a single string entry to the ConfigMap data.
     */
    fun addData(key: String, value: String) {
        if (data == null) data = mutableMapOf()
        data!![key] = value
    }

    /**
     * Configures string data entries via a [DataListBuilder].
     *
     * Example:
     * ```kotlin
     * data {
     *     entry("key1", "value1")
     *     entry("key2", "value2")
     * }
     * ```
     */
    fun data(prepare: DataListBuilder.() -> Unit) = DataListBuilder().apply(prepare)

    /**
     * Adds a single binary entry to the ConfigMap binaryData.
     */
    fun addBinaryData(key: String, value: ByteArray) {
        if (binaryData == null) binaryData = mutableMapOf()
        binaryData!![key] = value
    }

    /**
     * Configures binary data entries via a [BinaryDataListBuilder].
     *
     * Example:
     * ```kotlin
     * binaryData {
     *     entry("binKey", "content".toByteArray())
     * }
     * ```
     */
    fun binaryData(prepare: BinaryDataListBuilder.() -> Unit) = BinaryDataListBuilder().apply(prepare)

    override fun build(): ConfigMapSpec = ConfigMapSpec(data, binaryData, immutable)

    /**
     * Builder for adding string entries to the ConfigMap data.
     */
    inner class DataListBuilder internal constructor() {
        fun entry(key: String, value: String) = addData(key, value)
    }

    /**
     * Builder for adding binary entries to the ConfigMap binaryData.
     */
    inner class BinaryDataListBuilder internal constructor() {
        fun entry(key: String, value: ByteArray) = addBinaryData(key, value)
    }
}

/**
 * Creates a `TemplateSpec` for a `ConfigMapSpec` resource.
 *
 * Example:
 * ```kotlin
 * configMap {
 *     metadata("my-config") {
 *         namespace = "default"
 *     }
 *     spec {
 *         addData("key1", "value1")
 *         immutable = true
 *     }
 * }
 * ```
 */
fun configMap(prepare: FlatTemplateSpecBuilder<ConfigMapSpec, ConfigMapSpecBuilder>.() -> Unit): FlatTemplateSpec<ConfigMapSpec> =
    FlatTemplateSpecBuilder(ConfigMapSpec.API_VERSION, ConfigMapSpec.KIND, ConfigMapSpecBuilder())
        .apply(prepare)
        .build()
