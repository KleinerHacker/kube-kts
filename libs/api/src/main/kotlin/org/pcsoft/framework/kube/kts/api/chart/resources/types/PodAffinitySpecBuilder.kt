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
