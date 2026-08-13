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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AffinitySpecTest {
    companion object {
        private val maxSpec = AffinitySpecBuilder().apply {
            nodeAffinity {
                preferredDuringSchedulingIgnoredDuringExecution {
                    term(99) {
                        preference {

                        }
                    }
                }
                requiredDuringSchedulingIgnoredDuringExecution {
                    term {

                    }
                }
            }
            podAffinity {
                preferredDuringSchedulingIgnoredDuringExecution {
                    term(99) {
                        podAffinityTerm("key") {

                        }
                    }
                }
                requiredDuringSchedulingIgnoredDuringExecution {
                    term("key") {

                    }
                }
            }
            podAntiAffinity {
                preferredDuringSchedulingIgnoredDuringExecution {
                    term(99) {
                        podAffinityTerm("key") {

                        }
                    }
                }
                requiredDuringSchedulingIgnoredDuringExecution {
                    term("key") {

                    }
                }
            }
        }.build()

        private val minSpec = AffinitySpecBuilder().build()
    }

    /**
     * Verifies that the maximal AffinitySpec definition is built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertNotNull(maxSpec.nodeAffinity)
        assertNotNull(maxSpec.nodeAffinity.preferredDuringSchedulingIgnoredDuringExecution)
        assertEquals(1, maxSpec.nodeAffinity.preferredDuringSchedulingIgnoredDuringExecution.size)
        assertEquals(99, maxSpec.nodeAffinity.preferredDuringSchedulingIgnoredDuringExecution[0].weight)
        assertNotNull(maxSpec.nodeAffinity.requiredDuringSchedulingIgnoredDuringExecution)
        assertEquals(1, maxSpec.nodeAffinity.requiredDuringSchedulingIgnoredDuringExecution.size)
        assertNotNull(maxSpec.podAffinity)
        assertNotNull(maxSpec.podAffinity.preferredDuringSchedulingIgnoredDuringExecution)
        assertEquals(1, maxSpec.podAffinity.preferredDuringSchedulingIgnoredDuringExecution.size)
        assertEquals(99, maxSpec.podAffinity.preferredDuringSchedulingIgnoredDuringExecution[0].weight)
        assertEquals("key", maxSpec.podAffinity.preferredDuringSchedulingIgnoredDuringExecution[0].podAffinityTerm.topologyKey)
        assertNotNull(maxSpec.podAffinity.requiredDuringSchedulingIgnoredDuringExecution)
        assertEquals(1, maxSpec.podAffinity.requiredDuringSchedulingIgnoredDuringExecution.size)
        assertEquals("key", maxSpec.podAffinity.requiredDuringSchedulingIgnoredDuringExecution[0].topologyKey)
        assertNotNull(maxSpec.podAntiAffinity)
        assertNotNull(maxSpec.podAntiAffinity.preferredDuringSchedulingIgnoredDuringExecution)
        assertEquals(1, maxSpec.podAntiAffinity.preferredDuringSchedulingIgnoredDuringExecution.size)
        assertEquals("key", maxSpec.podAntiAffinity.preferredDuringSchedulingIgnoredDuringExecution[0].podAffinityTerm.topologyKey)
        assertNotNull(maxSpec.podAntiAffinity.requiredDuringSchedulingIgnoredDuringExecution)
        assertEquals(1, maxSpec.podAntiAffinity.requiredDuringSchedulingIgnoredDuringExecution.size)
        assertEquals("key", maxSpec.podAntiAffinity.requiredDuringSchedulingIgnoredDuringExecution[0].topologyKey)
    }

    /**
     * Verifies that the maximal AffinitySpec definition is serialised into the expected YAML
     * document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "nodeAffinity": {
          |    "preferredDuringSchedulingIgnoredDuringExecution": [
          |      {
          |        "weight": 99,
          |        "preference": {
          |        }
          |      }
          |    ],
          |    "requiredDuringSchedulingIgnoredDuringExecution": {
          |      "nodeSelectorTerms": [
          |        {
          |        }
          |      ]
          |    }
          |  },
          |  "podAffinity": {
          |    "preferredDuringSchedulingIgnoredDuringExecution": [
          |      {
          |        "weight": 99,
          |        "podAffinityTerm": {
          |          "topologyKey": "key"
          |        }
          |      }
          |    ],
          |    "requiredDuringSchedulingIgnoredDuringExecution": [
          |      {
          |        "topologyKey": "key"
          |      }
          |    ]
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal AffinitySpec definition is built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertNull(minSpec.nodeAffinity)
        assertNull(minSpec.podAffinity)
        assertNull(minSpec.podAntiAffinity)
    }

    /**
     * Verifies that the minimal AffinitySpec definition is serialised into the expected YAML
     * document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        val actualJson = minSpec.toJson()
        val expectedJson = "{}"

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

}

class NodeAffinitySpecTest {
    companion object {
        private val maxSpec = NodeAffinitySpecBuilder().apply {
            requiredDuringSchedulingIgnoredDuringExecution {
                term {
                }
            }
            preferredDuringSchedulingIgnoredDuringExecution {
                term(99) {
                    preference {

                    }
                }
            }
        }.build()

        private val minSpec = NodeAffinitySpecBuilder().apply {
            requiredDuringSchedulingIgnoredDuringExecution {

            }
            preferredDuringSchedulingIgnoredDuringExecution {

            }
        }.build()
    }

    /**
     * Verifies that the maximal NodeAffinitySpec definition is built into the expected spec
     * object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertNotNull(maxSpec.requiredDuringSchedulingIgnoredDuringExecution)
        assertEquals(1, maxSpec.requiredDuringSchedulingIgnoredDuringExecution.size)

        assertNotNull(maxSpec.preferredDuringSchedulingIgnoredDuringExecution)
        assertEquals(1, maxSpec.preferredDuringSchedulingIgnoredDuringExecution.size)
    }

    /**
     * Verifies that the maximal NodeAffinitySpec definition is serialised into the expected YAML
     * document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "requiredDuringSchedulingIgnoredDuringExecution": {
          |    "nodeSelectorTerms": [
          |      {
          |      }
          |    ]
          |  },
          |  "preferredDuringSchedulingIgnoredDuringExecution": [
          |    {
          |      "weight": 99,
          |      "preference": {
          |      }
          |    }
          |  ]
          |}
        """.trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal NodeAffinitySpec definition is built into the expected spec
     * object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertNull(minSpec.requiredDuringSchedulingIgnoredDuringExecution)
        assertNull(minSpec.preferredDuringSchedulingIgnoredDuringExecution)
    }

    /**
     * Verifies that the minimal NodeAffinitySpec definition is serialised into the expected YAML
     * document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        val actualJson = minSpec.toJson()
        val expectedJson = "{}"

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

}

class PodAffinitySpecTest {
    companion object {
        private val maxSpec = PodAffinitySpecBuilder().apply {
            requiredDuringSchedulingIgnoredDuringExecution {
                term("key") {

                }
            }
            preferredDuringSchedulingIgnoredDuringExecution {
                term(99) {
                    podAffinityTerm("key") {

                    }
                }
            }
        }.build()

        private val minSpec = PodAffinitySpecBuilder().apply {
            requiredDuringSchedulingIgnoredDuringExecution {

            }
            preferredDuringSchedulingIgnoredDuringExecution {

            }
        }.build()
    }

    /**
     * Verifies that the maximal PodAffinitySpec definition is built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertNotNull(maxSpec.requiredDuringSchedulingIgnoredDuringExecution)
        assertEquals(1, maxSpec.requiredDuringSchedulingIgnoredDuringExecution.size)
        assertEquals("key", maxSpec.requiredDuringSchedulingIgnoredDuringExecution[0].topologyKey)
        assertNotNull(maxSpec.preferredDuringSchedulingIgnoredDuringExecution)
        assertEquals(1, maxSpec.preferredDuringSchedulingIgnoredDuringExecution.size)
        assertEquals(99, maxSpec.preferredDuringSchedulingIgnoredDuringExecution[0].weight)
        assertEquals("key", maxSpec.preferredDuringSchedulingIgnoredDuringExecution[0].podAffinityTerm.topologyKey)
    }

    /**
     * Verifies that the maximal PodAffinitySpec definition is serialised into the expected YAML
     * document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "requiredDuringSchedulingIgnoredDuringExecution": [
          |    {
          |      "topologyKey": "key"
          |    }
          |  ],
          |  "preferredDuringSchedulingIgnoredDuringExecution": [
          |    {
          |      "weight": 99,
          |      "podAffinityTerm": {
          |        "topologyKey": "key"
          |      }
          |    }
          |  ]
          |}
        """.trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT)
    }

    /**
     * Verifies that the minimal PodAffinitySpec definition is built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertNull(minSpec.requiredDuringSchedulingIgnoredDuringExecution)
        assertNull(minSpec.preferredDuringSchedulingIgnoredDuringExecution)
    }

    /**
     * Verifies that the minimal PodAffinitySpec definition is serialised into the expected YAML
     * document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        val actualJson = minSpec.toJson()
        val expectedJson = "{}"

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT)
    }
}