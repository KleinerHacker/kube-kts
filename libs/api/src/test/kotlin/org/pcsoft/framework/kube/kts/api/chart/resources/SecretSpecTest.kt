/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.template.FlatTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SecretSpecTest {
    companion object {
        private val maxSpecBuilder = SecretSpecBuilder().apply {
            type = SecretSpec.Type.Opaque
            addData("binKey", "test".toByteArray())
            addStringData("username", "admin")
            addStringData("password", "s3cr3t")
            immutable = true
        }

        private val maxSpec = maxSpecBuilder.build()
        private val maxTemplate = FlatTemplateSpecBuilder(SecretSpec.API_VERSION, SecretSpec.KIND, maxSpecBuilder).apply {
            metadata("name") {
                namespace = "namespace"
                generateName = "generateName"
                labels {
                    label("key", "value")
                }
                annotations {
                    annotation("key", "value")
                }
                finalizers {
                    finalizer("finalizer")
                }
            }
        }.build()

        private val minSpec = SecretSpecBuilder().build()
        private val minTemplate = FlatTemplateSpecBuilder(SecretSpec.API_VERSION, SecretSpec.KIND, SecretSpecBuilder()).apply {
            metadata("name") {}
        }.build()
    }

    @Test
    fun testMaxContent() {
        assertEquals(SecretSpec.Type.Opaque, maxSpec.type)
        assertNotNull(maxSpec.data)
        assertContentEquals("test".toByteArray(), maxSpec.data["binKey"])
        assertEquals(mapOf("username" to "admin", "password" to "s3cr3t"), maxSpec.stringData)
        assertEquals(true, maxSpec.immutable)
    }

    @Test
    fun testMinContent() {
        assertNull(minSpec.type)
        assertNull(minSpec.data)
        assertNull(minSpec.stringData)
        assertNull(minSpec.immutable)
    }

    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/secret.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals(
            """{
              |  "apiVersion": "v1",
              |  "kind": "Secret",
              |  "metadata": {
              |    "name": "name"
              |  }
              |}""".trimMargin(),
            minTemplate.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    @Test
    fun testDataBuilderDsl() {
        val spec = SecretSpecBuilder().apply {
            data {
                entry("bin1", "test1".toByteArray())
                entry("bin2", "test2".toByteArray())
            }
        }.build()

        assertNotNull(spec.data)
        assertEquals(2, spec.data.size)
        assertContentEquals("test1".toByteArray(), spec.data["bin1"])
        assertContentEquals("test2".toByteArray(), spec.data["bin2"])
        assertNull(spec.stringData)
    }

    @Test
    fun testStringDataBuilderDsl() {
        val spec = SecretSpecBuilder().apply {
            stringData {
                entry("username", "admin")
                entry("password", "s3cr3t")
            }
        }.build()

        assertEquals(mapOf("username" to "admin", "password" to "s3cr3t"), spec.stringData)
        assertNull(spec.data)
    }

    @Test
    fun testTlsTypeYaml() {
        val specBuilder = SecretSpecBuilder().apply {
            type = SecretSpec.Type.Tls
            addData("tls.crt", "cert".toByteArray())
            addData("tls.key", "key".toByteArray())
        }
        val template = FlatTemplateSpecBuilder(SecretSpec.API_VERSION, SecretSpec.KIND, specBuilder).apply {
            metadata("name") {}
        }.build()

        JSONAssert.assertEquals(
            """{
              |  "type": "kubernetes.io/tls",
              |  "data": {
              |    "tls.crt": "Y2VydA==",
              |    "tls.key": "a2V5"
              |  }
              |}""".trimMargin(),
            template.toJson(),
            JSONCompareMode.LENIENT
        )
    }
}
