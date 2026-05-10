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
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class ProbeSpecTest {

    @Test
    fun testHttpGetMaxContent() {
        val spec = ProbeSpecBuilder().apply {
            httpGet(9999) {
                path = "/path"
                host = "example.com"
                scheme = ProbeSpec.Scheme.HTTPS
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

        assertIs<ProbeSpec.HttpGetAction>(spec.action)
        assertEquals(9999, spec.action.port)
        assertEquals("/path", spec.action.path)
        assertEquals("example.com", spec.action.host)
        assertEquals(ProbeSpec.Scheme.HTTPS, spec.action.scheme)
        assertNotNull(spec.action.httpHeaders)
        assertEquals(mapOf("header" to "value"), spec.action.httpHeaders)
        assertNotNull(spec.initialDelaySeconds)
        assertEquals(10, spec.initialDelaySeconds.toSeconds())
        assertNotNull(spec.periodSeconds)
        assertEquals(30, spec.periodSeconds.toSeconds())
        assertNotNull(spec.timeoutSeconds)
        assertEquals(20, spec.timeoutSeconds.toSeconds())
        assertNotNull(spec.successThreshold)
        assertEquals(1, spec.successThreshold)
        assertNotNull(spec.failureThreshold)
        assertEquals(3, spec.failureThreshold)
        assertNotNull(spec.terminationGracePeriodSeconds)
        assertEquals(60, spec.terminationGracePeriodSeconds.toSeconds())
    }

    @Test
    fun testHttpGetMaxYaml() {
        val spec = ProbeSpecBuilder().apply {
            httpGet(9999) {
                path = "/path"
                host = "example.com"
                scheme = ProbeSpec.Scheme.HTTPS
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

        val actualJson = spec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testHttpGetMinContent() {
        val spec = ProbeSpecBuilder().apply {
            httpGet(9999)
        }.build()

        assertIs<ProbeSpec.HttpGetAction>(spec.action)
        assertEquals(9999, spec.action.port)
        assertNull(spec.action.path)
        assertNull(spec.action.host)
        assertNull(spec.action.scheme)
        assertNull(spec.action.httpHeaders)
        assertNull(spec.initialDelaySeconds)
        assertNull(spec.periodSeconds)
        assertNull(spec.timeoutSeconds)
        assertNull(spec.successThreshold)
        assertNull(spec.failureThreshold)
        assertNull(spec.terminationGracePeriodSeconds)
    }

    @Test
    fun testHttpGetMinYaml() {
        val spec = ProbeSpecBuilder().apply {
            httpGet(9999)
        }.build()

        JSONAssert.assertEquals("""{"httpGet":{"port":9999}}""", spec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testExecMaxContent() {
        val spec = ProbeSpecBuilder().apply {
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

        assertIs<ProbeSpec.ExecAction>(spec.action)
        assertEquals(listOf("demo", "help"), spec.action.command)
        assertNotNull(spec.initialDelaySeconds)
        assertEquals(10, spec.initialDelaySeconds.toSeconds())
        assertNotNull(spec.periodSeconds)
        assertEquals(30, spec.periodSeconds.toSeconds())
        assertNotNull(spec.timeoutSeconds)
        assertEquals(20, spec.timeoutSeconds.toSeconds())
        assertNotNull(spec.successThreshold)
        assertEquals(1, spec.successThreshold)
        assertNotNull(spec.failureThreshold)
        assertEquals(3, spec.failureThreshold)
        assertNotNull(spec.terminationGracePeriodSeconds)
        assertEquals(60, spec.terminationGracePeriodSeconds.toSeconds())
    }

    @Test
    fun testExecMaxYaml() {
        val spec = ProbeSpecBuilder().apply {
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

        val actualJson = spec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testExecMinContent() {
        val spec = ProbeSpecBuilder().apply {
            exec {
                command("demo")
            }
        }.build()

        assertIs<ProbeSpec.ExecAction>(spec.action)
        assertEquals(listOf("demo"), spec.action.command)
        assertNull(spec.initialDelaySeconds)
        assertNull(spec.periodSeconds)
        assertNull(spec.timeoutSeconds)
        assertNull(spec.successThreshold)
        assertNull(spec.failureThreshold)
        assertNull(spec.terminationGracePeriodSeconds)
    }

    @Test
    fun testExecMinYaml() {
        val spec = ProbeSpecBuilder().apply {
            exec {
                command("demo")
            }
        }.build()

        JSONAssert.assertEquals("""{"exec":{"command":["demo"]}}""", spec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testExecNoCommand() {
        assertThrows<IllegalArgumentException> {
            ProbeSpecBuilder().apply {
                exec { }
            }.build()
        }
    }

    @Test
    fun testTcpSocketMaxContent() {
        val spec = ProbeSpecBuilder().apply {
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

        assertIs<ProbeSpec.TCPSocketAction>(spec.action)
        assertEquals(9999, spec.action.port)
        assertEquals("example.com", spec.action.host)
        assertNotNull(spec.initialDelaySeconds)
        assertEquals(10, spec.initialDelaySeconds.toSeconds())
        assertNotNull(spec.periodSeconds)
        assertEquals(30, spec.periodSeconds.toSeconds())
        assertNotNull(spec.timeoutSeconds)
        assertEquals(20, spec.timeoutSeconds.toSeconds())
        assertNotNull(spec.successThreshold)
        assertEquals(1, spec.successThreshold)
        assertNotNull(spec.failureThreshold)
        assertEquals(3, spec.failureThreshold)
        assertNotNull(spec.terminationGracePeriodSeconds)
        assertEquals(60, spec.terminationGracePeriodSeconds.toSeconds())
    }

    @Test
    fun testTcpSocketMaxYaml() {
        val spec = ProbeSpecBuilder().apply {
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

        val actualJson = spec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testTcpSocketMinContent() {
        val spec = ProbeSpecBuilder().apply {
            tcpSocket(9999)
        }.build()

        assertIs<ProbeSpec.TCPSocketAction>(spec.action)
        assertEquals(9999, spec.action.port)
        assertNull(spec.action.host)
        assertNull(spec.initialDelaySeconds)
        assertNull(spec.periodSeconds)
        assertNull(spec.timeoutSeconds)
        assertNull(spec.successThreshold)
        assertNull(spec.failureThreshold)
        assertNull(spec.terminationGracePeriodSeconds)
    }

    @Test
    fun testTcpSocketMinYaml() {
        val spec = ProbeSpecBuilder().apply {
            tcpSocket(9999)
        }.build()

        JSONAssert.assertEquals("""{"tcpSocket":{"port":9999}}""", spec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testGrpcMaxContent() {
        val spec = ProbeSpecBuilder().apply {
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

        assertIs<ProbeSpec.GRPCAction>(spec.action)
        assertEquals(9999, spec.action.port)
        assertEquals("myService", spec.action.service)
        assertNotNull(spec.initialDelaySeconds)
        assertEquals(10, spec.initialDelaySeconds.toSeconds())
        assertNotNull(spec.periodSeconds)
        assertEquals(30, spec.periodSeconds.toSeconds())
        assertNotNull(spec.timeoutSeconds)
        assertEquals(20, spec.timeoutSeconds.toSeconds())
        assertNotNull(spec.successThreshold)
        assertEquals(1, spec.successThreshold)
        assertNotNull(spec.failureThreshold)
        assertEquals(3, spec.failureThreshold)
        assertNotNull(spec.terminationGracePeriodSeconds)
        assertEquals(60, spec.terminationGracePeriodSeconds.toSeconds())
    }

    @Test
    fun testGrpcMaxYaml() {
        val spec = ProbeSpecBuilder().apply {
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

        val actualJson = spec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testGrpcMinContent() {
        val spec = ProbeSpecBuilder().apply {
            grpc(9999)
        }.build()

        assertIs<ProbeSpec.GRPCAction>(spec.action)
        assertEquals(9999, spec.action.port)
        assertNull(spec.action.service)
        assertNull(spec.initialDelaySeconds)
        assertNull(spec.periodSeconds)
        assertNull(spec.timeoutSeconds)
        assertNull(spec.successThreshold)
        assertNull(spec.failureThreshold)
        assertNull(spec.terminationGracePeriodSeconds)
    }

    @Test
    fun testGrpcMinYaml() {
        val spec = ProbeSpecBuilder().apply {
            grpc(9999)
        }.build()

        JSONAssert.assertEquals("""{"grpc":{"port":9999}}""", spec.toJson(), JSONCompareMode.LENIENT)
    }

}