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

/**
 * Tests for [ContainerPortSpec], the network port a container exposes.
 */
class ContainerPortSpecTest {
    companion object {
        private val maxSpec = ContainerSpecBuilder.ContainerPortSpecBuilder(8080).apply {
            name = "http"
            protocol = Protocol.TCP
            hostPort = 18080
            hostIP = "127.0.0.1"
        }.build()

        private val minSpec = ContainerSpecBuilder.ContainerPortSpecBuilder(8080).build()
    }

    /**
     * Verifies that a container port with every optional field set is mapped onto the specification.
     *
     * The host mapping is part of it, which pins the pod to nodes where that host port is free.
     */
    @Test
    fun testMaxContent() {
        assertEquals("http", maxSpec.name)
        assertEquals(8080, maxSpec.containerPort)
        assertEquals(18080, maxSpec.hostPort)
        assertEquals("127.0.0.1", maxSpec.hostIP)
        assertEquals(Protocol.TCP, maxSpec.protocol)
    }

    /**
     * Verifies that a minimal container port only carries the port number.
     *
     * Without a name the port cannot be referenced by name, and without a host mapping the pod stays
     * freely schedulable, which is the desired default.
     */
    @Test
    fun testMinContent() {
        assertEquals(8080, minSpec.containerPort)
        assertNull(minSpec.name)
        assertNull(minSpec.hostPort)
        assertNull(minSpec.hostIP)
        assertNull(minSpec.protocol)
    }

    /**
     * Verifies that a fully configured container port is rendered with all of its fields.
     */
    @Test
    fun testMaxYaml() {
        val expectedJson = """
          |{
          |  "name": "http",
          |  "containerPort": 8080,
          |  "hostPort": 18080,
          |  "hostIP": "127.0.0.1",
          |  "protocol": "TCP"
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, maxSpec.toJson(), JSONCompareMode.STRICT)
    }

    /**
     * Verifies that a minimal container port omits every unset field from the rendered manifest.
     */
    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals("""{"containerPort":8080}""", minSpec.toJson(), JSONCompareMode.STRICT)
    }

    /**
     * Verifies that both port numbers must lie within the valid range.
     */
    @Test
    fun testRejectsOutOfRangePorts() {
        assertFailsWith<IllegalArgumentException> { ContainerPortSpec(null, 0, null, null, null) }
        assertFailsWith<IllegalArgumentException> { ContainerPortSpec(null, 65536, null, null, null) }
        assertFailsWith<IllegalArgumentException> { ContainerPortSpec(null, 8080, 0, null, null) }
    }

    /**
     * Verifies that a port name longer than the IANA limit is rejected.
     *
     * Kubernetes caps port names at 15 characters so they can be used as service names.
     */
    @Test
    fun testRejectsOverlongName() {
        assertFailsWith<IllegalArgumentException> {
            ContainerPortSpec("this-name-is-too-long", 8080, null, null, null)
        }
    }
}
