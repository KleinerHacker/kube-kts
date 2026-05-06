package org.pcsoft.framework.kube.kts.api.types

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.pcsoft.framework.kube.kts.api.types.RelativeValue.Companion.absolute
import org.pcsoft.framework.kube.kts.api.types.RelativeValue.Companion.percent
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