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
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Tests for [ResourceClaimReferenceSpecBuilder], the builder referencing a pod resource claim.
 */
class ResourceClaimReferenceSpecBuilderTest {
    /**
     * Verifies that a reference without a request may use every request of the claim.
     */
    @Test
    fun testBuildsMinimalReference() {
        val spec = ResourceClaimReferenceSpecBuilder("gpu").build()

        assertEquals("gpu", spec.name)
        assertNull(spec.request)
    }

    /**
     * Verifies that a reference narrowed down to a single request carries that request.
     */
    @Test
    fun testBuildsReferenceWithRequest() {
        val spec = ResourceClaimReferenceSpecBuilder("gpu").apply { request = "shared" }.build()

        assertEquals("gpu", spec.name)
        assertEquals("shared", spec.request)
    }

    /**
     * Verifies that a blank request name is rejected.
     */
    @Test
    fun testRejectsBlankRequest() {
        assertFailsWith<IllegalArgumentException> {
            ResourceClaimReferenceSpecBuilder("gpu").apply { request = " " }.build()
        }
    }

    /**
     * Verifies that the container resource DSL routes both claim forms through the builder.
     *
     * The shorthand taking the request directly must produce the same result as the lambda form.
     */
    @Test
    fun testClaimsThroughResourceBuilder() {
        val spec = HardwareResourceSpecBuilder().apply {
            claims {
                claim("gpu") { request = "shared" }
                claim("fpga", null)
            }
        }.build()

        assertEquals(2, spec.claims?.size)
        assertEquals("shared", spec.claims?.get(0)?.request)
        assertEquals("fpga", spec.claims?.get(1)?.name)
        assertNull(spec.claims?.get(1)?.request)
    }
}
