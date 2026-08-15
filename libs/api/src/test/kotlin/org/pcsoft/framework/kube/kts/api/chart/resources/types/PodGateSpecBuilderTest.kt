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

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Tests for [ReadinessGateSpecBuilder] and [SchedulingGateSpecBuilder], the gate builders of a pod.
 */
class PodGateSpecBuilderTest {
    /**
     * Verifies that a readiness gate carries the configured condition type.
     *
     * The pod only becomes ready once a controller reports that condition as true.
     */
    @Test
    fun testBuildsReadinessGate() {
        assertEquals(
            "www.example.com/feature-1",
            ReadinessGateSpecBuilder("www.example.com/feature-1").build().conditionType
        )
    }

    /**
     * Verifies that a scheduling gate carries the configured name.
     *
     * As long as the gate is present the scheduler leaves the pod pending.
     */
    @Test
    fun testBuildsSchedulingGate() {
        assertEquals("wait-for-quota", SchedulingGateSpecBuilder("wait-for-quota").build().name)
    }
}
