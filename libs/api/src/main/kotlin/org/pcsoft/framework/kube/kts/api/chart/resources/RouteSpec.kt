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

package org.pcsoft.framework.kube.kts.api.chart.resources

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RoutePortSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RouteTargetSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RouteTlsSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents the specification for an OpenShift Route resource.
 *
 * **Note:** Route is **not** part of standard Kubernetes. It is an OpenShift/OKD specific resource
 * (`apiVersion: route.openshift.io/v1`) and only works on clusters that provide the OpenShift
 * router (OpenShift, OKD, or compatible distributions). On vanilla Kubernetes, use
 * [IngressSpec] instead.
 *
 * A Route exposes a [to] service under an external host name and optionally terminates TLS at the
 * router. Traffic can be split across additional services via [alternateBackends] using weights.
 *
 * @property host             The externally reachable host name the Route is published under.
 *                            If null, the router may generate one automatically.
 * @property path             An optional path the Route matches (path-based routing).
 * @property to               The primary backend the Route directs traffic to (a Service).
 * @property alternateBackends Optional additional backends for weighted traffic splitting.
 * @property port             The target port on the backing service the Route points to.
 * @property tls              Optional TLS termination configuration.
 * @property wildcardPolicy   Controls whether the Route applies to a single host or a subdomain.
 */
@NoArgs
data class RouteSpec(
    val host: String?,
    val path: String?,
    val to: RouteTargetSpec?,
    val alternateBackends: List<RouteTargetSpec>?,
    val port: RoutePortSpec?,
    val tls: RouteTlsSpec?,
    val wildcardPolicy: WildcardPolicy?
) : ResourceSpec {

    /**
     * Controls whether a Route applies to a single exact host or to all hosts of a subdomain.
     */
    enum class WildcardPolicy {
        @JsonProperty("None")
        None,

        @JsonProperty("Subdomain")
        Subdomain
    }

    companion object {
        /**
         * Identifies this resource as an OpenShift Route.
         */
        const val KIND = "Route"

        /**
         * Specifies the API version for OpenShift Route resources.
         *
         * Value: "route.openshift.io/v1"
         */
        const val API_VERSION = "route.openshift.io/v1"
    }

}
