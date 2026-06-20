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
import org.pcsoft.framework.kube.kts.api.types.RelativeValue

/**
 * Represents the update strategy for a Kubernetes StatefulSet, controlling how Pods are replaced
 * when the StatefulSet's [PodTemplateSpec] is updated.
 *
 * It supports two types of strategies:
 * - `RollingUpdate`: Pods are updated in reverse ordinal order, one at a time, optionally honouring
 *   a [RollingUpdateStatefulSetSpec.partition] and [RollingUpdateStatefulSetSpec.maxUnavailable].
 * - `OnDelete`: The controller does not automatically update Pods; the user has to delete Pods
 *   manually to trigger the creation of updated Pods.
 *
 * @property type The type of update strategy used. Can be either `RollingUpdate` or `OnDelete`.
 * @property rollingUpdate Configuration details for the `RollingUpdate` strategy. This is only
 * relevant when the `RollingUpdate` type is selected.
 */
@NoArgs
data class StatefulSetUpdateStrategySpec(
    val type: Type?,
    val rollingUpdate: RollingUpdateStatefulSetSpec?
) {
    /**
     * Specifies the type of update strategy for rolling out changes to the Pods of a StatefulSet.
     */
    @Suppress("unused")
    enum class Type {
        /**
         * Pods are updated automatically in reverse ordinal order, one Pod at a time.
         */
        RollingUpdate,

        /**
         * Pods are not updated automatically. The user must delete Pods manually to let the
         * controller recreate them with the updated specification.
         */
        OnDelete
    }

    /**
     * Specifies the configuration for the rolling update strategy of a StatefulSet.
     *
     * @property partition Indicates the ordinal at which the StatefulSet should be partitioned for
     * updates. All Pods with an ordinal greater than or equal to the partition are updated; Pods
     * with a smaller ordinal are left untouched. Used for staged/canary rollouts.
     * @property maxUnavailable The maximum number of Pods that can be unavailable during the update.
     * This value can be defined either as a percentage (relative to the desired number of Pods) or
     * as an absolute value.
     */
    @NoArgs
    data class RollingUpdateStatefulSetSpec(
        val partition: Int?,
        val maxUnavailable: RelativeValue<*, *>?
    )
}
