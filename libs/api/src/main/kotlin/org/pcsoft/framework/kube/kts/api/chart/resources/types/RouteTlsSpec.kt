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

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Defines the TLS termination configuration of an OpenShift Route.
 *
 * @property termination                   The TLS termination type (edge, passthrough or reencrypt).
 * @property insecureEdgeTerminationPolicy Behaviour for insecure (HTTP) traffic on a TLS-enabled Route.
 * @property key                           The PEM encoded private key (edge/reencrypt).
 * @property certificate                   The PEM encoded certificate (edge/reencrypt).
 * @property caCertificate                 The PEM encoded CA certificate chain (edge/reencrypt).
 * @property destinationCACertificate      The PEM encoded CA certificate used to validate the backend
 *                                         service certificate (reencrypt only).
 */
@NoArgs
data class RouteTlsSpec(
    val termination: Termination,
    val insecureEdgeTerminationPolicy: InsecureEdgeTerminationPolicy?,
    val key: String?,
    val certificate: String?,
    val caCertificate: String?,
    val destinationCACertificate: String?
) {

    /**
     * The supported TLS termination types of an OpenShift Route.
     */
    enum class Termination {
        @JsonProperty("edge")
        Edge,

        @JsonProperty("passthrough")
        Passthrough,

        @JsonProperty("reencrypt")
        Reencrypt
    }

    /**
     * Controls how insecure (plain HTTP) traffic is handled on a TLS-enabled Route.
     */
    enum class InsecureEdgeTerminationPolicy {
        @JsonProperty("None")
        None,

        @JsonProperty("Allow")
        Allow,

        @JsonProperty("Redirect")
        Redirect
    }
}
