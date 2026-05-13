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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Defines the specifications for affinity and anti-affinity rules that influence
 * pod scheduling within a Kubernetes cluster.
 *
 * This data class aggregates the node affinity, pod affinity, and pod anti-affinity
 * configurations to customize the behavior of the Kubernetes scheduler. It enables
 * fine-grained control over pod placement relative to nodes and other pods based on
 * defined criteria.
 *
 * @property nodeAffinity Specifies the node affinity rules governing how pods should
 * be scheduled onto nodes. Node affinity allows configuring rules at the node level,
 * including mandatory constraints and weighting preferences.
 *
 * @property podAffinity Specifies the pod affinity rules for controlling the co-location
 * of pods. Pod affinity rules determine the conditions under which a pod should be
 * scheduled close to specific other pods.
 *
 * @property podAntiAffinity Specifies the pod anti-affinity rules for controlling the
 * segregation of pods. Pod anti-affinity rules define conditions under which a pod
 * should avoid being scheduled near specific other pods.
 */
@NoArgs
data class AffinitySpec(
    val nodeAffinity: NodeAffinitySpec?,
    val podAffinity: PodAffinitySpec?,
    val podAntiAffinity: PodAffinitySpec?,
)

/**
 * Represents the node affinity specifications that control how pods are scheduled
 * onto nodes based on certain criteria. This includes both required and preferred
 * affinity rules, which define mandatory constraints and preferred preferences for
 * selecting nodes during scheduling.
 *
 * @property requiredDuringSchedulingIgnoredDuringExecution A list of node selector terms
 * that specify the mandatory constraints for node selection. All terms in this list must be
 * satisfied for a pod to be scheduled onto a node. These constraints are enforced at the
 * time of scheduling but are not reevaluated during execution.
 *
 * @property preferredDuringSchedulingIgnoredDuringExecution A list of preferred scheduling
 * terms that specify preferences for node selection. Each term defines a preference with
 * an associated weight, where higher weights indicate stronger preferences. These preferences
 * are considered during scheduling but do not prevent scheduling onto nodes that do not match.
 */
@NoArgs
data class NodeAffinitySpec(
    val requiredDuringSchedulingIgnoredDuringExecution: List<NodeSelectorTermSpec>? = null,
    val preferredDuringSchedulingIgnoredDuringExecution: List<PreferredSchedulingTermSpec>? = null
)

/**
 * Represents the specification for pod affinity or anti-affinity rules in Kubernetes.
 *
 * This class provides the configuration for defining affinity or anti-affinity
 * considerations that affect pod scheduling in a Kubernetes cluster. Affinities
 * and anti-affinities are used to control how pods are co-located or segregated
 * within nodes based on specific criteria.
 *
 * @property requiredDuringSchedulingIgnoredDuringExecution A list of pod affinity
 * or anti-affinity terms that must be met for the pod to be scheduled onto a node.
 * These terms are strictly enforced during scheduling but are ignored during
 * execution if the scheduling constraints are violated for any reason.
 *
 * @property preferredDuringSchedulingIgnoredDuringExecution A list of weighted pod
 * affinity or anti-affinity terms that influence scheduling preferences for the pod.
 * These terms are considered during scheduling to guide placement, but they are not
 * strictly enforced.
 */
@NoArgs
data class PodAffinitySpec(
    val requiredDuringSchedulingIgnoredDuringExecution: List<PodAffinityTermSpec>?,
    val preferredDuringSchedulingIgnoredDuringExecution: List<WeightedPodAffinityTermSpec>?
)
