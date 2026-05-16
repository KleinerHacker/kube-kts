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

import org.pcsoft.framework.kube.kts.api.chart.types.MetadataPodSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import org.pcsoft.framework.kube.kts.api.types.CpuValue
import org.pcsoft.framework.kube.kts.api.types.MemoryValue
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Represents the specification of a Pod in Kubernetes.
 *
 * A Pod is the smallest deployable unit in Kubernetes, consisting of one or more containers
 * that share storage, network resources, and a specification for how to run the containers.
 *
 * @property containers List of containers that are part of the Pod. Each container runs an application.
 * @property initContainers List of initialization containers that run before the main containers start.
 *                          These containers are used for setup tasks and run to completion sequentially.
 * @property ephemeralContainers List of ephemeral containers that can be added to a running Pod for debugging purposes.
 * @property restartPolicy The restart policy for all containers within the Pod. Defines when containers should be restarted.
 * @property dnsPolicy The DNS policy for the Pod. Determines how DNS resolution is handled for containers.
 * @property dnsConfig Custom DNS configuration settings for the Pod, including nameservers, search domains, and options.
 * @property serviceAccountName The name of the ServiceAccount to use for running the Pod. ServiceAccounts provide identity for processes running in the Pod.
 * @property automountServiceAccountToken Indicates whether the ServiceAccount token should be automatically mounted into the Pod.
 * @property nodeName The name of the node where the Pod should be scheduled. Used for direct node assignment.
 * @property hostNetwork If true, the Pod uses the host's network namespace instead of creating a separate network namespace.
 * @property hostPID If true, the Pod uses the host's PID namespace, allowing containers to see all processes on the host.
 * @property hostIPC If true, the Pod uses the host's IPC namespace for inter-process communication.
 * @property shareProcessNamespace If true, containers in the Pod share a single process namespace, making all processes visible to each other.
 * @property hostname The hostname to set for the Pod. If not specified, the Pod name is used.
 * @property subdomain The subdomain to set for the Pod. Combined with the hostname, this forms the fully qualified domain name.
 * @property setHostnameAsFQDN If true, sets the Pod's hostname to its fully qualified domain name (FQDN).
 * @property priorityClassName The name of the PriorityClass to use for the Pod. Determines scheduling priority.
 * @property priority The priority value for the Pod. Higher values indicate higher priority in scheduling decisions.
 * @property preemptionPolicy The preemption policy for the Pod. Determines whether lower-priority Pods can be evicted to make room for this Pod.
 * @property schedulerName The name of the scheduler to use for placing the Pod on a node.
 * @property runtimeClassName The name of the RuntimeClass to use for running the Pod. Defines the container runtime configuration.
 * @property os The operating system required by the Pod (Linux or Windows). Used for scheduling on compatible nodes.
 * @property overhead Resource overhead required by the Pod infrastructure, in addition to container resource requirements.
 * @property nodeSelector Map of key-value pairs used to select nodes for Pod placement based on node labels.
 * @property imagePullSecrets List of names of Secrets containing credentials for pulling container images from private registries.
 * @property volumes List of volumes that can be mounted by containers in the Pod. Volumes provide storage that persists across container restarts.
 * @property enableServiceLinks If true, environment variables containing service connection information are injected into containers.
 * @property topologySpreadConstraints List of constraints that control how Pods are spread across topology domains like zones and nodes.
 * @property affinity Affinity and anti-affinity rules that influence Pod scheduling decisions based on node or Pod characteristics.
 * @property tolerations List of tolerations that allow the Pod to be scheduled on nodes with matching taints.
 * @property securityContext Security settings applied at the Pod level, such as user IDs, SELinux options, and sysctls.
 * @property terminationGracePeriodSeconds Duration in seconds the Pod needs to terminate gracefully after receiving a termination signal.
 * @property activeDeadlineSeconds Duration in seconds relative to Pod start time after which the Pod is terminated if still running.
 * @property readinessGates List of additional conditions that must be met before the Pod is considered ready.
 * @property hostAliases List of host aliases that add entries to the Pod's /etc/hosts file for custom hostname resolution.
 * @property resourceClaims List of resource claims that allow Pods to request dynamically allocated resources.
 */
@NoArgs
data class PodSpec(
    val containers: List<ContainerSpec>,
    val initContainers: List<ContainerSpec>?,
    val ephemeralContainers: List<ContainerSpec>?,
    val restartPolicy: RestartPolicy?,
    val dnsPolicy: DnsPolicy?,
    val dnsConfig: DnsConfigurationSpec?,
    val serviceAccountName: String?,
    val automountServiceAccountToken: Boolean?,
    val nodeName: String?,
    val hostNetwork: Boolean?,
    val hostPID: Boolean?,
    val hostIPC: Boolean?,
    val shareProcessNamespace: Boolean?,
    val hostname: String?,
    val subdomain: String?,
    val setHostnameAsFQDN: Boolean?,
    val priorityClassName: String?,
    val priority: Int?,
    val preemptionPolicy: PreemptionPolicy?,
    val schedulerName: String?,
    val runtimeClassName: String?,
    val os: OS?,
    val overhead: OverheadSpec?,
    val nodeSelector: Map<String, String>?,
    val imagePullSecrets: List<String>?,
    val volumes: List<VolumeSpec>?,
    val enableServiceLinks: Boolean?,
    val topologySpreadConstraints: List<TopologySpreadConstraintSpec>?,
    val affinity: AffinitySpec?,
    val tolerations: List<TolerationSpec>?,
    val securityContext: PodSecurityContextSpec?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val terminationGracePeriodSeconds: Duration?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val activeDeadlineSeconds: Duration?,
    val readinessGates: List<String>?,
    val hostAliases: List<HostAliasSpec>?,
    val resourceClaims: List<ResourceClaimSpec>?,
) {
    /**
     * Specifies the restart policy for a Pod.
     *
     * The restart policy determines under what conditions a container in the Pod
     * should be restarted after it terminates. It is important for controlling
     * the behavior of workloads in scenarios such as failures or updates.
     */
    @Suppress("unused")
    enum class RestartPolicy {
        /**
         * Represents the "Always" restart policy for a Pod.
         *
         * This policy ensures that the containers in a Pod are always restarted after they terminate,
         * regardless of the exit status. It is suitable for cases where the workload must run continuously.
         */
        Always,

        /**
         * Represents the "OnFailure" restart policy for a Pod.
         *
         * This policy ensures that containers in a Pod are restarted only if they terminate with
         * a failure (non-zero exit code). It is suitable for scenarios where retries are required
         * for transient errors but not for successful terminations or expected failures.
         */
        OnFailure,

        /**
         * Represents the "Never" restart policy for a Pod.
         *
         * This policy ensures that containers in a Pod are never restarted, regardless
         * of the exit status. It is suitable for workloads where container restarts
         * are not desired, such as for debugging or specific one-time execution tasks.
         */
        Never
    }

    /**
     * Specifies the DNS policy for a pod in Kubernetes.
     *
     * This enum defines the DNS settings that determine how DNS resolution is handled for the containers
     * within a pod. The DNS policy affects how domain names are resolved and whether certain DNS features
     * like custom DNS settings are applicable.
     */
    @Suppress("unused")
    enum class DnsPolicy {
        /**
         * Specifies the DNS policy "ClusterFirst" for a pod in Kubernetes.
         *
         * This DNS policy prioritizes the resolution of service names within the Kubernetes cluster
         * before falling back to external DNS resolution. It is the default DNS setting for most pods,
         * unless the host networking is enabled or otherwise overridden.
         */
        ClusterFirst,

        /**
         * Specifies the DNS policy "ClusterFirstWithHostNet" for a pod in Kubernetes.
         *
         * This DNS policy is used when the pod uses host networking (HostNetwork=true),
         * ensuring that DNS queries are resolved using the same DNS configuration as the host.
         *
         * Unlike the "ClusterFirst" policy, "ClusterFirstWithHostNet" prioritizes resolution
         * within the cluster but adapts to the host network's DNS settings when host networking is enabled.
         *
         * Commonly applied in scenarios where pods need to operate within the host network's namespace
         * while maintaining a preference for resolving Kubernetes service names within the cluster.
         */
        ClusterFirstWithHostNet,

        /**
         * Specifies the default DNS policy for a pod in Kubernetes.
         *
         * This DNS policy indicates that the pod will use the default DNS settings configured
         * on the system or node where it is running. It does not prioritize cluster-first resolution
         * or apply custom Kubernetes DNS configurations. Typically used for pods that do not require
         * Kubernetes-specific DNS resolution behaviors.
         */
        Default,

        /**
         * Represents the absence of a DNS policy.
         *
         * This type is used to explicitly indicate that no specific DNS policy is
         * applied to a resource. It serves as a placeholder or default value
         * for scenarios where DNS settings are intentionally left undefined.
         */
        None
    }

    /**
     * Specifies the preemption policy for a Pod in Kubernetes.
     *
     * Preemption policies determine how the Kubernetes scheduler handles
     * the eviction of lower-priority Pods to make room for higher-priority Pods.
     * This is particularly useful in resource-constrained environments
     * to ensure that critical workloads are not starved of resources.
     */
    @Suppress("unused")
    enum class PreemptionPolicy {
        /**
         * Indicates that lower-priority Pods can be preempted to make room for higher-priority Pods.
         *
         * This preemption policy allows the Kubernetes scheduler to evict existing Pods with lower priority
         * when there are resource constraints, ensuring that higher-priority workloads have the resources
         * they require to run.
         *
         * Use this setting in environments where prioritization of critical workloads is necessary,
         * and the disruption of non-critical workloads is acceptable under resource contention scenarios.
         */
        PreemptLowerPriority,

        /**
         * Indicates that no preemption of lower-priority Pods is allowed.
         *
         * This preemption policy ensures that existing Pods are never evicted to accommodate
         * higher-priority Pods. It is used to guarantee that workloads with lower priority
         * remain stable and uninterrupted, even under resource-constrained conditions.
         */
        Never
    }

    /**
     * Represents the supported operating systems for a pod specification.
     *
     * This enum is used to define the operational platform the container runtime
     * will execute on, helping to ensure compatibility with the host environment.
     */
    @Suppress("unused")
    enum class OS {
        /**
         * Represents the Linux operating system within the context of a pod specification.
         *
         * This value is used to denote that the container runtime will operate on a Linux-based
         * environment. It ensures compatibility and alignment with the underlying host system
         * and runtime requirements specific to Linux platforms.
         */
        Linux,

        /**
         * Represents the Windows operating system within the context of a pod specification.
         *
         * This value is used to denote that the container runtime will operate on a Windows-based
         * environment. It ensures compatibility and alignment with the underlying host system and
         * runtime requirements specific to Windows platforms.
         */
        Windows
    }

    /**
     * Represents an overhead specification that defines the resource requirements
     * for CPU and memory associated with a workload or container.
     *
     * This class provides a structured way to define the resource overhead,
     * helping in managing and allocating system resources effectively.
     *
     * @property cpu The CPU overhead represented as a `CpuValue`, which indicates
     * the required CPU units for the workload.
     * @property memory The memory overhead represented as a `MemoryValue`, which
     * indicates the required memory allocation for the workload.
     */
    @NoArgs
    data class OverheadSpec(
        val cpu: CpuValue,
        val memory: MemoryValue
    )

    /**
     * Represents a host alias configuration in the context of a Kubernetes Pod specification.
     *
     * The HostAliasSpec class is used to provide mappings between an IP address and a list of associated hostnames.
     * These mappings are typically added to the /etc/hosts file of the Pod at runtime, enabling the Pod to resolve
     * custom hostnames to the specified IP address.
     *
     * This can be useful for scenarios such as:
     * - Overriding DNS resolution for specific hostnames within a Pod.
     * - Defining custom hostname-to-IP mappings for testing or development purposes.
     *
     * @property ip The IP address to which the hostnames will be mapped.
     * @property hostnames A list of hostnames that will be resolved to the provided IP address.
     */
    @NoArgs
    data class HostAliasSpec(
        val ip: String,
        val hostnames: List<String>
    ) {
        init {
            require(ip.isNotBlank()) { "IP address cannot be blank" }
            require(hostnames.isNotEmpty()) { "At least one hostname is required" }
        }
    }

    /**
     * Describes a specification for a resource claim within a pod.
     *
     * A resource claim allows pods to dynamically request and use specific
     * resources such as storage or compute capacity. This class provides
     * the details necessary to associate a pod with an existing resource claim
     * or to define a new one using a resource claim template.
     *
     * @property name The name of the resource claim as referenced in the pod.
     * @property resourceClaimName The name of an existing resource claim to use.
     *                             If null, a resource claim will be created based
     *                             on the associated template (if provided).
     * @property resourceClaimTemplateName The name of a template to dynamically create
     *                                     a resource claim for the pod. Ignored if
     *                                     `resourceClaimName` is set.
     */
    @NoArgs
    data class ResourceClaimSpec(
        val name: String,
        val resourceClaimName: String?,
        val resourceClaimTemplateName: String?
    )

    /**
     * Represents the DNS configuration specification for a resource.
     *
     * This class is used to define custom DNS settings, including nameservers,
     * search domains, and options, typically for use in Kubernetes pod specifications.
     *
     * @property nameservers A list of IP addresses representing the DNS nameservers to use.
     *                       Can be null if the default nameservers are preferred.
     * @property searches A list of search domains to append when resolving DNS queries.
     *                    Can be null if no search domains are specified.
     * @property options A map of key-value pairs representing DNS resolver options.
     *                   Can be null if no additional options are required.
     */
    @NoArgs
    data class DnsConfigurationSpec(
        val nameservers: List<String>?,
        val searches: List<String>?,
        val options: Map<String, String>?
    )
}

/**
 * Represents the pod template specification, providing metadata and a desired configuration
 * for a group of pods in a Kubernetes environment.
 *
 * The `PodTemplateSpec` is used to define a template for creating pod instances. It specifies
 * the metadata for labeling and annotating the pods, along with the desired pod configuration
 * such as containers, volumes, and resource constraints.
 *
 * @property metadata Metadata associated with the pod template. It may include information
 * such as labels and annotations used for identification, organization, or attaching additional
 * non-identifying metadata.
 * @property spec The desired specification for the pod, encapsulating key configurations for
 * containers, volumes, network policies, scheduling policies, and other runtime settings.
 */
@NoArgs
data class PodTemplateSpec(
    val metadata: MetadataPodSpec?,
    val spec: PodSpec
)
