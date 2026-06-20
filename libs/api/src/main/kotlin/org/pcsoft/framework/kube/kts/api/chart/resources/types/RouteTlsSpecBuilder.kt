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
 * Builder for [RouteTlsSpec], configuring the TLS termination of an OpenShift Route.
 *
 * @constructor Creates a builder for the given TLS [termination] type. Internal by design.
 * @param termination The TLS termination type (edge, passthrough or reencrypt).
 */
class RouteTlsSpecBuilder internal constructor(private val termination: RouteTlsSpec.Termination) {
    /**
     * Behaviour for insecure (plain HTTP) traffic on a TLS-enabled Route.
     */
    var insecureEdgeTerminationPolicy: RouteTlsSpec.InsecureEdgeTerminationPolicy? = null

    /**
     * The PEM encoded private key (edge/reencrypt termination).
     */
    var key: String? = null

    /**
     * The PEM encoded certificate (edge/reencrypt termination).
     */
    var certificate: String? = null

    /**
     * The PEM encoded CA certificate chain (edge/reencrypt termination).
     */
    var caCertificate: String? = null

    /**
     * The PEM encoded CA certificate used to validate the backend service certificate
     * (reencrypt termination only).
     */
    var destinationCACertificate: String? = null

    internal fun build(): RouteTlsSpec = RouteTlsSpec(
        termination,
        insecureEdgeTerminationPolicy,
        key,
        certificate,
        caCertificate,
        destinationCACertificate
    )
}
