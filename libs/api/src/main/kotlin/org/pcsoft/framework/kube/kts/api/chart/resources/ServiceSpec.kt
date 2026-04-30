package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortMappingSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Represents the specification for a Kubernetes Service.
 *
 * @property type The type of the service (ClusterIP, NodePort, etc.).
 * @property selector The label selector to identify the pods this service targets.
 * @property ports The list of ports that the service exposes.
 * @property clusterIP The IP address of the service; usually assigned by the master.
 * @property clusterIPs The list of IP addresses assigned to the service.
 * @property ipFamilies The list of IP families (IPv4, IPv6) assigned to the service.
 * @property ipFamilyPolicy The policy for IP family assignment (SingleStack, PreferDualStack, etc.).
 * @property externalIPs The list of IP addresses for which nodes in the cluster will also accept traffic for this service.
 * @property externalName The external reference that clients will use (for ExternalName type).
 * @property externalTrafficPolicy The policy for external traffic (Cluster, Local).
 * @property internalTrafficPolicy The policy for internal traffic (Cluster, Local).
 * @property allocateLoadBalancerNodePorts Whether to allocate node ports for LoadBalancer type.
 * @property loadBalancerIP The requested IP for the load balancer (deprecated).
 * @property loadBalancerClass The class of the load balancer.
 * @property loadBalancerSourceRanges The list of IP ranges allowed to access the load balancer.
 * @property sessionAffinity The session affinity policy.
 * @property sessionAffinityConfig The configuration for session affinity.
 * @property publishNotReadyAddresses Whether to publish the addresses of pods that are not ready.
 * @property healthCheckNodePort The port for health checks (for LoadBalancer type).
 * @property trafficDistribution The traffic distribution policy.
 */
@NoArgs
data class ServiceSpec(
    val type: Type?,
    val selector: Map<String, String>?, //TODO: replace with reference
    val ports: List<PortMappingSpec>,
    val clusterIP: String?,
    val clusterIPs: List<String>?,
    val ipFamilies: Set<IPFamily>?,
    val ipFamilyPolicy: FamilyPolicy?,
    val externalIPs: List<String>?,
    val externalName: String?,
    val externalTrafficPolicy: TrafficPolicy?,
    val internalTrafficPolicy: TrafficPolicy?,
    val allocateLoadBalancerNodePorts: Boolean?,
    @Deprecated("This field was under-specified and its meaning varies across implementations. Using it is non-portable and it may not support dual-stack. Users are encouraged to use implementation-specific annotations when available.")
    val loadBalancerIP: String?,
    val loadBalancerClass: String?,
    val loadBalancerSourceRanges: List<String>?,
    val sessionAffinity: SessionAffinity?,
    val sessionAffinityConfig: SessionAffinityConfig?,
    val publishNotReadyAddresses: Boolean?,
    val healthCheckNodePort: Int?,
    val trafficDistribution: TrafficDistribution?
) : ResourceSpec {
    companion object {
        /**
         * Represents the kind of Kubernetes resource associated with this specification.
         *
         * This constant identifies the resource as a Service. It is used to specify
         * the resource type in Kubernetes configurations and APIs.
         */
        const val KIND = "Service"

        /**
         * Specifies the API version used for Kubernetes Service resources.
         *
         * This constant defines the API group and version under which the Service
         * resource is defined in Kubernetes. It is commonly used in the resource's
         * metadata to indicate the version of the Kubernetes API applicable to the
         * Service specification.
         */
        const val API_VERSION = "v1"
    }

    /**
     * Represents the type of service in Kubernetes.
     *
     * The `Type` enum defines the ways a Kubernetes service can expose
     * itself to the network. Each value corresponds to a specific service
     * exposure method.
     *
     * This enum is used by `ServiceSpec` to specify the desired service
     * type and its behavior within the cluster or externally.
     */
    @Suppress("unused")
    enum class Type {
        /**
         * Represents a Kubernetes service type where the service is only accessible
         * internally within the cluster.
         *
         * The `ClusterIP` type is the default service type in Kubernetes. It assigns
         * a stable, internal IP address to the service, which can be used by other
         * services or pods within the same cluster to communicate.
         *
         * This service type does not allow external access outside the cluster.
         */
        ClusterIP,

        /**
         * Represents a Kubernetes service type where the service is exposed externally via a specific port on each node.
         *
         * The `NodePort` type allows external access to a service by exposing a port on each node's network interface.
         * When this type is used, Kubernetes maps the requested service port to a specific port number on each
         * cluster node, enabling communication from sources outside the cluster.
         *
         * This service type is commonly used for exposing a service for development or debugging purposes,
         * or when load balancing is managed outside Kubernetes. It requires a specific port range to be
         * configured in the Kubernetes cluster.
         */
        NodePort,

        /**
         * Represents a Kubernetes service type that provisions a load balancer to expose the service externally.
         *
         * The `LoadBalancer` type is used in Kubernetes to automatically configure an external load balancer provided by
         * the cloud provider, or an on-premises solution integrated with the cluster. This service type routes external
         * traffic to the pods running the specified service through the load balancer.
         *
         * Characteristics:
         * - Automatically assigns an external IP address to allow communication with the service from outside the cluster.
         * - Provides built-in load-balancing capabilities to distribute incoming requests across multiple pods.
         * - Typically used in cloud environments where native load balancing is supported, such as AWS, GCP, or Azure.
         *
         * Constraints:
         * - Requires appropriate integration with the infrastructure provider.
         * - Depending on the provider, additional costs might be incurred due to the creation and use of the load balancer.
         *
         * It's commonly used to expose high-availability services to external users while dynamically managing traffic distribution.
         */
        LoadBalancer,

        /**
         * Represents an externalized name, often serving as a reference or alias to a specific entity
         * within a Kubernetes resource specification or other related abstractions.
         *
         * This class may be used to encapsulate naming conventions or perform mappings for external-facing
         * naming requirements pertinent to Kubernetes or other resource management systems.
         */
        ExternalName
    }

    /**
     * Defines the IP family policy for a Kubernetes Service.
     *
     * This enumeration is used to configure the behavior of a Service
     * with respect to the selection and requirement of IP address families
     * (IPv4, IPv6, or both).
     *
     * This policy helps control how Services interact with the networking stack and manage
     * IP address assignments based on the cluster's capability and configuration.
     */
    @Suppress("unused")
    enum class FamilyPolicy {
        /**
         * Specifies the IP family policy as "SingleStack" for a Kubernetes Service.
         *
         * This policy ensures that the Service uses a single IP address family,
         * either IPv4 or IPv6, depending on the cluster configuration.
         *
         * It is suitable for clusters that do not support dual-stack or prefer
         * to enforce the use of a single IP address family for simplicity.
         */
        SingleStack,

        /**
         * Specifies the IP family policy as "PreferDualStack" for a Kubernetes Service.
         *
         * This policy indicates a preference for using dual-stack (both IPv4 and IPv6)
         * when assigning IP addresses to the Service, if the cluster is dual-stack capable.
         * If dual-stack is not supported, the Service may fall back to a single IP family
         * based on the cluster configuration.
         *
         * It is useful for scenarios where dual-stack is desired but not strictly required,
         * allowing for greater flexibility in environments with varying networking capabilities.
         */
        PreferDualStack,

        /**
         * Specifies the IP family policy as "RequireDualStack" for a Kubernetes Service.
         *
         * This policy enforces the use of dual-stack (both IPv4 and IPv6) when assigning
         * IP addresses to the Service. It ensures that the Service cannot operate unless
         * both IP families are supported and allocated by the cluster.
         *
         * "RequireDualStack" is used in scenarios where strict dual-stack functionality
         * is essential, and fallback to a single IP family is not permissible.
         */
        RequireDualStack
    }

    /**
     * Represents the traffic routing policy for a Service
     * in a Kubernetes cluster.
     *
     * The traffic policy determines how traffic is distributed
     * between nodes within the cluster and can affect performance,
     * reliability, and accessibility.
     */
    @Suppress("unused")
    enum class TrafficPolicy {
        /**
         * Represents the traffic routing policy within the Kubernetes Service.
         *
         * This policy dictates how traffic is balanced and handled across the cluster,
         * influencing key aspects such as node accessibility, load distribution, and
         * service reliability. This option is typically coupled with a Service resource
         * to define its associated traffic characteristics.
         *
         * The `Cluster` routing policy ensures that traffic is routed to any node
         * within the cluster, regardless of its origin. This provides improved
         * availability and load balancing across all nodes.
         */
        Cluster,

        /**
         * Specifies the `Local` traffic routing policy for a Service in a Kubernetes cluster.
         *
         * The `Local` traffic policy ensures that service traffic is only routed to the node
         * where the associated pod is running. This policy is typically used to preserve client
         * source IPs during traffic routing and to minimize cross-node traffic within the cluster.
         * It is best suited for scenarios where strict locality of traffic is a requirement, such as
         * when working with external load balancers or implementing specific network configurations.
         *
         * Using this policy may limit the availability of a Service if no pods are available on a
         * given node to handle incoming traffic.
         */
        Local
    }

    /**
     * Specifies the IP address family used for a Kubernetes Service.
     *
     * The IP family determines whether the Service will use IPv4 or IPv6 addresses.
     * This is relevant in environments that support dual-stack networking, allowing
     * Services to explicitly choose their preferred IP version family.
     */
    @Suppress("unused")
    enum class IPFamily {
        /**
         * Represents the IPv4 address family.
         *
         * This is an enumeration value used to specify that the associated Kubernetes
         * Service will operate using the IPv4 address protocol. It is relevant in
         * environments that support dual-stack networking or in scenarios where explicitly
         * setting the IP family is required.
         */
        IPv4,

        /**
         * Represents the IPv6 address family.
         *
         * This enumeration value is used to specify that the associated Kubernetes
         * Service will operate using the IPv6 address protocol. It is relevant in
         * environments that support dual-stack networking or in scenarios where explicitly
         * defining the IP family is necessary for a resource's configuration.
         */
        IPv6
    }

    /**
     * Defines the session affinity settings for a Kubernetes Service.
     *
     * Session affinity determines how successive connections from the same client
     * are routed to the same service backend. This allows for client-specific
     * session persistence.
     */
    @Suppress("unused")
    enum class SessionAffinity {
        /**
         * Represents the absence of session affinity for a Kubernetes Service.
         *
         * When `None` is set for session affinity in a Service specification, client requests
         * are not constrained to any specific backend pod. Each request is routed independently,
         * allowing the traffic to be distributed among all backend pods based on the service's
         * defined load balancing strategy.
         */
        None,

        /**
         * Specifies the `ClientIP` session affinity setting for a Kubernetes Service.
         *
         * When `ClientIP` is configured, requests from the same client will be consistently routed to the
         * same backend pod. This is achieved by caching the client's IP address in a session affinity
         * table on the node where the Kubernetes Service resides. This setting is typically used to
         * maintain session persistence for stateful applications.
         */
        ClientIP
    }

    /**
     * Defines the available options for configuring traffic distribution in a Kubernetes Service resource.
     *
     * The `TrafficDistribution` enum specifies how traffic is distributed within the service,
     * providing options to balance or route traffic according to different criteria.
     *
     * @see ServiceSpec.trafficDistribution
     */
    @Suppress("unused")
    enum class TrafficDistribution {
        /**
         * Represents a preference for directing traffic to endpoints that are geographically
         * or logically closer to the client in a Kubernetes service configuration.
         *
         * This enum constant is used for configuring traffic distribution within a service,
         * optimizing latency by selecting endpoints that are nearer to the request origin whenever possible.
         */
        PreferClose
    }

    /**
     * Configuration for session affinity in a Kubernetes Service.
     *
     * This class defines the session affinity settings for a Service, determining how requests
     * from a client are consistently routed to the same backend pod. The session affinity is
     * based on the client's IP address, which is configured via the `ClientIPConfig`.
     *
     * @property clientIP Configuration for session affinity based on client IP.
     */
    data class SessionAffinityConfig(val clientIP: ClientIPConfig)

    /**
     * Represents the configuration for client IP settings in a Kubernetes Service specification.
     *
     * This data class is used to define the timeout duration for client IP session affinity,
     * which determines how long a client's IP address will remain associated with their session.
     *
     * @property timeoutSeconds The duration, in seconds, defining the timeout for client IP session affinity.
     * This value is serialized and deserialized using custom serializers to handle duration-related logic.
     */
    data class ClientIPConfig(
        @field:JsonSerialize(using = DurationInSecondsSerializer::class)
        @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
        val timeoutSeconds: Duration
    )
}