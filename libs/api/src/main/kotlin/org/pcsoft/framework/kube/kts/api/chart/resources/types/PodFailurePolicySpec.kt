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
 * Describes the Pod failure policy of a Kubernetes Job, a list of [Rule]s evaluated against failed
 * Pods to control how the Job reacts to specific failures.
 *
 * @property rules The ordered list of rules. The first matching rule decides the action.
 */
@NoArgs
data class PodFailurePolicySpec(
    val rules: List<Rule>
) {

    /**
     * A single Pod failure policy rule.
     *
     * @property action          The action taken when the rule matches.
     * @property onExitCodes      Matches based on container exit codes.
     * @property onPodConditions  Matches based on Pod conditions.
     */
    @NoArgs
    data class Rule(
        val action: Action,
        val onExitCodes: OnExitCodes?,
        val onPodConditions: List<OnPodCondition>?
    )

    /**
     * The action a [Rule] takes when it matches a failed Pod.
     */
    enum class Action {
        /** Fail the whole Job. */
        FailJob,

        /** Ignore the failure; the retry does not count towards `backoffLimit`. */
        Ignore,

        /** Count the failure towards `backoffLimit` (default behaviour). */
        Count,

        /** Fail the index (only for indexed Jobs). */
        FailIndex
    }

    /**
     * Matches a failed Pod based on container exit codes.
     *
     * @property containerName The container the exit code applies to. If null, any container matches.
     * @property operator      How [values] is matched against the exit code.
     * @property values        The set of exit codes to match.
     */
    @NoArgs
    data class OnExitCodes(
        val containerName: String?,
        val operator: Operator,
        val values: List<Int>
    ) {
        /**
         * The match operator for [OnExitCodes].
         */
        enum class Operator {
            In,
            NotIn
        }
    }

    /**
     * Matches a failed Pod based on a Pod condition.
     *
     * @property type   The Pod condition type (e.g. `DisruptionTarget`).
     * @property status The required status of the condition (e.g. `True`).
     */
    @NoArgs
    data class OnPodCondition(
        val type: String,
        val status: String
    )
}
