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
 * Builder for [PodFailurePolicySpec].
 */
class PodFailurePolicySpecBuilder internal constructor() {
    private val rules = mutableListOf<RuleBuilder>()

    /**
     * Adds a Pod failure policy rule.
     *
     * Example:
     * ```kotlin
     * rule(PodFailurePolicySpec.Action.FailJob) {
     *     onExitCodes(PodFailurePolicySpec.OnExitCodes.Operator.In) {
     *         values(1, 42)
     *     }
     * }
     * ```
     *
     * @param action The action taken when the rule matches.
     * @param prepare A lambda with a receiver of [RuleBuilder] to configure the match conditions.
     */
    fun rule(action: PodFailurePolicySpec.Action, prepare: RuleBuilder.() -> Unit = {}) {
        rules.add(RuleBuilder(action).apply(prepare))
    }

    internal fun build(): PodFailurePolicySpec {
        require(rules.isNotEmpty()) { "Pod failure policy requires at least one rule" }

        return PodFailurePolicySpec(rules.map { it.build() })
    }

    /**
     * Builder for a single [PodFailurePolicySpec.Rule].
     */
    class RuleBuilder internal constructor(private val action: PodFailurePolicySpec.Action) {
        private var onExitCodes: OnExitCodesBuilder? = null
        private var onPodConditions: MutableList<PodFailurePolicySpec.OnPodCondition>? = null

        /**
         * Matches the rule based on container exit codes.
         *
         * @param operator How the configured values are matched against the exit code.
         * @param prepare A lambda with a receiver of [OnExitCodesBuilder] to configure container/values.
         */
        fun onExitCodes(operator: PodFailurePolicySpec.OnExitCodes.Operator, prepare: OnExitCodesBuilder.() -> Unit) {
            onExitCodes = OnExitCodesBuilder(operator).apply(prepare)
        }

        /**
         * Matches the rule based on a Pod condition.
         *
         * @param type   The Pod condition type (e.g. `DisruptionTarget`).
         * @param status The required status of the condition (e.g. `True`).
         */
        fun onPodCondition(type: String, status: String) {
            if (onPodConditions == null) {
                onPodConditions = mutableListOf()
            }

            onPodConditions!!.add(PodFailurePolicySpec.OnPodCondition(type, status))
        }

        internal fun build(): PodFailurePolicySpec.Rule =
            PodFailurePolicySpec.Rule(action, onExitCodes?.build(), onPodConditions)
    }

    /**
     * Builder for [PodFailurePolicySpec.OnExitCodes].
     */
    class OnExitCodesBuilder internal constructor(private val operator: PodFailurePolicySpec.OnExitCodes.Operator) {
        private val values = mutableListOf<Int>()

        /**
         * The container the exit code applies to. If null, any container matches.
         */
        var containerName: String? = null

        /**
         * Adds the given exit codes to the match set.
         */
        fun values(vararg codes: Int) {
            values.addAll(codes.toList())
        }

        internal fun build(): PodFailurePolicySpec.OnExitCodes {
            require(values.isNotEmpty()) { "onExitCodes requires at least one value" }

            return PodFailurePolicySpec.OnExitCodes(containerName, operator, values)
        }
    }
}
