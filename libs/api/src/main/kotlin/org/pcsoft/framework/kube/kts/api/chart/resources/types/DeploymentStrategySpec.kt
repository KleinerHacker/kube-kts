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
 * Represents the deployment strategy for a Kubernetes resource, allowing for the specification
 * of how updates to Pods within a deployment are managed.
 *
 * This class provides a way to define the type of deployment strategy and its associated configuration.
 * It supports two types of strategies:
 * - `Recreate`: Terminates all the existing Pods and recreates new ones.
 * - `RollingUpdate`: Updates Pods incrementally, ensuring a controlled replacement process.
 *
 * @property type The type of deployment strategy used. Can be either `Recreate` or `RollingUpdate`.
 * @property rollingUpdate Configuration details for the `RollingUpdate` strategy, including parameters
 * like maximum surge and maximum unavailable Pods. This is only relevant when the `RollingUpdate` type
 * is selected.
 */
@NoArgs
data class DeploymentStrategySpec(
    val type: Type?,
    val rollingUpdate: RollingUpdateDeploymentSpec?
) {
    /**
     * Specifies the type of deployment strategy for rolling out updates to a set of resources.
     *
     * This enum is used to define the manner in which updates are applied to the underlying
     * resources during a deployment, allowing for control over the disruption and downtime
     * associated with the update process.
     */
    @Suppress("unused")
    enum class Type {
        /**
         * Represents the recreate deployment strategy type.
         *
         * This strategy involves terminating all existing resources before creating new ones.
         * It ensures that updates are rolled out in a manner where no overlapping versions
         * of resources exist during the update process. While this approach can result in
         * downtime, it is suitable for scenarios where resource state consistency is critical
         * and downtime is acceptable.
         */
        Recreate,

        /**
         * Represents the configuration for a rolling update deployment strategy.
         *
         * This class is used to define the behavior of rolling updates when updating a set of resources.
         * Rolling updates gradually replace old resource instances with new ones, ensuring minimal disruption
         * and downtime during the update process. It allows for fine-grained control of the batch size
         * and the total number of resources that can be unavailable or exceeding the desired count
         * during the update.
         */
        RollingUpdate
    }

    /**
     * Specifies the configuration for rolling update deployment strategy, which controls the
     * behavior of updates to Pods during a rolling update process.
     *
     * This specification allows fine-grained control over how many Pods can be unavailable or
     * created beyond the desired number of Pods during the update process.
     *
     * @property maxUnavailable The maximum number of Pods that can be unavailable
     * during the rolling update. This value can be defined as a percentage (relative to the
     * desired number of Pods) or as an absolute value.
     * @property maxSurge The maximum number of additional Pods that can be created
     * beyond the desired number of Pods during the rolling update. This value can also be
     * defined either as a percentage or an absolute value.
     */
    @NoArgs
    data class RollingUpdateDeploymentSpec(
        val maxUnavailable: RelativeValue<*,*>?,
        val maxSurge: RelativeValue<*,*>?
    )
}