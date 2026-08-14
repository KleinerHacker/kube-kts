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
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.types.NamePortValue
import org.pcsoft.framework.kube.kts.api.types.NumberPortValue
import org.pcsoft.framework.kube.kts.api.types.ofPortName
import org.pcsoft.framework.kube.kts.api.types.ofPortNumber
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

/**
 * Tests that every field accepting a Kubernetes `IntOrString` port works in both flavours.
 *
 * Each field is exercised once with a port number and once with the name of a container port, and the
 * rendered YAML is checked to carry the matching scalar type.
 */
class NamedPortSpecTest {
    companion object {
        private val probeByNumber = ProbeSpecBuilder().apply {
            httpGet(8080) { path = "/healthz" }
        }.build()

        private val probeByName = ProbeSpecBuilder().apply {
            httpGet("http") { path = "/healthz" }
        }.build()

        private val tcpProbeByNumber = ProbeSpecBuilder().apply { tcpSocket(8080) }.build()

        private val tcpProbeByName = ProbeSpecBuilder().apply { tcpSocket("http") }.build()

        private val lifecycleByNumber = LifecycleSpecBuilder().apply {
            preStop { httpGet(8080) { path = "/shutdown" } }
        }.build()

        private val lifecycleByName = LifecycleSpecBuilder().apply {
            preStop { httpGet("http") { path = "/shutdown" } }
        }.build()

        private val portMappingByNumber = PortMappingSpecBuilder("http", 80).apply {
            targetPort = 8080
        }.build(ServiceSpec.Type.ClusterIP)

        private val portMappingByName = PortMappingSpecBuilder("http", 80).apply {
            targetPortName = "http"
        }.build(ServiceSpec.Type.ClusterIP)
    }

    /**
     * Verifies that an HTTP probe accepts a port number and renders it as a JSON number.
     */
    @Test
    fun testHttpProbeByNumber() {
        val action = assertIs<ProbeSpec.HttpGetAction>(probeByNumber.action)
        assertEquals(ofPortNumber(8080), action.port)
        assertIs<NumberPortValue>(action.port)

        JSONAssert.assertEquals(
            """{"httpGet":{"path":"/healthz","port":8080}}""",
            probeByNumber.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that an HTTP probe accepts a container port name and renders it as a JSON string.
     *
     * This is what allows a probe to stay valid when the container's port number changes.
     */
    @Test
    fun testHttpProbeByName() {
        val action = assertIs<ProbeSpec.HttpGetAction>(probeByName.action)
        assertEquals(ofPortName("http"), action.port)
        assertIs<NamePortValue>(action.port)

        JSONAssert.assertEquals(
            """{"httpGet":{"path":"/healthz","port":"http"}}""",
            probeByName.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a TCP probe accepts a port number and renders it as a JSON number.
     */
    @Test
    fun testTcpProbeByNumber() {
        val action = assertIs<ProbeSpec.TCPSocketAction>(tcpProbeByNumber.action)
        assertEquals(ofPortNumber(8080), action.port)

        JSONAssert.assertEquals(
            """{"tcpSocket":{"port":8080}}""",
            tcpProbeByNumber.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a TCP probe accepts a container port name and renders it as a JSON string.
     */
    @Test
    fun testTcpProbeByName() {
        val action = assertIs<ProbeSpec.TCPSocketAction>(tcpProbeByName.action)
        assertEquals(ofPortName("http"), action.port)

        JSONAssert.assertEquals(
            """{"tcpSocket":{"port":"http"}}""",
            tcpProbeByName.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a lifecycle hook accepts a port number and renders it as a JSON number.
     */
    @Test
    fun testLifecycleByNumber() {
        val action = assertIs<LifecycleSpec.HttpGetAction>(lifecycleByNumber.preStop)
        assertEquals(ofPortNumber(8080), action.port)

        JSONAssert.assertEquals(
            """{"preStop":{"httpGet":{"path":"/shutdown","port":8080}}}""",
            lifecycleByNumber.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a lifecycle hook accepts a container port name and renders it as a JSON string.
     */
    @Test
    fun testLifecycleByName() {
        val action = assertIs<LifecycleSpec.HttpGetAction>(lifecycleByName.preStop)
        assertEquals(ofPortName("http"), action.port)

        JSONAssert.assertEquals(
            """{"preStop":{"httpGet":{"path":"/shutdown","port":"http"}}}""",
            lifecycleByName.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a service port forwards to a numeric target port.
     */
    @Test
    fun testServicePortByNumber() {
        assertEquals(ofPortNumber(8080), portMappingByNumber.targetPort)

        JSONAssert.assertEquals(
            """{"name":"http","port":80,"targetPort":8080}""",
            portMappingByNumber.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a service port forwards to a named container port.
     *
     * Referencing the target by name decouples the service from the port numbers its pods listen on.
     */
    @Test
    fun testServicePortByName() {
        assertEquals(ofPortName("http"), portMappingByName.targetPort)

        JSONAssert.assertEquals(
            """{"name":"http","port":80,"targetPort":"http"}""",
            portMappingByName.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a service port rejects being given both a numeric and a named target.
     *
     * The two are mutually exclusive, so setting both must fail rather than silently pick one.
     */
    @Test
    fun testServicePortRejectsBothTargetForms() {
        assertFailsWith<IllegalArgumentException> {
            PortMappingSpecBuilder("http", 80).apply {
                targetPort = 8080
                targetPortName = "http"
            }.build(ServiceSpec.Type.ClusterIP)
        }
    }
}
