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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Describes when an indexed Job can be declared successful, as a list of [Rule]s. Only applicable to
 * Jobs with `completionMode = Indexed`.
 *
 * @property rules The ordered list of rules. The Job succeeds as soon as one rule is satisfied.
 */
@NoArgs
data class SuccessPolicySpec(
    val rules: List<Rule>
) {

    /**
     * A single success policy rule.
     *
     * @property succeededIndexes A set of indexes (e.g. `"0-2"`) that must succeed for the rule to match.
     * @property succeededCount   The minimum number of succeeded indexes for the rule to match.
     */
    @NoArgs
    data class Rule(
        val succeededIndexes: String?,
        val succeededCount: Int?
    )
}
