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
import kotlin.test.assertNull

class CompleteEnvironmentSpecTest {
    companion object {
        private val configMapMaxSpec = CompleteEnvironmentSpecBuilder().apply {
            prefix = "prefix"
            configMapRef("config-map") {
                optional = true
            }
        }.build()

        private val configMapMinSpec = CompleteEnvironmentSpecBuilder().apply {
            configMapRef("config-map")
        }.build()

        private val secretMaxSpec = CompleteEnvironmentSpecBuilder().apply {
            prefix = "prefix"
            secretRef("secret") {
                optional = true
            }
        }.build()

        private val secretMinSpec = CompleteEnvironmentSpecBuilder().apply {
            secretRef("secret")
        }.build()
    }

    @Test
    fun testConfigMapRefContentMax() {
        assertEquals("prefix", configMapMaxSpec.prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, configMapMaxSpec.source.type)
        assertEquals("config-map", configMapMaxSpec.source.name)
        assertEquals(true, configMapMaxSpec.source.optional)
    }

    @Test
    fun testConfigMapRefYamlMax() {
        val expectedJson = """{
          |  "prefix": "prefix",
          |  "configMapRef": {
          |    "name": "config-map",
          |    "optional": true
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, configMapMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testConfigMapRefContentMin() {
        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, configMapMinSpec.source.type)
        assertNull(configMapMinSpec.prefix)
        assertEquals("config-map", configMapMinSpec.source.name)
        assertNull(configMapMinSpec.source.optional)
    }

    @Test
    fun testConfigMapRefYamlMin() {
        val expectedJson = """{"configMapRef":{"name":"config-map"}}"""

        JSONAssert.assertEquals(expectedJson, configMapMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testSecretRefContentMax() {
        assertEquals("prefix", secretMaxSpec.prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.Secret, secretMaxSpec.source.type)
        assertEquals("secret", secretMaxSpec.source.name)
        assertEquals(true, secretMaxSpec.source.optional)
    }

    @Test
    fun testSecretRefYamlMax() {
        val expectedJson = """{
          |  "prefix": "prefix",
          |  "secretRef": {
          |    "name": "secret",
          |    "optional": true
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, secretMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testSecretRefContentMin() {
        assertEquals(CompleteEnvironmentSpec.SourceType.Secret, secretMinSpec.source.type)
        assertNull(secretMinSpec.prefix)
        assertEquals("secret", secretMinSpec.source.name)
        assertNull(secretMinSpec.source.optional)
    }

    @Test
    fun testSecretRefYamlMin() {
        val expectedJson = """{"secretRef":{"name":"secret"}}"""

        JSONAssert.assertEquals(expectedJson, secretMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

}
