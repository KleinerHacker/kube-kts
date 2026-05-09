package org.pcsoft.framework.kube.kts.api.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MemoryValueTest {

    @Test
    fun testYaml() {
        assertEquals("100", 100.bytes.toYamlValue())
        assertEquals("1000", 1000.bytes.toYamlValue())
        assertEquals("9Ki", 10000.bytes.toYamlValue())
        assertEquals("10Mi", 10.miBytes.toYamlValue())
        assertEquals("10Gi", 10.giBytes.toYamlValue())
    }

    @Test
    fun testParse() {
        assertEquals(100.bytes, MemoryValue.parse("100"))
        assertEquals(10.miBytes, MemoryValue.parse("10Mi"))
        assertEquals(10.giBytes, MemoryValue.parse("10Gi"))

        assertEquals(9216.bytes, MemoryValue.parse("9Ki"))
    }

}