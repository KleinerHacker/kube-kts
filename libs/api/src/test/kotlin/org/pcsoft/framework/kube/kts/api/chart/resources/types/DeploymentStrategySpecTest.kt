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
import org.pcsoft.framework.kube.kts.api.types.absolute
import org.pcsoft.framework.kube.kts.api.types.percent
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeploymentStrategySpecTest {
    companion object {
        private val maxSpec = DeploymentStrategySpecBuilder().apply {
            type = DeploymentStrategySpec.Type.RollingUpdate
            rollingUpdate {
                maxSurge = 10.percent
                maxUnavailable = 3.absolute
            }
        }.build()

        private val minSpec = DeploymentStrategySpecBuilder().build()

        private val rollingUpdateMinSpec = DeploymentStrategySpecBuilder().apply {
            rollingUpdate {
            }
        }.build()
    }

    /**
     * Verifies that the maximal DeploymentStrategySpec definition is built into the expected spec
     * object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertEquals(DeploymentStrategySpec.Type.RollingUpdate, maxSpec.type)
        assertNotNull(maxSpec.rollingUpdate)
        assertEquals(10.percent, maxSpec.rollingUpdate.maxSurge)
        assertEquals(3.absolute, maxSpec.rollingUpdate.maxUnavailable)
    }

    /**
     * Verifies that the maximal DeploymentStrategySpec definition is serialised into the expected
     * YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxContentYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "type": "RollingUpdate",
          |  "rollingUpdate": {
          |    "maxSurge": "10%",
          |    "maxUnavailable": 3
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal DeploymentStrategySpec definition is built into the expected spec
     * object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertNull(minSpec.type)
        assertNull(minSpec.rollingUpdate)
    }

    /**
     * Verifies that the minimal DeploymentStrategySpec definition is serialised into the expected
     * YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinContentYaml() {
        assertEquals("""{}""", minSpec.toJson())
    }

    /**
     * Verifies that the minimal DeploymentStrategySpec definition of the rolling update flavour is
     * built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testRollingUpdateMinContent() {
        assertNull(rollingUpdateMinSpec.type)
        assertNotNull(rollingUpdateMinSpec.rollingUpdate)
        assertNull(rollingUpdateMinSpec.rollingUpdate.maxSurge)
        assertNull(rollingUpdateMinSpec.rollingUpdate.maxUnavailable)
    }

    /**
     * Verifies that the minimal DeploymentStrategySpec definition of the rolling update flavour is
     * serialised into the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testRollingUpdateMinContentYaml() {
        assertEquals("""{"rollingUpdate":{}}""", rollingUpdateMinSpec.toJson())
    }
}
