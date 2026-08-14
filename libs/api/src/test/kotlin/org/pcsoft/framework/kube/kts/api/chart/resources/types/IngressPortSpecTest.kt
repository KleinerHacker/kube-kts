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
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class IngressPortSpecTest {
    companion object {
        private val nameSpec = IngressPortSpecBuilder("demo").build()
        private val numberSpec = IngressPortSpecBuilder(8080).build()
    }

    /**
     * Verifies that the PortSpec definition of the name flavour is built into the expected spec
     * object.
     *
     * The fields relevant for this case are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testNameContent() {
        assertNull(nameSpec.number)
        assertEquals("demo", nameSpec.name)
    }

    /**
     * Verifies that the PortSpec definition of the name flavour is serialised into the expected
     * YAML document.
     *
     * The fields relevant for this case are set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testNameYaml() {
        val actualJson = nameSpec.toJson()
        val expectedJson = "{\"name\":\"demo\"}"

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the PortSpec definition of the number flavour is built into the expected spec
     * object.
     *
     * The fields relevant for this case are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testNumberContent() {
        assertNull(numberSpec.name)
        assertEquals(8080, numberSpec.number)
    }

    /**
     * Verifies that the PortSpec definition of the number flavour is serialised into the expected
     * YAML document.
     *
     * The fields relevant for this case are set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testNumberYaml() {
        val actualJson = numberSpec.toJson()
        val expectedJson = "{\"number\":8080}"

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that building a [PortSpec] fails when an empty value is used.
     *
     * The builder must reject the input for name with an exception instead of producing an
     * incomplete specification that the API server would refuse later.
     */
    @Test
    fun testEmptyNameContent() {
        assertFailsWith<IllegalArgumentException> { IngressPortSpecBuilder("").build() }
    }

    /**
     * Verifies that building a [PortSpec] fails when a negative value is used.
     *
     * The builder must reject the input for port number with an exception instead of producing an
     * incomplete specification that the API server would refuse later.
     */
    @Test
    fun testNegativePortNumber() {
        assertFailsWith<IllegalArgumentException> { IngressPortSpecBuilder(-1).build() }
    }

    /**
     * Verifies that building a [PortSpec] fails when the value exceeds the allowed maximum.
     *
     * The builder must reject the input for port number maximum with an exception instead of
     * producing an incomplete specification that the API server would refuse later.
     */
    @Test
    fun testPortNumberExceedsMaximum() {
        assertFailsWith<IllegalArgumentException> { IngressPortSpecBuilder(65536).build() }
    }

}
