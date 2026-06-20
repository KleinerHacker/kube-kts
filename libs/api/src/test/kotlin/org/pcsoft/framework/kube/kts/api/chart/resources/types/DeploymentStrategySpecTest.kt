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

    @Test
    fun testMaxContent() {
        assertEquals(DeploymentStrategySpec.Type.RollingUpdate, maxSpec.type)
        assertNotNull(maxSpec.rollingUpdate)
        assertEquals(10.percent, maxSpec.rollingUpdate.maxSurge)
        assertEquals(3.absolute, maxSpec.rollingUpdate.maxUnavailable)
    }

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

    @Test
    fun testMinContent() {
        assertNull(minSpec.type)
        assertNull(minSpec.rollingUpdate)
    }

    @Test
    fun testMinContentYaml() {
        assertEquals("""{}""", minSpec.toJson())
    }

    @Test
    fun testRollingUpdateMinContent() {
        assertNull(rollingUpdateMinSpec.type)
        assertNotNull(rollingUpdateMinSpec.rollingUpdate)
        assertNull(rollingUpdateMinSpec.rollingUpdate.maxSurge)
        assertNull(rollingUpdateMinSpec.rollingUpdate.maxUnavailable)
    }

    @Test
    fun testRollingUpdateMinContentYaml() {
        assertEquals("""{"rollingUpdate":{}}""", rollingUpdateMinSpec.toJson())
    }
}
