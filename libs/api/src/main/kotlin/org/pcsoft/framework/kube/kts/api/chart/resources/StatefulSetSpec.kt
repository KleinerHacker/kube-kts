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
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PersistentVolumeClaimRetentionPolicySpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.StatefulSetUpdateStrategySpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeClaimTemplateSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Represents the specification for a Kubernetes StatefulSet resource.
 *
 * A StatefulSet manages the deployment and scaling of a set of Pods and provides guarantees about
 * the ordering and uniqueness of these Pods. Unlike a Deployment, each Pod gets a stable, sticky
 * identity (a persistent network identifier and stable storage via [volumeClaimTemplates]). It is
 * suited for stateful applications such as databases or message brokers.
 *
 * @property replicas The desired number of replicas (Pod instances) to maintain. If null, Kubernetes
 * defaults to one replica.
 * @property selector The label selector used to identify the set of Pods managed by this StatefulSet.
 * It must match the labels of the [template].
 * @property serviceName The name of the governing (usually headless) Service that controls the network
 * domain of the StatefulSet's Pods.
 * @property podManagementPolicy Controls how Pods are created and deleted during scaling
 * ([PodManagementPolicy.OrderedReady] or [PodManagementPolicy.Parallel]).
 * @property updateStrategy The strategy that determines how updates to the [template] are rolled out
 * to the Pods.
 * @property volumeClaimTemplates A list of PersistentVolumeClaim templates. One claim per Pod is
 * provisioned from each template, providing stable per-Pod storage.
 * @property persistentVolumeClaimRetentionPolicy Controls whether the PersistentVolumeClaims created
 * from the [volumeClaimTemplates] are retained or deleted when the StatefulSet is deleted or scaled down.
 * @property revisionHistoryLimit The maximum number of revisions to retain in the StatefulSet's history
 * for rollback purposes.
 * @property minReadySeconds The minimum number of seconds for which a newly created Pod should be ready
 * without any of its containers crashing, for it to be considered available.
 * @property ordinals Configures the numbering of the StatefulSet's replica indices.
 * @property template The Pod template describing the Pods created by this StatefulSet.
 */
@NoArgs
data class StatefulSetSpec(
    val replicas: Int?,
    val selector: LabelSelectorSpec,
    val serviceName: String,
    val podManagementPolicy: PodManagementPolicy?,
    val updateStrategy: StatefulSetUpdateStrategySpec?,
    val volumeClaimTemplates: List<VolumeClaimTemplateSpec>?,
    val persistentVolumeClaimRetentionPolicy: PersistentVolumeClaimRetentionPolicySpec?,
    val revisionHistoryLimit: Int?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val minReadySeconds: Duration?,
    val ordinals: OrdinalsSpec?,
    val template: PodTemplateSpec
) : ResourceSpec {

    /**
     * Controls how Pods of a StatefulSet are created and terminated during scaling operations.
     */
    @Suppress("unused")
    enum class PodManagementPolicy {
        /**
         * Pods are created and terminated one at a time in strict ordinal order, waiting for each
         * Pod to become Running and Ready (or fully terminated) before acting on the next one.
         */
        OrderedReady,

        /**
         * Pods are created and terminated in parallel, without waiting for predecessors.
         */
        Parallel
    }

    /**
     * Configures the ordinal numbering of a StatefulSet's replica indices.
     *
     * @property start The number representing the first replica index. Defaults to 0 if not set.
     */
    @NoArgs
    data class OrdinalsSpec(
        val start: Int?
    )

    companion object {
        /**
         * Identifies this resource as a StatefulSet.
         */
        const val KIND = "StatefulSet"

        /**
         * Specifies the API version for Kubernetes StatefulSet resources.
         *
         * Value: "apps/v1"
         */
        const val API_VERSION = "apps/v1"
    }
}
