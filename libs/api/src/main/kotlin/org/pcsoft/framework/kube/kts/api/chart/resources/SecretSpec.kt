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
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents the specification for a Kubernetes Secret.
 *
 * @property type The type of the Secret (e.g. [Type.Opaque], [Type.Tls]).
 * @property data Key-value pairs of binary data. Values are base64-encoded in the rendered YAML.
 * @property stringData Key-value pairs of plain (non base64-encoded) string data. Write-only convenience
 *                      field; Kubernetes merges it into [data].
 * @property immutable If true, ensures the Secret cannot be updated once created.
 */
@NoArgs
data class SecretSpec(
    val type: Type?,
    val data: Map<String, ByteArray>?,
    val stringData: Map<String, String>?,
    val immutable: Boolean?
) : ResourceSpec {

    /**
     * The built-in Kubernetes Secret types.
     */
    enum class Type {
        @JsonProperty("Opaque")
        Opaque,

        @JsonProperty("kubernetes.io/service-account-token")
        ServiceAccountToken,

        @JsonProperty("kubernetes.io/dockercfg")
        DockerCfg,

        @JsonProperty("kubernetes.io/dockerconfigjson")
        DockerConfigJson,

        @JsonProperty("kubernetes.io/basic-auth")
        BasicAuth,

        @JsonProperty("kubernetes.io/ssh-auth")
        SshAuth,

        @JsonProperty("kubernetes.io/tls")
        Tls,

        @JsonProperty("bootstrap.kubernetes.io/token")
        BootstrapToken
    }

    companion object {
        /**
         * Identifies this resource as a Secret.
         */
        const val KIND = "Secret"

        /**
         * Specifies the API version for Kubernetes Secret resources.
         */
        const val API_VERSION = "v1"
    }
}
