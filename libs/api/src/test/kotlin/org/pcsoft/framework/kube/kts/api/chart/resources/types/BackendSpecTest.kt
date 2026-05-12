package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class BackendSpecTest {

    @Test
    fun testServiceBackendContent() {
        val backendSpec = ServiceBackendSpecBuilder("service").apply {
            port("name")
        }.build()

        assertIs<ServiceBackendSpec>(backendSpec)
        assertEquals("service", backendSpec.name)
        assertEquals("name", backendSpec.port.name)
        assertNull(backendSpec.port.number)
    }

    @Test
    fun testServiceBackendYaml() {
        val backendSpec = ServiceBackendSpecBuilder("service").apply {
            port("name")
        }.build()

        val expectedJson = """{
          |  "service": {
          |    "name": "service",
          |    "port": {
          |      "name": "name"
          |    }
          |  }
          |}""".trimMargin()
        val actualJson = backendSpec.toJson()
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testResourceBackendMaxContent() {
        val backendSpec = ResourceBackendSpecBuilder("resource", "kind").apply {
            apiGroup = "group"
        }.build()

        assertIs<ResourceBackendSpec>(backendSpec)
        assertEquals("resource", backendSpec.name)
        assertEquals("kind", backendSpec.kind)
        assertEquals("group", backendSpec.apiGroup)
    }

    @Test
    fun testResourceBackendMaxYaml() {
        val backendSpec = ResourceBackendSpecBuilder("resource", "kind").apply {
            apiGroup = "group"
        }.build()

        val expectedJson = """{
          |  "resource": {
          |    "name": "resource",
          |    "kind": "kind",
          |    "apiGroup": "group"
          |  }
          |}""".trimMargin()
        val actualJson = backendSpec.toJson()
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testResourceBackendMinContent() {
        val backendSpec = ResourceBackendSpecBuilder("resource", "kind").build()

        assertIs<ResourceBackendSpec>(backendSpec)
        assertEquals("resource", backendSpec.name)
        assertEquals("kind", backendSpec.kind)
        assertNull(backendSpec.apiGroup)
    }

    @Test
    fun testResourceBackendMinYaml() {
        val backendSpec = ResourceBackendSpecBuilder("resource", "kind").build()

        val expectedJson = """{
          |  "resource": {
          |    "name": "resource",
          |    "kind": "kind"
          |  }
          |}""".trimMargin()
        val actualJson = backendSpec.toJson()
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

}