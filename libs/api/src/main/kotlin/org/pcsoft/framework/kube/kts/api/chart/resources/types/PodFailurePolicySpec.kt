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
 * Controls how a Job reacts to specific Pod failures.
 *
 * Without a failure policy every failed Pod counts against the Job's backoff limit. A policy lets the
 * Job distinguish between failures worth retrying - a spot instance being reclaimed - and failures that
 * will never succeed, such as a configuration error signalled by a dedicated exit code.
 *
 * @property rules The rules evaluated in order. The first matching rule decides the outcome.
 */
@NoArgs
data class PodFailurePolicySpec(
    val rules: List<Rule>
) {
    /**
     * Validates that at least one rule is given.
     */
    init {
        require(rules.isNotEmpty()) { "At least one pod failure policy rule is required" }
    }

    /**
     * A single rule matching a class of Pod failures and declaring what to do about it.
     *
     * Exactly one of [onExitCodes] and [onPodConditions] has to be given.
     *
     * @property action          What happens when this rule matches.
     * @property onExitCodes     Matches on the exit code of a container.
     * @property onPodConditions Matches on conditions present on the failed Pod.
     */
    @NoArgs
    data class Rule(
        val action: Action,
        val onExitCodes: OnExitCodes?,
        val onPodConditions: List<OnPodCondition>?
    ) {
        /**
         * Validates that exactly one matching criterion is given.
         */
        init {
            require((onExitCodes == null) != (onPodConditions.isNullOrEmpty())) {
                "Exactly one of 'onExitCodes' and 'onPodConditions' must be set"
            }
        }
    }

    /**
     * What a Job does when a failure policy rule matches.
     */
    @Suppress("unused")
    enum class Action {
        /**
         * The Job fails immediately and its remaining Pods are terminated.
         */
        FailJob,

        /**
         * The failure is not counted against the backoff limit and the Pod is replaced.
         */
        Ignore,

        /**
         * The failure is counted against the backoff limit, as it would be without a policy.
         */
        Count,

        /**
         * The failed index is marked as failed without failing the whole Job. Indexed Jobs only.
         */
        FailIndex
    }

    /**
     * Matches a Pod failure on the exit code of one of its containers.
     *
     * @property containerName The container whose exit code is inspected. Matches any container when unset.
     * @property operator      How [values] is interpreted.
     * @property values        The exit codes to compare against. Must not contain 0.
     */
    @NoArgs
    data class OnExitCodes(
        val containerName: String?,
        val operator: Operator,
        val values: List<Int>
    ) {
        /**
         * Validates the compared exit codes.
         */
        init {
            require(values.isNotEmpty()) { "At least one exit code is required" }
            require(0 !in values) { "Exit code 0 must not be used in a pod failure policy" }
            containerName?.let { require(it.isNotBlank()) { "Container name must not be blank" } }
        }

        /**
         * How the listed exit codes are matched.
         */
        @Suppress("unused")
        enum class Operator {
            /**
             * The rule matches when the exit code is one of the listed values.
             */
            In,

            /**
             * The rule matches when the exit code is none of the listed values.
             */
            NotIn
        }
    }

    /**
     * Matches a Pod failure on a condition present on the failed Pod.
     *
     * @property type   The type of the Pod condition, for example `DisruptionTarget`.
     * @property status The status the condition must have for the rule to match.
     */
    @NoArgs
    data class OnPodCondition(
        val type: String,
        val status: ConditionStatus
    ) {
        /**
         * Validates that the condition type is not blank.
         */
        init {
            require(type.isNotBlank()) { "Condition type must not be blank" }
        }
    }

    /**
     * The status of a Pod condition.
     */
    @Suppress("unused")
    enum class ConditionStatus {
        /**
         * The condition holds.
         */
        True,

        /**
         * The condition does not hold.
         */
        False,

        /**
         * The condition's state cannot be determined.
         */
        Unknown
    }
}
