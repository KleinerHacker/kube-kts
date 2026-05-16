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
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RelativeValueTest {

    @Test
    fun testPercent() {
        val percent = 100.percent
        assertIs<PercentageValue>(percent)
        assertEquals(1f, percent.value)
        assertEquals("100%", percent.toYamlValue())
    }

    @Test
    fun testPercentNegative() {
        assertThrows<IllegalArgumentException> { (-1).percent }
    }

    @Test
    fun testPercentTooLarge() {
        assertThrows<IllegalArgumentException> { 101.percent }
    }

    @Test
    fun testAbsolute() {
        val absolute = 100.absolute
        assertIs<AbsoluteValue>(absolute)
        assertEquals(100, absolute.value)
        assertEquals(100, absolute.toYamlValue())
    }

    @Test
    fun testAbsoluteNegative() {
        assertThrows<IllegalArgumentException> { (-1).absolute }
    }
}