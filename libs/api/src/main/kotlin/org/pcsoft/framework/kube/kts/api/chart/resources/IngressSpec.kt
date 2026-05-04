/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.resources.types.BackendSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RulesSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.TlsSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents the specification for a Kubernetes Ingress resource.
 *
 * This data class defines the configuration structure for ingress resources, including
 * the ingress class name, default backend, TLS settings, and routing rules. It is
 * primarily used to describe how external traffic is directed to services within
 * a Kubernetes cluster.
 *
 * @property ingressClassName The name of the ingress class to use for this resource.
 *                            Can be null to allow the default ingress class to be applied.
 * @property defaultBackend   An optional default backend to be used when no rules apply.
 * @property tls              An optional list of TLS configurations for securing specific hosts.
 * @property rules            An optional list of rules for routing external traffic to
 *                            appropriate services or backends based on hostnames and paths.
 */
@NoArgs
data class IngressSpec(
    val ingressClassName: String?,
    val defaultBackend: BackendSpec?,
    val tls: List<TlsSpec>?,
    val rules: List<RulesSpec>?
) : ResourceSpec {

    companion object {
        /**
         * Represents the kind of Kubernetes resource associated with this specification.
         * This constant identifies the resource as an Ingress.
         */
        const val KIND = "Ingress"

        /**
         * Defines the API version used for Kubernetes Ingress resources.
         *
         * This constant specifies the API group and version under which the Ingress
         * resource is defined in Kubernetes. It is typically used in the resource's
         * metadata to indicate the version of the Kubernetes API being used.
         *
         * Value: "networking.k8s.io/v1"
         */
        const val API_VERSION = "networking.k8s.io/v1"
    }

}