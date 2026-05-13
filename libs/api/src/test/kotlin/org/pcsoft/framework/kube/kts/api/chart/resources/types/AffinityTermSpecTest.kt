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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.NodeSelectorTermSpec.NodeSelectorRequirementSpec.*
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NodeSelectorTermSpecTest {
    companion object {
        private val maxSpec = NodeSelectorTermSpecBuilder().apply {
            matchExpressions {
                match("key", Operator.DoesNotExist) {
                    values {
                        value("value")
                    }
                }
            }
            matchFields {
                match("key", Operator.NotIn) {
                    values {
                        value("value")
                    }
                }
            }
        }.build()

        private val minSpec = NodeSelectorTermSpecBuilder().build()
    }

    @Test
    fun testMaxContent() {
        assertNotNull(maxSpec.matchExpressions)
        assertEquals(1, maxSpec.matchExpressions.size)
        assertEquals("key", maxSpec.matchExpressions[0].key)
        assertEquals(Operator.DoesNotExist, maxSpec.matchExpressions[0].operator)
        assertEquals(1, maxSpec.matchExpressions[0].values!!.size)
        assertEquals("value", maxSpec.matchExpressions[0].values?.get(0))

        assertNotNull(maxSpec.matchFields)
        assertEquals(1, maxSpec.matchFields.size)
        assertEquals("key", maxSpec.matchFields[0].key)
        assertEquals(Operator.NotIn, maxSpec.matchFields[0].operator)
        assertNotNull(maxSpec.matchFields[0].values)
        assertEquals(1, maxSpec.matchFields[0].values!!.size)
        assertEquals("value", maxSpec.matchFields[0].values?.get(0))
    }

    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "matchExpressions": [
          |    {
          |      "key": "key",
          |      "operator": "DoesNotExist",
          |      "values": [
          |        "value"
          |      ]
          |    }
          |  ],
          |  "matchFields": [
          |    {
          |      "key": "key",
          |      "operator": "NotIn",
          |      "values": [
          |        "value"
          |      ]
          |    }
          |  ]
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        assertNull(minSpec.matchExpressions)
        assertNull(minSpec.matchFields)
    }

    @Test
    fun testMinYaml() {
        val actualJson = minSpec.toJson()
        val expectedJson = "{}"

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)       
    }

}

class PreferredSchedulingTermSpecTest {
    companion object {
        private val maxSpec = PreferredSchedulingTermSpecBuilder(99).apply {
            preference {
                matchExpressions {
                    match("key", Operator.DoesNotExist) {
                        values {
                            value("value")
                        }
                    }
                }
                matchFields {
                    match("key", Operator.NotIn) {
                        values {
                            value("value")
                        }
                    }
                }
            }
        }.build()

        private val minSpec = PreferredSchedulingTermSpecBuilder(99).apply {
            preference {

            }
        }.build()
    }

    @Test
    fun testMaxContent() {
        assertNotNull(maxSpec.preference.matchExpressions)
        assertEquals("key", maxSpec.preference.matchExpressions[0].key)
        assertEquals(Operator.DoesNotExist, maxSpec.preference.matchExpressions[0].operator)
        assertNotNull(maxSpec.preference.matchExpressions[0].values)
        assertEquals(1, maxSpec.preference.matchExpressions[0].values!!.size)
        assertEquals("value", maxSpec.preference.matchExpressions[0].values!![0])
        assertNotNull(maxSpec.preference.matchFields)
        assertEquals("key", maxSpec.preference.matchFields[0].key)
        assertEquals(Operator.NotIn, maxSpec.preference.matchFields[0].operator)
        assertNotNull(maxSpec.preference.matchFields[0].values)
        assertEquals(1, maxSpec.preference.matchFields[0].values!!.size)
        assertEquals("value", maxSpec.preference.matchFields[0].values!![0])
    }

    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "weight": 99,
          |  "preference": {
          |    "matchExpressions": [
          |      {
          |        "key": "key",
          |        "operator": "DoesNotExist",
          |        "values": [
          |          "value"
          |        ]
          |      }
          |    ],
          |    "matchFields": [
          |      {
          |        "key": "key",
          |        "operator": "NotIn",
          |        "values": [
          |          "value"
          |        ]
          |      }
          |    ]
          |  }
          |}
        """.trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        assertNull(minSpec.preference.matchExpressions)
        assertNull(minSpec.preference.matchFields)
    }

    @Test
    fun testMinYaml() {
        val actualJson = minSpec.toJson()
        val expectedJson = """{
          |  "weight": 99,
          |  "preference": {}
          |}
        """.trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }
}

class PodAffinityTermSpecTest {
    companion object {
        private val maxSpec = PodAffinityTermSpecBuilder("key").apply {
            labelSelector {
                matchLabels {
                    label("key", "value")
                }
                matchExpressions {
                    expression("key", LabelSelectorRequirementSpec.Operator.DoesNotExist) {
                        values {
                            value("value")
                        }
                    }
                }
            }
            namespaces {
                namespace("namespace")
            }
            namespaceSelector {
                matchLabels {
                    label("key", "value")
                }
                matchExpressions {
                    expression("key", LabelSelectorRequirementSpec.Operator.DoesNotExist) {
                        values {
                            value("value")
                        }
                    }
                }
            }
            matchLabelKeys {
                key("key")
            }
            mismatchLabelKeys {
                key("key")
            }
        }.build()

        private val minSpec = PodAffinityTermSpecBuilder("key").build()
    }

    @Test
    fun testMaxContent() {
        assertNotNull(maxSpec.labelSelector)
        assertNotNull(maxSpec.labelSelector.matchLabels)
        assertEquals("value", maxSpec.labelSelector.matchLabels["key"])
        assertNotNull(maxSpec.labelSelector.matchExpressions)
        assertEquals(1, maxSpec.labelSelector.matchExpressions.size)
        assertEquals("key", maxSpec.labelSelector.matchExpressions[0].key)
        assertEquals(LabelSelectorRequirementSpec.Operator.DoesNotExist, maxSpec.labelSelector.matchExpressions[0].operator)
        assertEquals(listOf("value"), maxSpec.labelSelector.matchExpressions[0].values)
        assertNotNull(maxSpec.namespaceSelector)
        assertNotNull(maxSpec.namespaceSelector.matchLabels)
        assertEquals("value", maxSpec.namespaceSelector.matchLabels["key"])
        assertNotNull(maxSpec.namespaceSelector.matchExpressions)
        assertEquals(1, maxSpec.namespaceSelector.matchExpressions.size)
        assertEquals("key", maxSpec.namespaceSelector.matchExpressions[0].key)
        assertEquals(LabelSelectorRequirementSpec.Operator.DoesNotExist, maxSpec.namespaceSelector.matchExpressions[0].operator)
        assertEquals(listOf("value"), maxSpec.namespaceSelector.matchExpressions[0].values)
        assertNotNull(maxSpec.matchLabelKeys)
        assertEquals(1, maxSpec.matchLabelKeys.keys.size)
        assertEquals(listOf("key"), maxSpec.matchLabelKeys.keys)
        assertNotNull(maxSpec.mismatchLabelKeys)
        assertEquals(1, maxSpec.mismatchLabelKeys.keys.size)
        assertEquals(listOf("key"), maxSpec.mismatchLabelKeys.keys)
    }

    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "labelSelector": {
          |    "matchLabels": {
          |      "key": "value"
          |    },
          |    "matchExpressions": [
          |      {
          |        "key": "key",
          |        "operator": "DoesNotExist",
          |        "values": [
          |          "value"
          |        ]
          |      }
          |    ]
          |  },
          |  "namespaces": [
          |    "namespace"
          |  ],
          |  "topologyKey": "key",
          |  "namespaceSelector": {
          |    "matchLabels": {
          |      "key": "value"
          |    },
          |    "matchExpressions": [
          |      {
          |        "key": "key",
          |        "operator": "DoesNotExist",
          |        "values": [
          |          "value"
          |        ]
          |      }
          |    ]
          |  },
          |  "matchLabelKeys": [
          |    "key"
          |  ],
          |  "mismatchLabelKeys": [
          |    "key"
          |  ]
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        assertNull(minSpec.labelSelector)
        assertNull(minSpec.namespaceSelector)
        assertNull(minSpec.matchLabelKeys)
        assertNull(minSpec.mismatchLabelKeys)
        assertNull(minSpec.namespaces)
        assertEquals("key", minSpec.topologyKey)
    }

}

class WeightedPodAffinityTermSpecTest {
    companion object {
        val maxSpec = WeightedPodAffinityTermSpecBuilder(99).apply {
            podAffinityTerm("key") {
                labelSelector {
                    matchLabels {
                        label("key", "value")
                    }
                    matchExpressions {
                        expression("key", LabelSelectorRequirementSpec.Operator.DoesNotExist) {
                            values {
                                value("value")
                            }
                        }
                    }
                }
                namespaces {
                    namespace("namespace")
                }
                namespaceSelector {
                    matchLabels {
                        label("key", "value")
                    }
                    matchExpressions {
                        expression("key", LabelSelectorRequirementSpec.Operator.DoesNotExist) {
                            values {
                                value("value")
                            }
                        }
                    }
                }
                matchLabelKeys {
                    key("key")
                }
                mismatchLabelKeys {
                    key("key")
                }
            }
        }.build()

        private val minSpec = WeightedPodAffinityTermSpecBuilder(99).apply {
            podAffinityTerm("key") {

            }
        }.build()
    }

    @Test
    fun testMaxContent() {
        assertEquals(99, maxSpec.weight)
        assertNotNull(maxSpec.podAffinityTerm.labelSelector)
        assertNotNull(maxSpec.podAffinityTerm.labelSelector.matchLabels)
        assertEquals("value", maxSpec.podAffinityTerm.labelSelector.matchLabels["key"])
        assertNotNull(maxSpec.podAffinityTerm.labelSelector.matchExpressions)
        assertEquals(1, maxSpec.podAffinityTerm.labelSelector.matchExpressions.size)
        assertEquals("key", maxSpec.podAffinityTerm.labelSelector.matchExpressions[0].key)
        assertEquals(LabelSelectorRequirementSpec.Operator.DoesNotExist, maxSpec.podAffinityTerm.labelSelector.matchExpressions[0].operator)
        assertEquals(listOf("value"), maxSpec.podAffinityTerm.labelSelector.matchExpressions[0].values)
        assertNotNull(maxSpec.podAffinityTerm.namespaceSelector)
        assertNotNull(maxSpec.podAffinityTerm.namespaceSelector.matchLabels)
        assertEquals("value", maxSpec.podAffinityTerm.namespaceSelector.matchLabels["key"])
        assertNotNull(maxSpec.podAffinityTerm.namespaceSelector.matchExpressions)
        assertEquals(1, maxSpec.podAffinityTerm.namespaceSelector.matchExpressions.size)
        assertEquals("key", maxSpec.podAffinityTerm.namespaceSelector.matchExpressions[0].key)
        assertEquals(LabelSelectorRequirementSpec.Operator.DoesNotExist, maxSpec.podAffinityTerm.namespaceSelector.matchExpressions[0].operator)
        assertEquals(listOf("value"), maxSpec.podAffinityTerm.namespaceSelector.matchExpressions[0].values)
        assertNotNull(maxSpec.podAffinityTerm.matchLabelKeys)
        assertEquals(1, maxSpec.podAffinityTerm.matchLabelKeys.keys.size)
        assertEquals(listOf("key"), maxSpec.podAffinityTerm.matchLabelKeys.keys)
        assertNotNull(maxSpec.podAffinityTerm.mismatchLabelKeys)
        assertEquals(1, maxSpec.podAffinityTerm.mismatchLabelKeys.keys.size)
        assertEquals(listOf("key"), maxSpec.podAffinityTerm.mismatchLabelKeys.keys)
    }

    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "weight": 99,
          |  "podAffinityTerm": {
          |    "labelSelector": {
          |      "matchLabels": {
          |        "key": "value"
          |      },
          |      "matchExpressions": [
          |        {
          |          "key": "key",
          |          "operator": "DoesNotExist",
          |          "values": [
          |            "value"
          |          ]
          |        }
          |      ]
          |    },
          |    "namespaces": [
          |      "namespace"
          |    ],
          |    "topologyKey": "key",
          |    "namespaceSelector": {
          |      "matchLabels": {
          |        "key": "value"
          |      },
          |      "matchExpressions": [
          |        {
          |          "key": "key",
          |          "operator": "DoesNotExist",
          |          "values": [
          |            "value"
          |          ]
          |        }
          |      ]
          |    },
          |    "matchLabelKeys": [
          |      "key"
          |    ],
          |    "mismatchLabelKeys": [
          |      "key"
          |    ]
          |  }
          |}
        """.trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        assertEquals(99, minSpec.weight)
        assertNotNull(minSpec.podAffinityTerm)
        assertNull(minSpec.podAffinityTerm.labelSelector)
        assertNull(minSpec.podAffinityTerm.namespaceSelector)
        assertNull(minSpec.podAffinityTerm.matchLabelKeys)
        assertNull(minSpec.podAffinityTerm.mismatchLabelKeys)
        assertNull(minSpec.podAffinityTerm.namespaces)
        assertEquals("key", minSpec.podAffinityTerm.topologyKey)
    }

    @Test
    fun testMinYaml() {
        val actualJson = minSpec.toJson()
        val expectedJson = """{
          |  "weight": 99,
          |  "podAffinityTerm": {
          |    "topologyKey": "key"
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }
}