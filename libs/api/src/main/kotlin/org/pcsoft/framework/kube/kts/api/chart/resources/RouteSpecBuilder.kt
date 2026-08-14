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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.RouteHttpHeaderActionsSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RouteHttpHeaderSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RouteHttpHeadersSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RoutePortSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RouteTargetSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RouteTlsSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RouteTlsSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder

/**
 * Builder class for constructing instances of [RouteSpec], the specification for an OpenShift Route.
 *
 * **Note:** Route is an OpenShift/OKD specific resource and is not available on vanilla Kubernetes
 * clusters. On standard Kubernetes use the `ingress { }` DSL instead.
 *
 * The builder allows configuration of the external host and path, the primary backend ([to]),
 * additional weighted backends ([addAlternateBackend] / [alternateBackends]), the target [port],
 * TLS termination ([tls]) and the [wildcardPolicy].
 *
 * All values are optional.
 *
 * @constructor Creates an instance of [RouteSpecBuilder] for internal usage.
 */
class RouteSpecBuilder internal constructor() : ResourceSpecBuilder<RouteSpec> {
    private var to: RouteTargetSpecBuilder? = null
    private var alternateBackends: MutableList<RouteTargetSpecBuilder>? = null
    private var port: RoutePortSpec? = null
    private var tls: RouteTlsSpecBuilder? = null

    /**
     * The externally reachable host name the Route is published under. If null, the OpenShift router
     * may generate one automatically.
     */
    var host: String? = null

    /**
     * An optional path the Route matches (path-based routing).
     */
    var path: String? = null

    /**
     * The subdomain the route is published under.
     *
     * The router combines it with the domain of the matching Ingress Controller. Mutually exclusive
     * with [host].
     */
    var subdomain: String? = null

    private var httpHeaders: RouteHttpHeadersSpec? = null

    /**
     * Configures HTTP header manipulations the router applies to requests and responses.
     *
     * Example:
     * ```kotlin
     * httpHeaders {
     *     setRequestHeader("X-Forwarded-Proto", "https")
     *     deleteResponseHeader("Server")
     * }
     * ```
     *
     * @param prepare A lambda with a receiver of [RouteHttpHeadersBuilder] to configure the header actions.
     */
    fun httpHeaders(prepare: RouteHttpHeadersBuilder.() -> Unit) {
        httpHeaders = RouteHttpHeadersBuilder().apply(prepare).build()
    }

    /**
     * A builder for the HTTP header actions of a route.
     */
    class RouteHttpHeadersBuilder internal constructor() {
        private val request = mutableListOf<RouteHttpHeaderSpec>()
        private val response = mutableListOf<RouteHttpHeaderSpec>()

        /**
         * Sets a header on requests forwarded to the backend, replacing any existing value.
         *
         * @param name  The name of the header.
         * @param value The value to set.
         */
        fun setRequestHeader(name: String, value: String) {
            request += RouteHttpHeaderSpec(
                name,
                RouteHttpHeaderSpec.Action(RouteHttpHeaderSpec.Type.Set, RouteHttpHeaderSpec.SetAction(value))
            )
        }

        /**
         * Removes a header from requests forwarded to the backend.
         *
         * @param name The name of the header.
         */
        fun deleteRequestHeader(name: String) {
            request += RouteHttpHeaderSpec(name, RouteHttpHeaderSpec.Action(RouteHttpHeaderSpec.Type.Delete, null))
        }

        /**
         * Sets a header on responses returned to the client, replacing any existing value.
         *
         * @param name  The name of the header.
         * @param value The value to set.
         */
        fun setResponseHeader(name: String, value: String) {
            response += RouteHttpHeaderSpec(
                name,
                RouteHttpHeaderSpec.Action(RouteHttpHeaderSpec.Type.Set, RouteHttpHeaderSpec.SetAction(value))
            )
        }

        /**
         * Removes a header from responses returned to the client.
         *
         * @param name The name of the header.
         */
        fun deleteResponseHeader(name: String) {
            response += RouteHttpHeaderSpec(name, RouteHttpHeaderSpec.Action(RouteHttpHeaderSpec.Type.Delete, null))
        }

        /**
         * Builds the configured header actions.
         *
         * @return A [RouteHttpHeadersSpec] carrying the configured request and response actions.
         */
        internal fun build(): RouteHttpHeadersSpec = RouteHttpHeadersSpec(
            RouteHttpHeaderActionsSpec(
                request = request.takeIf { it.isNotEmpty() },
                response = response.takeIf { it.isNotEmpty() }
            )
        )
    }

    /**
     * Controls whether the Route applies to a single exact host or to all hosts of a subdomain.
     */
    var wildcardPolicy: RouteSpec.WildcardPolicy? = null

    /**
     * Configures the primary backend the Route directs traffic to.
     *
     * Example:
     * ```kotlin
     * to("my-service") {
     *     weight = 100
     * }
     * ```
     *
     * @param name The name of the Service to direct traffic to.
     * @param prepare A lambda with a receiver of [RouteTargetSpecBuilder] to configure weight/kind.
     */
    fun to(name: String, prepare: RouteTargetSpecBuilder.() -> Unit = {}) {
        to = RouteTargetSpecBuilder(name).apply(prepare)
    }

    /**
     * Adds an alternate backend for weighted traffic splitting.
     *
     * Example:
     * ```kotlin
     * addAlternateBackend("canary-service") {
     *     weight = 20
     * }
     * ```
     */
    fun addAlternateBackend(name: String, prepare: RouteTargetSpecBuilder.() -> Unit = {}) {
        if (alternateBackends == null) {
            alternateBackends = mutableListOf()
        }

        alternateBackends!!.add(RouteTargetSpecBuilder(name).apply(prepare))
    }

    /**
     * Configures the list of alternate backends for weighted traffic splitting.
     *
     * Example:
     * ```kotlin
     * alternateBackends {
     *     backend("canary-service") { weight = 20 }
     *     backend("other-service") { weight = 10 }
     * }
     * ```
     *
     * @param prepare A lambda with a receiver of [AlternateBackendListBuilder] to define the backends.
     */
    fun alternateBackends(prepare: AlternateBackendListBuilder.() -> Unit) =
        AlternateBackendListBuilder().apply(prepare)

    /**
     * Configures the target port of the backing service by its name.
     *
     * @param name The name of the target port on the backing service.
     */
    fun port(name: String) {
        port = RoutePortSpec(name, null)
    }

    /**
     * Configures the target port of the backing service by its number.
     *
     * @param number The numeric target port on the backing service.
     */
    fun port(number: Int) {
        port = RoutePortSpec(null, number)
    }

    /**
     * Configures TLS termination for the Route.
     *
     * Example:
     * ```kotlin
     * tls(RouteTlsSpec.Termination.Edge) {
     *     insecureEdgeTerminationPolicy = RouteTlsSpec.InsecureEdgeTerminationPolicy.Redirect
     * }
     * ```
     *
     * @param termination The TLS termination type (edge, passthrough or reencrypt).
     * @param prepare A lambda with a receiver of [RouteTlsSpecBuilder] to configure certificates/policy.
     */
    fun tls(termination: RouteTlsSpec.Termination, prepare: RouteTlsSpecBuilder.() -> Unit = {}) {
        tls = RouteTlsSpecBuilder(termination).apply(prepare)
    }

    override fun build(): RouteSpec {
        require(to != null) { "A route must declare its primary backend via 'to'" }

        return RouteSpec(
            host = host,
            subdomain = subdomain,
            path = path,
            to = to!!.build(),
            alternateBackends = alternateBackends?.map { it.build() },
            port = port,
            tls = tls?.build(),
            wildcardPolicy = wildcardPolicy,
            httpHeaders = httpHeaders
        )
    }

    /**
     * A builder for configuring a list of alternate backends of a Route.
     */
    inner class AlternateBackendListBuilder internal constructor() {
        /**
         * Adds an alternate backend.
         *
         * @param name The name of the Service to direct traffic to.
         * @param prepare A lambda with a receiver of [RouteTargetSpecBuilder] to configure weight/kind.
         */
        fun backend(name: String, prepare: RouteTargetSpecBuilder.() -> Unit = {}) =
            addAlternateBackend(name, prepare)
    }
}

/**
 * Creates an OpenShift Route resource specification by applying the given configuration.
 *
 * **Note:** Route is not part of standard Kubernetes. It requires an OpenShift/OKD cluster
 * (`apiVersion: route.openshift.io/v1`). On vanilla Kubernetes use [ingress] instead.
 *
 * @param prepare The configuration lambda used to build the Route specification. It is applied to a
 * [ExplicitTemplateSpecBuilder] for [RouteSpec] and can define metadata and specification details.
 * @return A [ExplicitTemplateSpec] containing the built Route resource specification.
 *
 * Example:
 * ```kotlin
 * route {
 *     metadata("my-route") {
 *         namespace = "default"
 *     }
 *     spec {
 *         host = "www.example.com"
 *         to("my-service") {
 *             weight = 100
 *         }
 *         port(8080)
 *         tls(RouteTlsSpec.Termination.Edge) {
 *             insecureEdgeTerminationPolicy = RouteTlsSpec.InsecureEdgeTerminationPolicy.Redirect
 *         }
 *     }
 * }
 * ```
 */
fun route(prepare: ExplicitTemplateSpecBuilder<RouteSpec, RouteSpecBuilder>.() -> Unit): ExplicitTemplateSpec<RouteSpec> =
    ExplicitTemplateSpecBuilder(RouteSpec.API_VERSION, RouteSpec.KIND, RouteSpecBuilder())
        .apply(prepare)
        .build()
