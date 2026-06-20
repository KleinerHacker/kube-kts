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

import org.pcsoft.framework.kube.kts.api.types.RelativeValue

/**
 * A builder class for constructing instances of [StatefulSetUpdateStrategySpec].
 *
 * This builder allows the configuration of the StatefulSet update strategy, including the type
 * selection (`RollingUpdate` or `OnDelete`) and the optional specification of rolling update
 * parameters such as `partition` and `maxUnavailable`.
 *
 * All values are optional.
 *
 * @constructor Creates an instance of `StatefulSetUpdateStrategySpecBuilder`.
 */
class StatefulSetUpdateStrategySpecBuilder internal constructor() {
    private var rollingUpdate: RollingUpdateStatefulSetSpecBuilder? = null

    /**
     * Specifies the type of update strategy used for managing updates to the Pods of the StatefulSet.
     *
     * This property is optional and can be set to `null` if no specific strategy is to be defined.
     */
    var type: StatefulSetUpdateStrategySpec.Type? = null

    /**
     * Configures the rolling update strategy for the StatefulSet.
     *
     * This method allows the specification of parameters for a rolling update, providing controlled
     * and incremental updates of the Pods during a rollout.
     *
     * @param prepare The configuration block used to define the settings for the rolling update.
     *                This block applies to an instance of [RollingUpdateStatefulSetSpecBuilder],
     *                where properties such as `partition` and `maxUnavailable` can be configured.
     */
    fun rollingUpdate(prepare: RollingUpdateStatefulSetSpecBuilder.() -> Unit) {
        rollingUpdate = RollingUpdateStatefulSetSpecBuilder().apply(prepare)
    }

    /**
     * Builds and returns an instance of [StatefulSetUpdateStrategySpec] representing the configured
     * update strategy.
     *
     * @return An instance of [StatefulSetUpdateStrategySpec] representing the configured update strategy.
     */
    internal fun build(): StatefulSetUpdateStrategySpec = StatefulSetUpdateStrategySpec(
        type = type,
        rollingUpdate = rollingUpdate?.build()
    )

    /**
     * Builder class for configuring the rolling update strategy of a StatefulSet.
     *
     * @constructor Creates an instance of `RollingUpdateStatefulSetSpecBuilder` for internal use by its parent builder class.
     *
     * @property partition The ordinal at which the StatefulSet is partitioned for updates. All Pods with an ordinal
     * greater than or equal to the partition are updated.
     * @property maxUnavailable The maximum number of Pods that can be unavailable during the rolling update. This can
     * be specified as either an absolute value or a percentage relative to the desired state.
     */
    class RollingUpdateStatefulSetSpecBuilder internal constructor() {
        var partition: Int? = null
        var maxUnavailable: RelativeValue<*, *>? = null

        /**
         * Builds and returns an instance of [StatefulSetUpdateStrategySpec.RollingUpdateStatefulSetSpec]
         * configured with the provided `partition` and `maxUnavailable` values.
         *
         * @return A [StatefulSetUpdateStrategySpec.RollingUpdateStatefulSetSpec] instance containing the
         *         rolling update strategy configuration.
         */
        internal fun build(): StatefulSetUpdateStrategySpec.RollingUpdateStatefulSetSpec =
            StatefulSetUpdateStrategySpec.RollingUpdateStatefulSetSpec(partition, maxUnavailable)
    }
}
