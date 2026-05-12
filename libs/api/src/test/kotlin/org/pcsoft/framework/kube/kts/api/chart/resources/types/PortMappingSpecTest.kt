/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PortMappingSpecTest {

    @Test
    fun testMaxContent() {
        val spec = PortMappingSpecBuilder("name", 9999).apply {
            targetPort = 8888
            nodePort = 7777
            appProtocol = "http"
            protocol = Protocol.UDP
        }.build(ServiceSpec.Type.NodePort)

        assertEquals("name", spec.name)
        assertEquals(9999, spec.port)
        assertEquals(8888, spec.targetPort)
        assertEquals(7777, spec.nodePort)
        assertEquals("http", spec.appProtocol)
        assertEquals(Protocol.UDP, spec.protocol)
    }

    @Test
    fun testMaxYaml() {
        val spec = PortMappingSpecBuilder("name", 9999).apply {
            this.targetPort = 8888
            this.nodePort = 7777
            this.appProtocol = "http"
            this.protocol = Protocol.UDP
        }.build(ServiceSpec.Type.NodePort)

        val actualJson = spec.toJson()
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

    @Test
    fun testMinContent() {
        val spec = PortMappingSpecBuilder("name", 9999).apply {
        }.build(ServiceSpec.Type.LoadBalancer)

        assertEquals("name", spec.name)
        assertEquals(9999, spec.port)
        assertNull(spec.targetPort)
        assertNull(spec.nodePort)
        assertNull(spec.appProtocol)
        assertNull(spec.protocol)

    }

    @Test
    fun testMinYaml() {
        val spec = PortMappingSpecBuilder("name", 9999).build(ServiceSpec.Type.LoadBalancer)

        val actualJson = spec.toJson()
        val expectedJson = """{"name":"name","port":9999}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testNodePortNotFound() {
        assertThrows<IllegalArgumentException> {
            PortMappingSpecBuilder("name", 9999).build(ServiceSpec.Type.NodePort)
        }
    }

}