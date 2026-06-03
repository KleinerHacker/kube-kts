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
import org.pcsoft.framework.kube.kts.api.types.cpu
import org.pcsoft.framework.kube.kts.api.types.giBytes
import org.pcsoft.framework.kube.kts.api.types.miBytes
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HardwareResourceSpecTest {

    @Test
    fun testMaxContent() {
        val spec = HardwareResourceSpecBuilder().apply {
            limits {
                cpu = 0.01f.cpu
                memory = 256.miBytes
                ephemeralStorage = 1.giBytes
                extendedResources {
                    extendedResource("nvidia.com/gpu", "1")
                }
            }
            requests {
            }
        }.build()

        assertNotNull(spec.limits)
        assertNotNull(spec.limits!!.cpu)
        assertEquals(0.01f.cpu, spec.limits!!.cpu)
        assertNotNull(spec.limits!!.memory)
        assertEquals(256.miBytes, spec.limits!!.memory)
        assertNotNull(spec.limits!!.ephemeralStorage)
        assertEquals(1.giBytes, spec.limits!!.ephemeralStorage)
        assertNotNull(spec.limits!!.extendedResources)
        assertEquals(mapOf("nvidia.com/gpu" to "1"), spec.limits!!.extendedResources)

        assertNotNull(spec.requests)
        assertNull(spec.requests!!.cpu)
        assertNull(spec.requests!!.memory)
        assertNull(spec.requests!!.ephemeralStorage)
        assertNull(spec.requests!!.extendedResources)
    }

    @Test
    fun testMaxYaml() {
        val spec = HardwareResourceSpecBuilder().apply {
            limits {
                cpu = 0.01f.cpu
                memory = 256.miBytes
                ephemeralStorage = 1.giBytes
                extendedResources {
                    extendedResource("nvidia.com/gpu", "1")
                }
            }
            requests {
            }
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{
          |  "limits": {
          |    "cpu": "10m",
          |    "memory": "256Mi",
          |    "ephemeral-storage": "1Gi",
          |    "nvidia.com/gpu": "1"
          |  },
          |  "requests": {}
          |}""".trimMargin()

        println(actualJson)
        println(expectedJson)

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        val spec = HardwareResourceSpecBuilder().build()

        assertNull(spec.limits)
        assertNull(spec.requests)
    }

    @Test
    fun testMinYaml() {
        val spec = HardwareResourceSpecBuilder().build()

        assertEquals("""{}""", spec.toJson())
    }

    @Test
    fun testInvalidCpuLimitSmallerThanRequest() {
        assertFailsWith<IllegalArgumentException> {
            HardwareResourceSpecBuilder().apply {
                limits {
                    cpu = 0.01f.cpu
                }
                requests {
                    cpu = 0.05f.cpu
                }
            }.build()
        }
    }

    @Test
    fun testInvalidMemoryLimitSmallerThanRequest() {
        assertFailsWith<IllegalArgumentException> {
            HardwareResourceSpecBuilder().apply {
                limits {
                    memory = 128.miBytes
                }
                requests {
                    memory = 256.miBytes
                }
            }.build()
        }
    }

    @Test
    fun testInvalidEphemeralStorageLimitSmallerThanRequest() {
        assertFailsWith<IllegalArgumentException> {
            HardwareResourceSpecBuilder().apply {
                limits {
                    ephemeralStorage = 512.miBytes
                }
                requests {
                    ephemeralStorage = 1.giBytes
                }
            }.build()
        }
    }


}