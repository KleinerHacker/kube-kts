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
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Allows a Pod to be scheduled onto nodes carrying a matching taint.
 *
 * Taints let a node repel Pods; a toleration is the Pod's counterpart that makes it acceptable anyway.
 * A toleration matches a taint when key, value and effect all agree - with an empty [key] and
 * [Operator.Exists] matching every taint.
 *
 * @property key               The taint key this toleration applies to. An empty key together with
 *                             [Operator.Exists] tolerates every taint.
 * @property operator          How [value] is compared. Defaults to [Operator.Equal] when unset.
 * @property value             The taint value to match. Must be empty when [operator] is [Operator.Exists].
 * @property effect            The taint effect to match. Matches every effect when unset.
 * @property tolerationSeconds How long the Pod stays bound to the node after a [Effect.NoExecute] taint
 *                             appears. Only meaningful for that effect; unset means forever.
 */
@NoArgs
data class TolerationSpec(
    val key: String?,
    val operator: Operator?,
    val value: String?,
    val effect: Effect?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val tolerationSeconds: Duration?
) {
    /**
     * How a toleration compares itself against a taint.
     */
    @Suppress("unused")
    enum class Operator {
        /**
         * The taint's value must equal the toleration's value.
         */
        Equal,

        /**
         * The taint only has to carry the key; its value is ignored.
         */
        Exists
    }

    /**
     * The taint effect a toleration applies to.
     */
    @Suppress("unused")
    enum class Effect {
        /**
         * New Pods are not scheduled onto the node; running Pods stay.
         */
        NoSchedule,

        /**
         * The scheduler avoids the node but may still use it if nothing else fits.
         */
        PreferNoSchedule,

        /**
         * New Pods are not scheduled and running Pods are evicted.
         */
        NoExecute
    }
}
