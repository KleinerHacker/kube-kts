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
 * Represents the specification for a Kubernetes ConfigMap.
 *
 * @property data Key-value pairs of string data to store in the ConfigMap.
 * @property binaryData Key-value pairs of binary data (base64-encoded in YAML) to store in the ConfigMap.
 * @property immutable If true, ensures the ConfigMap cannot be updated once created.
 */
@NoArgs
data class ConfigMapSpec(
    val data: Map<String, String>?,
    val binaryData: Map<String, ByteArray>?,
    val immutable: Boolean?
) : ResourceSpec {
    companion object {
        /**
         * Identifies this resource as a ConfigMap.
         */
        const val KIND = "ConfigMap"

        /**
         * Specifies the API version for Kubernetes ConfigMap resources.
         */
        const val API_VERSION = "v1"
    }
}
