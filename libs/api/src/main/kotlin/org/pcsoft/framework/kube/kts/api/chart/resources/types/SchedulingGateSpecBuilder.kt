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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodSpec.SchedulingGateSpec

/**
 * Builder class for creating instances of [SchedulingGateSpec].
 *
 * A scheduling gate blocks the pod from being scheduled until it is removed by a controller.
 *
 * @constructor Creates an instance of [SchedulingGateSpecBuilder] for internal usage.
 * @param name The name of the scheduling gate.
 */
class SchedulingGateSpecBuilder internal constructor(private val name: String) {
    /**
     * Constructs and returns a [SchedulingGateSpec] instance for the configured name.
     *
     * @return A [SchedulingGateSpec] carrying the configured name.
     */
    internal fun build(): SchedulingGateSpec = SchedulingGateSpec(name)
}
