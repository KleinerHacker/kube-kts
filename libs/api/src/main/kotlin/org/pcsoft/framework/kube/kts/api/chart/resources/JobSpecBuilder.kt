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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.LabelSelectorSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodFailurePolicySpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.resources.types.SuccessPolicySpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder
import java.time.Duration

/**
 * A builder class for constructing [JobSpec] objects, which define the specification for a
 * Kubernetes Job.
 */
class JobSpecBuilder internal constructor() : ResourceSpecBuilder<JobSpec> {
    private var template: PodTemplateSpecBuilder? = null
    private var selector: LabelSelectorSpecBuilder? = null
    private var podFailurePolicy: PodFailurePolicySpecBuilder? = null
    private var successPolicy: SuccessPolicySpecBuilder? = null

    /**
     * The maximum number of Pods that should run in parallel.
     */
    var parallelism: Int? = null

    /**
     * The desired number of successfully finished Pods.
     */
    var completions: Int? = null

    /**
     * Controls how Pod completions are tracked ([JobSpec.CompletionMode.NonIndexed] or
     * [JobSpec.CompletionMode.Indexed]).
     */
    var completionMode: JobSpec.CompletionMode? = null

    /**
     * The number of retries before the Job is marked as failed.
     */
    var backoffLimit: Int? = null

    /**
     * The number of retries per index before marking the index as failed (indexed Jobs only).
     */
    var backoffLimitPerIndex: Int? = null

    /**
     * The maximal number of failed indexes before the Job is marked as failed (indexed Jobs only).
     */
    var maxFailedIndexes: Int? = null

    /**
     * The maximum duration the Job may be active before being terminated.
     */
    var activeDeadlineSeconds: Duration? = null

    /**
     * The time-to-live after the Job finished, after which it is eligible for automatic cleanup.
     */
    var ttlSecondsAfterFinished: Duration? = null

    /**
     * If true, the Job controller does not create any Pods (the Job is suspended).
     */
    var suspend: Boolean? = null

    /**
     * If true, the [selector] is managed by the user instead of the system.
     */
    var manualSelector: Boolean? = null

    /**
     * Controls when failed Pods are replaced.
     */
    var podReplacementPolicy: JobSpec.PodReplacementPolicy? = null

    /**
     * Configures the label selector identifying the Pods managed by this Job.
     *
     * Reuses the shared [LabelSelectorSpecBuilder]. Usually auto-managed; only set together with
     * [manualSelector].
     *
     * @param prepare A lambda with a receiver of [LabelSelectorSpecBuilder].
     */
    fun selector(prepare: LabelSelectorSpecBuilder.() -> Unit) {
        selector = LabelSelectorSpecBuilder().apply(prepare)
    }

    /**
     * Configures the Pod failure policy, controlling how the Job reacts to specific Pod failures.
     *
     * @param prepare A lambda with a receiver of [PodFailurePolicySpecBuilder].
     */
    fun podFailurePolicy(prepare: PodFailurePolicySpecBuilder.() -> Unit) {
        podFailurePolicy = PodFailurePolicySpecBuilder().apply(prepare)
    }

    /**
     * Configures the success policy defining when an indexed Job is declared successful.
     *
     * @param prepare A lambda with a receiver of [SuccessPolicySpecBuilder].
     */
    fun successPolicy(prepare: SuccessPolicySpecBuilder.() -> Unit) {
        successPolicy = SuccessPolicySpecBuilder().apply(prepare)
    }

    /**
     * Configures the Pod template for the Job.
     *
     * The Pod's `restartPolicy` must be `Never` or `OnFailure`.
     *
     * @param prepare A lambda block used to configure the Pod template via [PodTemplateSpecBuilder].
     */
    fun template(prepare: PodTemplateSpecBuilder.() -> Unit) {
        template = PodTemplateSpecBuilder().apply(prepare)
    }

    override fun build(): JobSpec {
        require(template != null) { "Template must be set!" }

        return JobSpec(
            parallelism = parallelism,
            completions = completions,
            completionMode = completionMode,
            backoffLimit = backoffLimit,
            backoffLimitPerIndex = backoffLimitPerIndex,
            maxFailedIndexes = maxFailedIndexes,
            activeDeadlineSeconds = activeDeadlineSeconds,
            ttlSecondsAfterFinished = ttlSecondsAfterFinished,
            suspend = suspend,
            manualSelector = manualSelector,
            podReplacementPolicy = podReplacementPolicy,
            selector = selector?.build(),
            podFailurePolicy = podFailurePolicy?.build(),
            successPolicy = successPolicy?.build(),
            template = template!!.build()
        )
    }
}

/**
 * Constructs a Job specification for a Kubernetes Job resource using a builder-style DSL.
 *
 * @param prepare A lambda with a receiver of type [ExplicitTemplateSpecBuilder] used to configure
 * the metadata and specification for the Job resource.
 * @return A [ExplicitTemplateSpec] instance containing the configured [JobSpec].
 *
 * Example:
 * ```kotlin
 * job {
 *     metadata("my-job") {
 *         namespace = "default"
 *     }
 *     spec {
 *         backoffLimit = 4
 *         template {
 *             spec {
 *                 containers {
 *                     container("worker", "busybox") { }
 *                 }
 *                 restartPolicy = PodSpec.RestartPolicy.Never
 *             }
 *         }
 *     }
 * }
 * ```
 */
fun job(prepare: ExplicitTemplateSpecBuilder<JobSpec, JobSpecBuilder>.() -> Unit): ExplicitTemplateSpec<JobSpec> =
    ExplicitTemplateSpecBuilder(JobSpec.API_VERSION, JobSpec.KIND, JobSpecBuilder())
        .apply(prepare)
        .build()
