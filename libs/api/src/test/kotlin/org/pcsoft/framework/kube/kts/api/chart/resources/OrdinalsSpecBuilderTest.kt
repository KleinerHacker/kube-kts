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

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests for [OrdinalsSpecBuilder], the builder of the replica numbering of a StatefulSet.
 */
class OrdinalsSpecBuilderTest {
    /**
     * Verifies that an unconfigured builder leaves the start ordinal to the cluster default.
     */
    @Test
    fun testBuildsEmptyOrdinals() {
        assertNull(OrdinalsSpecBuilder().build().start)
    }

    /**
     * Verifies that a configured start ordinal is carried into the specification.
     *
     * Shifting the first ordinal is what allows a StatefulSet to be migrated replica by replica.
     */
    @Test
    fun testBuildsConfiguredOrdinals() {
        assertEquals(3, OrdinalsSpecBuilder().apply { start = 3 }.build().start)
    }
}
