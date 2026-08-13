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
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class TolerationSpecTest {
    companion object {
        private val maxSpec = TolerationSpecBuilder().apply {
            key = "key"
            operator = TolerationSpec.Operator.Equal
            value = "value"
            effect = TolerationSpec.Effect.NoSchedule
            tolerationSeconds = 100.seconds.toJavaDuration()
        }.build()

        private val minSpec = TolerationSpecBuilder().build()
    }

    /**
     * Verifies that the maximal TolerationSpec definition is built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertEquals("key", maxSpec.key)
        assertEquals(TolerationSpec.Operator.Equal, maxSpec.operator)
        assertEquals("value", maxSpec.value)
        assertEquals(TolerationSpec.Effect.NoSchedule, maxSpec.effect)
        assertEquals(100.seconds.toJavaDuration(), maxSpec.tolerationSeconds)
    }

    /**
     * Verifies that the maximal TolerationSpec definition is serialised into the expected YAML
     * document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """
            |{
            |  "key": "key",
            |  "operator": "Equal",
            |  "value": "value",
            |  "effect": "NoSchedule",
            |  "tolerationSeconds": 100
            |}
            """.trimMargin()
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal TolerationSpec definition is built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertEquals(null, minSpec.key)
        assertEquals(null, minSpec.operator)
        assertEquals(null, minSpec.value)
        assertEquals(null, minSpec.effect)
        assertEquals(null, minSpec.tolerationSeconds)
    }

    /**
     * Verifies that the minimal TolerationSpec definition is serialised into the expected YAML
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