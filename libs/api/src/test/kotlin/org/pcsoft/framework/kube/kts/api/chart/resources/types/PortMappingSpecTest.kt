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

import org.pcsoft.framework.kube.kts.api.types.ofPortNumber
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PortMappingSpecTest {
    companion object {
        private val maxSpec = PortMappingSpecBuilder("name", 9999).apply {
            targetPort = 8888
            nodePort = 7777
            appProtocol = "http"
            protocol = Protocol.UDP
        }.build(ServiceSpec.Type.NodePort)

        private val minSpec = PortMappingSpecBuilder("name", 9999).build(ServiceSpec.Type.LoadBalancer)
    }

    /**
     * Verifies that the maximal PortMappingSpec definition is built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertEquals("name", maxSpec.name)
        assertEquals(9999, maxSpec.port)
        assertEquals(ofPortNumber(8888), maxSpec.targetPort)
        assertEquals(7777, maxSpec.nodePort)
        assertEquals("http", maxSpec.appProtocol)
        assertEquals(Protocol.UDP, maxSpec.protocol)
    }

    /**
     * Verifies that the maximal PortMappingSpec definition is serialised into the expected YAML
     * document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "name": "name",
          |  "port": 9999,
          |  "targetPort": 8888,
          |  "nodePort": 7777,
          |  "appProtocol": "http",
          |  "protocol": "UDP"
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal PortMappingSpec definition is built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertEquals("name", minSpec.name)
        assertEquals(9999, minSpec.port)
        assertNull(minSpec.targetPort)
        assertNull(minSpec.nodePort)
        assertNull(minSpec.appProtocol)
        assertNull(minSpec.protocol)
    }

    /**
     * Verifies that the minimal PortMappingSpec definition is serialised into the expected YAML
     * document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        val actualJson = minSpec.toJson()
        val expectedJson = """{"name":"name","port":9999}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that building a [PortMappingSpec] fails when a referenced entry does not exist.
     *
     * The builder must reject the input for node port not found with an exception instead of
     * producing an incomplete specification that the API server would refuse later.
     */
    @Test
    fun testNodePortNotFound() {
        assertFailsWith<IllegalArgumentException> {
            PortMappingSpecBuilder("name", 9999).build(ServiceSpec.Type.NodePort)
        }
    }

}
