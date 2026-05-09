package org.pcsoft.framework.kube.kts.api.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CpuValueTest {

    @Test
    fun testYaml() {
        assertEquals("100m", 0.1f.cpu.toYamlValue())
        assertEquals("1000m", 1f.cpu.toYamlValue())
        assertEquals("10000m", 10f.cpu.toYamlValue())
    }

    @Test
    fun testParse() {
        assertEquals(0.1f, CpuValue.parse("100m").value)
        assertEquals(1f, CpuValue.parse("1000m").value)
        assertEquals(10f, CpuValue.parse("10000m").value)

        assertEquals(10f, CpuValue.parse("10").value)
    }

}