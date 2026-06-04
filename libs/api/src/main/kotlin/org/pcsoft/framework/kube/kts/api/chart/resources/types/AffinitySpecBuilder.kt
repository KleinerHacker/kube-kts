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
 * A builder class for constructing an `AffinitySpec` object.
 *
 * This class provides configuration options for specifying node affinity, 
 * pod affinity, and pod anti-affinity rules for Kubernetes workloads.
 * It allows fine-grained control over how pods are scheduled onto nodes or
 * interact with other pods in the cluster.
 *
 * The `AffinitySpecBuilder` should be used in conjunction with its 
 * associated builders (`NodeAffinitySpecBuilder` and `PodAffinitySpecBuilder`) 
 * to configure each type of affinity or anti-affinity.
 *
 * The resulting `AffinitySpec` can be used to define the `affinity` field
 * in a Kubernetes workload specification.
 */
class AffinitySpecBuilder internal constructor() {
    private var nodeAffinity: NodeAffinitySpecBuilder? = null
    private var podAffinity: PodAffinitySpecBuilder? = null
    private var podAntiAffinity: PodAffinitySpecBuilder? = null

    /**
     * Configures the node affinity rules for a Kubernetes workload.
     *
     * This method allows you to define constraints or preferences that influence
     * how pods are scheduled onto nodes. It uses a DSL-style builder, enabling
     * declarative and type-safe configuration within the provided block.
     *
     * Example:
     * ```kotlin
     * nodeAffinity {
     *     requiredDuringSchedulingIgnoredDuringExecution {
     *         term {
     *             matchExpressions {
     *                 expression {
     *                     key = "kubernetes.io/hostname"
     *                     operator = "In"
     *                     values = listOf("node1", "node2")
     *                 }
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda with receiver of type `NodeAffinitySpecBuilder` 
     * that is used to configure the node affinity rules.
     */
    fun nodeAffinity(prepare: NodeAffinitySpecBuilder.() -> Unit) {
        nodeAffinity = NodeAffinitySpecBuilder().apply(prepare)
    }

    /**
     * Configures the pod affinity rules for a Kubernetes workload.
     *
     * This method allows you to define constraints or preferences that influence
     * how pods are scheduled in relation to other pods. It uses a DSL-style builder, 
     * enabling declarative and type-safe configuration within the provided block.
     *
     * Example:
     * ```kotlin
     * podAffinity {
     *     requiredDuringSchedulingIgnoredDuringExecution {
     *         term(topologyKey = "kubernetes.io/hostname") {
     *             labelSelector {
     *                 matchLabels {
     *                     label("app", "nginx")
     *                 }
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda with receiver of type `PodAffinitySpecBuilder` 
     * that is used to configure the pod affinity rules.
     */
    fun podAffinity(prepare: PodAffinitySpecBuilder.() -> Unit) {
        podAffinity = PodAffinitySpecBuilder().apply(prepare)
    }

    /**
     * Configures the pod anti-affinity rules for a Kubernetes workload.
     *
     * This method allows you to define constraints or preferences that influence
     * how pods are scheduled in relation to other pods. It uses a DSL-style builder,
     * enabling declarative and type-safe configuration within the provided block.
     *
     * Example:
     * ```kotlin
     * podAntiAffinity {
     *     requiredDuringSchedulingIgnoredDuringExecution {
     *         term(topologyKey = "kubernetes.io/hostname") {
     *             labelSelector {
     *                 matchLabels {
     *                     label("app", "database")
     *                 }
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda with receiver of type `PodAffinitySpecBuilder`
     * that is used to configure the pod anti-affinity rules.
     */
    fun podAntiAffinity(prepare: PodAffinitySpecBuilder.() -> Unit) {
        podAntiAffinity = PodAffinitySpecBuilder().apply(prepare)
    }

    /**
     * Constructs an `AffinitySpec` instance based on the current state of the builder.
     *
     * This method aggregates the configurations for node affinity, pod affinity, 
     * and pod anti-affinity. It invokes their respective `build` methods, ensuring
     * that the defined rules for affinity and anti-affinity are transformed into
     * a finalized `AffinitySpec` object.
     *
     * @return A new `AffinitySpec` instance encapsulating node, pod, and pod anti-affinity rules.
     */
    fun build() = AffinitySpec(
        nodeAffinity = nodeAffinity?.build(),
        podAffinity = podAffinity?.build(),
        podAntiAffinity = podAntiAffinity?.build()
    )
}

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

/**
 * Builder class for constructing instances of `PodAffinitySpec`. Pod affinity rules dictate
 * how pods should be scheduled on nodes based on their affinity or anti-affinity to other pods.
 * This includes both required and preferred scheduling rules.
 *
 * @constructor Internal constructor to create an instance of `PodAffinitySpecBuilder`.
 */
class PodAffinitySpecBuilder internal constructor() {
    private var requiredDuringSchedulingIgnoredDuringExecution: MutableList<PodAffinityTermSpecBuilder>? = null
    private var preferredDuringSchedulingIgnoredDuringExecution: MutableList<WeightedPodAffinityTermSpecBuilder>? = null

    /**
     * Adds a required pod affinity term to the `requiredDuringSchedulingIgnoredDuringExecution` list.
     * This specifies hard constraints for pod scheduling based on the provided `topologyKey`.
     *
     * Example:
     * ```kotlin
     * addRequiredDuringSchedulingIgnoredDuringExecution(topologyKey = "kubernetes.io/hostname") {
     *     labelSelector {
     *         matchLabels {
     *             label("app", "nginx")
     *         }
     *     }
     *     namespaces = listOf("production")
     * }
     * ```
     *
     * @param topologyKey The topology key used to identify the node labels for matching.
     * @param prepare A builder block to define the configuration of the pod affinity term.
     */
    fun addRequiredDuringSchedulingIgnoredDuringExecution(
        topologyKey: String,
        prepare: PodAffinityTermSpecBuilder.() -> Unit
    ) {
        if (requiredDuringSchedulingIgnoredDuringExecution == null) {
            requiredDuringSchedulingIgnoredDuringExecution = mutableListOf()
        }
        requiredDuringSchedulingIgnoredDuringExecution!!.add(PodAffinityTermSpecBuilder(topologyKey).apply(prepare))
    }

    /**
     * Adds a weighted pod affinity term to the `preferredDuringSchedulingIgnoredDuringExecution` list.
     * This defines a soft preference for pod scheduling, allowing Kubernetes to prioritize nodes based 
     * on the specified weight while considering the specified affinity criteria.
     *
     * Example:
     * ```kotlin
     * addPreferredDuringSchedulingIgnoredDuringExecution(weight = 100) {
     *     podAffinityTerm(topologyKey = "topology.kubernetes.io/zone") {
     *         labelSelector {
     *             matchExpressions {
     *                 expression {
     *                     key = "app"
     *                     operator = "In"
     *                     values = listOf("cache")
     *                 }
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param weight The weight associated with this preference. Higher weights indicate stronger preferences.
     * @param prepare A lambda function used to configure the weighted pod affinity term.
     */
    fun addPreferredDuringSchedulingIgnoredDuringExecution(
        weight: Int,
        prepare: WeightedPodAffinityTermSpecBuilder.() -> Unit
    ) {
        if (preferredDuringSchedulingIgnoredDuringExecution == null) {
            preferredDuringSchedulingIgnoredDuringExecution = mutableListOf()
        }
        preferredDuringSchedulingIgnoredDuringExecution!!.add(WeightedPodAffinityTermSpecBuilder(weight).apply(prepare))
    }

    /**
     * Configures a list of required pod affinity terms for the `requiredDuringSchedulingIgnoredDuringExecution` field.
     * These terms define hard constraints for pod scheduling, specifying conditions that must be met for a pod
     * to be scheduled on a node. The configuration is applied through the provided builder block.
     *
     * Example:
     * ```kotlin
     * requiredDuringSchedulingIgnoredDuringExecution {
     *     term(topologyKey = "kubernetes.io/hostname") {
     *         labelSelector {
     *             matchLabels {
     *                 label("app", "database")
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda function used to configure the list of pod affinity terms.
     */
    fun requiredDuringSchedulingIgnoredDuringExecution(prepare: PodAffinityTermSpecListBuilder.() -> Unit) =
        PodAffinityTermSpecListBuilder().apply(prepare)

    /**
     * Configures a list of weighted pod affinity terms for the `preferredDuringSchedulingIgnoredDuringExecution` field.
     * These terms define soft preferences for pod scheduling, specifying conditions that Kubernetes should prioritize 
     * during scheduling but does not mandate as strict requirements.
     *
     * Example:
     * ```kotlin
     * preferredDuringSchedulingIgnoredDuringExecution {
     *     term(weight = 50) {
     *         podAffinityTerm(topologyKey = "topology.kubernetes.io/zone") {
     *             labelSelector {
     *                 matchLabels {
     *                     label("environment", "staging")
     *                 }
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda function used to configure the list of weighted pod affinity terms.
     */
    fun preferredDuringSchedulingIgnoredDuringExecution(prepare: WeightedPodAffinityTermSpecListBuilder.() -> Unit) =
        WeightedPodAffinityTermSpecListBuilder().apply(prepare)

    /**
     * Builds a `PodAffinitySpec` instance based on the current state of the builder.
     *
     * This method aggregates the configuration of `requiredDuringSchedulingIgnoredDuringExecution`
     * and `preferredDuringSchedulingIgnoredDuringExecution`, transforming each item in these lists
     * by invoking their respective `build` methods.
     *
     * @return A new `PodAffinitySpec` instance containing the configured lists of required and preferred
     *         pod affinity terms.
     */
    fun build() = PodAffinitySpec(
        requiredDuringSchedulingIgnoredDuringExecution = requiredDuringSchedulingIgnoredDuringExecution?.map { it.build() },
        preferredDuringSchedulingIgnoredDuringExecution = preferredDuringSchedulingIgnoredDuringExecution?.map { it.build() }
    )

    /**
     * A builder class for constructing a list of pod affinity terms.
     *
     * This class is typically used to configure the `requiredDuringSchedulingIgnoredDuringExecution` field
     * of a `PodAffinitySpecBuilder`. Pod affinity terms define scheduling constraints that dictate
     * the placement of pods on nodes based on the specified `topologyKey` and affinity rules.
     */
    inner class PodAffinityTermSpecListBuilder internal constructor() {
        /**
         * Adds a pod affinity term to the list of required affinity rules, specifying constraints for pod scheduling
         * based on the given topology key and additional affinity conditions.
         *
         * @param topologyKey The key of the node topology that the affinity rule is scoped to. This determines the scheduling
         *                    constraint based on the specified topology such as hostname, zone, or region.
         * @param prepare A lambda allowing configuration of additional conditions or parameters for the pod affinity term.
         */
        fun term(topologyKey: String, prepare: PodAffinityTermSpecBuilder.() -> Unit) = 
            addRequiredDuringSchedulingIgnoredDuringExecution(topologyKey, prepare)   
    }

    /**
     * A builder class that facilitates the creation and configuration of a list of 
     * `WeightedPodAffinityTermSpec` instances. This class is specifically designed 
     * for defining weighted pod affinity terms to be used as soft preferences during 
     * pod scheduling in Kubernetes.
     */
    inner class WeightedPodAffinityTermSpecListBuilder internal constructor() {
        /**
         * Adds a weighted pod affinity term to the list of soft preferences for pod scheduling.
         * This method configures a preference that influences the scheduler to prioritize nodes
         * based on the specified weight and affinity criteria, without mandating strict placement.
         *
         * @param weight The weight associated with the affinity term. Higher weights indicate
         *               stronger preferences for the defined criteria.
         * @param prepare A lambda function used to configure the `WeightedPodAffinityTermSpecBuilder`
         *                for defining the weighted pod affinity term.
         */
        fun term(weight: Int, prepare: WeightedPodAffinityTermSpecBuilder.() -> Unit) = 
            addPreferredDuringSchedulingIgnoredDuringExecution(weight, prepare)   
    }   
}