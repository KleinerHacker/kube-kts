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

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.resources.types.LabelSelectorSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodFailurePolicySpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.SuccessPolicySpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Represents the specification for a Kubernetes Job resource.
 *
 * A Job creates one or more Pods and ensures that a specified number of them successfully
 * terminate. Jobs are used for batch/run-to-completion workloads, as opposed to long-running
 * services managed by a Deployment.
 *
 * @property parallelism             The maximum number of Pods that should run in parallel.
 * @property completions             The desired number of successfully finished Pods.
 * @property completionMode          Controls how Pod completions are tracked ([CompletionMode.NonIndexed]
 *                                   or [CompletionMode.Indexed]).
 * @property backoffLimit            The number of retries before the Job is marked as failed.
 * @property backoffLimitPerIndex    The number of retries per index before marking the index as failed
 *                                   (indexed Jobs only).
 * @property maxFailedIndexes        The maximal number of failed indexes before the Job is marked as failed
 *                                   (indexed Jobs only).
 * @property activeDeadlineSeconds   The maximum duration the Job may be active before being terminated.
 * @property ttlSecondsAfterFinished The time-to-live after the Job finished, after which it is eligible
 *                                   for automatic cleanup.
 * @property suspend                 If true, the Job controller does not create any Pods (the Job is suspended).
 * @property manualSelector          If true, the [selector] is managed by the user instead of the system.
 * @property podReplacementPolicy    Controls when failed Pods are replaced.
 * @property selector                The label selector identifying the Pods managed by this Job. Reuses the
 *                                   shared [LabelSelectorSpec]. Usually auto-managed; only set together with
 *                                   [manualSelector].
 * @property podFailurePolicy        Rules controlling how the Job reacts to specific Pod failures.
 * @property successPolicy           Rules defining when an indexed Job is declared successful.
 * @property template                The Pod template describing the Pods created by this Job. The Pod's
 *                                   `restartPolicy` must be `Never` or `OnFailure`.
 */
@NoArgs
data class JobSpec(
    val parallelism: Int?,
    val completions: Int?,
    val completionMode: CompletionMode?,
    val backoffLimit: Int?,
    val backoffLimitPerIndex: Int?,
    val maxFailedIndexes: Int?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val activeDeadlineSeconds: Duration?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val ttlSecondsAfterFinished: Duration?,
    val suspend: Boolean?,
    val manualSelector: Boolean?,
    val podReplacementPolicy: PodReplacementPolicy?,
    val selector: LabelSelectorSpec?,
    val podFailurePolicy: PodFailurePolicySpec?,
    val successPolicy: SuccessPolicySpec?,
    val template: PodTemplateSpec
) : ResourceSpec {

    /**
     * Controls how the Job tracks Pod completions.
     */
    enum class CompletionMode {
        /**
         * Pods are considered interchangeable; the Job is complete when [completions] Pods succeed.
         */
        NonIndexed,

        /**
         * Each Pod gets an associated completion index from 0 to [completions] - 1.
         */
        Indexed
    }

    /**
     * Controls when failed Pods of a Job are replaced.
     */
    enum class PodReplacementPolicy {
        /**
         * Replace Pods as soon as they are terminating or fully failed.
         */
        TerminatingOrFailed,

        /**
         * Replace Pods only once they reach the `Failed` phase.
         */
        Failed
    }

    companion object {
        /**
         * Identifies this resource as a Job.
         */
        const val KIND = "Job"

        /**
         * Specifies the API version for Kubernetes Job resources.
         *
         * Value: "batch/v1"
         */
        const val API_VERSION = "batch/v1"
    }
}
