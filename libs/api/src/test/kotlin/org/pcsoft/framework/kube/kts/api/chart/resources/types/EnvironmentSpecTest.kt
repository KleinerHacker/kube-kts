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

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertIs

class EnvironmentSpecTest {

    @Test
    fun testValueContent() {
        val envSpec = EnvironmentSpecBuild("MY_VAR").apply {
            from {
                value("my-static-value")
            }
        }.build()

        assertEquals("MY_VAR", envSpec.name)
        assertIs<EnvironmentSpec.ValueSource>(envSpec.source)
        assertEquals("my-static-value", envSpec.source.value)
    }

    @Test
    fun testValueYaml() {
        val envSpec = EnvironmentSpecBuild("MY_VAR").apply {
            from {
                value("my-static-value")
            }
        }.build()

        val actualJson = envSpec.toJson()
        val expectedJson = """{"name":"MY_VAR","value":"my-static-value"}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testFromFieldContent() {
        val envSpec = EnvironmentSpecBuild("MY_VAR").apply {
            from {
                fieldReference("my-field")
            }
        }.build()

        assertEquals("MY_VAR", envSpec.name)
        assertIs<EnvironmentSpec.FieldReferenceSource>(envSpec.source)
        assertEquals("my-field", envSpec.source.fieldPath)
    }

    @Test
    fun testFromResourceFieldContentYaml() {
        val envSpec = EnvironmentSpecBuild("MY_VAR").apply {
            from {
                resourceFieldReference("my-field")
            }
        }.build()

        val actualJson = envSpec.toJson()
        val expectedJson = """{"name":"MY_VAR","valueFrom":{"resourceFieldRef":{"resource":"my-field"}}}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testFromResourceFieldContent() {
        val envSpec = EnvironmentSpecBuild("MY_VAR").apply {
            from {
                resourceFieldReference("my-field")
            }
        }.build()

        assertEquals("MY_VAR", envSpec.name)
        assertIs<EnvironmentSpec.ResourceFieldReferenceSource>(envSpec.source)
        assertEquals("my-field", envSpec.source.resource)
    }

    @Test
    fun testFromResourceFieldYaml() {
        val envSpec = EnvironmentSpecBuild("MY_VAR").apply {
            from {
                resourceFieldReference("my-field")
            }
        }.build()

        val actualJson = envSpec.toJson()
        val expectedJson = """{"name":"MY_VAR","valueFrom":{"resourceFieldRef":{"resource":"my-field"}}}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testFromSecretContent() {
        val envSpec = EnvironmentSpecBuild("MY_VAR").apply {
            from {
                secretKeyReference("my-secret", "my-key")
            }
        }.build()

        assertEquals("MY_VAR", envSpec.name)
        assertIs<EnvironmentSpec.SecretKeyReferenceSource>(envSpec.source)
        assertEquals("my-key", envSpec.source.key)
        assertEquals("my-secret", envSpec.source.name)
    }

    @Test
    fun testFromSecretYaml() {
        val envSpec = EnvironmentSpecBuild("MY_VAR").apply {
            from {
                secretKeyReference("my-secret", "my-key")
            }
        }.build()

        val actualJson = envSpec.toJson()
        val expectedJson = """{"name":"MY_VAR","valueFrom":{"secretKeyRef":{"key":"my-key","name":"my-secret"}}}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testFromConfigMapContent() {
        val envSpec = EnvironmentSpecBuild("MY_VAR").apply {
            from {
                configMapKeyReference("my-config-map", "my-key")
            }
        }.build()

        assertEquals("MY_VAR", envSpec.name)
        assertIs<EnvironmentSpec.ConfigMapKeyReferenceSource>(envSpec.source)
        assertEquals("my-key", envSpec.source.key)
        assertEquals("my-config-map", envSpec.source.name)
    }

    @Test
    fun testFromConfigMapYaml() {
        val envSpec = EnvironmentSpecBuild("MY_VAR").apply {
            from {
                configMapKeyReference("my-config-map", "my-key")
            }
        }.build()

        val actualJson = envSpec.toJson()
        val expectedJson = """{"name":"MY_VAR","valueFrom":{"configMapKeyRef":{"key":"my-key","name":"my-config-map"}}}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }
}