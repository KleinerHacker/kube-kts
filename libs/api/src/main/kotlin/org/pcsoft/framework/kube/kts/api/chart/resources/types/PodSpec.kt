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

import com.fasterxml.jackson.annotation.JsonIgnore
import org.pcsoft.framework.kube.kts.api.chart.types.MetadataPodSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.MapToNameValueDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.MapToNameValueSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Describes the desired state of a Pod: the containers it runs and the environment they run in.
 *
 * @property containers                    The containers of this Pod. At least one is required.
 * @property initContainers                Containers run to completion, in order, before the main [containers]
 *                                         start. An init container with [ContainerSpec.restartPolicy] set to
 *                                         `Always` becomes a native sidecar instead.
 * @property ephemeralContainers           Temporary debugging containers. Not settable through a manifest.
 * @property restartPolicy                 Controls whether and when the Pod's containers are restarted.
 * @property dnsPolicy                     Controls how DNS resolution is configured for the Pod.
 * @property dnsConfig                     Additional nameservers, search domains and resolver options,
 *                                         merged according to [dnsPolicy].
 * @property serviceAccountName            The ServiceAccount this Pod runs as.
 * @property automountServiceAccountToken  Whether the ServiceAccount token is mounted automatically.
 * @property nodeName                      Pins the Pod to a specific node, bypassing the scheduler.
 * @property hostNetwork                   If true, the Pod shares the host's network namespace.
 * @property hostPID                       If true, the Pod shares the host's process ID namespace.
 * @property hostIPC                       If true, the Pod shares the host's IPC namespace.
 * @property hostUsers                     If false, the Pod runs in its own user namespace, mapping container
 *                                         root to an unprivileged host user.
 * @property shareProcessNamespace         If true, all containers of the Pod share a single process namespace.
 * @property hostname                      Overrides the Pod's hostname.
 * @property subdomain                     The subdomain used to build the Pod's fully qualified domain name.
 * @property setHostnameAsFQDN             If true, the Pod's hostname is set to its fully qualified domain name.
 * @property priorityClassName             The PriorityClass determining this Pod's scheduling priority.
 * @property priority                      The resolved numeric priority. Normally set by the system from
 *                                         [priorityClassName].
 * @property preemptionPolicy              Controls whether this Pod may preempt lower-priority Pods.
 * @property schedulerName                 The scheduler responsible for placing this Pod.
 * @property runtimeClassName              The RuntimeClass selecting the container runtime configuration.
 * @property os                            The operating system the Pod's containers require.
 * @property overhead                      The resources consumed by the Pod's sandbox itself, on top of its
 *                                         containers' requests. Normally set by the system from [runtimeClassName].
 * @property resources                     Pod-level resource requests and limits, shared by all containers.
 * @property nodeSelector                  Restricts scheduling to nodes carrying all of these labels.
 * @property imagePullSecrets              Secrets holding registry credentials for pulling the containers' images.
 * @property volumes                       The volumes available to this Pod's containers.
 * @property enableServiceLinks            Whether Service information is injected as environment variables.
 * @property topologySpreadConstraints     Rules spreading the Pods of this workload across failure domains.
 * @property affinity                      Node and Pod affinity rules influencing scheduling.
 * @property tolerations                   The node taints this Pod tolerates.
 * @property securityContext               Pod-wide security settings, inherited by all containers.
 * @property terminationGracePeriodSeconds How long containers may take to shut down before being killed.
 * @property activeDeadlineSeconds         The maximum lifetime of the Pod before it is actively terminated.
 * @property readinessGates                Additional conditions that must hold before the Pod is considered ready.
 * @property schedulingGates                Gates that block scheduling until they are removed by a controller.
 * @property hostAliases                   Additional entries written into the containers' `/etc/hosts`.
 * @property resourceClaims                Claims for specialised hardware that containers may reference.
 */
@NoArgs
data class PodSpec(
    val containers: List<ContainerSpec>,
    val initContainers: List<ContainerSpec>?,
    @Deprecated(
        message = "Ephemeral containers cannot be set through a manifest. They are added at runtime through " +
                "the pod's 'ephemeralcontainers' subresource, for example with 'kubectl debug'. The field is " +
                "therefore excluded from the rendered YAML.",
        level = DeprecationLevel.WARNING
    )
    @field:JsonIgnore
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
    val hostUsers: Boolean?,
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
    val overhead: HardwareResourceSpec.Data?,
    val resources: HardwareResourceSpec?,
    val nodeSelector: Map<String, String>?,
    val imagePullSecrets: List<LocalObjectReferenceSpec>?,
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
    val readinessGates: List<ReadinessGateSpec>?,
    val schedulingGates: List<SchedulingGateSpec>?,
    val hostAliases: List<HostAliasSpec>?,
    val resourceClaims: List<ResourceClaimSpec>?,
) {
    /**
     * Validates that the Pod declares at least one container.
     */
    init {
        require(containers.isNotEmpty()) { "At least one container is required" }
    }

    /**
     * Controls whether and when the containers of a Pod are restarted.
     */
    @Suppress("unused")
    enum class RestartPolicy {
        /**
         * Containers are always restarted. The only valid choice for long-running workloads.
         */
        Always,

        /**
         * Containers are restarted only if they exit with a non-zero status.
         */
        OnFailure,

        /**
         * Containers are never restarted.
         */
        Never
    }

    /**
     * Controls how DNS resolution is configured for a Pod.
     */
    @Suppress("unused")
    enum class DnsPolicy {
        /**
         * The Pod resolves through the cluster DNS service. This is the default.
         */
        ClusterFirst,

        /**
         * Like [ClusterFirst], but also usable when the Pod runs with `hostNetwork` enabled.
         */
        ClusterFirstWithHostNet,

        /**
         * The Pod inherits the node's resolver configuration.
         */
        Default,

        /**
         * No resolver configuration is generated; everything comes from `dnsConfig`.
         */
        None
    }

    /**
     * Controls whether a Pod may preempt lower-priority Pods when it cannot be scheduled.
     */
    @Suppress("unused")
    enum class PreemptionPolicy {
        /**
         * The Pod may evict lower-priority Pods to make room for itself.
         */
        PreemptLowerPriority,

        /**
         * The Pod waits in the scheduling queue instead of evicting other Pods.
         */
        Never
    }

    /**
     * The operating system the containers of a Pod require.
     */
    @Suppress("unused")
    enum class OS {
        /**
         * The Pod runs on Linux nodes.
         */
        Linux,

        /**
         * The Pod runs on Windows nodes.
         */
        Windows
    }

    /**
     * An additional entry written into the containers' `/etc/hosts`.
     *
     * @property ip        The IP address the hostnames resolve to.
     * @property hostnames The hostnames mapped to [ip].
     */
    @NoArgs
    data class HostAliasSpec(
        val ip: String,
        val hostnames: List<String>
    ) {
        /**
         * Validates that an address and at least one hostname are given.
         */
        init {
            require(ip.isNotBlank()) { "IP address cannot be blank" }
            require(hostnames.isNotEmpty()) { "At least one hostname is required" }
        }
    }

    /**
     * An additional condition that must hold before the Pod is considered ready.
     *
     * The condition is not evaluated by Kubernetes itself; an external controller has to set it on the
     * Pod status. Until it is true, the Pod stays out of its Services' endpoints.
     *
     * @property conditionType The type of the Pod condition that has to become true.
     */
    @NoArgs
    data class ReadinessGateSpec(
        val conditionType: String
    ) {
        /**
         * Validates that the condition type is not blank.
         */
        init {
            require(conditionType.isNotBlank()) { "Condition type must not be blank" }
        }
    }

    /**
     * A gate that blocks scheduling of the Pod until it is removed.
     *
     * Scheduling gates let a controller hold a Pod in the `Pending` phase until some precondition is
     * met - for example until a required resource has been provisioned.
     *
     * @property name The name identifying this gate.
     */
    @NoArgs
    data class SchedulingGateSpec(
        val name: String
    ) {
        /**
         * Validates that the gate name is not blank.
         */
        init {
            require(name.isNotBlank()) { "Scheduling gate name must not be blank" }
        }
    }

    /**
     * A claim for specialised hardware that the Pod's containers may reference.
     *
     * Exactly one of [resourceClaimName] and [resourceClaimTemplateName] has to be given: the former
     * binds an existing claim, the latter generates a per-Pod claim from a template.
     *
     * @property name                     The name under which containers reference this claim.
     * @property resourceClaimName        The name of an existing ResourceClaim to bind.
     * @property resourceClaimTemplateName The name of a ResourceClaimTemplate to generate a claim from.
     */
    @NoArgs
    data class ResourceClaimSpec(
        val name: String,
        val resourceClaimName: String?,
        val resourceClaimTemplateName: String?
    ) {
        /**
         * Validates the claim name and that exactly one source is given.
         */
        init {
            require(name.isNotBlank()) { "Resource claim name must not be blank" }
            require((resourceClaimName == null) != (resourceClaimTemplateName == null)) {
                "Exactly one of 'resourceClaimName' and 'resourceClaimTemplateName' must be set"
            }
        }
    }

    /**
     * Additional resolver configuration merged into the Pod's `/etc/resolv.conf`.
     *
     * How the entries combine with the cluster defaults is governed by [PodSpec.dnsPolicy].
     *
     * @property nameservers The IP addresses of additional nameservers.
     * @property searches    Additional DNS search domains.
     * @property options     Additional resolver options. Rendered as a list of `name`/`value` pairs; an
     *                       option without a value is expressed by an empty string.
     */
    @NoArgs
    data class DnsConfigurationSpec(
        val nameservers: List<String>?,
        val searches: List<String>?,
        @field:JsonSerialize(using = MapToNameValueSerializer::class)
        @field:JsonDeserialize(using = MapToNameValueDeserializer::class)
        val options: Map<String, String>?
    )
}

/**
 * Pairs Pod metadata with a [PodSpec] to form the template a controller creates Pods from.
 *
 * @property metadata The labels and annotations applied to the created Pods. Must carry the labels the
 *                    owning controller's selector matches on.
 * @property spec     The specification of the created Pods.
 */
@NoArgs
data class PodTemplateSpec(
    val metadata: MetadataPodSpec?,
    val spec: PodSpec
)
