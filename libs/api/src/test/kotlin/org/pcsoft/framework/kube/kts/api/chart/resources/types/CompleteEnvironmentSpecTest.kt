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

    @Test
    fun testConfigMapRefContentMax() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            prefix = "prefix"
            configMapRef("config-map") {
                optional = true
            }
        }.build()

        assertEquals("prefix", spec.prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, spec.source.type)
        assertEquals("config-map", spec.source.name)
        assertEquals(true, spec.source.optional)
    }

    @Test
    fun testConfigMapRefYamlMax() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            prefix = "prefix"
            configMapRef("config-map") {
                optional = true
            }
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{
          |  "prefix": "prefix",
          |  "configMapRef": {
          |    "name": "config-map",
          |    "optional": true
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testConfigMapRefContentMin() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            configMapRef("config-map")
        }.build()

        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, spec.source.type)
        assertNull(spec.prefix)
        assertEquals("config-map", spec.source.name)
        assertNull(spec.source.optional)
    }

    @Test
    fun testConfigMapRefYamlMin() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            configMapRef("config-map")
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{"configMapRef":{"name":"config-map"}}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testSecretRefContentMax() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            prefix = "prefix"
            secretRef("secret") {
                optional = true
            }
        }.build()

        assertEquals("prefix", spec.prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.Secret, spec.source.type)
        assertEquals("secret", spec.source.name)
        assertEquals(true, spec.source.optional)
    }

    @Test
    fun testSecretRefYamlMax() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            prefix = "prefix"
            secretRef("secret") {
                optional = true
            }
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{
          |  "prefix": "prefix",
          |  "secretRef": {
          |    "name": "secret",
          |    "optional": true
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testSecretRefContentMin() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            secretRef("secret")
        }.build()

        assertEquals(CompleteEnvironmentSpec.SourceType.Secret, spec.source.type)
        assertNull(spec.prefix)
        assertEquals("secret", spec.source.name)
        assertNull(spec.source.optional)
    }

    @Test
    fun testSecretRefYamlMin() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            secretRef("secret")
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{"secretRef":{"name":"secret"}}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

}