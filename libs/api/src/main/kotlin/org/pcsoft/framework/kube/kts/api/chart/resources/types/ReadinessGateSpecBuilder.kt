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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodSpec.ReadinessGateSpec

/**
 * Builder class for creating instances of [ReadinessGateSpec].
 *
 * A readiness gate names an additional pod condition that has to be true before the pod is considered
 * ready.
 *
 * @constructor Creates an instance of [ReadinessGateSpecBuilder] for internal usage.
 * @param conditionType The type of the pod condition that has to be true.
 */
class ReadinessGateSpecBuilder internal constructor(private val conditionType: String) {
    /**
     * Constructs and returns a [ReadinessGateSpec] instance for the configured condition type.
     *
     * @return A [ReadinessGateSpec] referencing the configured condition.
     */
    internal fun build(): ReadinessGateSpec = ReadinessGateSpec(conditionType)
}
