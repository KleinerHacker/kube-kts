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
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class LifecycleSpecTest {
    companion object {
        private val execMaxSpec = LifecycleSpecBuilder().apply {
            postStart {
                exec {
                    command("echo", "Hello, World!")
                }
            }
            preStop {
                exec {
                    command("echo", "Goodbye, World!")
                }
            }
        }.build()

        private val execMinSpec = LifecycleSpecBuilder().apply {
            postStart {
                exec { command("echo") }
            }
            preStop {
                exec { command("run") }
            }
        }.build()

        private val httpGetMaxSpec = LifecycleSpecBuilder().apply {
            postStart {
                httpGet(9999) {
                    path = "/health"
                    host = "localhost"
                    scheme = ProtocolScheme.HTTPS
                    httpHeaders {
                        httpHeader("X-Custom-Header", "value1")
                        httpHeader("Authorization", "Bearer token")
                    }
                }
            }
            preStop {
                httpGet(8888) {
                    path = "/shutdown"
                    host = "localhost"
                    scheme = ProtocolScheme.HTTP
                    httpHeaders {
                        httpHeader("X-Shutdown", "graceful")
                    }
                }
            }
        }.build()

        private val httpGetMinSpec = LifecycleSpecBuilder().apply {
            postStart {
                httpGet(9999)
            }
            preStop {
                httpGet(8888)
            }
        }.build()

        private val sleepMaxSpec = LifecycleSpecBuilder().apply {
            postStart {
                sleep(10.seconds.toJavaDuration())
            }
            preStop {
                sleep(5.seconds.toJavaDuration())
            }
        }.build()

        private val minSpec = LifecycleSpecBuilder().build()
    }

    @Test
    fun testExecMaxContent() {
        assertIs<LifecycleSpec.ExecAction>(execMaxSpec.postStart)
        assertEquals(listOf("echo", "Hello, World!"), execMaxSpec.postStart.command)
        assertIs<LifecycleSpec.ExecAction>(execMaxSpec.preStop)
        assertEquals(listOf("echo", "Goodbye, World!"), execMaxSpec.preStop.command)
    }

    @Test
    fun testExecMaxYaml() {
        val expectedJson = """{
        |  "postStart": {
        |    "exec": {
        |      "command": [
        |        "echo",
        |        "Hello, World!"
        |      ]
        |    }
        |  },
        |  "preStop": {
        |    "exec": {
        |      "command": [
        |        "echo",
        |        "Goodbye, World!"
        |      ]
        |    }
        |  }
        |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, execMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testExecMinContent() {
        assertIs<LifecycleSpec.ExecAction>(execMinSpec.postStart)
        assertEquals(listOf("echo"), execMinSpec.postStart.command)
        assertIs<LifecycleSpec.ExecAction>(execMinSpec.preStop)
        assertEquals(listOf("run"), execMinSpec.preStop.command)
    }

    @Test
    fun testExecMinYaml() {
        val expectedJson = """{
        |  "postStart": {
        |    "exec": {
        |      "command": [
        |        "echo"
        |      ]
        |    }
        |  },
        |  "preStop": {
        |    "exec": {
        |      "command": [
        |        "run"
        |      ]
        |    }
        |  }
        |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, execMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testExecMissingCommand() {
        assertFailsWith<IllegalArgumentException> {
            LifecycleSpecBuilder().apply {
                postStart {
                    exec { }
                }
            }.build()
        }
    }

    @Test
    fun testHttpGetMaxContent() {
        assertIs<LifecycleSpec.HttpGetAction>(httpGetMaxSpec.postStart)
        assertEquals("/health", httpGetMaxSpec.postStart.path)
        assertEquals(9999, httpGetMaxSpec.postStart.port)
        assertEquals("localhost", httpGetMaxSpec.postStart.host)
        assertEquals(ProtocolScheme.HTTPS, httpGetMaxSpec.postStart.scheme)
        assertEquals(
            mapOf("X-Custom-Header" to "value1", "Authorization" to "Bearer token"),
            httpGetMaxSpec.postStart.httpHeaders
        )
        assertIs<LifecycleSpec.HttpGetAction>(httpGetMaxSpec.preStop)
        assertEquals("/shutdown", httpGetMaxSpec.preStop.path)
        assertEquals(8888, httpGetMaxSpec.preStop.port)
        assertEquals("localhost", httpGetMaxSpec.preStop.host)
        assertEquals(ProtocolScheme.HTTP, httpGetMaxSpec.preStop.scheme)
        assertEquals(mapOf("X-Shutdown" to "graceful"), httpGetMaxSpec.preStop.httpHeaders)
    }

    @Test
    fun testHttpGetMaxYaml() {
        val expectedJson = """{
        |  "postStart": {
        |    "httpGet": {
        |      "path": "/health",
        |      "port": 9999,
        |      "host": "localhost",
        |      "scheme": "HTTPS",
        |      "httpHeaders": {
        |        "X-Custom-Header": "value1",
        |        "Authorization": "Bearer token"
        |      }
        |    }
        |  },
        |  "preStop": {
        |    "httpGet": {
        |      "path": "/shutdown",
        |      "port": 8888,
        |      "host": "localhost",
        |      "scheme": "HTTP",
        |      "httpHeaders": {
        |        "X-Shutdown": "graceful"
        |      }
        |    }
        |  }
        |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, httpGetMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testHttpGetMinContent() {
        assertIs<LifecycleSpec.HttpGetAction>(httpGetMinSpec.postStart)
        assertEquals(9999, httpGetMinSpec.postStart.port)
        assertIs<LifecycleSpec.HttpGetAction>(httpGetMinSpec.preStop)
        assertEquals(8888, httpGetMinSpec.preStop.port)
    }

    @Test
    fun testHttpGetMinYaml() {
        val expectedJson = """{
        |  "postStart": {
        |    "httpGet": {
        |      "port": 9999
        |    }
        |  },
        |  "preStop": {
        |    "httpGet": {
        |      "port": 8888
        |    }
        |  }
        |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, httpGetMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testSleepMaxContent() {
        assertIs<LifecycleSpec.SleepAction>(sleepMaxSpec.postStart)
        assertEquals(10.seconds.toJavaDuration(), sleepMaxSpec.postStart.seconds)
        assertIs<LifecycleSpec.SleepAction>(sleepMaxSpec.preStop)
        assertEquals(5.seconds.toJavaDuration(), sleepMaxSpec.preStop.seconds)
    }

    @Test
    fun testSleepMaxYaml() {
        val expectedJson = """{
        |  "postStart": {
        |    "sleep": {
        |      "seconds": 10
        |    }
        |  },
        |  "preStop": {
        |    "sleep": {
        |      "seconds": 5
        |    }
        |  }
        |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, sleepMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        assertNull(minSpec.postStart)
        assertNull(minSpec.preStop)
    }

    @Test
    fun testMinYaml() {
        assertEquals("""{}""", minSpec.toJson())
    }

}
