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
 * Builder for [SuccessPolicySpec].
 */
class SuccessPolicySpecBuilder internal constructor() {
    private val rules = mutableListOf<RuleBuilder>()

    /**
     * Adds a success policy rule.
     *
     * Example:
     * ```kotlin
     * rule {
     *     succeededIndexes = "0-2"
     *     succeededCount = 2
     * }
     * ```
     *
     * @param prepare A lambda with a receiver of [RuleBuilder] to configure the rule.
     */
    fun rule(prepare: RuleBuilder.() -> Unit) {
        rules.add(RuleBuilder().apply(prepare))
    }

    internal fun build(): SuccessPolicySpec {
        require(rules.isNotEmpty()) { "Success policy requires at least one rule" }

        return SuccessPolicySpec(rules.map { it.build() })
    }

    /**
     * Builder for a single [SuccessPolicySpec.Rule].
     */
    class RuleBuilder internal constructor() {
        /**
         * A set of indexes (e.g. `"0-2"`) that must succeed for the rule to match.
         */
        var succeededIndexes: String? = null

        /**
         * The minimum number of succeeded indexes for the rule to match.
         */
        var succeededCount: Int? = null

        internal fun build(): SuccessPolicySpec.Rule = SuccessPolicySpec.Rule(succeededIndexes, succeededCount)
    }
}
