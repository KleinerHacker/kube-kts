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

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.chart.types.MatchLabelKeySpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents a specification for a node selector term, which is used to identify a specific
 * group of nodes based on a set of match expressions and fields. This is typically used
 * in scenarios such as scheduling and affinity/anti-affinity rules for workloads.
 *
 * @property matchExpressions A list of match expressions based on labels to filter nodes.
 * Each expression consists of a key, an operator, and optionally a set of values.
 * @property matchFields A list of match fields based on node fields to filter nodes.
 * Similar to matchExpressions but applies to node fields rather than labels.
 */
@NoArgs
data class NodeSelectorTermSpec(
    val matchExpressions: List<NodeSelectorRequirementSpec>?,
    val matchFields: List<NodeSelectorRequirementSpec>?
) {
    /**
     * Represents a requirement specification for a node selector, which defines a key, an operator,
     * and an optional list of values to use when filtering or matching nodes.
     *
     * This is commonly used in scheduling and affinity/anti-affinity mechanisms to determine
     * the applicability of nodes for workloads.
     *
     * @property key The label or field key to match against.
     * @property operator The operator to apply when evaluating the match condition.
     * Operators include equality/inequality checks, existence checks, and comparisons.
     * @property values An optional list of values to match against, depending on the operator.
     * For example, certain operators like `In` and `NotIn` require a list of values, while others
     * like `Exists` or `DoesNotExist` do not.
     */
    @NoArgs
    data class NodeSelectorRequirementSpec(
        val key: String,
        val operator: Operator,
        val values: List<String>?
    ) {
        init {
            require(key.isNotBlank()) { "Key must not be blank" }
        }

        /**
         * Defines the set of operators used to specify matching criteria
         * in node selector requirements.
         */
        @Suppress("unused")
        enum class Operator {
            /**
             * Represents the `In` operator used to specify that a given label key must have
             * at least one of the provided values in its node selector requirement.
             */
            In,

            /**
             * Represents the `NotIn` operator used to specify that a given label key must not have
             * any of the provided values in its node selector requirement.
             */
            NotIn,

            /**
             * Represents the `Exists` operator used to indicate that a given label key
             * must be present in a node selector requirement, regardless of its value.
             */
            Exists,

            /**
             * Represents the `DoesNotExist` operator used to indicate that a given label key
             * must not be present in a node selector requirement.
             */
            DoesNotExist,

            /**
             * Represents the `Gt` operator used to specify that a given label key must have a value
             * greater than the provided value in its node selector requirement.
             */
            @JsonProperty("Gt")
            GreaterThan,

            /**
             * Represents the `Lt` operator used to specify that a given label key must have a value
             * less than the provided value in its node selector requirement.
             */
            @JsonProperty("Lt")
            LessThan
        }
    }
}

/**
 * Represents a preferred scheduling term specification used in node affinity rules.
 * A preferred scheduling term expresses a preference for scheduling workloads onto
 * certain nodes while assigning a weight to determine the priority of the preference.
 *
 * @property weight The weight associated with the preference, where higher values indicate
 * stronger preferences. Weight is used to prioritize nodes when multiple preferences exist.
 * @property preference The node selector term that specifies the criteria for selecting nodes.
 * This defines the conditions a node must satisfy to be considered a preferred match.
 */
@NoArgs
data class PreferredSchedulingTermSpec(
    val weight: Int,
    val preference: NodeSelectorTermSpec
)

/**
 * Represents the specification for a PodAffinityTerm in Kubernetes.
 *
 * A PodAffinityTerm defines criteria for defining affinities or anti-affinities
 * between pods based on labels and topology keys. This is commonly used in pod
 * scheduling to determine the placement of pods within a Kubernetes cluster.
 *
 * @property topologyKey Specifies the key of the node label. Pods will be distributed
 *                       based on the value of this key (e.g., "kubernetes.io/hostname").
 *                       Nodes with the same value for this key will be treated as a group.
 * @property labelSelector A label selector that specifies the set of pods considered
 *                         when applying this affinity or anti-affinity term. If null,
 *                         all pods are eligible for matching.
 * @property namespaces A list of namespaces that restrict the scope of the labelSelector
 *                      to only the specified namespaces. If null, the labelSelector is
 *                      applicable across all namespaces.
 * @property namespaceSelector A label selector for selecting namespaces whose pods
 *                             should be considered. If specified, this takes precedence
 *                             over the `namespaces` property.
 * @property matchLabelKeys Specifies keys for label-based matching. Pods are selected
 *                          based on whether labels corresponding to these keys both
 *                          exist and match for the affinity or anti-affinity rules.
 * @property mismatchLabelKeys Specifies keys for label-based mismatching. Pods are selected
 *                             based on whether labels corresponding to these keys exist
 *                             but do not match for the affinity or anti-affinity rules.
 */
@NoArgs
data class PodAffinityTermSpec(
    val topologyKey: String,
    val labelSelector: LabelSelectorSpec?,
    val namespaces: List<String>?,
    val namespaceSelector: LabelSelectorSpec?,
    val matchLabelKeys: MatchLabelKeySpec?,
    val mismatchLabelKeys: MatchLabelKeySpec?
)

/**
 * Represents a weighted specification of a PodAffinityTerm in Kubernetes.
 *
 * This class is used to define a PodAffinityTerm along with an associated weight,
 * which influences the scheduling preferences of pods within a Kubernetes cluster.
 *
 * @property weight The weight assigned to this PodAffinityTerm. Higher weight values
 *                  indicate stronger preference for the specified term during scheduling decisions.
 * @property podAffinityTerm The definition of the PodAffinityTerm, specifying criteria
 *                           for affinity or anti-affinity between pods based on labels
 *                           and topology keys.
 */
@NoArgs
data class WeightedPodAffinityTermSpec(
    val weight: Int,
    val podAffinityTerm: PodAffinityTermSpec
)