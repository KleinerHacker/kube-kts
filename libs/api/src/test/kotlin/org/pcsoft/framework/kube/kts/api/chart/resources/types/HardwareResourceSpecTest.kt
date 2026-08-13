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
    companion object {
        private val maxSpec = HardwareResourceSpecBuilder().apply {
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

        private val minSpec = HardwareResourceSpecBuilder().build()
    }

    /**
     * Verifies that the maximal HardwareResourceSpec definition is built into the expected spec
     * object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        val limits = assertNotNull(maxSpec.limits)
        assertNotNull(limits.cpu)
        assertEquals(0.01f.cpu, limits.cpu)
        assertNotNull(limits.memory)
        assertEquals(256.miBytes, limits.memory)
        assertNotNull(limits.ephemeralStorage)
        assertEquals(1.giBytes, limits.ephemeralStorage)
        assertNotNull(limits.extendedResources)
        assertEquals(mapOf("nvidia.com/gpu" to "1"), limits.extendedResources)

        val requests = assertNotNull(maxSpec.requests)
        assertNull(requests.cpu)
        assertNull(requests.memory)
        assertNull(requests.ephemeralStorage)
        assertNull(requests.extendedResources)
    }

    /**
     * Verifies that the maximal HardwareResourceSpec definition is serialised into the expected
     * YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "limits": {
          |    "cpu": "10m",
          |    "memory": "256Mi",
          |    "ephemeral-storage": "1Gi",
          |    "nvidia.com/gpu": "1"
          |  },
          |  "requests": {}
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal HardwareResourceSpec definition is built into the expected spec
     * object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertNull(minSpec.limits)
        assertNull(minSpec.requests)
    }

    /**
     * Verifies that the minimal HardwareResourceSpec definition is serialised into the expected
     * YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        assertEquals("""{}""", minSpec.toJson())
    }

    /**
     * Verifies that building a [HardwareResourceSpec] fails when an invalid combination of values
     * is used.
     *
     * The builder must reject the input for cpu limit smaller than request with an exception
     * instead of producing an incomplete specification that the API server would refuse later.
     */
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

    /**
     * Verifies that building a [HardwareResourceSpec] fails when an invalid combination of values
     * is used.
     *
     * The builder must reject the input for memory limit smaller than request with an exception
     * instead of producing an incomplete specification that the API server would refuse later.
     */
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

    /**
     * Verifies that building a [HardwareResourceSpec] fails when an invalid combination of values
     * is used.
     *
     * The builder must reject the input for ephemeral storage limit smaller than request with an
     * exception instead of producing an incomplete specification that the API server would refuse
     * later.
     */
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
