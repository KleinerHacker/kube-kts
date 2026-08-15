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

/**
 * Builder class to configure node affinity specifications for scheduling and execution of workloads.
 *
 * Node affinity rules can specify requirements for node selection during scheduling, as well as preferences
 * that influence scheduling decisions. This builder allows defining both required and preferred rules
 * with flexible configurations.
 *
 * The required rules enforce strict constraints that nodes must satisfy, while preferred rules
 * specify a weighted preference towards certain nodes.
 *
 * Internal constructor is used to ensure controlled instantiation, typically as part of a larger configuration system.
 *
 * Functions provide the ability to add individual terms for required and preferred rules, 
 * as well as configure lists of terms using nested builders.
 */
class NodeAffinitySpecBuilder internal constructor() {
    private var requiredDuringSchedulingIgnoredDuringExecution: MutableList<NodeSelectorTermSpecBuilder>? = null
    private var preferredDuringSchedulingIgnoredDuringExecution: MutableList<PreferredSchedulingTermSpecBuilder>? = null

    /**
     * Adds a `NodeSelectorTermSpec` to the `requiredDuringSchedulingIgnoredDuringExecution` list.
     * This method allows defining node affinity rules that must be met during scheduling 
     * but are not enforced during execution.
     *
     * Example:
     * ```kotlin
     * addRequiredDuringSchedulingIgnoredDuringExecution {
     *     matchExpressions {
     *         expression {
     *             key = "kubernetes.io/hostname"
     *             operator = "In"
     *             values = listOf("node1", "node2")
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda function used to configure the `NodeSelectorTermSpecBuilder` 
     *                for constructing a node selector term.
     */
    fun addRequiredDuringSchedulingIgnoredDuringExecution(prepare: NodeSelectorTermSpecBuilder.() -> Unit) {
        if (requiredDuringSchedulingIgnoredDuringExecution == null) {
            requiredDuringSchedulingIgnoredDuringExecution = mutableListOf()
        }
        requiredDuringSchedulingIgnoredDuringExecution!!.add(NodeSelectorTermSpecBuilder().apply(prepare))
    }

    /**
     * Adds a `PreferredSchedulingTermSpec` to the `preferredDuringSchedulingIgnoredDuringExecution` list.
     * This method is used to define preferred node affinity rules that influence pod scheduling
     * but are not enforced during execution.
     *
     * Example:
     * ```kotlin
     * addPreferredDuringSchedulingIgnoredDuringExecution(weight = 100) {
     *     preference {
     *         matchExpressions {
     *             expression {
     *                 key = "node-type"
     *                 operator = "In"
     *                 values = listOf("high-memory")
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param weight The weight associated with the preferred scheduling term. Higher weights denote
     *               stronger preferences for the specified node selector term.
     * @param prepare A lambda function used to configure the `PreferredSchedulingTermSpecBuilder`
     *                for constructing a preferred scheduling term.
     */
    fun addPreferredDuringSchedulingIgnoredDuringExecution(
        weight: Int,
        prepare: PreferredSchedulingTermSpecBuilder.() -> Unit
    ) {
        if (preferredDuringSchedulingIgnoredDuringExecution == null) {
            preferredDuringSchedulingIgnoredDuringExecution = mutableListOf()
        }
        preferredDuringSchedulingIgnoredDuringExecution!!.add(PreferredSchedulingTermSpecBuilder(weight).apply(prepare))
    }

    /**
     * Configures a list of `NodeSelectorTermSpec` for required node affinity rules.
     * These rules must be satisfied during pod scheduling but are not enforced during execution.
     *
     * Example:
     * ```kotlin
     * requiredDuringSchedulingIgnoredDuringExecution {
     *     term {
     *         matchExpressions {
     *             expression {
     *                 key = "disktype"
     *                 operator = "In"
     *                 values = listOf("ssd")
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda function used to define the node selector terms by configuring 
     *                a `NodeSelectorTermSpecListBuilder`.
     */
    fun requiredDuringSchedulingIgnoredDuringExecution(prepare: NodeSelectorTermSpecListBuilder.() -> Unit) =
        NodeSelectorTermSpecListBuilder().apply(prepare)

    /**
     * Configures a list of preferred node affinity rules that influence pod scheduling
     * but are not strictly enforced during execution. Each rule is associated with a weight
     * to indicate the strength of the preference.
     *
     * Example:
     * ```kotlin
     * preferredDuringSchedulingIgnoredDuringExecution {
     *     term(weight = 50) {
     *         preference {
     *             matchExpressions {
     *                 expression {
     *                     key = "zone"
     *                     operator = "In"
     *                     values = listOf("us-west-1a")
     *                 }
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda function used to define the preferred scheduling terms by 
     *                configuring a `PreferredSchedulingTermSpecListBuilder`.
     */
    fun preferredDuringSchedulingIgnoredDuringExecution(prepare: PreferredSchedulingTermSpecListBuilder.() -> Unit) =
        PreferredSchedulingTermSpecListBuilder().apply(prepare)

    /**
     * Builds a `NodeAffinitySpec` instance using the current state of the `NodeAffinitySpecBuilder`.
     *
     * This method constructs a `NodeAffinitySpec` object by transforming the configured
     * `requiredDuringSchedulingIgnoredDuringExecution` and `preferredDuringSchedulingIgnoredDuringExecution` 
     * properties into their respective finalized specifications.
     *
     * The `requiredDuringSchedulingIgnoredDuringExecution` list is mapped to a collection of 
     * `NodeSelectorTermSpec` objects, and the `preferredDuringSchedulingIgnoredDuringExecution` list
     * is converted to a collection of `PreferredSchedulingTermSpec` objects. This allows for a 
     * comprehensive definition of node affinity rules, including both required constraints and
     * preferred preferences for node selection during pod scheduling.
     *
     * @return A fully constructed `NodeAffinitySpec` that encapsulates both mandatory 
     * and preferred node affinity rules.
     */
    internal fun build() = NodeAffinitySpec(
        requiredDuringSchedulingIgnoredDuringExecution = requiredDuringSchedulingIgnoredDuringExecution?.map { it.build() },
        preferredDuringSchedulingIgnoredDuringExecution = preferredDuringSchedulingIgnoredDuringExecution?.map { it.build() }
    )

    /**
     * A builder class for constructing a list of `NodeSelectorTermSpec` instances.
     * Used for defining node affinity rules that must be met during pod scheduling 
     * but are not required during execution.
     *
     * This class is intended for internal use and provides a fluent API for adding 
     * `NodeSelectorTermSpec` definitions to the associated context.
     */
    inner class NodeSelectorTermSpecListBuilder internal constructor() {
        /**
         * Adds a `NodeSelectorTermSpec` definition to the list of node affinity rules 
         * that must be satisfied during pod scheduling but are ignored during execution.
         *
         * @param prepare A lambda with a receiver for configuring the `NodeSelectorTermSpec`. 
         *                The receiver provides a fluent interface for defining the term's attributes.
         */
        fun term(prepare: NodeSelectorTermSpecBuilder.() -> Unit) =
            addRequiredDuringSchedulingIgnoredDuringExecution(prepare)
    }

    /**
     * Builder class for configuring a list of preferred scheduling terms in node affinity rules.
     *
     * This class enables the construction of `PreferredSchedulingTermSpec` entries, which represent
     * preferred scheduling preferences influencing pod placement on nodes. Preferred scheduling
     * terms are not strictly enforced but are weighted to indicate the strength of the preference.
     *
     * Instances of this class are accessed through the `preferredDuringSchedulingIgnoredDuringExecution`
     * method of the `NodeAffinitySpecBuilder` class.
     *
     * @constructor Internal constructor to prevent direct instantiation.
     */
    inner class PreferredSchedulingTermSpecListBuilder internal constructor() {
        /**
         * Adds a preferred scheduling term to the list of preferred node affinity rules.
         *
         * This method allows you to define a scheduling preference by specifying a weight and
         * configuring the corresponding node selector term using the provided setup block. 
         * Preferred scheduling terms influence the likelihood of pod placement on nodes 
         * but are not strictly enforced.
         *
         * @param weight The weight associated with the preferred scheduling term. 
         *               Higher weights indicate stronger preferences for the specified conditions.
         * @param prepare A lambda function used to configure the `PreferredSchedulingTermSpecBuilder`, 
         *                which defines the conditions for the preferred scheduling term.
         */
        fun term(weight: Int, prepare: PreferredSchedulingTermSpecBuilder.() -> Unit) =
            addPreferredDuringSchedulingIgnoredDuringExecution(weight, prepare)
    }
}
