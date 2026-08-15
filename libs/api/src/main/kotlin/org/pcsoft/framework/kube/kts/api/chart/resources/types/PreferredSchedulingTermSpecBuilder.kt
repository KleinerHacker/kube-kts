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
 * Builder class for creating a `PreferredSchedulingTermSpec` instance.
 *
 * This builder is used to define a preferred scheduling term by configuring
 * its weight and the corresponding node selector term specification.
 *
 * @constructor Instantiates a builder with the given weight for the preferred scheduling term.
 * @param weight The weight associated with the preferred scheduling term.
 */
class PreferredSchedulingTermSpecBuilder internal constructor(private val weight: Int) {
    private var preference: NodeSelectorTermSpecBuilder? = null

    /**
     * Configures the node selector term specification for the preferred scheduling term.
     *
     * This function allows you to define the details of the node selector term by applying
     * the provided setup block to an instance of [NodeSelectorTermSpecBuilder].
     *
     * Example:
     * ```kotlin
     * preference {
     *     matchExpressions {
     *         match("kubernetes.io/hostname", Operator.In) {
     *             addValues("node1", "node2")
     *         }
     *     }
     * }
     * ```
     *
     * @param setup A lambda function used to configure the node selector term specification.
     */
    fun preference(setup: NodeSelectorTermSpecBuilder.() -> Unit) {
        preference = NodeSelectorTermSpecBuilder().apply(setup)
    }

    /**
     * Builds and returns an instance of `PreferredSchedulingTermSpec` using the configured
     * weight and node selector term specification.
     *
     * This method validates that the required fields are properly set and constructs
     * a `PreferredSchedulingTermSpec` object. The resulting specification
     * is used to represent a preferred scheduling term in node affinity rules.
     *
     * @return A fully constructed `PreferredSchedulingTermSpec` containing the configured
     * weight and node selector term specification.
     * @throws IllegalStateException if the preference is not configured prior to calling this method.
     */
    internal fun build(): PreferredSchedulingTermSpec {
        require(preference != null) { "Preference is required" }

        return PreferredSchedulingTermSpec(weight, preference!!.build())
    }
}
