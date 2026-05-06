package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.junit.jupiter.api.Test
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
        JSONAssert.assertEquals("{\"name\":\"demo\"}", actualJson, JSONCompareMode.LENIENT)
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
        JSONAssert.assertEquals("{\"number\":8080}", actualJson, JSONCompareMode.LENIENT)
    }   

}