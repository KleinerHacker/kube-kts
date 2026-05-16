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
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class LifecycleSpecTest {

    @Test
    fun testExecMaxContent() {
        val lifecycleSpec = LifecycleSpecBuilder().apply {
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

        assertIs<LifecycleSpec.ExecAction>(lifecycleSpec.postStart)
        assertEquals(listOf("echo", "Hello, World!"), lifecycleSpec.postStart.command)
        assertIs<LifecycleSpec.ExecAction>(lifecycleSpec.preStop)
        assertEquals(listOf("echo", "Goodbye, World!"), lifecycleSpec.preStop.command)
    }

    @Test
    fun testExecMaxYaml() {
        val lifecycleSpec = LifecycleSpecBuilder().apply {
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

        val actualJson = lifecycleSpec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testExecMinContent() {
        val lifecycleSpec = LifecycleSpecBuilder().apply {
            postStart {
                exec { command("echo") }
            }
            preStop {
                exec { command("run") }
            }
        }.build()

        assertIs<LifecycleSpec.ExecAction>(lifecycleSpec.postStart)
        assertEquals(listOf("echo"), lifecycleSpec.postStart.command)
        assertIs<LifecycleSpec.ExecAction>(lifecycleSpec.preStop)
        assertEquals(listOf("run"), lifecycleSpec.preStop.command)
    }

    @Test
    fun testExecMinYaml() {
        val lifecycleSpec = LifecycleSpecBuilder().apply {
            postStart {
                exec { command("echo") }
            }
            preStop {
                exec { command("run") }
            }
        }.build()

        val actualJson = lifecycleSpec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testExecMissingCommand() {
        assertThrows<IllegalArgumentException> {
            LifecycleSpecBuilder().apply {
                postStart {
                    exec {  }
                }
            }.build()
        }
    }

    @Test
    fun testHttpGetMaxContent() {
        val lifecycleSpec = LifecycleSpecBuilder().apply {
            postStart {
                httpGet(9999) {
                    path="/health"
                    host="localhost"
                    scheme=ProtocolScheme.HTTPS
                    httpHeaders {
                        httpHeader("X-Custom-Header", "value1")
                        httpHeader("Authorization", "Bearer token")
                    }
                }
            }
            preStop {
                httpGet(8888) {
                    path="/shutdown"
                    host="localhost"
                    scheme=ProtocolScheme.HTTP
                    httpHeaders {
                        httpHeader("X-Shutdown", "graceful")
                    }
                }
            }
        }.build()

        assertIs<LifecycleSpec.HttpGetAction>(lifecycleSpec.postStart)
        assertEquals("/health", lifecycleSpec.postStart.path)
        assertEquals(9999, lifecycleSpec.postStart.port)
        assertEquals("localhost", lifecycleSpec.postStart.host)
        assertEquals(ProtocolScheme.HTTPS, lifecycleSpec.postStart.scheme)
        assertEquals(
            mapOf("X-Custom-Header" to "value1", "Authorization" to "Bearer token"),
            lifecycleSpec.postStart.httpHeaders
        )
        assertIs<LifecycleSpec.HttpGetAction>(lifecycleSpec.preStop)
        assertEquals("/shutdown", lifecycleSpec.preStop.path)
        assertEquals(8888, lifecycleSpec.preStop.port)
        assertEquals("localhost", lifecycleSpec.preStop.host)
        assertEquals(ProtocolScheme.HTTP, lifecycleSpec.preStop.scheme)
        assertEquals(mapOf("X-Shutdown" to "graceful"), lifecycleSpec.preStop.httpHeaders)
    }

    @Test
    fun testHttpGetMaxYaml() {
        val lifecycleSpec = LifecycleSpecBuilder().apply {
            postStart {
                httpGet(9999) {
                    path="/health"
                    host="localhost"
                    scheme=ProtocolScheme.HTTPS
                    httpHeaders {
                        httpHeader("X-Custom-Header", "value1")
                        httpHeader("Authorization", "Bearer token")
                    }
                }
            }
            preStop {
                httpGet(8888) {
                    path="/shutdown"
                    host="localhost"
                    scheme=ProtocolScheme.HTTP
                    httpHeaders {
                        httpHeader("X-Shutdown", "graceful")
                    }
                }
            }
        }.build()

        val actualJson = lifecycleSpec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testHttpGetMinContent() {
        val lifecycleSpec = LifecycleSpecBuilder().apply {
            postStart {
                httpGet(9999)
            }
            preStop {
                httpGet(8888)
            }
        }.build()

        assertIs<LifecycleSpec.HttpGetAction>(lifecycleSpec.postStart)
        assertEquals(9999, lifecycleSpec.postStart.port)
        assertIs<LifecycleSpec.HttpGetAction>(lifecycleSpec.preStop)
        assertEquals(8888, lifecycleSpec.preStop.port)
    }

    @Test
    fun testHttpGetMinYaml() {
        val lifecycleSpec = LifecycleSpecBuilder().apply {
            postStart {
                httpGet(9999)
            }
            preStop {
                httpGet(8888)
            }
        }.build()

        val actualJson = lifecycleSpec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testSleepMaxContent() {
        val lifecycleSpec = LifecycleSpecBuilder().apply {
            postStart {
                sleep(10.seconds.toJavaDuration())
            }
            preStop {
                sleep(5.seconds.toJavaDuration())
            }
        }.build()

        assertIs<LifecycleSpec.SleepAction>(lifecycleSpec.postStart)
        assertEquals(10.seconds.toJavaDuration(), lifecycleSpec.postStart.seconds)
        assertIs<LifecycleSpec.SleepAction>(lifecycleSpec.preStop)
        assertEquals(5.seconds.toJavaDuration(), lifecycleSpec.preStop.seconds)
    }

    @Test
    fun testSleepMaxYaml() {
        val lifecycleSpec = LifecycleSpecBuilder().apply {
            postStart {
                sleep(10.seconds.toJavaDuration())
            }
            preStop {
                sleep(5.seconds.toJavaDuration())
            }
        }.build()

        val actualJson = lifecycleSpec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        val lifecycleSpec = LifecycleSpecBuilder().build()

        assertNull(lifecycleSpec.postStart)
        assertNull(lifecycleSpec.preStop)
    }

    @Test
    fun testMinYaml() {
        val lifecycleSpec = LifecycleSpecBuilder().build()

        assertEquals("""{}""", lifecycleSpec.toJson())
    }

}