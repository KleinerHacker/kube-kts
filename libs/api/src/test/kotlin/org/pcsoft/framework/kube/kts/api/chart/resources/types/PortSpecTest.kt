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