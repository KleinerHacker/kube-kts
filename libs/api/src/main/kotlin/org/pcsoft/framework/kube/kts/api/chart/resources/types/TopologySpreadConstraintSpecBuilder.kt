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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.TopologySpreadConstraintSpec.NodePolicy
import org.pcsoft.framework.kube.kts.api.chart.resources.types.TopologySpreadConstraintSpec.WhenUnsatisfiable
import org.pcsoft.framework.kube.kts.api.chart.types.MatchLabelKeySpecBuilder

/**
 * A builder class for configuring and creating a `TopologySpreadConstraintSpec` instance.
 *
 * This class provides methods for specifying topology spreading constraints for pods in a Kubernetes cluster,
 * ensuring that pod placement adheres to the defined constraints to optimize distribution or availability.
 *
 * @constructor Creates an instance of `TopologySpreadConstraintSpecBuilder` that must be initialized
 * with specific required parameters.
 * 
 * @param maxSkew The maximum skew allowed across topology domains. This defines the maximum
 * difference in the number of matching pods between any two topology domains.
 * @param topologyKey The key of the topology domain used to group nodes. Nodes with the same value
 * for the specified topology key belong to the same domain.
 * @param whenUnsatisfiable The action to take when the topology constraint cannot be satisfied.
 * Possible values are defined by the `WhenUnsatisfiable` enumeration.
 */
class TopologySpreadConstraintSpecBuilder internal constructor(
    private val maxSkew: Int,
    private val topologyKey: String,
    private val whenUnsatisfiable: WhenUnsatisfiable
) {
    private var labelSelector: LabelSelectorSpecBuilder? = null
    private var matchLabelKeys: MatchLabelKeySpecBuilder? = null

    /**
     * The minimum number of domains that must have pods satisfying the spread constraint.
     *
     * This variable is used to determine the minimum number of topology domains
     * where pods should be evenly spread based on the given constraints.
     * If set to `null`, the system may calculate or assume a default value.
     */
    var minDomains: Int? = null

    /**
     * Specifies the policy governing how node affinity rules are applied when scheduling pods.
     *
     * The `nodeAffinityPolicy` determines whether the scheduler respects or ignores
     * node affinity constraints when examining potential placement locations for pods.
     *
     * If set to `null`, the default behavior is implementation-specific.
     */
    var nodeAffinityPolicy: NodePolicy? = null

    /**
     * Configures the policy for handling taints on nodes when determining topology spread constraints.
     * 
     * This property defines whether the taints on nodes should be honored or ignored during the
     * calculation of pod placement. 
     * 
     * If set to null, the default behavior will be determined by the context in which this property
     * is used.
     */
    var nodeTaintsPolicy: NodePolicy? = null

    /**
     * Configures the label selector for the topology spread constraint.
     *
     * Example:
     * ```kotlin
     * labelSelector {
     *     matchLabels("app" to "my-app", "tier" to "backend")
     *     matchExpression {
     *         key = "environment"
     *         operator = In
     *         values("production", "staging")
     *     }
     * }
     * ```
     *
     * @param block A lambda with receiver of type LabelSelectorSpecBuilder that allows configuration 
     *              of the label selector used to determine which labels are applied.
     */
    fun labelSelector(block: LabelSelectorSpecBuilder.() -> Unit) {
        labelSelector = LabelSelectorSpecBuilder().apply(block)
    }

    /**
     * Adds a new key-value pair to the match label keys used for the topology spread constraint.
     *
     * @param key The key of the match label to be added.
     */
    fun addMatchLabelKey(key: String) {
        if (matchLabelKeys == null) {
            matchLabelKeys = MatchLabelKeySpecBuilder()
        }
        matchLabelKeys!!.key(key)
    }

    /**
     * Adds one or more match label keys to the `matchLabelKeys` property of the builder.
     *
     * This method appends the provided keys to the internal `MatchLabelKeySpecBuilder` instance,
     * creating the builder if it is uninitialized. The keys are used as part of the topology
     * spread constraint configuration to match labels that determine pod placement.
     *
     * @param keys The match label keys to be added.
     */
    fun addMatchLabelKeys(vararg keys: String) {
        if (matchLabelKeys == null) {
            matchLabelKeys = MatchLabelKeySpecBuilder()
        }
        matchLabelKeys!!.keys(*keys)
    }

    /**
     * Configures a set of match label keys used for defining the topology spread constraints.
     *
     * This method allows you to specify multiple key-value pairs that will be used to match labels 
     * applicable to the topology spread constraint. The provided `block` is invoked with a 
     * `MatchLabelKeySpecBuilder` receiver, enabling configuration of the desired match label keys.
     *
     * Example:
     * ```kotlin
     * matchLabelKeys {
     *     match("app")
     *     matches("version", "v1.0")
     * }
     * ```
     *
     * @param block A lambda with receiver of type `MatchLabelKeySpecBuilder` that provides
     *              functionality to add match label keys.
     */
    fun matchLabelKeys(block: MatchLabelKeySpecBuilder.() -> Unit) {
        matchLabelKeys = MatchLabelKeySpecBuilder().apply(block)
    }

    /**
     * Builds a `TopologySpreadConstraintSpec` instance using the current state of the builder’s properties.
     *
     * The resulting object encapsulates the configuration for topology spread constraints,
     * which are used to define how pods should be distributed across clusters to achieve desirable 
     * levels of availability and fault tolerance.
     *
     * The constructed `TopologySpreadConstraintSpec` includes the following components:
     * - `maxSkew`: The maximum allowable skew for topology balancing.
     * - `topologyKey`: The key used to define the topology grouping criteria.
     * - `whenUnsatisfiable`: The policy to apply when the topology spread constraint cannot be satisfied.
     * - `labelSelector`: An optional label selector used to filter applicable resources.
     * - `minDomains`: The minimum number of domains required for satisfying the constraint (optional).
     * - `nodeAffinityPolicy`: A node policy defining how node affinity impacts the constraint (optional).
     * - `nodeTaintsPolicy`: A node policy defining how node taints impact the constraint (optional).
     * - `matchLabelKeys`: A map of label keys and values to be matched (optional).
     *
     * @return A new `TopologySpreadConstraintSpec` instance containing the configured properties.
     */
    fun build() = TopologySpreadConstraintSpec(
        maxSkew,
        topologyKey,
        whenUnsatisfiable,
        labelSelector?.build(),
        minDomains,
        nodeAffinityPolicy,
        nodeTaintsPolicy,
        matchLabelKeys?.build()
    )
}