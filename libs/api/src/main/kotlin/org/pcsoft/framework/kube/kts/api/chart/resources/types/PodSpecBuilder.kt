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

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodSpec.*
import org.pcsoft.framework.kube.kts.api.types.CpuValue
import org.pcsoft.framework.kube.kts.api.types.MemoryValue

/**
 * Builder class for configuring and constructing the specification of a Kubernetes Pod.
 *
 * The `PodSpecBuilder` class provides an extensive set of methods and properties to define
 * the configuration of a Kubernetes Pod, including containers, networking settings, volumes,
 * security, resource claims, and more.
 **/
class PodSpecBuilder internal constructor() {
    private var containers: MutableList<ContainerSpecBuilder>? = null
    private var initContainers: MutableList<ContainerSpecBuilder>? = null
    private var ephemeralContainers: MutableList<ContainerSpecBuilder>? = null
    private var dnsConfig: DnsConfigurationSpecBuilder? = null
    private var overhead: OverheadSpecBuilder? = null
    private var nodeSelector: MutableMap<String, String>? = null
    private var imagePullSecrets: MutableList<String>? = null
    private var volumes: MutableList<VolumeSpecBuilder>? = null
    private var topologySpreadConstraints: MutableList<TopologySpreadConstraintSpecBuilder>? = null
    private var affinity: AffinitySpecBuilder? = null
    private var tolerations: MutableList<TolerationSpecBuilder>? = null
    private var securityContext: PodSecurityContextSpecBuilder? = null
    private var readinessGates: MutableList<String>? = null
    private var hostAliases: MutableList<HostAliasSpecBuilder>? = null
    private var resourceClaims: MutableList<ResourceClaimSpecBuilder>? = null

    /**
     * Specifies the restart policy for a Pod.
     */
    var restartPolicy: RestartPolicy? = null

    /**
     * Specifies the DNS policy for the Pod.
     */
    var dnsPolicy: DnsPolicy? = null

    /**
     * Specifies the name of the service account to be used by the pod.
     *
     * When set, the pod will use the defined service account for running within the specified namespace.
     * This allows the pod to inherit permissions and credentials associated with the service account.
     *
     * If not set, the default service account in the namespace will be used.
     */
    var serviceAccountName: String? = null

    /**
     * Specifies the security context for the Pod.
     *
     * The security context defines privilege and access control settings for the Pod.
     * It includes settings like user and group IDs, capabilities, and SELinux options.
     */
    var automountServiceAccountToken: Boolean? = null

    /**
     * Specifies the node selector for the Pod.
     *
     * The node selector allows the pod to be scheduled on specific nodes based on labels.
     * It is a map of key-value pairs that must match with node labels for scheduling.
     */
    var nodeName: String? = null

    /**
     * Specifies the host networking configuration for the Pod.
     *
     * The host networking configuration determines whether the Pod shares the host's network namespace.
     * When set to true, the Pod will use the host's network stack, allowing direct access to the host's network interfaces.
     */
    var hostNetwork: Boolean? = null

    /**
     * Specifies the host PID configuration for the Pod.
     *
     * The host PID configuration determines whether the Pod shares the host's process namespace.
     * When set to true, the Pod will share the host's process namespace, allowing processes in the Pod to see and interact with processes on the host.
     */
    var hostPID: Boolean? = null

    /**
     * Specifies the host IPC configuration for the Pod.
     *
     * The host IPC configuration determines whether the Pod shares the host's IPC namespace.
     * When set to true, the Pod will share the host's IPC namespace, allowing processes in the Pod to communicate with processes on the host using IPC mechanisms.
     */
    var hostIPC: Boolean? = null

    /**
     * Specifies the security context for the Pod's containers.
     *
     * The security context defines privilege and access control settings for the Pod's containers.
     * It includes settings like user and group IDs, capabilities, and SELinux options.
     */
    var shareProcessNamespace: Boolean? = null

    /**
     * Specifies the hostname for the Pod.
     *
     * The hostname is the name assigned to the Pod within the cluster.
     * It can be used for internal communication and identification within the cluster.
     */
    var hostname: String? = null

    /**
     * Specifies the subdomain for the Pod.
     *
     * The subdomain is an optional part of the Pod's hostname, allowing for hierarchical organization within the cluster.
     * It can be used to group Pods logically and improve readability.
     */
    var subdomain: String? = null

    /**
     * Specifies the hostname as FQDN for the Pod.
     *
     * When set to true, the Pod's hostname will be formatted as FQDN (Fully Qualified Domain Name),
     * which includes the subdomain and the cluster's domain name.
     */
    var setHostnameAsFQDN: Boolean? = null

    /**
     * Specifies the priority class for the Pod.
     *
     * The priority class determines the scheduling priority of the Pod within the cluster.
     * Pods with higher priority classes are scheduled before those with lower priority classes.
     */
    var priorityClassName: String? = null

    /**
     * Specifies the priority for the Pod.
     *
     * The priority determines the scheduling priority of the Pod within the cluster.
     * Pods with higher priority values are scheduled before those with lower priority values.
     */
    var priority: Int? = null

    /**
     * Specifies the maximum number of seconds the Pod can be scheduled before it is terminated.
     *
     * This setting allows for graceful termination of Pods that are not able to be scheduled within the specified time frame.
     */
    var preemptionPolicy: PreemptionPolicy? = null

    /**
     * Specifies the maximum number of seconds the Pod can be scheduled before it is terminated.
     *
     * This setting allows for graceful termination of Pods that are not able to be scheduled within the specified time frame.
     * It provides a safety mechanism to prevent Pods from being stuck in a scheduling loop indefinitely.
     */
    var schedulerName: String? = null

    /**
     * Specifies the runtime class for the Pod.
     *
     * The runtime class defines the specific runtime environment in which the Pod will be executed.
     * It allows for customization of the Pod's execution environment, such as using a specific container runtime or runtime options.
     */
    var runtimeClassName: String? = null

    /**
     * Specifies the operating system for the Pod.
     */
    var os: OS? = null

    /**
     * Specifies whether the Pod should be automatically assigned a hostname.
     */
    var enableServiceLinks: Boolean? = null

    /**
     * Specifies the termination grace period for the Pod.
     *
     * The termination grace period defines the amount of time given to the Pod to gracefully terminate before it is forcefully terminated.
     * It allows for cleanup operations and ensures a smooth shutdown process.
     */
    var terminationGracePeriodSeconds: Int? = null

    /**
     * Specifies the maximum number of seconds the Pod can be active before it is considered to be unhealthy.
     *
     * This setting allows for proactive detection of unhealthy Pods and facilitates timely intervention to prevent potential issues.
     */
    var activeDeadlineSeconds: Int? = null

    /**
     * Adds a new container specification to the list of containers.
     *
     * @param name The name of the container to be added.
     * @param image The image name, including the tag, for the container.
     * @param prepare A lambda for defining additional specifications or properties of the container.
     */
    fun addContainer(name: String, image: String, prepare: ContainerSpecBuilder.() -> Unit) {
        if (containers == null) {
            containers = mutableListOf()
        }
        containers!!.add(ContainerSpecBuilder(name, image).apply(prepare))
    }

    /**
     * Adds a new init container specification to the list of init containers.
     *
     * @param name The name of the init container to be added.
     * @param image The image name, including the tag, for the init container.
     * @param prepare A lambda for defining additional specifications or properties of the init container.
     */
    fun addInitContainer(name: String, image: String, prepare: ContainerSpecBuilder.() -> Unit) {
        if (initContainers == null) {
            initContainers = mutableListOf()
        }
        initContainers!!.add(ContainerSpecBuilder(name, image).apply(prepare))
    }

    /**
     * Adds a new ephemeral container specification to the list of ephemeral containers.
     *
     * @param name The name of the ephemeral container to be added.
     * @param image The image name, including the tag, for the ephemeral container.
     * @param prepare A lambda for defining additional specifications or properties of the ephemeral container.
     */
    fun addEphemeralContainer(name: String, image: String, prepare: ContainerSpecBuilder.() -> Unit) {
        if (ephemeralContainers == null) {
            ephemeralContainers = mutableListOf()
        }
        ephemeralContainers!!.add(ContainerSpecBuilder(name, image).apply(prepare))
    }

    /**
     * Configures a list of container specifications for the pod.
     *
     * @param prepare A lambda with receiver that defines the configuration for the containers using the
     *                `ContainerSpecListBuilder`. This builder allows the addition and customization
     *                of multiple container specifications within the pod.
     */
    fun containers(prepare: ContainerSpecListBuilder.() -> Unit) {
        ContainerSpecListBuilder().apply(prepare)
    }

    /**
     * Configures a list of init container specifications for the pod.
     *
     * @param prepare A lambda with receiver that defines the configuration for the init containers
     *                using the `InitContainerSpecListBuilder`. This builder allows the addition
     *                and customization of multiple init container specifications within the pod.
     */
    fun initContainers(prepare: InitContainerSpecListBuilder.() -> Unit) {
        InitContainerSpecListBuilder().apply(prepare)
    }

    /**
     * Configures a list of ephemeral container specifications for the pod.
     *
     * @param prepare A lambda with receiver that defines the configuration for the ephemeral containers
     *                using the `EphemeralContainerSpecListBuilder`. This builder allows the addition
     *                and customization of multiple ephemeral container specifications within the pod.
     */
    fun ephemeralContainers(prepare: EphemeralContainerSpecListBuilder.() -> Unit) {
        EphemeralContainerSpecListBuilder().apply(prepare)
    }

    /**
     * Configures DNS settings for the pod.
     *
     * @param prepare A lambda with receiver that defines the DNS configuration using the
     *                `DnsConfigurationSpecBuilder`. This allows you to configure nameservers,
     *                search domains, and DNS options for the pod.
     */
    fun dnsConfig(prepare: DnsConfigurationSpecBuilder.() -> Unit) {
        dnsConfig = DnsConfigurationSpecBuilder().apply(prepare)
    }

    /**
     * Configures the overhead settings for the pod, specifying the CPU and memory resource requirements
     * consumed by the pod's infrastructure.
     *
     * @param cpu The CPU value to specify the overhead resource requirement associated with the pod.
     * @param memory The memory value to specify the overhead resource requirement associated with the pod.
     */
    fun overhead(cpu: CpuValue, memory: MemoryValue) {
        overhead = OverheadSpecBuilder(cpu, memory)
    }

    /**
     * Adds a key-value pair to the node selector for the pod.
     * The node selector allows you to specify the set of nodes on which the pod is eligible to run.
     * This can be used to constrain pods to run on specific nodes based on their labels.
     *
     * @param key The label key to match against nodes.
     * @param value The corresponding value for the label key. Pods will run on nodes that have a matching label with this key-value pair.
     */
    fun addNodeSelector(key: String, value: String) {
        if (nodeSelector == null) {
            nodeSelector = mutableMapOf()
        }
        nodeSelector!![key] = value
    }

    /**
     * Configures the node selector for the pod. The node selector is used to define a set of key-value
     * pairs that restrict the set of nodes eligible to run the pod. Nodes must have matching labels
     * for the pod to be scheduled onto them.
     *
     * @param prepare A lambda with receiver that allows you to define the node selector configuration.
     *                Use the provided `NodeSelectorMapBuilder` to specify the key-value pairs for
     *                selecting nodes.
     */
    fun nodeSelector(prepare: NodeSelectorMapBuilder.() -> Unit) {
        NodeSelectorMapBuilder().apply(prepare)
    }

    /**
     * Adds an image pull secret to the list of image pull secrets for the pod.
     *
     * An image pull secret is used to provide credentials for accessing private container
     * image registries.
     *
     * @param secret The name of the image pull secret to add. This should correspond to a
     *               secret defined in the cluster that contains the necessary authentication
     *               credentials for the private image registry.
     */
    fun addImagePullSecret(secret: String) {
        if (imagePullSecrets == null) {
            imagePullSecrets = mutableListOf()
        }
        imagePullSecrets!!.add(secret)
    }

    /**
     * Configures a list of image pull secrets for the pod.
     *
     * An image pull secret is used to store authentication credentials required
     * to access private container image registries. This method allows the setup
     * of multiple image pull secrets using a builder pattern.
     *
     * @param prepare A lambda with receiver that defines the configuration for
     *                image pull secrets using the `ImagePullSecretListBuilder`.
     *                Use this builder to specify the required image pull secrets.
     */
    fun imagePullSecrets(prepare: ImagePullSecretListBuilder.() -> Unit) {
        ImagePullSecretListBuilder().apply(prepare)
    }

    /**
     * Adds a new volume specification to the pod's volume list.
     *
     * @param name The name of the volume to be added.
     * @param prepare A lambda with receiver that defines additional specifications
     *                or properties of the volume using the `VolumeSpecBuilder`.
     */
    fun addVolume(name: String, prepare: VolumeSpecBuilder.() -> Unit) {
        if (volumes == null) {
            volumes = mutableListOf()
        }
        volumes!!.add(VolumeSpecBuilder(name).apply(prepare))
    }

    /**
     * Configures a list of volume specifications for the pod.
     *
     * @param prepare A lambda with receiver that defines the configuration for
     *                volumes using the `VolumeSpecListBuilder`. This builder
     *                allows the addition and customization of multiple volume
     *                specifications within the pod.
     */
    fun volumes(prepare: VolumeSpecListBuilder.() -> Unit) {
        VolumeSpecListBuilder().apply(prepare)
    }

    /**
     * Adds a topology spread constraint to the resource. This constraint ensures that pods are
     * distributed across the specified topology key based on the given parameters.
     *
     * @param maxSkew The maximum number of pods that can differ between the spread domains.
     * @param topologyKey The key used to denote the topology domain (e.g., node, zone).
     * @param whenUnsatisfiable The action to take when the constraint is not satisfied.
     * @param prepare A builder block to customize the topology spread constraint spec.
     */
    fun addTopologySpreadConstraint(
        maxSkew: Int,
        topologyKey: String,
        whenUnsatisfiable: TopologySpreadConstraintSpec.WhenUnsatisfiable,
        prepare: TopologySpreadConstraintSpecBuilder.() -> Unit
    ) {
        if (topologySpreadConstraints == null) {
            topologySpreadConstraints = mutableListOf()
        }
        topologySpreadConstraints!!.add(
            TopologySpreadConstraintSpecBuilder(
                maxSkew,
                topologyKey,
                whenUnsatisfiable
            ).apply(prepare)
        )
    }

    /**
     * Configures topology spread constraints by applying the provided configuration block.
     *
     * @param prepare A lambda receiver to define a list of topology spread constraints
     * using the TopologySpreadConstraintSpecListBuilder.
     */
    fun topologySpreadConstraints(prepare: TopologySpreadConstraintSpecListBuilder.() -> Unit) {
        TopologySpreadConstraintSpecListBuilder().apply(prepare)
    }

    /**
     * Configures an affinity specification using the provided builder function.
     *
     * @param prepare A lambda with receiver of type AffinitySpecBuilder
     *                used to build the affinity specification.
     */
    fun affinity(prepare: AffinitySpecBuilder.() -> Unit) {
        affinity = AffinitySpecBuilder().apply(prepare)
    }

    /**
     * Adds a toleration to the tolerations list, initializing the list if necessary.
     *
     * @param prepare A lambda function that applies a configuration to a TolerationSpecBuilder instance.
     */
    fun addToleration(prepare: TolerationSpecBuilder.() -> Unit) {
        if (tolerations == null) {
            tolerations = mutableListOf()
        }
        tolerations!!.add(TolerationSpecBuilder().apply(prepare))
    }

    /**
     * Configures a list of tolerations to customize behavior related to taints in a Kubernetes environment.
     *
     * @param prepare a lambda with a receiver of type `TolationSpecListBuilder` used to define the tolerations.
     */
    fun tolerations(prepare: TolerationSpecListBuilder.() -> Unit) {
        TolerationSpecListBuilder().apply(prepare)
    }

    /**
     * Configures the security context for a pod by applying the provided customization logic.
     *
     * @param prepare A lambda with receiver of type PodSecurityContextSpecBuilder that defines
     *                the customization logic for the pod's security context.
     */
    fun securityContext(prepare: PodSecurityContextSpecBuilder.() -> Unit) {
        securityContext = PodSecurityContextSpecBuilder().apply(prepare)
    }

    /**
     * Adds a readiness gate to the list of readiness gates. Readiness gates are used to specify
     * conditions that must be met for a resource to be considered ready.
     *
     * @param name The name of the readiness gate to be added.
     */
    fun addReadinessGate(name: String) {
        if (readinessGates == null) {
            readinessGates = mutableListOf()
        }
        readinessGates!!.add(name)
    }

    /**
     * Configures readiness gates using the provided builder configuration.
     *
     * The readiness gates define conditions that must be met before a resource is considered ready.
     *
     * @param prepare A lambda with receiver used to build and configure the readiness gates.
     */
    fun readinessGates(prepare: ReadinessGateListBuilder.() -> Unit) {
        ReadinessGateListBuilder().apply(prepare)
    }

    /**
     * Adds a host alias to the list of host aliases. If the list does not exist, it initializes a new list.
     *
     * @param ip The IP address associated with the host alias.
     * @param prepare A lambda function for configuring the host alias using the HostAliasSpecBuilder.
     */
    fun addHostAlias(ip: String, prepare: HostAliasSpecBuilder.() -> Unit) {
        if (hostAliases == null) {
            hostAliases = mutableListOf()
        }
        hostAliases!!.add(HostAliasSpecBuilder(ip).apply(prepare))
    }

    /**
     * Configures and applies a list of host aliases using the provided builder.
     *
     * @param prepare A lambda with receiver that configures the HostAliasListBuilder.
     */
    fun hostAliases(prepare: HostAliasListBuilder.() -> Unit) {
        HostAliasListBuilder().apply(prepare)
    }

    /**
     * Adds a new resource claim to the list of resource claims. If the list is not initialized,
     * it will create a new mutable list before adding the resource claim.
     *
     * @param name The name of the resource claim to be added.
     * @param prepare A lambda function used to configure the `ResourceClaimSpecBuilder` for the resource claim.
     */
    fun addResourceClaim(name: String, prepare: ResourceClaimSpecBuilder.() -> Unit) {
        if (resourceClaims == null) {
            resourceClaims = mutableListOf()
        }
        resourceClaims!!.add(ResourceClaimSpecBuilder(name).apply(prepare))
    }

    /**
     * Applies the provided configuration to a `ResourceClaimSpecListBuilder` instance.
     *
     * @param prepare a lambda with receiver used to configure the `ResourceClaimSpecListBuilder`.
     */
    fun resourceClaims(prepare: ResourceClaimSpecListBuilder.() -> Unit) {
        ResourceClaimSpecListBuilder().apply(prepare)
    }

    /**
     * Adds a new volume mount to the list of volume mounts. If the list does not exist, it initializes a new list.
     */
    class DnsConfigurationSpecBuilder internal constructor() {
        private var nameservers: MutableList<String>? = null
        private var searches: MutableList<String>? = null
        private var options: MutableMap<String, String>? = null

        /**
         * Adds a nameserver to the list of nameservers. If the list is not initialized, it creates a new one.
         *
         * @param nameserver The nameserver address to be added.
         */
        fun addNameserver(nameserver: String) {
            if (nameservers == null) {
                nameservers = mutableListOf()
            }
            nameservers!!.add(nameserver)
        }

        /**
         * Adds a search domain to the list of search domains. If the list is not initialized, it creates a new one.
         *
         * @param search The search domain to be added.
         */
        fun addSearch(search: String) {
            if (searches == null) {
                searches = mutableListOf()
            }
            searches!!.add(search)
        }

        /**
         * Adds an option to the map of options. If the options map is not initialized,
         * it creates a new one before adding the specified option.
         *
         * @param name The name of the option to be added.
         * @param value The value associated with the option name.
         */
        fun addOption(name: String, value: String) {
            if (options == null) {
                options = mutableMapOf()
            }
            options!![name] = value
        }

        /**
         * Configures the list of nameservers using the provided builder action.
         *
         * @param prepare A lambda with receiver that defines the configuration for the nameservers.
         */
        fun nameservers(prepare: NameserverListBuilder.() -> Unit) {
            NameserverListBuilder().apply(prepare)
        }

        /**
         * Configures the list of search domains using the provided builder action.
         *
         * @param prepare A lambda with receiver that allows configuration of the search domains.
         */
        fun searches(prepare: SearchListBuilder.() -> Unit) {
            SearchListBuilder().apply(prepare)
        }

        /**
         * Configures the options using the provided builder action.
         *
         * @param prepare A lambda with receiver that defines the configuration for the options.
         */
        fun options(prepare: OptionListBuilder.() -> Unit) {
            OptionListBuilder().apply(prepare)
        }

        /**
         * Builds and returns a `DnsConfigurationSpec` instance based on the current configuration.
         *
         * This method combines the `nameservers`, `searches`, and `options` that were added or configured
         * using the builder into a complete `DnsConfigurationSpec` object.
         *
         * @return A `DnsConfigurationSpec` instance containing the configured DNS settings.
         */
        internal fun build() = DnsConfigurationSpec(nameservers, searches, options)

        /**
         * Builder class for constructing a list of nameservers within the DNS configuration specification.
         *
         * This class is designed to be used internally by the containing `DnsConfigurationSpecBuilder` to provide
         * a fluent API for adding nameservers to the DNS configuration setup.
         *
         * The `nameserver` method allows the addition of an individual nameserver to the list.
         */
        inner class NameserverListBuilder internal constructor() {
            /**
             * Adds a nameserver to the DNS configuration.
             *
             * @param nameserver The nameserver address to be added to the configuration.
             */
            fun nameserver(nameserver: String) = addNameserver(nameserver)
        }

        /**
         * Builder class for constructing a list of search domains within the DNS configuration specification.
         */
        inner class SearchListBuilder internal constructor() {
            /**
             * Adds a search domain to the list of search domains by delegating to the `addSearch` method.
             *
             * @param search The search domain to be added.
             */
            fun search(search: String) = addSearch(search)
        }

        /**
         * Builder class for constructing a map of options within the DNS configuration specification.
         */
        inner class OptionListBuilder internal constructor() {
            /**
             * Adds a DNS configuration option to the list of options.
             *
             * @param name The name of the option to be added.
             * @param value The value corresponding to the specified option name.
             */
            fun option(name: String, value: String) = addOption(name, value)
        }
    }

    /**
     * Builder class for constructing instances of OverheadSpec.
     *
     * @constructor Constructs an OverheadSpecBuilder using the specified CPU and memory values.
     * @param cpu The CPU overhead value to be used in the built OverheadSpec.
     * @param memory The memory overhead value to be used in the built OverheadSpec.
     */
    class OverheadSpecBuilder internal constructor(private val cpu: CpuValue, private val memory: MemoryValue) {
        /**
         * Builds and returns an instance of OverheadSpec using the specified CPU and memory values.
         *
         * This method finalizes the construction process by encapsulating the provided CPU and memory
         * overheads into an immutable OverheadSpec object. The resulting object represents the overhead
         * resource requirements for CPU and memory for a particular workload or container.
         *
         * @return An OverheadSpec instance containing the configured CPU and memory overhead values.
         */
        internal fun build() = OverheadSpec(cpu, memory)
    }

    /**
     * Builder class for constructing a `HostAliasSpec` object, which associates an IP address
     * with one or more specified hostnames.
     *
     * @constructor Creates a `HostAliasSpecBuilder` with the specified IP address.
     * The constructor is marked as internal and is not intended for direct instantiation
     * outside of the package.
     *
     * @param ip The IP address to associate with the specified hostnames.
     */
    class HostAliasSpecBuilder internal constructor(private val ip: String) {
        private var hostnames: MutableList<String>? = null

        /**
         * Adds a hostname to the list of hostnames associated with the IP address.
         *
         * @param hostname The hostname to be added to the list.
         */
        fun addHostname(hostname: String) {
            if (hostnames == null) {
                hostnames = mutableListOf()
            }
            hostnames!!.add(hostname)
        }

        /**
         * Defines a list of hostnames using the provided configuration block.
         * The `prepare` block is executed on an internal `HostnameListBuilder`,
         * allowing hostnames to be added to the list.
         *
         * @param prepare A lambda with receiver of type `HostnameListBuilder`,
         *                which is used to configure and add hostnames.
         */
        fun hostnames(prepare: HostnameListBuilder.() -> Unit) {
            HostnameListBuilder().apply(prepare)
        }

        /**
         * Builds and returns a `HostAliasSpec` instance using the current configuration.
         *
         * This method finalizes the construction process and creates a `HostAliasSpec`
         * object that associates the specified IP address with the configured list of hostnames.
         * It validates that at least one hostname has been set before creating the object.
         *
         * @return A `HostAliasSpec` object containing the IP address and the list of associated hostnames.
         * @throws IllegalArgumentException if the hostname list has not been set.
         */
        internal fun build(): HostAliasSpec {
            require(hostnames != null) { "Hostname List must be set!" }

            return HostAliasSpec(ip, hostnames!!)
        }

        /**
         * Builder class for constructing a list of hostnames within a `HostAliasSpec`.
         */
        inner class HostnameListBuilder internal constructor() {
            /**
             * Adds a hostname to the list of hostnames managed by the builder.
             *
             * @param hostname The hostname to be added.
             */
            fun hostname(hostname: String) = addHostname(hostname)
        }
    }

    /**
     * A builder class for constructing instances of `ResourceClaimSpec`.
     *
     * This class is used to configure and create `ResourceClaimSpec` objects.
     * It provides methods to set optional parameters for a resource claim specification
     * and a function to finalize and build the corresponding `ResourceClaimSpec` instance.
     *
     * @constructor Creates a `ResourceClaimSpecBuilder` with the specified `name`.
     *
     * @param name The name of the resource claim specification.
     */
    class ResourceClaimSpecBuilder internal constructor(private val name: String) {
        /**
         * Sets the name of the resource claim associated with this specification.
         */
        var resourceClaimName: String? = null

        /**
         * Sets the name of the resource claim template associated with this specification.
         */
        var resourceClaimTemplateName: String? = null

        /**
         * Finalizes and constructs a `ResourceClaimSpec` instance using the parameters defined
         * in the `ResourceClaimSpecBuilder`.
         *
         * The method creates an immutable `ResourceClaimSpec` object by using the mandatory `name`
         * and optional `resourceClaimName` and `resourceClaimTemplateName` properties as configured
         * in the builder.
         *
         * @return A new instance of `ResourceClaimSpec` with the configured properties.
         */
        internal fun build() = ResourceClaimSpec(name, resourceClaimName, resourceClaimTemplateName)
    }

    /**
     * A sealed interface that provides a mechanism for building a list of container specifications.
     * It allows users to define individual containers with specific names, images, and configuration details.
     */
    sealed interface CommonContainerSpecListBuilder {
        /**
         * Defines a container with the specified name, image, and configuration.
         *
         * @param name The unique name of the container.
         * @param image The image to be used for the container.
         * @param prepare A lambda expression used to specify additional configuration for the container.
         */
        fun container(name: String, image: String, prepare: ContainerSpecBuilder.() -> Unit)
    }

    /**
     * A builder class for constructing a list of container specifications. This class is used to define
     * and configure multiple container definitions within a container spec list context.
     */
    inner class ContainerSpecListBuilder internal constructor() : CommonContainerSpecListBuilder {
        /**
         * Adds a container specification to the container list.
         *
         * @param name The unique name of the container.
         * @param image The container image to use, including the version tag if appropriate.
         * @param prepare A lambda for configuring additional specifications for the container.
         */
        override fun container(name: String, image: String, prepare: ContainerSpecBuilder.() -> Unit) =
            addContainer(name, image, prepare)
    }

    /**
     * Builder class for managing a list of init container specifications in a pod definition.
     *
     * This class is used to define and configure init containers, which run to completion before
     * any other containers in the pod are started. It provides methods to add and configure
     * init containers using a fluent interface.
     */
    inner class InitContainerSpecListBuilder internal constructor() : CommonContainerSpecListBuilder {
        /**
         * Adds an init container definition to the pod specification.
         *
         * @param name The name of the container.
         * @param image The container image to use.
         * @param prepare A lambda to configure the container using the ContainerSpecBuilder.
         */
        override fun container(name: String, image: String, prepare: ContainerSpecBuilder.() -> Unit) =
            addInitContainer(name, image, prepare)
    }

    /**
     * Builder class for creating and managing a list of ephemeral container specifications.
     *
     * This builder facilitates the addition of ephemeral containers with specific configurations
     * by implementing the `CommonContainerSpecListBuilder` interface. Each container can
     * be customized by specifying its name, image, and additional attributes defined within
     * the provided `prepare` block.
     *
     * Ephemeral containers are temporary containers that can be added to a running pod
     * for administrative purposes, such as debugging.
     */
    inner class EphemeralContainerSpecListBuilder internal constructor() : CommonContainerSpecListBuilder {
        /**
         * Adds an ephemeral container to the list with the specified name, image, and configuration.
         *
         * Ephemeral containers are typically used for tasks such as debugging
         * and are only added temporarily to a running pod.
         *
         * @param name The name of the ephemeral container to be added.
         * @param image The container image to be used for the ephemeral container.
         * @param prepare A lambda function used to configure additional properties
         * of the ephemeral container through the `ContainerSpecBuilder`.
         */
        override fun container(name: String, image: String, prepare: ContainerSpecBuilder.() -> Unit) =
            addEphemeralContainer(name, image, prepare)
    }

    /**
     * Builder class for constructing a list of container specifications.
     */
    inner class NodeSelectorMapBuilder internal constructor() {
        /**
         * Adds a key-value pair to the node selector map. The node selector constrains
         * pods to run on nodes that have matching labels with the specified key-value pair.
         *
         * @param key The label key to match against nodes.
         * @param value The corresponding value for the label key. Pods will run on nodes
         *              that have a matching label with this key-value pair.
         */
        fun select(key: String, value: String) = addNodeSelector(key, value)
    }

    /**
     * A builder class responsible for constructing a list of image pull secrets.
     * This class is used to configure Kubernetes image pull secrets dynamically.
     *
     * Use the `secret` function to add image pull secrets to the list.
     */
    inner class ImagePullSecretListBuilder internal constructor() {
        /**
         * Adds an image pull secret to the list of image pull secrets for the pod.
         *
         * Image pull secrets are used to authenticate against private container image registries.
         *
         * @param secret The name of the image pull secret to add. This must correspond to a
         *               secret defined in the Kubernetes cluster containing the required
         *               authentication credentials.
         */
        fun secret(secret: String) = addImagePullSecret(secret)
    }

    /**
     * Builder class for constructing a list of volume specifications.
     *
     * This class is used to define and configure volumes within a pod specification.
     * It provides methods to add and configure volumes using a fluent interface.
     */
    inner class VolumeSpecListBuilder internal constructor() {
        /**
         * Adds a new volume specification to the volume list with the provided name and configuration.
         *
         * This method allows defining and configuring a volume using the `VolumeSpecBuilder`. The configured
         * volume is then added to the list of volumes for a pod. Volumes are used in Kubernetes to provide
         * data storage for containers, such as temporary storage, secrets, or configuration maps.
         *
         * @param name The name of the volume to be added. This must be unique within the pod's volume list.
         * @param prepare A lambda with receiver that defines the volume's properties and source
         *                using the `VolumeSpecBuilder`.
         */
        fun volume(name: String, prepare: VolumeSpecBuilder.() -> Unit) = addVolume(name, prepare)
    }

    /**
     * Builder class for constructing a list of volume mount specifications.
     */
    inner class TopologySpreadConstraintSpecListBuilder internal constructor() {
        /**
         * Adds a topology spread constraint to control pod distribution across topology domains.
         *
         * This method allows you to specify rules (e.g., spreading pods evenly across zones or nodes)
         * to improve workload balancing and fault tolerance within your cluster.
         *
         * @param maxSkew The maximum allowed skew between the number of pods in one topology domain and another.
         *                A lower value enforces stricter balancing.
         * @param topologyKey The key indicating the topology domain to spread pods across (e.g.,
         *                    `failure-domain.beta.kubernetes.io/zone`).
         * @param whenUnsatisfiable Defines the action to take if the constraint cannot be satisfied.
         *                          Options include `DoNotSchedule` or `ScheduleAnyway`.
         * @param prepare A lambda for configuring additional details for the topology spread constraint
         *                using a `TopologySpreadConstraintSpecBuilder`.
         */
        fun constraint(
            maxSkew: Int,
            topologyKey: String,
            whenUnsatisfiable: TopologySpreadConstraintSpec.WhenUnsatisfiable,
            prepare: TopologySpreadConstraintSpecBuilder.() -> Unit
        ) = addTopologySpreadConstraint(maxSkew, topologyKey, whenUnsatisfiable, prepare)
    }

    /**
     * Builder class for constructing a list of toleration specifications.
     * This is used to define tolerations, typically for Kubernetes resource scheduling.
     *
     * The `TolerationSpecListBuilder` allows adding individual toleration specifications
     * using a configuration block that is provided via the `TolerationSpecBuilder`.
     *
     * This class is intended for internal use and should not be instantiated directly.
     */
    inner class TolerationSpecListBuilder internal constructor() {
        /**
         * Adds a toleration by applying the provided configuration to a [TolerationSpecBuilder].
         * This allows defining scheduling and execution rules based on taints in Kubernetes-like environments.
         *
         * @param prepare A lambda function that configures a [TolerationSpecBuilder] instance to define
         * the toleration's properties such as key, operator, value, effect, and toleration duration.
         */
        fun toleration(prepare: TolerationSpecBuilder.() -> Unit) = addToleration(prepare)
    }

    /**
     * Builder class for constructing a list of node affinity specifications.
     * This class is used to define node affinity rules for scheduling pods on specific nodes.
     */
    inner class ReadinessGateListBuilder internal constructor() {
        /**
         * Adds a readiness gate to the readiness gate list. Readiness gates specify additional conditions
         * that must be met for a resource to be considered ready.
         *
         * @param name The name of the readiness gate to be added.
         */
        fun readinessGate(name: String) = addReadinessGate(name)
    }

    /**
     * A builder class to construct a list of host aliases.
     *
     * This class provides functionality to define and manage
     * host aliases by associating an IP address with one or more hostnames.
     * It is used to facilitate configuration of host alias entries.
     */
    inner class HostAliasListBuilder internal constructor() {
        /**
         * Defines a host alias by associating an IP address with one or more hostnames.
         * This method leverages a builder-style configuration block to specify the hostnames.
         *
         * @param ip The IP address to be associated with the hostnames.
         * @param prepare A lambda with receiver of type `HostAliasSpecBuilder` used to configure the hostnames.
         */
        fun alias(ip: String, prepare: HostAliasSpecBuilder.() -> Unit) = addHostAlias(ip, prepare)
    }

    /**
     * A builder class for constructing a list of pod security context specifications.
     */
    inner class ResourceClaimSpecListBuilder internal constructor() {
        /**
         * Adds a new resource claim specification to the list of resource claims being built.
         *
         * This function creates a `ResourceClaimSpecBuilder` for the given resource claim name,
         * applies the specified configuration block to it, and adds the resulting configuration
         * to the list.
         *
         * @param name The name of the resource claim to be added.
         * @param prepare A lambda function used to configure the `ResourceClaimSpecBuilder` for the resource claim.
         */
        fun claim(name: String, prepare: ResourceClaimSpecBuilder.() -> Unit) = addResourceClaim(name, prepare)
    }
}