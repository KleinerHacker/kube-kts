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
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Suppress("DEPRECATION")
class ProbeSpecTest {
    companion object {
        private val httpGetMaxSpec = ProbeSpecBuilder().apply {
            httpGet(9999) {
                path = "/path"
                host = "example.com"
                scheme = ProtocolScheme.HTTPS
                httpHeaders {
                    httpHeader("header", "value")
                }
            }

            initialDelaySeconds = 10.seconds.toJavaDuration()
            periodSeconds = 30.seconds.toJavaDuration()
            timeoutSeconds = 20.seconds.toJavaDuration()
            successThreshold = 1
            failureThreshold = 3
            terminationGracePeriodSeconds = 60.seconds.toJavaDuration()
        }.build()

        private val httpGetMinSpec = ProbeSpecBuilder().apply {
            httpGet(9999)
        }.build()

        private val execMaxSpec = ProbeSpecBuilder().apply {
            exec {
                command("demo", "help")
            }

            initialDelaySeconds = 10.seconds.toJavaDuration()
            periodSeconds = 30.seconds.toJavaDuration()
            timeoutSeconds = 20.seconds.toJavaDuration()
            successThreshold = 1
            failureThreshold = 3
            terminationGracePeriodSeconds = 60.seconds.toJavaDuration()
        }.build()

        private val execMinSpec = ProbeSpecBuilder().apply {
            exec {
                command("demo")
            }
        }.build()

        private val tcpSocketMaxSpec = ProbeSpecBuilder().apply {
            tcpSocket(9999) {
                host = "example.com"
            }

            initialDelaySeconds = 10.seconds.toJavaDuration()
            periodSeconds = 30.seconds.toJavaDuration()
            timeoutSeconds = 20.seconds.toJavaDuration()
            successThreshold = 1
            failureThreshold = 3
            terminationGracePeriodSeconds = 60.seconds.toJavaDuration()
        }.build()

        private val tcpSocketMinSpec = ProbeSpecBuilder().apply {
            tcpSocket(9999)
        }.build()

        private val grpcMaxSpec = ProbeSpecBuilder().apply {
            grpc(9999) {
                service = "myService"
            }

            initialDelaySeconds = 10.seconds.toJavaDuration()
            periodSeconds = 30.seconds.toJavaDuration()
            timeoutSeconds = 20.seconds.toJavaDuration()
            successThreshold = 1
            failureThreshold = 3
            terminationGracePeriodSeconds = 60.seconds.toJavaDuration()
        }.build()

        private val grpcMinSpec = ProbeSpecBuilder().apply {
            grpc(9999)
        }.build()
    }

    /**
     * Verifies that the maximal ProbeSpec definition of the http get flavour is built into the
     * expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testHttpGetMaxContent() {
        assertIs<ProbeSpec.HttpGetAction>(httpGetMaxSpec.action)
        assertEquals(9999, httpGetMaxSpec.action.port)
        assertEquals("/path", httpGetMaxSpec.action.path)
        assertEquals("example.com", httpGetMaxSpec.action.host)
        assertEquals(ProtocolScheme.HTTPS, httpGetMaxSpec.action.scheme)
        assertNotNull(httpGetMaxSpec.action.httpHeaders)
        assertEquals(mapOf("header" to "value"), httpGetMaxSpec.action.httpHeaders)
        assertNotNull(httpGetMaxSpec.initialDelaySeconds)
        assertEquals(10, httpGetMaxSpec.initialDelaySeconds.toSeconds())
        assertNotNull(httpGetMaxSpec.periodSeconds)
        assertEquals(30, httpGetMaxSpec.periodSeconds.toSeconds())
        assertNotNull(httpGetMaxSpec.timeoutSeconds)
        assertEquals(20, httpGetMaxSpec.timeoutSeconds.toSeconds())
        assertNotNull(httpGetMaxSpec.successThreshold)
        assertEquals(1, httpGetMaxSpec.successThreshold)
        assertNotNull(httpGetMaxSpec.failureThreshold)
        assertEquals(3, httpGetMaxSpec.failureThreshold)
        assertNotNull(httpGetMaxSpec.terminationGracePeriodSeconds)
        assertEquals(60, httpGetMaxSpec.terminationGracePeriodSeconds.toSeconds())
    }

    /**
     * Verifies that the maximal ProbeSpec definition of the http get flavour is serialised into
     * the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testHttpGetMaxYaml() {
        val expectedJson = """{
            |  "httpGet": {
            |    "host": "example.com",
            |    "httpHeaders": {
            |      "header": "value"
            |    },
            |    "path": "/path",
            |    "port": 9999,
            |    "scheme": "HTTPS"
            |  },
            |  "failureThreshold": 3,
            |  "initialDelaySeconds": 10,
            |  "periodSeconds": 30,
            |  "successThreshold": 1,
            |  "timeoutSeconds": 20,
            |  "terminationGracePeriodSeconds": 60
            |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, httpGetMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal ProbeSpec definition of the http get flavour is built into the
     * expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testHttpGetMinContent() {
        assertIs<ProbeSpec.HttpGetAction>(httpGetMinSpec.action)
        assertEquals(9999, httpGetMinSpec.action.port)
        assertNull(httpGetMinSpec.action.path)
        assertNull(httpGetMinSpec.action.host)
        assertNull(httpGetMinSpec.action.scheme)
        assertNull(httpGetMinSpec.action.httpHeaders)
        assertNull(httpGetMinSpec.initialDelaySeconds)
        assertNull(httpGetMinSpec.periodSeconds)
        assertNull(httpGetMinSpec.timeoutSeconds)
        assertNull(httpGetMinSpec.successThreshold)
        assertNull(httpGetMinSpec.failureThreshold)
        assertNull(httpGetMinSpec.terminationGracePeriodSeconds)
    }

    /**
     * Verifies that the minimal ProbeSpec definition of the http get flavour is serialised into
     * the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testHttpGetMinYaml() {
        JSONAssert.assertEquals("""{"httpGet":{"port":9999}}""", httpGetMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the maximal ProbeSpec definition of the exec flavour is built into the
     * expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testExecMaxContent() {
        assertIs<ProbeSpec.ExecAction>(execMaxSpec.action)
        assertEquals(listOf("demo", "help"), execMaxSpec.action.command)
        assertNotNull(execMaxSpec.initialDelaySeconds)
        assertEquals(10, execMaxSpec.initialDelaySeconds.toSeconds())
        assertNotNull(execMaxSpec.periodSeconds)
        assertEquals(30, execMaxSpec.periodSeconds.toSeconds())
        assertNotNull(execMaxSpec.timeoutSeconds)
        assertEquals(20, execMaxSpec.timeoutSeconds.toSeconds())
        assertNotNull(execMaxSpec.successThreshold)
        assertEquals(1, execMaxSpec.successThreshold)
        assertNotNull(execMaxSpec.failureThreshold)
        assertEquals(3, execMaxSpec.failureThreshold)
        assertNotNull(execMaxSpec.terminationGracePeriodSeconds)
        assertEquals(60, execMaxSpec.terminationGracePeriodSeconds.toSeconds())
    }

    /**
     * Verifies that the maximal ProbeSpec definition of the exec flavour is serialised into the
     * expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testExecMaxYaml() {
        val expectedJson = """{
            |  "exec": {
            |    "command": [
            |      "demo",
            |      "help"
            |    ]
            |  },
            |  "initialDelaySeconds": 10,
            |  "periodSeconds": 30,
            |  "timeoutSeconds": 20,
            |  "successThreshold": 1,
            |  "failureThreshold": 3,
            |  "terminationGracePeriodSeconds": 60
            |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, execMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal ProbeSpec definition of the exec flavour is built into the
     * expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testExecMinContent() {
        assertIs<ProbeSpec.ExecAction>(execMinSpec.action)
        assertEquals(listOf("demo"), execMinSpec.action.command)
        assertNull(execMinSpec.initialDelaySeconds)
        assertNull(execMinSpec.periodSeconds)
        assertNull(execMinSpec.timeoutSeconds)
        assertNull(execMinSpec.successThreshold)
        assertNull(execMinSpec.failureThreshold)
        assertNull(execMinSpec.terminationGracePeriodSeconds)
    }

    /**
     * Verifies that the minimal ProbeSpec definition of the exec flavour is serialised into the
     * expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testExecMinYaml() {
        JSONAssert.assertEquals("""{"exec":{"command":["demo"]}}""", execMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that building a [ProbeSpec] fails when no command is given.
     *
     * The builder must reject the input for exec no command with an exception instead of producing
     * an incomplete specification that the API server would refuse later.
     */
    @Test
    fun testExecNoCommand() {
        assertFailsWith<IllegalArgumentException> {
            ProbeSpecBuilder().apply {
                exec { }
            }.build()
        }
    }

    /**
     * Verifies that the maximal ProbeSpec definition of the tcp socket flavour is built into the
     * expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testTcpSocketMaxContent() {
        assertIs<ProbeSpec.TCPSocketAction>(tcpSocketMaxSpec.action)
        assertEquals(9999, tcpSocketMaxSpec.action.port)
        assertEquals("example.com", tcpSocketMaxSpec.action.host)
        assertNotNull(tcpSocketMaxSpec.initialDelaySeconds)
        assertEquals(10, tcpSocketMaxSpec.initialDelaySeconds.toSeconds())
        assertNotNull(tcpSocketMaxSpec.periodSeconds)
        assertEquals(30, tcpSocketMaxSpec.periodSeconds.toSeconds())
        assertNotNull(tcpSocketMaxSpec.timeoutSeconds)
        assertEquals(20, tcpSocketMaxSpec.timeoutSeconds.toSeconds())
        assertNotNull(tcpSocketMaxSpec.successThreshold)
        assertEquals(1, tcpSocketMaxSpec.successThreshold)
        assertNotNull(tcpSocketMaxSpec.failureThreshold)
        assertEquals(3, tcpSocketMaxSpec.failureThreshold)
        assertNotNull(tcpSocketMaxSpec.terminationGracePeriodSeconds)
        assertEquals(60, tcpSocketMaxSpec.terminationGracePeriodSeconds.toSeconds())
    }

    /**
     * Verifies that the maximal ProbeSpec definition of the tcp socket flavour is serialised into
     * the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testTcpSocketMaxYaml() {
        val expectedJson = """{
            |  "tcpSocket": {
            |    "host": "example.com",
            |    "port": 9999
            |  },
            |  "failureThreshold": 3,
            |  "initialDelaySeconds": 10,
            |  "periodSeconds": 30,
            |  "successThreshold": 1,
            |  "timeoutSeconds": 20,
            |  "terminationGracePeriodSeconds": 60
            |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, tcpSocketMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal ProbeSpec definition of the tcp socket flavour is built into the
     * expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testTcpSocketMinContent() {
        assertIs<ProbeSpec.TCPSocketAction>(tcpSocketMinSpec.action)
        assertEquals(9999, tcpSocketMinSpec.action.port)
        assertNull(tcpSocketMinSpec.action.host)
        assertNull(tcpSocketMinSpec.initialDelaySeconds)
        assertNull(tcpSocketMinSpec.periodSeconds)
        assertNull(tcpSocketMinSpec.timeoutSeconds)
        assertNull(tcpSocketMinSpec.successThreshold)
        assertNull(tcpSocketMinSpec.failureThreshold)
        assertNull(tcpSocketMinSpec.terminationGracePeriodSeconds)
    }

    /**
     * Verifies that the minimal ProbeSpec definition of the tcp socket flavour is serialised into
     * the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testTcpSocketMinYaml() {
        JSONAssert.assertEquals("""{"tcpSocket":{"port":9999}}""", tcpSocketMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the maximal ProbeSpec definition of the grpc flavour is built into the
     * expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testGrpcMaxContent() {
        assertIs<ProbeSpec.GRPCAction>(grpcMaxSpec.action)
        assertEquals(9999, grpcMaxSpec.action.port)
        assertEquals("myService", grpcMaxSpec.action.service)
        assertNotNull(grpcMaxSpec.initialDelaySeconds)
        assertEquals(10, grpcMaxSpec.initialDelaySeconds.toSeconds())
        assertNotNull(grpcMaxSpec.periodSeconds)
        assertEquals(30, grpcMaxSpec.periodSeconds.toSeconds())
        assertNotNull(grpcMaxSpec.timeoutSeconds)
        assertEquals(20, grpcMaxSpec.timeoutSeconds.toSeconds())
        assertNotNull(grpcMaxSpec.successThreshold)
        assertEquals(1, grpcMaxSpec.successThreshold)
        assertNotNull(grpcMaxSpec.failureThreshold)
        assertEquals(3, grpcMaxSpec.failureThreshold)
        assertNotNull(grpcMaxSpec.terminationGracePeriodSeconds)
        assertEquals(60, grpcMaxSpec.terminationGracePeriodSeconds.toSeconds())
    }

    /**
     * Verifies that the maximal ProbeSpec definition of the grpc flavour is serialised into the
     * expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testGrpcMaxYaml() {
        val expectedJson = """{
            |  "grpc": {
            |    "port": 9999,
            |    "service": "myService"
            |  },
            |  "failureThreshold": 3,
            |  "initialDelaySeconds": 10,
            |  "periodSeconds": 30,
            |  "successThreshold": 1,
            |  "timeoutSeconds": 20,
            |  "terminationGracePeriodSeconds": 60
            |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, grpcMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal ProbeSpec definition of the grpc flavour is built into the
     * expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testGrpcMinContent() {
        assertIs<ProbeSpec.GRPCAction>(grpcMinSpec.action)
        assertEquals(9999, grpcMinSpec.action.port)
        assertNull(grpcMinSpec.action.service)
        assertNull(grpcMinSpec.initialDelaySeconds)
        assertNull(grpcMinSpec.periodSeconds)
        assertNull(grpcMinSpec.timeoutSeconds)
        assertNull(grpcMinSpec.successThreshold)
        assertNull(grpcMinSpec.failureThreshold)
        assertNull(grpcMinSpec.terminationGracePeriodSeconds)
    }

    /**
     * Verifies that the minimal ProbeSpec definition of the grpc flavour is serialised into the
     * expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testGrpcMinYaml() {
        JSONAssert.assertEquals("""{"grpc":{"port":9999}}""", grpcMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

}
