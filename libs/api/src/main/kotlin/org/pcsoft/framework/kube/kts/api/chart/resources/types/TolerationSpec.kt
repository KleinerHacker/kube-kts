/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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

import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Represents a specification for tolerations in Kubernetes-like configurations. Tolerations allow scheduling
 * rules to be applied to workloads that need to tolerate specific taints on nodes.
 *
 * @property key The key of the taint that the toleration applies to. If null, the toleration applies to all keys.
 * @property operator Specifies the relationship between the key and the value. Possible values are `Equal` or `Exists`.
 * @property value Optional GPU-specific toleration, allowing tolerations to be scoped to GPU workloads.
 * @property effect Specifies the effect of the taint to tolerate. Possible values are `NoSchedule`, `PreferNoSchedule`, or `NoExecute`.
 * @property tolerationSeconds The duration in seconds for which the toleration is applicable.
 */
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
     * Defines the operator used in toleration to specify the relationship between a key and a value.
     */
    @Suppress("unused")
    enum class Operator {
        /**
         * Represents an equality operator used to specify the relationship between a key and a value.
         */
        Equal,

        /**
         * Represents the "Exists" operator, typically used to specify the existence of a key
         * without requiring an associated value. It defines a condition where the presence of the key alone suffices.
         */
        Exists
    }

    /**
     * Specifies the effects of taints that toleration can tolerate. The effect determines the impact
     * on workload scheduling or execution when the taint is present on a node.
     */
    @Suppress("unused")
    enum class Effect {
        /**
         * Represents the "NoSchedule" effect, which prevents scheduling of workloads on nodes
         * that have the specified taint.
         */
        NoSchedule,

        /**
         * Represents the "PreferNoSchedule" effect, which prefers scheduling of workloads on nodes
         * that have the specified taint, but allows scheduling of workloads on nodes that do not have the taint.
         */
        PreferNoSchedule,

        /**
         * Represents the "NoExecute" effect, which prevents scheduling of workloads on nodes
         * that have the specified taint and prevents the pod from being rescheduled on the node.
         */
        NoExecute
    }
}
