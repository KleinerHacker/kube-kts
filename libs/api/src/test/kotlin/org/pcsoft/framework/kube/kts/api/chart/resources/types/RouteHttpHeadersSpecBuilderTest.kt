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
 * Tests for [RouteHttpHeadersSpecBuilder] and the header builders it delegates to.
 */
class RouteHttpHeadersSpecBuilderTest {
    /**
     * Verifies that the shortcut functions produce set and delete actions on both directions.
     *
     * These shortcuts are the common case, so they must keep working without the nested action DSL.
     */
    @Test
    fun testShortcutsBuildActions() {
        val spec = RouteHttpHeadersSpecBuilder().apply {
            setRequestHeader("X-Forwarded-Proto", "https")
            deleteResponseHeader("Server")
        }.build()

        val request = spec.actions.request!!.single()
        assertEquals("X-Forwarded-Proto", request.name)
        assertEquals(RouteHttpHeaderSpec.Type.Set, request.action.type)
        assertEquals("https", request.action.set?.value)

        val response = spec.actions.response!!.single()
        assertEquals("Server", response.name)
        assertEquals(RouteHttpHeaderSpec.Type.Delete, response.action.type)
        assertNull(response.action.set)
    }

    /**
     * Verifies that the nested action DSL configures the same structure as the shortcuts.
     */
    @Test
    fun testActionsBlock() {
        val spec = RouteHttpHeadersSpecBuilder().apply {
            actions {
                addRequestHeader("X-Trace-Id") { set("abc") }
                addResponseHeader("X-Powered-By") { delete() }
            }
        }.build()

        assertEquals("abc", spec.actions.request!!.single().action.set?.value)
        assertEquals(RouteHttpHeaderSpec.Type.Delete, spec.actions.response!!.single().action.type)
    }

    /**
     * Verifies that a direction without any manipulation stays absent from the manifest.
     *
     * Rendering an empty list would produce noise the OpenShift router does not need.
     */
    @Test
    fun testOmitsEmptyDirections() {
        val spec = RouteHttpHeadersSpecBuilder().apply { setRequestHeader("X-A", "b") }.build()

        assertNull(spec.actions.response)
        assertEquals(1, spec.actions.request?.size)
    }

    /**
     * Verifies that a header without a configured action is rejected.
     *
     * Without `set` or `delete` the router would not know what to do with the header.
     */
    @Test
    fun testRejectsHeaderWithoutAction() {
        assertFailsWith<IllegalArgumentException> { RouteHttpHeaderSpecBuilder("X-A").build() }
    }

    /**
     * Verifies that a later action replaces an earlier one on the same header builder.
     */
    @Test
    fun testLastActionWins() {
        val spec = RouteHttpHeaderSpecBuilder("X-A").apply {
            set("first")
            delete()
        }.build()

        assertEquals(RouteHttpHeaderSpec.Type.Delete, spec.action.type)
    }
}
