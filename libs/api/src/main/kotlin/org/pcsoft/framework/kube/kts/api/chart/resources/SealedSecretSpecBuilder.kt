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

import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder

/**
 * A builder class for constructing `SealedSecretSpec` objects.
 *
 * Provides DSL-style methods for setting the encrypted data and the optional Secret template.
 */
class SealedSecretSpecBuilder internal constructor() : ResourceSpecBuilder<SealedSecretSpec> {
    private var encryptedData: MutableMap<String, String>? = null
    private var template: SealedSecretTemplateSpecBuilder? = null

    /**
     * Adds a single encrypted entry to the SealedSecret encryptedData.
     */
    fun addEncryptedData(key: String, value: String) {
        if (encryptedData == null) encryptedData = mutableMapOf()
        encryptedData!![key] = value
    }

    /**
     * Configures encrypted data entries via an [EncryptedDataListBuilder].
     *
     * Example:
     * ```kotlin
     * encryptedData {
     *     entry("password", "AgBy3i4OJSWK+PiTySYZZA9rO...")
     * }
     * ```
     */
    fun encryptedData(prepare: EncryptedDataListBuilder.() -> Unit) = EncryptedDataListBuilder().apply(prepare)

    /**
     * Configures the Secret template produced by the controller.
     *
     * Example:
     * ```kotlin
     * template {
     *     type = SecretSpec.Type.Opaque
     *     immutable = true
     *     metadata {
     *         labels {
     *             label("app", "demo")
     *         }
     *     }
     * }
     * ```
     */
    fun template(prepare: SealedSecretTemplateSpecBuilder.() -> Unit) {
        template = SealedSecretTemplateSpecBuilder().apply(prepare)
    }

    override fun build(): SealedSecretSpec {
        require(!encryptedData.isNullOrEmpty()) { "At least one encrypted data entry is required" }
        return SealedSecretSpec(encryptedData!!, template?.build())
    }

    /**
     * Builder for adding encrypted entries to the SealedSecret encryptedData.
     */
    inner class EncryptedDataListBuilder internal constructor() {
        fun entry(key: String, value: String) = addEncryptedData(key, value)
    }
}

/**
 * Creates a `TemplateSpec` for a `SealedSecretSpec` resource.
 *
 * Example:
 * ```kotlin
 * sealedSecret {
 *     metadata("my-sealed-secret") {
 *         namespace = "default"
 *     }
 *     spec {
 *         addEncryptedData("password", "AgBy3i4OJSWK+PiTySYZZA9rO...")
 *         template {
 *             type = SecretSpec.Type.Opaque
 *         }
 *     }
 * }
 * ```
 */
fun sealedSecret(prepare: ExplicitTemplateSpecBuilder<SealedSecretSpec, SealedSecretSpecBuilder>.() -> Unit): ExplicitTemplateSpec<SealedSecretSpec> =
    ExplicitTemplateSpecBuilder(SealedSecretSpec.API_VERSION, SealedSecretSpec.KIND, SealedSecretSpecBuilder())
        .apply(prepare)
        .build()
