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

import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec.*
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortMappingSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpecBuilder
import java.time.Duration

/**
 * A builder class for constructing `ServiceSpec` objects, which define the specification for a Kubernetes Service.
 *
 * This builder provides various configuration options, such as port mappings, IP settings, traffic policies,
 * session affinity, and other service-related attributes.
 *
 * The builder enforces the creation of at least one port mapping as part of the service specification.
 *
 * Some values are required.
 *
 * @constructor Creates an instance of `ServiceSpecBuilder`. This constructor is intended for internal use.
 */
class ServiceSpecBuilder internal constructor() : ResourceSpecBuilder<ServiceSpec> {
    private val ports = mutableListOf<PortMappingSpecBuilder>()
    private val selector: Map<String, String>? = null //TODO: replace with reference
    private var clusterIP: String? = null
    private var clusterIPs: MutableList<String>? = null
    private var ipFamilies: MutableSet<IPFamily>? = null
    private var externalIPs: MutableList<String>? = null
    private var loadBalancerSourceRanges: MutableList<String>? = null

    /**
     * Specifies the type of Kubernetes service to be used in the `ServiceSpecBuilder`.
     *
     * This variable determines how the service will be exposed within or outside the cluster.
     * It uses the `Type` enum to define the service type, which includes options such as `ClusterIP`, 
     * `NodePort`, `LoadBalancer`, or `ExternalName`. Depending on the selected type, 
     * different behaviors and configurations are applied to the service.
     *
     * This property is optional and can be null if the service type is not explicitly specified.
     */
    var type: Type? = null

    /**
     * Configures the IP family policy for a Kubernetes Service.
     *
     * This property determines how the Service manages IP configurations related to the use of
     * IPv4, IPv6, or both. It allows specifying whether the Service should use a single IP family,
     * prioritize dual-stack support, or strictly require dual-stack functionality.
     *
     * The policy settings are based on the `FamilyPolicy` enum, which provides options for controlling
     * IP family behavior in alignment with the cluster's capabilities and configuration.
     *
     * This property is optional and, if not specified, the default behavior may depend on the cluster's
     * predefined IP family policy or configuration.
     */
    var ipFamilyPolicy: FamilyPolicy? = null

    /**
     * Specifies the external name used for the Kubernetes Service.
     *
     * This property is typically used for ExternalName services, which allow the service
     * to act as an alias for an external resource (e.g., a DNS name for a service outside
     * the Kubernetes cluster). When set, the service type should be `ExternalName`, and 
     * the value of this property should be the DNS name of the external resource.
     *
     * Set this value to null if the service does not require an external name.
     */
    var externalName: String? = null

    /**
     * Specifies the external traffic routing policy for the Service.
     *
     * The `externalTrafficPolicy` determines whether traffic for the Service should be 
     * routed to the cluster nodes (`Cluster`) or strictly to those nodes where the 
     * corresponding pods are running (`Local`). This setting affects the behavior of
     * external traffic clients and can influence load balancing, source IP preservation,
     * and network performance.
     *
     * This property is particularly relevant when working with external load balancers 
     * or when specific networking configurations are required.
     */
    var externalTrafficPolicy: TrafficPolicy? = null

    /**
     * Specifies the internal traffic routing policy for a Service within the Kubernetes cluster.
     *
     * This property determines how internal traffic is distributed among the Service's pods,
     * influencing factors such as traffic locality, performance, and source IP preservation.
     *
     * Configuring this policy can affect the Service's behavior with respect to performance, 
     * reliability, and network configuration, making it an important consideration for workloads 
     * requiring specific traffic routing characteristics.
     */
    var internalTrafficPolicy: TrafficPolicy? = null

    /**
     * Indicates whether node ports should be automatically allocated for the Service when the type is set to LoadBalancer.
     *
     * When set to `true`, Kubernetes will allocate node ports for the Service, allowing external traffic
     * to be routed to the nodes hosting the Service. If `false` or `null`, node ports will not be allocated,
     * and external access may depend on additional configurations or the chosen load balancer implementation.
     */
    var allocateLoadBalancerNodePorts: Boolean? = null

    /**
     * Specifies the IP address of the load balancer for the service.
     *
     * This value is used to set a static IP address for the load balancer, if supported by the underlying cloud provider or platform.
     * However, its behavior and support may vary across implementations, and it may not accommodate dual-stack configurations.
     * 
     * This property is deprecated because it lacks standardization and is not portable across different environments. 
     * Users are advised to use implementation-specific annotations or configuration options as alternatives where applicable.
     */
    @Deprecated("This field was under-specified and its meaning varies across implementations. Using it is non-portable and it may not support dual-stack. Users are encouraged to use implementation-specific annotations when available.")
    var loadBalancerIP: String? = null

    /**
     * Specifies the class of the load balancer to be used for this service.
     *
     * This property allows you to define a custom load balancer implementation or configuration
     * by referencing a specific load balancer class. If set to null, the default load balancer 
     * behavior determined by the cluster will be used.
     *
     * Use this to override or customize the load balancing mechanism for the service.
     */
    var loadBalancerClass: String? = null

    /**
     * Configures the session affinity type for the Kubernetes service.
     *
     * Session affinity determines how requests from a client are routed 
     * to the service backend. By enabling session affinity, successive 
     * requests from the same client can be routed to the same backend pod 
     * to ensure session persistence.
     */
    var sessionAffinity: SessionAffinity? = null

    /**
     * Specifies the duration for session affinity client timeout in a Kubernetes Service.
     *
     * This property defines the amount of time session affinity should be maintained for a client
     * once an initial connection has been established. If set to `null`, no explicit timeout will
     * be applied for session affinity purposes.
     */
    var sessionAffinityClientTimeout: Duration? = null

    /**
     * Specifies whether Kubernetes should publish the addresses of not-ready endpoints.
     *
     * When set to `true`, the addresses of endpoints that are not yet ready (e.g., pods
     * that have not completed their initialization or failed readiness probes) will be
     * published. This can be useful for scenarios where services need early access to
     * endpoints or for debugging purposes.
     *
     * By default, this property is `null`, which means that the Kubernetes behavior is
     * determined by the cluster's default configuration.
     */
    var publishNotReadyAddresses: Boolean? = null

    /**
     * Specifies the port on each node where the Kubernetes control plane performs health checks on 
     * the service's endpoints. This is primarily used in conjunction with services that have type set 
     * to `NodePort` or `LoadBalancer` with health checking enabled.
     *
     * The value should be an integer representing a valid port number, or null if the default health 
     * check behavior is enough.
     *
     * - When defined, the specified port will be used for health checks.
     * - When null, the control plane will manage health checks without a specific node port.
     *
     * This property is useful for scenarios requiring precise control over node-specific health checks.
     */
    var healthCheckNodePort: Int? = null

    /**
     * Configures traffic distribution for the Kubernetes Service resource defined by this builder.
     *
     * This property determines how traffic is balanced or routed within the service, enabling
     * options such as preferring geographically or logically closer endpoints to improve
     * latency and efficiency.
     *
     * When specified, it allows customization of the service's behavior in terms of traffic distribution
     * strategy, aligning with the Kubernetes features designed for traffic routing and balancing.
     *
     * @see TrafficDistribution
     */
    var trafficDistribution: TrafficDistribution? = null

    /**
     * Adds a port mapping specification to the service configuration.
     *
     * This method allows defining a new port mapping for the Kubernetes service by providing
     * a port name and a configuration block. The configuration block is used to customize
     * the port mapping details through the provided [PortMappingSpecBuilder].
     * 
     * Example:
     * ```kotlin
     * addPort("http", 80) {
     *     targetPort = 8080
     * }
     * ```
     *
     * @param name The name of the port mapping. This is a unique identifier for the port and must not be blank.
     * @param port The port number for the port mapping. This should be a valid port number.
     * @param prepare A lambda with receiver that provides a [PortMappingSpecBuilder] to configure the
     *                port specifics such as target port, protocol, and more.
     */
    fun addPort(name: String, port: Int, prepare: PortMappingSpecBuilder.() -> Unit) {
        val portSpec = PortMappingSpecBuilder(name, port).apply(prepare)
        ports.add(portSpec)
    }

    /**
     * Configures a list of port mappings for the service specification.
     *
     * This method provides a [PortMappingListBuilder] to define and manage multiple port
     * mappings as part of the service configuration. The provided lambda allows detailed
     * customization of each port mapping through the builder.
     *
     * Example:
     * ```kotlin
     * ports {
     *     port("http") {
     *         port = 80
     *         targetPort = 8080
     *     }
     *     port("https") {
     *         port = 443
     *         targetPort = 8443
     *     }
     * }
     * ```
     *
     * @param prepare A lambda with receiver that provides a [PortMappingListBuilder] to define and
     *                configure the port mappings for the service.
     */
    fun ports(prepare: PortMappingListBuilder.() -> Unit) {
        PortMappingListBuilder().apply(prepare)
    }

    /**
     * Adds a ClusterIP address to the service specification.
     *
     * This method updates the list of ClusterIP addresses associated with the service. 
     * If the list is not yet initialized, it creates a new one and adds the provided 
     * ClusterIP to it. Subsequently, it updates the primary ClusterIP of the service 
     * to be the first entry in the list.
     *
     * @param clusterIP The ClusterIP address to be added to the service. This should be 
     *                  a valid IP address that conforms to Kubernetes conventions.
     */
    fun addClusterIP(clusterIP: String) {
        if (this.clusterIPs == null) {
            this.clusterIPs = mutableListOf()
        }
        this.clusterIPs!!.add(clusterIP)
        this.clusterIP = this.clusterIPs!!.first()
    }

    /**
     * Adds multiple ClusterIP addresses to the service specification.
     *
     * This method updates the list of ClusterIP addresses associated with the service.
     * If the list is not initialized, it creates a new one and adds the provided
     * ClusterIP addresses to it.
     *
     * @param clusterIPs A variable number of ClusterIP addresses to add. Each address
     *                   should be a valid IP address conforming to Kubernetes conventions.
     */
    fun addClusterIPs(vararg clusterIPs: String) {
        if (this.clusterIPs == null) {
            this.clusterIPs = mutableListOf()
        }
        this.clusterIPs!!.addAll(clusterIPs.toList())
        this.clusterIP = this.clusterIPs!!.first()
    }

    /**
     * Configures a list of ClusterIP addresses for the service specification.
     *
     * This method provides a [ClusterIpListBuilder] to define and manage multiple
     * ClusterIP addresses associated with the service. The provided lambda allows
     * customization of the list of ClusterIP addresses through the builder.
     *
     * Example:
     * ```kotlin
     * clusterIPs {
     *     clusterIP("10.0.0.1")
     *     clusterIP("10.0.0.2")
     * }
     * ```
     *
     * @param prepare A lambda with receiver that provides a [ClusterIpListBuilder] 
     *                to configure the ClusterIP addresses for the Kubernetes service.
     */
    fun clusterIPs(prepare: ClusterIpListBuilder.() -> Unit) =
        ClusterIpListBuilder().apply(prepare)

    /**
     * Adds an IP family to the service specification.
     *
     * This method updates the set of IP families associated with the service. If the set
     * has not been initialized, it creates a new one and adds the provided IP family to it.
     *
     * @param ipFamily The IP family to add. This should be an instance of [IPFamily], which specifies
     *                 whether the service uses IPv4 or IPv6.
     */
    fun addIpFamily(ipFamily: IPFamily) {
        if (this.ipFamilies == null) {
            this.ipFamilies = mutableSetOf()
        }
        this.ipFamilies!!.add(ipFamily)
    }

    /**
     * Adds multiple IP families to the service specification.
     *
     * This method updates the set of IP families associated with the service. If the set
     * is not initialized, it creates a new one and adds the provided IP families to it.
     * 
     * @param ipFamilies A variable number of IP families to add. Each IP family should be an instance 
     *                   of [IPFamily], representing whether the service uses IPv4 or IPv6.
     */
    fun addIpFamilies(vararg ipFamilies: IPFamily) {
        if (this.ipFamilies == null) {
            this.ipFamilies = mutableSetOf()
        }
        this.ipFamilies!!.addAll(ipFamilies.toSet())
    }

    /**
     * Adds all available IP families to the service specification.
     *
     * This method ensures that the set of IP families for the service includes all entries
     * defined in the [IPFamily] enumeration. If the `ipFamilies` set has not been initialized,
     * a new mutable set is created and populated with all available IP families.
     * 
     * This is typically used to configure the service to support both IPv4 and IPv6 address
     * protocols in environments that allow dual-stack networking.
     */
    fun addAllIpFamilies() {
        if (this.ipFamilies == null) {
            this.ipFamilies = mutableSetOf()
        }
        this.ipFamilies!!.addAll(IPFamily.entries.toSet())
    }

    /**
     * Configures a list of IP families for the service specification.
     *
     * This method provides an [IpFamilyListBuilder] to define and manage the IP families 
     * associated with the service. IP families determine whether the service will use IPv4, 
     * IPv6, or both address types in dual-stack networking environments. The provided lambda 
     * can be used to customize the list of IP families through the builder.
     *
     * Example:
     * ```kotlin
     * ipFamilies {
     *     ipFamily(IPFamily.IPv4)
     *     ipFamily(IPFamily.IPv6)
     * }
     * ```
     *
     * @param prepare A lambda with receiver that provides an [IpFamilyListBuilder] to define 
     *                and configure the IP families for the Kubernetes service.
     */
    fun ipFamilies(prepare: IpFamilyListBuilder.() -> Unit) =
        IpFamilyListBuilder().apply(prepare)

    /**
     * Adds an external IP address to the service specification.
     *
     * This method updates the list of external IPs associated with the service.
     * If the list is not yet initialized, it creates a new one and adds the provided
     * external IP address to it.
     *
     * @param externalIP The external IP address to add. This should be a valid IP address
     *                   compliant with Kubernetes conventions.
     */
    fun addExternalIP(externalIP: String) {
        if (this.externalIPs == null) {
            this.externalIPs = mutableListOf()
        }
        this.externalIPs!!.add(externalIP)
    }

    /**
     * Adds one or more external IP addresses to the service specification.
     *
     * This method appends the provided external IP addresses to the list of external IPs
     * associated with the service. If the list is uninitialized, a new mutable list is created
     * and the external IP addresses are added to it.
     *
     * @param externalIPs A variable number of external IP addresses to add. Each address 
     *                    should be a valid IP address compliant with Kubernetes conventions.
     */
    fun addExternalIPs(vararg externalIPs: String) {
        if (this.externalIPs == null) {
            this.externalIPs = mutableListOf()
        }
        this.externalIPs!!.addAll(externalIPs.toList())
    }

    /**
     * Configures a list of external IP addresses for the service specification.
     *
     * This method provides an [ExternalIpListBuilder] to define and manage multiple external 
     * IP addresses associated with the service. External IPs are IP addresses outside the 
     * cluster that route traffic to the service. The provided lambda allows customization and 
     * configuration of the external IP addresses through the builder.
     *
     * Example:
     * ```kotlin
     * externalIPs {
     *     externalIP("203.0.113.1")
     *     externalIP("203.0.113.2")
     * }
     * ```
     *
     * @param prepare A lambda with receiver that provides an [ExternalIpListBuilder] to define 
     *                and configure the external IPs for the Kubernetes service.
     */
    fun externalIPs(prepare: ExternalIpListBuilder.() -> Unit) =
        ExternalIpListBuilder().apply(prepare)

    /**
     * Adds a new source range to the load balancer's source ranges.
     *
     * @param loadBalancerSourceRange The CIDR block representing the source range 
     * to be added to the load balancer.
     */
    fun addLoadBalancerSourceRange(loadBalancerSourceRange: String) {
        if (this.loadBalancerSourceRanges == null) {
            this.loadBalancerSourceRanges = mutableListOf()
        }
        this.loadBalancerSourceRanges!!.add(loadBalancerSourceRange)
    }

    /**
     * Adds one or more source ranges to the list of load balancer source ranges.
     *
     * @param loadBalancerSourceRanges A vararg parameter representing the source ranges to be added. 
     * Each source range should be a string, typically in CIDR (Classless Inter-Domain Routing) format.
     */
    fun addLoadBalancerSourceRanges(vararg loadBalancerSourceRanges: String) {
        if (this.loadBalancerSourceRanges == null) {
            this.loadBalancerSourceRanges = mutableListOf()
        }
        this.loadBalancerSourceRanges!!.addAll(loadBalancerSourceRanges.toList())
    }

    /**
     * Configures the load balancer source ranges by applying the specified configuration block.
     *
     * Example:
     * ```kotlin
     * loadBalancerSourceRanges {
     *     loadBalancerSourceRange("10.0.0.0/8")
     *     loadBalancerSourceRange("192.168.0.0/16")
     * }
     * ```
     *
     * @param prepare A lambda function used to define the load balancer source ranges 
     * within the context of the LoadBalancerSourceRangeListBuilder.
     */
    fun loadBalancerSourceRanges(prepare: LoadBalancerSourceRangeListBuilder.() -> Unit) = 
        LoadBalancerSourceRangeListBuilder().apply(prepare)

    @Suppress("DEPRECATION")
    override fun build(): ServiceSpec {
        require(ports.isNotEmpty()) { "At least one port is required at ${ServiceSpec::class.simpleName}" }

        return ServiceSpec(
            type,
            selector,
            ports.map { it.build(type) },
            clusterIP,
            clusterIPs,
            ipFamilies,
            ipFamilyPolicy,
            externalIPs,
            externalName,
            externalTrafficPolicy,
            internalTrafficPolicy,
            allocateLoadBalancerNodePorts,
            loadBalancerIP,
            loadBalancerClass,
            loadBalancerSourceRanges,
            sessionAffinity,
            sessionAffinityClientTimeout?.let {
                SessionAffinityConfig(ClientIPConfig(sessionAffinityClientTimeout!!))
            },
            publishNotReadyAddresses,
            healthCheckNodePort,
            trafficDistribution
        )
    }

    /**
     * Builder class for creating and managing a list of port mappings within a service specification.
     *
     * This class provides a convenient way to define and configure multiple port mappings for a Kubernetes 
     * service. Each port mapping is specified by its name and details, which can be customized using the 
     * provided [PortMappingSpecBuilder].
     *
     * The [PortMappingListBuilder] is used to group and manage port definitions as part of the service specification. 
     * It is designed to be used internally within the [ServiceSpecBuilder] class.
     *
     * @constructor Constructs an instance of [PortMappingListBuilder].
     */
    inner class PortMappingListBuilder internal constructor() {
        /**
         * Defines a port mapping for the service specification.
         *
         * This method allows adding a port mapping by specifying its name and configuring the port details
         * using the provided [PortMappingSpecBuilder]. The configuration block is used to customize
         * properties such as port number, target port, protocol, and more.
         *
         * Example:
         * ```kotlin
     *     port("http", 80) {
     *         targetPort = 8080
     *     }
         * ```
         *
         * @param name The name of the port mapping. This serves as a unique identifier and must not be blank.
         * @param port The port number for the port mapping. This should be a valid port number.
         * @param prepare A lambda with receiver that provides a [PortMappingSpecBuilder] to define the port's
         *                configuration details, such as target port, protocol, and other specifications.
         */
        fun port(name: String, port: Int, prepare: PortMappingSpecBuilder.() -> Unit) =
            addPort(name, port, prepare)
    }

    /**
     * A builder for configuring and managing the list of ClusterIP addresses in a Kubernetes service specification.
     *
     * This inner class is designed to encapsulate the addition of ClusterIP addresses for the parent service builder,
     * allowing for a fluent API style.
     *
     * The `ClusterIpListBuilder` provides a method to add a single ClusterIP address to the service configuration by
     * leveraging the functionality of the `addClusterIP` method in the parent class.
     *
     * Note that this builder is not intended to be created directly by users; it is instantiated internally by the
     * containing `ServiceSpecBuilder` class.
     */
    inner class ClusterIpListBuilder internal constructor() {
        /**
         * Adds a ClusterIP address to the service specification.
         *
         * This method allows the addition of a single ClusterIP address to the list of ClusterIPs
         * associated with the Kubernetes service configuration. ClusterIP addresses are used 
         * to enable internal service access within a Kubernetes cluster.
         *
         * @param clusterIP The ClusterIP address to add. It should be a valid IP address that 
         *                  adheres to the Kubernetes standards for service ClusterIPs.
         */
        fun clusterIP(clusterIP: String) = addClusterIP(clusterIP)
    }

    /**
     * A builder class for configuring the list of IP families associated with a Kubernetes Service.
     *
     * This class provides a DSL for adding specific IP families to the service configuration. 
     * IP families determine whether the Service will use IPv4 or IPv6 addresses, which is 
     * particularly relevant in environments supporting dual-stack networking.
     *
     * Instances of this class are created internally and used to populate the service's IP family list.
     */
    inner class IpFamilyListBuilder internal constructor() {
        /**
         * Adds an IP family to the list of IP families associated with a Kubernetes Service.
         *
         * This method allows specifying whether the Service should use IPv4 or IPv6 addresses. The IP family 
         * selection is particularly important in environments that support dual-stack networking or where 
         * explicit configuration of the address family is required.
         *
         * @param ipFamily The IP family to add to the configuration. Should be one of the values from the [IPFamily] enum.
         */
        fun ipFamily(ipFamily: IPFamily) = addIpFamily(ipFamily)
    }

    /**
     * A builder class for configuring the list of external IPs associated with a service specification.
     *
     * This class is designed to be used internally to manage and customize external IP addresses
     * for a Kubernetes service. External IPs allow traffic to be routed to the service from outside
     * the cluster.
     *
     * Example usage involves adding individual external IP addresses using the [externalIP] method, 
     * which delegates to the parent configuration logic.
     *
     * This builder is part of the internal implementation of the [ServiceSpecBuilder], 
     * and it should not be instantiated directly by end users.
     */
    inner class ExternalIpListBuilder internal constructor() {
        /**
         * Adds an external IP address to the list of external IPs for the service specification.
         *
         * This method delegates to the internal functionality of `addExternalIP`, 
         * allowing users to configure external IPs associated with a Kubernetes service. 
         * External IPs enable traffic from outside the cluster to be routed to the service.
         *
         * @param externalIP The external IP address to add to the service. It must be a valid IP address 
         *                   following Kubernetes external IP conventions.
         */
        fun externalIP(externalIP: String) = addExternalIP(externalIP)
    }

    /**
     * A builder class for constructing a list of source ranges for a load balancer.
     *
     * This class provides functionality to add source ranges to the list, which can be 
     * used to configure the access control for a load balancer by specifying the allowed IP ranges.
     */
    inner class LoadBalancerSourceRangeListBuilder {
        /**
         * Adds the specified CIDR block representing a source range to the load balancer configuration.
         *
         * This method is used to specify which IP ranges are allowed access to the load balancer. 
         * The provided source range should be in CIDR notation (e.g., "192.168.1.0/24").
         *
         * @param loadBalancerSourceRange The CIDR block representing the source range to be added.
         */
        fun loadBalancerSourceRange(loadBalancerSourceRange: String) = 
            addLoadBalancerSourceRange(loadBalancerSourceRange)
    }
}

/**
 * Creates a `TemplateSpec` for a `ServiceSpec` resource, allowing customization through the provided lambda.
 *
 * Example:
 * ```kotlin
 * service {
 *     metadata {
 *         name = "my-service"
 *     }
 *     spec {
 *         type = Type.ClusterIP
 *         addPort("http") {
 *             port = 80
 *             targetPort = 8080
 *         }
 *     }
 * }
 * ```
 *
 * @param prepare A lambda used to configure the `TemplateSpecBuilder` for the `ServiceSpec`.
 *                This lambda provides the ability to define metadata, specification,
 *                and other resource-specific configurations.
 * @return A `TemplateSpec` instance encapsulating the configured `ServiceSpec`.
 */
fun service(prepare: TemplateSpecBuilder<ServiceSpec, ServiceSpecBuilder>.() -> Unit): TemplateSpec<ServiceSpec> =
    TemplateSpecBuilder(ServiceSpec.API_VERSION, ServiceSpec.KIND, ServiceSpecBuilder())
        .apply(prepare)
        .build()