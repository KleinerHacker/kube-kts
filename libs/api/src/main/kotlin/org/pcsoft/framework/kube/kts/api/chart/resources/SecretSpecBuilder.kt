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
 * A builder class for constructing `SecretSpec` objects.
 *
 * Provides DSL-style methods for setting the secret type, binary data, string data, and immutability.
 */
class SecretSpecBuilder internal constructor() : ResourceSpecBuilder<SecretSpec> {
    private var data: MutableMap<String, ByteArray>? = null
    private var stringData: MutableMap<String, String>? = null

    /**
     * The type of the Secret. Defaults to `null`, which Kubernetes interprets as [SecretSpec.Type.Opaque].
     */
    var type: SecretSpec.Type? = null

    /**
     * If true, ensures the Secret cannot be updated once created.
     */
    var immutable: Boolean? = null

    /**
     * Adds a single binary entry to the Secret data. The value is base64-encoded in the rendered YAML.
     */
    fun addData(key: String, value: ByteArray) {
        if (data == null) data = mutableMapOf()
        data!![key] = value
    }

    /**
     * Configures binary data entries via a [DataListBuilder].
     *
     * Example:
     * ```kotlin
     * data {
     *     entry("tls.crt", certBytes)
     *     entry("tls.key", keyBytes)
     * }
     * ```
     */
    fun data(prepare: DataListBuilder.() -> Unit) = DataListBuilder().apply(prepare)

    /**
     * Adds a single plain string entry to the Secret stringData.
     */
    fun addStringData(key: String, value: String) {
        if (stringData == null) stringData = mutableMapOf()
        stringData!![key] = value
    }

    /**
     * Configures plain string data entries via a [StringDataListBuilder].
     *
     * Example:
     * ```kotlin
     * stringData {
     *     entry("username", "admin")
     *     entry("password", "s3cr3t")
     * }
     * ```
     */
    fun stringData(prepare: StringDataListBuilder.() -> Unit) = StringDataListBuilder().apply(prepare)

    override fun build(): SecretSpec = SecretSpec(type, data, stringData, immutable)

    /**
     * Builder for adding binary entries to the Secret data.
     */
    inner class DataListBuilder internal constructor() {
        fun entry(key: String, value: ByteArray) = addData(key, value)
    }

    /**
     * Builder for adding plain string entries to the Secret stringData.
     */
    inner class StringDataListBuilder internal constructor() {
        fun entry(key: String, value: String) = addStringData(key, value)
    }
}

/**
 * Creates a `TemplateSpec` for a `SecretSpec` resource.
 *
 * Example:
 * ```kotlin
 * secret {
 *     metadata("my-secret") {
 *         namespace = "default"
 *     }
 *     spec {
 *         type = SecretSpec.Type.Opaque
 *         addStringData("username", "admin")
 *         immutable = true
 *     }
 * }
 * ```
 */
fun secret(prepare: FlatTemplateSpecBuilder<SecretSpec, SecretSpecBuilder>.() -> Unit): FlatTemplateSpec<SecretSpec> =
    FlatTemplateSpecBuilder(SecretSpec.API_VERSION, SecretSpec.KIND, SecretSpecBuilder())
        .apply(prepare)
        .build()
