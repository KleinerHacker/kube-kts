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
 * Tests for [ResourceResizePolicySpecBuilder], the builder of the in-place resize behaviour of a container.
 */
class ResourceResizePolicySpecBuilderTest {
    /**
     * Verifies that an unconfigured policy defaults to resizing without a restart.
     *
     * That default matches Kubernetes, which only restarts a container when explicitly asked to.
     */
    @Test
    fun testDefaultsToNotRequired() {
        val spec = ResourceResizePolicySpecBuilder(ResourceResizePolicySpec.ResourceName.Cpu).build()

        assertEquals(ResourceResizePolicySpec.ResourceName.Cpu, spec.resourceName)
        assertEquals(ResourceResizePolicySpec.RestartPolicy.NotRequired, spec.restartPolicy)
    }

    /**
     * Verifies that an explicitly configured restart policy is carried into the specification.
     *
     * Memory resizes usually require a restart, which is what this configuration expresses.
     */
    @Test
    fun testAppliesConfiguredRestartPolicy() {
        val spec = ResourceResizePolicySpecBuilder(ResourceResizePolicySpec.ResourceName.Memory).apply {
            restartPolicy = ResourceResizePolicySpec.RestartPolicy.RestartContainer
        }.build()

        assertEquals(ResourceResizePolicySpec.ResourceName.Memory, spec.resourceName)
        assertEquals(ResourceResizePolicySpec.RestartPolicy.RestartContainer, spec.restartPolicy)
    }
}
