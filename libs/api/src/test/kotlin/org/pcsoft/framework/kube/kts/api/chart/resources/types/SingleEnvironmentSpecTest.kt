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
import kotlin.test.assertIs

class SingleEnvironmentSpecTest {
    companion object {
        private val valueSpec = SingleEnvironmentSpecBuilder("MY_VAR").apply {
            from {
                value("my-static-value")
            }
        }.build()

        private val fieldSpec = SingleEnvironmentSpecBuilder("MY_VAR").apply {
            from {
                fieldReference("my-field")
            }
        }.build()

        private val resourceFieldSpec = SingleEnvironmentSpecBuilder("MY_VAR").apply {
            from {
                resourceFieldReference("my-field")
            }
        }.build()

        private val secretSpec = SingleEnvironmentSpecBuilder("MY_VAR").apply {
            from {
                secretKeyReference("my-secret", "my-key")
            }
        }.build()

        private val configMapSpec = SingleEnvironmentSpecBuilder("MY_VAR").apply {
            from {
                configMapKeyReference("my-config-map", "my-key")
            }
        }.build()
    }

    @Test
    fun testValueContent() {
        assertEquals("MY_VAR", valueSpec.name)
        assertIs<SingleEnvironmentSpec.ValueSource>(valueSpec.source)
        assertEquals("my-static-value", valueSpec.source.value)
    }

    @Test
    fun testValueYaml() {
        val expectedJson = """{"name":"MY_VAR","value":"my-static-value"}"""

        JSONAssert.assertEquals(expectedJson, valueSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testFromFieldContent() {
        assertEquals("MY_VAR", fieldSpec.name)
        assertIs<SingleEnvironmentSpec.FieldReferenceSource>(fieldSpec.source)
        assertEquals("my-field", fieldSpec.source.fieldPath)
    }

    @Test
    fun testFromResourceFieldContent() {
        assertEquals("MY_VAR", resourceFieldSpec.name)
        assertIs<SingleEnvironmentSpec.ResourceFieldReferenceSource>(resourceFieldSpec.source)
        assertEquals("my-field", resourceFieldSpec.source.resource)
    }

    @Test
    fun testFromResourceFieldYaml() {
        val expectedJson = """{
          |  "name": "MY_VAR",
          |  "valueFrom": {
          |    "resourceFieldRef": {
          |      "resource": "my-field"
          |    }
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, resourceFieldSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testFromSecretContent() {
        assertEquals("MY_VAR", secretSpec.name)
        assertIs<SingleEnvironmentSpec.SecretKeyReferenceSource>(secretSpec.source)
        assertEquals("my-key", secretSpec.source.key)
        assertEquals("my-secret", secretSpec.source.name)
    }

    @Test
    fun testFromSecretYaml() {
        val expectedJson = """{
          |  "name": "MY_VAR",
          |  "valueFrom": {
          |    "secretKeyRef": {
          |      "key": "my-key",
          |      "name": "my-secret"
          |    }
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, secretSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testFromConfigMapContent() {
        assertEquals("MY_VAR", configMapSpec.name)
        assertIs<SingleEnvironmentSpec.ConfigMapKeyReferenceSource>(configMapSpec.source)
        assertEquals("my-key", configMapSpec.source.key)
        assertEquals("my-config-map", configMapSpec.source.name)
    }

    @Test
    fun testFromConfigMapYaml() {
        val expectedJson = """{
          |  "name": "MY_VAR",
          |  "valueFrom": {
          |    "configMapKeyRef": {
          |      "key": "my-key",
          |      "name": "my-config-map"
          |    }
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, configMapSpec.toJson(), JSONCompareMode.LENIENT)
    }
}
