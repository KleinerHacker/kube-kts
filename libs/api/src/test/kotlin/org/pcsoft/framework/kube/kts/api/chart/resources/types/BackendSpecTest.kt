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