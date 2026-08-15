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
 * A builder class for constructing `WeightedPodAffinityTermSpec` instances.
 * This class allows for the specification of a weighted pod affinity term 
 * which can be used in pod scheduling in Kubernetes.
 *
 * @constructor Creates an instance of `WeightedPodAffinityTermSpecBuilder` with the specified weight.
 * @param weight The weight associated with the pod affinity term.
 */
class WeightedPodAffinityTermSpecBuilder internal constructor(private val weight: Int) {
    private var podAffinityTerm: PodAffinityTermSpecBuilder? = null

    /**
     * Configures a pod affinity term with a specified topology key and additional setup logic.
     * A pod affinity term specifies that the pod should be scheduled on a node 
     * that meets specific criteria, such as being in the same topology domain.
     *
     * Example:
     * ```kotlin
     * podAffinityTerm("kubernetes.io/hostname") {
     *     labelSelector {
     *         matchLabels("app" to "frontend")
     *     }
     * }
     * ```
     *
     * @param topologyKey The key representing the topology domain (e.g., "kubernetes.io/hostname").
     * @param setup A lambda function for customizing the pod affinity term configuration.
     */
    fun podAffinityTerm(topologyKey: String, setup: PodAffinityTermSpecBuilder.() -> Unit) {
        podAffinityTerm = PodAffinityTermSpecBuilder(topologyKey).apply(setup)
    }

    /**
     * Builds a `WeightedPodAffinityTermSpec` instance based on the current configuration.
     *
     * Validates that the required `PodAffinityTermSpec` has been specified and constructs
     * a new `WeightedPodAffinityTermSpec` object with the provided weight and pod affinity term.
     *
     * @return A new instance of `WeightedPodAffinityTermSpec` containing the configured weight
     *         and pod affinity term.
     * @throws IllegalStateException if the `podAffinityTerm` is not configured before calling this method.
     */
    internal fun build(): WeightedPodAffinityTermSpec {
        require(podAffinityTerm != null) { "Pod affinity term is required" }

        return WeightedPodAffinityTermSpec(weight, podAffinityTerm!!.build())
    }
}
