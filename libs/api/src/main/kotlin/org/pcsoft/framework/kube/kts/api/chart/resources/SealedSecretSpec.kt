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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents the specification for a Bitnami SealedSecret resource.
 *
 * A SealedSecret holds encrypted data that only the in-cluster controller can decrypt into a regular
 * Kubernetes [SecretSpec]. The encrypted values are safe to store in version control.
 *
 * @property encryptedData Key-value pairs of encrypted (sealed) data. At least one entry is required.
 * @property template An optional template describing the Secret that the controller should produce
 *                    (type, immutability and additional metadata).
 */
@NoArgs
data class SealedSecretSpec(
    val encryptedData: Map<String, String>,
    val template: SealedSecretTemplateSpec?
) : ResourceSpec {
    companion object {
        /**
         * Identifies this resource as a SealedSecret.
         */
        const val KIND = "SealedSecret"

        /**
         * Specifies the API version for Bitnami SealedSecret resources.
         */
        const val API_VERSION = "bitnami.com/v1alpha1"
    }
}

/**
 * Describes the Secret template embedded in a [SealedSecretSpec].
 *
 * @property metadata Optional metadata (labels and annotations) applied to the produced Secret.
 * @property type The type of the produced Secret.
 * @property immutable If true, the produced Secret cannot be updated once created.
 */
@NoArgs
data class SealedSecretTemplateSpec(
    val metadata: SealedSecretTemplateMetadataSpec?,
    val type: SecretSpec.Type?,
    val immutable: Boolean?
)

/**
 * Metadata applied to the Secret produced by a SealedSecret.
 *
 * @property labels Labels applied to the produced Secret.
 * @property annotations Annotations applied to the produced Secret.
 */
@NoArgs
data class SealedSecretTemplateMetadataSpec(
    val labels: Map<String, String>?,
    val annotations: Map<String, String>?
)
