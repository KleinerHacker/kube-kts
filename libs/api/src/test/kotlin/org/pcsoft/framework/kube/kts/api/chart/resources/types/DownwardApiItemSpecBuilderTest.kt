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
 * Tests for [DownwardApiItemSpecBuilder] and the field selector builders it delegates to.
 */
class DownwardApiItemSpecBuilderTest {
    /**
     * Verifies that a pod field selector is built with its API version and file mode.
     */
    @Test
    fun testBuildsFieldRef() {
        val spec = DownwardApiItemSpecBuilder("labels").apply {
            mode = 0x1A4
            fieldRef("metadata.labels") { apiVersion = "v1" }
        }.build()

        assertEquals("labels", spec.path)
        assertEquals("metadata.labels", spec.fieldRef?.fieldPath)
        assertEquals("v1", spec.fieldRef?.apiVersion)
        assertEquals(0x1A4, spec.mode)
        assertNull(spec.resourceFieldRef)
    }

    /**
     * Verifies that a container resource selector is built with container name and divisor.
     */
    @Test
    fun testBuildsResourceFieldRef() {
        val spec = DownwardApiItemSpecBuilder("cpu_limit").apply {
            resourceFieldRef("limits.cpu") {
                containerName = "app"
                divisor = "1m"
            }
        }.build()

        assertEquals("limits.cpu", spec.resourceFieldRef?.resource)
        assertEquals("app", spec.resourceFieldRef?.containerName)
        assertEquals("1m", spec.resourceFieldRef?.divisor)
        assertNull(spec.fieldRef)
    }

    /**
     * Verifies that the two selector kinds are mutually exclusive.
     *
     * Kubernetes rejects an item carrying both, so the later selector must clear the earlier one.
     */
    @Test
    fun testSelectorsAreMutuallyExclusive() {
        val spec = DownwardApiItemSpecBuilder("value").apply {
            fieldRef("metadata.name")
            resourceFieldRef("limits.memory")
        }.build()

        assertNull(spec.fieldRef)
        assertEquals("limits.memory", spec.resourceFieldRef?.resource)
    }

    /**
     * Verifies that an item without any selector is rejected.
     */
    @Test
    fun testRejectsItemWithoutSelector() {
        assertFailsWith<IllegalArgumentException> { DownwardApiItemSpecBuilder("value").build() }
    }

    /**
     * Verifies that the volume source DSL routes both item forms through the item builder.
     */
    @Test
    fun testItemsThroughDownwardApiSource() {
        val spec = DownwardApiSourceSpecBuilder().apply {
            items {
                item("name") { fieldRef("metadata.name") }
            }
            addResourceFieldRef("cpu", "limits.cpu", "app")
        }.build()

        assertEquals(2, spec.items.size)
        assertEquals("metadata.name", spec.items[0].fieldRef?.fieldPath)
        assertEquals("app", spec.items[1].resourceFieldRef?.containerName)
    }
}
