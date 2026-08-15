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

/**
 * Tests for [LocalObjectReferenceSpecBuilder], the builder of a same-namespace object reference.
 */
class LocalObjectReferenceSpecBuilderTest {
    /**
     * Verifies that the configured name ends up in the built reference.
     *
     * Image pull secrets and volume credential references are rendered from exactly this shape.
     */
    @Test
    fun testBuildsReference() {
        assertEquals("registry-credentials", LocalObjectReferenceSpecBuilder("registry-credentials").build().name)
    }

    /**
     * Verifies that a blank name is rejected.
     *
     * An empty reference would silently point at no object at all once applied to the cluster.
     */
    @Test
    fun testRejectsBlankName() {
        assertFailsWith<IllegalArgumentException> { LocalObjectReferenceSpecBuilder("  ").build() }
    }
}
