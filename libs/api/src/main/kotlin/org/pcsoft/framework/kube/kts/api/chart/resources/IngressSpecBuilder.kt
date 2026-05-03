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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.*
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpecBuilder

/**
 * Builder class for constructing instances of `IngressSpec`, which describes
 * the specification for a Kubernetes Ingress resource.
 *
 * The `IngressSpec` defines configurations for backend services, TLS settings, 
 * and routing rules used for HTTP and HTTPS traffic routing based on host 
 * and path-based rules. This builder provides methods for defining a default 
 * backend, adding TLS configurations, and creating host-specific routing rules.
 *
 * This class internally manages the construction of `BackendSpec`, `TlsSpec`, 
 * and `RulesSpec` objects, ensuring modular and extensible configuration of 
 * the Ingress resource.
 *
 * Key Features:
 * - Set a default backend for handling requests that match no specific rules.
 * - Add customizable TLS configurations, including secrets and host-based settings.
 * - Define host-based routing rules, with support for path-based matching.
 *
 * Note: This builder is intended for internal use and ensures `IngressSpec` 
 * objects are built in a controlled and validated manner.
 *
 * @constructor Creates an instance of `IngressSpecBuilder` for internal usage.
 */
class IngressSpecBuilder internal constructor() : ResourceSpecBuilder<IngressSpec> {
    private var defaultBackend: BackendSpecBuilder? = null
    private var tls: MutableList<TlsSpecBuilder>? = null
    private var rules: MutableList<RulesSpecBuilder>? = null

    /**
     * Specifies the name of the IngressClass cluster resource to be associated with this Ingress.
     *
     * This property allows you to explicitly link the Ingress resource to a specific IngressClass,
     * which determines its behavior based on the controller handling the resource.
     * If set to null, the default IngressClass (if defined) will be used.
     */
    var ingressClassName: String? = null

    /**
     * Configures the default service backend for the Ingress specification.
     *
     * This method allows you to define the default backend service that handles requests
     * that do not match any of the specified rules in the Ingress. The backend is configured
     * using a `ServiceBackendSpecBuilder`.
     *
     * @param name The name of the Kubernetes Service to be used as the default backend.
     * @param prepare A lambda with a receiver of `ServiceBackendSpecBuilder` to further configure
     *                the service backend, such as specifying the service port.
     */
    fun defaultServiceBackend(name: String, prepare: ServiceBackendSpecBuilder.() -> Unit) {
        defaultBackend = ServiceBackendSpecBuilder(name).apply(prepare)
    }

    /**
     * Configures the default resource backend for the Ingress specification.
     *
     * This method allows you to define a default backend service that uses a generic Kubernetes
     * resource (e.g., ConfigMap, Secret) instead of a specific Service. It is useful for scenarios
     * where the backend should reference a resource type other than a Service. The backend is
     * configured using a `ResourceBackendSpecBuilder`.
     *
     * @param name The name of the Kubernetes resource to be used as the default backend.
     * @param kind The kind of the Kubernetes resource (e.g., "ConfigMap", "Secret").
     * @param prepare A lambda with a receiver of `ResourceBackendSpecBuilder` to further configure 
     *                the resource backend, such as specifying an API group if required.
     */
    fun defaultResourceBackend(name: String, kind: String, prepare: ResourceBackendSpecBuilder.() -> Unit) {
        defaultBackend = ResourceBackendSpecBuilder(name, kind).apply(prepare)
    }

    /**
     * Adds a TLS configuration block.
     *
     * Example:
     * ```kotlin
     * addTls {
     *     secretName = "my-tls-secret"
     *     addHost("example.com")
     * }
     * ```
     */
    fun addTls(prepare: TlsSpecBuilder.() -> Unit) {
        if (tls == null) {
            tls = mutableListOf()
        }

        tls!!.add(TlsSpecBuilder().apply(prepare))
    }

    /**
     * Configures the list of TLS configurations for the Ingress specification.
     *
     * This method allows defining multiple TLS configurations for securing the Ingress with 
     * HTTPS. Each TLS block can specify a secret containing the TLS certificates and the 
     * associated hosts covered by those certificates. The configuration is done using a 
     * `TlsListBuilder`, which allows chaining multiple TLS entries.
     *
     * Example:
     * ```kotlin
     * tlsList {
     *     tls {
     *         secretName = "my-tls-secret"
     *         addHost("example.com")
     *     }
     *     tls {
     *         secretName = "another-tls-secret"
     *         addHost("another.example.com")
     *     }
     * }
     * ```
     *
     * @param prepare A lambda with a receiver of `TlsListBuilder` to define the TLS configurations.
     */
    fun tlsList(prepare: TlsListBuilder.() -> Unit) = TlsListBuilder().apply(prepare)

    /**
     * Adds an Ingress rule.
     *
     * Example:
     * ```kotlin
     * addRule {
     *     host = "example.com"
     *     addHttpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
     *         path = "/"
     *         serviceBackend("my-service") {
     *             port(80)
     *         }
     *     }
     * }
     * ```
     */
    fun addRule(prepare: RulesSpecBuilder.() -> Unit) {
        if (rules == null) {
            rules = mutableListOf()
        }

        rules!!.add(RulesSpecBuilder().apply(prepare))
    }

    /**
     * Configures the list of rules for the Ingress specification.
     *
     * This method allows you to define one or more rules that dictate how requests are routed 
     * based on specific hosts and paths. Each rule is configured using a `RuleListBuilder` 
     * and can include HTTP path matching, host-based routing, and backend service specifications.
     *
     * Example:
     * ```kotlin
     * rules {
     *     rule {
     *         host = "example.com"
     *         addHttpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
     *             path = "/"
     *             serviceBackend("my-service") {
     *                 port(80)
     *             }
     *         }
     *     }
     *     rule {
     *         host = "another.example.com"
     *         addHttpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
     *             path = "/api"
     *             serviceBackend("api-service") {
     *                 port(8080)
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda with a receiver of `RuleListBuilder` to define the rules for the Ingress.
     */
    fun rules(prepare: RuleListBuilder.() -> Unit) = RuleListBuilder().apply(prepare)

    override fun build(): IngressSpec = IngressSpec(
        ingressClassName,
        defaultBackend?.build(),
        tls?.map { it.build() },
        rules?.map { it.build() }
    )

    /**
     * Builder class for constructing a list of TLS configurations for an Ingress specification.
     *
     * This class provides a method for defining and adding individual TLS configurations
     * by utilizing the `TlsSpecBuilder`. Each configuration typically includes a secret
     * containing the TLS certificate and private key, as well as a list of associated hosts.
     *
     * The `tls` method allows for the dynamic addition of TLS entries using a lambda receiver
     * to configure the `TlsSpecBuilder` instance.
     */
    inner class TlsListBuilder internal constructor() {
        /**
         * Adds a TLS configuration entry to the list of TLS settings for an Ingress specification.
         *
         * This method allows dynamic configuration of TLS settings by utilizing a [TlsSpecBuilder].
         * Use the provided [prepare] lambda function to define properties for the TLS configuration,
         * such as specifying the secret name and associated hosts.
         *
         * Example:
         * ```kotlin
         * tls {
         *     secretName = "my-tls-secret"
         *     addHost("example.com")
         *     addHost("www.example.com")
         * }
         * ```
         *
         * @param prepare A lambda with a receiver of [TlsSpecBuilder] used to configure the TLS settings.
         */
        fun tls(prepare: TlsSpecBuilder.() -> Unit) = addTls(prepare)
    }

    /**
     * A builder for configuring a list of Ingress rules.
     *
     * This class is used within the `IngressSpecBuilder` to define multiple routing rules.
     * Each rule specifies the host and paths to match, and the corresponding backends that 
     * process the traffic. Use this builder to add individual rules by invoking the `rule` method.
     */
    inner class RuleListBuilder internal constructor() {
        /**
         * Defines an Ingress rule using a DSL-based configuration.
         *
         * This method enables the creation of an Ingress rule by utilizing the [RulesSpecBuilder].
         * The rule specifies the host and the associated HTTP path configurations necessary for routing.
         *
         * Example:
         * ```kotlin
         * rule {
         *     host = "example.com"
         *     addHttpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
         *         path = "/"
         *         serviceBackend("my-service") {
         *             port(80)
         *         }
         *     }
         * }
         * ```
         *
         * @param prepare A lambda function to configure the rule. This lambda receives an instance
         *                of [RulesSpecBuilder] to define the rule's properties, such as host and HTTP paths.
         */
        fun rule(prepare: RulesSpecBuilder.() -> Unit) = addRule(prepare)
    }
}

/**
 * Creates an Ingress resource specification by applying the given configuration.
 *
 * @param prepare The configuration lambda used to build the Ingress specification.
 * This lambda is applied to a [TemplateSpecBuilder] for [IngressSpec] and can define
 * metadata and specification details for the Kubernetes Ingress resource.
 * @return A [TemplateSpec] containing the built Ingress resource specification.
 *
 * Example:
 * ```kotlin
 * ingress {
 *     metadata {
 *         name = "my-ingress"
 *         namespace = "default"
 *     }
 *     spec {
 *         ingressClassName = "nginx"
 *         addRule {
 *             host = "example.com"
 *             addHttpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
 *                 path = "/"
 *                 serviceBackend("my-service") {
 *                     port(80)
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 */
fun ingress(prepare: TemplateSpecBuilder<IngressSpec, IngressSpecBuilder>.() -> Unit): TemplateSpec<IngressSpec> =
    TemplateSpecBuilder(IngressSpec.API_VERSION, IngressSpec.KIND, IngressSpecBuilder())
        .apply(prepare)
        .build()