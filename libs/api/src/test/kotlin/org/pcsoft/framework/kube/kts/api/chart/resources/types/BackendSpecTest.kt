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

    /**
     * Verifies that the BackendSpec definition of the service backend flavour is built into the
     * expected spec object.
     *
     * The fields relevant for this case are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
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

    /**
     * Verifies that the BackendSpec definition of the service backend flavour is serialised into
     * the expected YAML document.
     *
     * The fields relevant for this case are set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
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

    /**
     * Verifies that the maximal BackendSpec definition of the resource backend flavour is built
     * into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
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

    /**
     * Verifies that the maximal BackendSpec definition of the resource backend flavour is
     * serialised into the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
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

    /**
     * Verifies that the minimal BackendSpec definition of the resource backend flavour is built
     * into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testResourceBackendMinContent() {
        val backendSpec = ResourceBackendSpecBuilder("resource", "kind").build()

        assertIs<ResourceBackendSpec>(backendSpec)
        assertEquals("resource", backendSpec.name)
        assertEquals("kind", backendSpec.kind)
        assertNull(backendSpec.apiGroup)
    }

    /**
     * Verifies that the minimal BackendSpec definition of the resource backend flavour is
     * serialised into the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
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