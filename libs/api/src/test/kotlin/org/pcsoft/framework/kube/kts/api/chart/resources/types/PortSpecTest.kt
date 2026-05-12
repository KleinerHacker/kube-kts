package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PortSpecTest {

    @Test
    fun testNameContent() {
        val portSpec = PortSpecBuilder("demo")
            .build()

        assertNull(portSpec.number)
        assertEquals("demo", portSpec.name)
    }

    @Test
    fun testNameYaml() {
        val portSpec = PortSpecBuilder("demo")
            .build()

        val actualJson = portSpec.toJson()
        val expectedJson = "{\"name\":\"demo\"}"

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testNumberContent() {
        val portSpec = PortSpecBuilder(8080)
            .build()

        assertNull(portSpec.name)
        assertEquals(8080, portSpec.number)
    }

    @Test
    fun testNumberYaml() {
        val portSpec = PortSpecBuilder(8080)
            .build()

        val actualJson = portSpec.toJson()
        val expectedJson = "{\"number\":8080}"

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testEmptyNameContent() {
        assertThrows<IllegalArgumentException> { PortSpecBuilder("").build() }
    }

    @Test
    fun testNegativePortNumber() {
        assertThrows<IllegalArgumentException> { PortSpecBuilder(-1).build() }
    }

    @Test
    fun testPortNumberExceedsMaximum() {
        assertThrows<IllegalArgumentException> { PortSpecBuilder(65536).build() }
    }

}