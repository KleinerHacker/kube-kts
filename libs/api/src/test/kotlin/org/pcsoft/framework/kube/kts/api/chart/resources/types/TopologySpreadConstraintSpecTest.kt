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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.TopologySpreadConstraintSpec.NodePolicy
import org.pcsoft.framework.kube.kts.api.chart.resources.types.TopologySpreadConstraintSpec.WhenUnsatisfiable
import org.pcsoft.framework.kube.kts.api.chart.types.MatchLabelKeySpec
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TopologySpreadConstraintSpecTest {
    companion object {
        private val maxSpec = TopologySpreadConstraintSpecBuilder(
            99,
            "key",
            WhenUnsatisfiable.DoNotSchedule
        ).apply {
            labelSelector {
                matchLabels {
                    label("key", "value")
                }
                matchExpressions {
                    expression("key", LabelSelectorRequirementSpec.Operator.Exists) {
                        values {
                            value("value")
                        }
                    }
                }
            }
            minDomains = 98
            nodeAffinityPolicy = NodePolicy.Honor
            nodeTaintsPolicy = NodePolicy.Ignore
            matchLabelKeys {
                key("key")
            }
        }.build()

        private val minSpec = TopologySpreadConstraintSpecBuilder(
            99,
            "key",
            WhenUnsatisfiable.DoNotSchedule
        ).build()
    }

    /**
     * Verifies that the maximal TopologySpreadConstraintSpec definition is built into the expected
     * spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertEquals(99, maxSpec.maxSkew)
        assertEquals("key", maxSpec.topologyKey)
        assertEquals(WhenUnsatisfiable.DoNotSchedule, maxSpec.whenUnsatisfiable)
        assertEquals(98, maxSpec.minDomains)
        assertEquals(NodePolicy.Honor, maxSpec.nodeAffinityPolicy)
        assertEquals(NodePolicy.Ignore, maxSpec.nodeTaintsPolicy)
        assertEquals(MatchLabelKeySpec(listOf("key")), maxSpec.matchLabelKeys)

        // Label Selector assertions
        assertNotNull(maxSpec.labelSelector)
        assertNotNull(maxSpec.labelSelector.matchLabels)
        assertEquals("value", maxSpec.labelSelector.matchLabels["key"])
        assertNotNull(maxSpec.labelSelector.matchExpressions)
        assertEquals(1, maxSpec.labelSelector.matchExpressions.size)
        assertEquals("key", maxSpec.labelSelector.matchExpressions[0].key)
        assertEquals(
            LabelSelectorRequirementSpec.Operator.Exists,
            maxSpec.labelSelector.matchExpressions[0].operator
        )
        assertEquals(listOf("value"), maxSpec.labelSelector.matchExpressions[0].values)

    }

    /**
     * Verifies that the maximal TopologySpreadConstraintSpec definition is serialised into the
     * expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "maxSkew": 99,
          |  "topologyKey": "key",
          |  "whenUnsatisfiable": "DoNotSchedule",
          |  "minDomains": 98,
          |  "nodeAffinityPolicy": "Honor",
          |  "nodeTaintsPolicy": "Ignore",
          |  "matchLabelKeys": [
          |    "key"
          |  ],
          |  "labelSelector": {
          |    "matchLabels": {
          |      "key": "value"
          |    },
          |    "matchExpressions": [
          |      {
          |        "key": "key",
          |        "operator": "Exists",
          |        "values": [
          |          "value"
          |        ]
          |      }
          |    ]
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal TopologySpreadConstraintSpec definition is built into the expected
     * spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertEquals(99, minSpec.maxSkew)
        assertEquals("key", minSpec.topologyKey)
        assertEquals(WhenUnsatisfiable.DoNotSchedule, minSpec.whenUnsatisfiable)
        assertNull(minSpec.minDomains)
        assertNull(minSpec.nodeAffinityPolicy)
        assertNull(minSpec.nodeTaintsPolicy)
        assertNull(minSpec.matchLabelKeys)
        assertNull(minSpec.labelSelector)
    }

    /**
     * Verifies that the minimal TopologySpreadConstraintSpec definition is serialised into the
     * expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        val actualJson = minSpec.toJson()
        val expectedJson = """{
          |  "maxSkew": 99,
          |  "topologyKey": "key",
          |  "whenUnsatisfiable": "DoNotSchedule"
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

}