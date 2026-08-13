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

package org.pcsoft.framework.kube.kts.api.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class RelativeValueTest {

    /**
     * Verifies that a percentage value is accepted and rendered with a percent sign.
     *
     * Kubernetes accepts relative values such as `25%` for rollout strategies and topology
     * constraints.
     */
    @Test
    fun testPercent() {
        val percent = 100.percent
        assertIs<PercentageValue>(percent)
        assertEquals(1f, percent.value)
        assertEquals("100%", percent.toYamlValue())
    }

    /**
     * Verifies that a negative percentage is rejected.
     *
     * A negative share is meaningless for Kubernetes and must fail when the value is created.
     */
    @Test
    fun testPercentNegative() {
        assertFailsWith<IllegalArgumentException> { (-1).percent }
    }

    /**
     * Verifies that a percentage above the allowed maximum is rejected.
     *
     * Values beyond 100% would be rejected by the API server, so they must fail earlier.
     */
    @Test
    fun testPercentTooLarge() {
        assertFailsWith<IllegalArgumentException> { 101.percent }
    }

    /**
     * Verifies that an absolute value is accepted and rendered as a plain number.
     *
     * The same DSL type covers absolute counts, which must not carry a percent sign.
     */
    @Test
    fun testAbsolute() {
        val absolute = 100.absolute
        assertIs<AbsoluteValue>(absolute)
        assertEquals(100, absolute.value)
        assertEquals(100, absolute.toYamlValue())
    }

    /**
     * Verifies that a negative absolute value is rejected.
     *
     * A negative count is meaningless for Kubernetes and must fail when the value is created.
     */
    @Test
    fun testAbsoluteNegative() {
        assertFailsWith<IllegalArgumentException> { (-1).absolute }
    }
}