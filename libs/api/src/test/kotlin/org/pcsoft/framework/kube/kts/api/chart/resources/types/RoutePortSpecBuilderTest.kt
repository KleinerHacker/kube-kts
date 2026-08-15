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
 * Tests for [RoutePortSpecBuilder], the builder of the target port of an OpenShift Route.
 */
class RoutePortSpecBuilderTest {
    /**
     * Verifies that a route port addressed by name carries only the port name.
     *
     * The router resolves the name against the backing service, so the numeric port must stay unset.
     */
    @Test
    fun testBuildsNamedPort() {
        val spec = RoutePortSpecBuilder("http").build()

        assertEquals("http", spec.targetPortName)
        assertNull(spec.targetPortNumber)
    }

    /**
     * Verifies that a route port addressed by number carries only the port number.
     */
    @Test
    fun testBuildsNumberedPort() {
        val spec = RoutePortSpecBuilder(8080).build()

        assertEquals(8080, spec.targetPortNumber)
        assertNull(spec.targetPortName)
    }

    /**
     * Verifies that a blank port name is rejected.
     *
     * A blank name would render an empty `targetPort` that the router cannot resolve.
     */
    @Test
    fun testRejectsBlankName() {
        assertFailsWith<IllegalArgumentException> { RoutePortSpecBuilder(" ").build() }
    }

    /**
     * Verifies that port numbers outside the valid TCP range are rejected.
     */
    @Test
    fun testRejectsOutOfRangeNumber() {
        assertFailsWith<IllegalArgumentException> { RoutePortSpecBuilder(0).build() }
        assertFailsWith<IllegalArgumentException> { RoutePortSpecBuilder(65536).build() }
    }
}
