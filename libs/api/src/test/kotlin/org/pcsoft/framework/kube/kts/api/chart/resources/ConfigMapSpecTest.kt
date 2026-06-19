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

class ConfigMapSpecTest {
    companion object {
        private val maxSpecBuilder = ConfigMapSpecBuilder().apply {
            addData("key1", "value1")
            addData("key2", "value2")
            addBinaryData("binKey", "test".toByteArray())
            immutable = true
        }

        private val maxSpec = maxSpecBuilder.build()
        private val maxTemplate = FlatTemplateSpecBuilder(ConfigMapSpec.API_VERSION, ConfigMapSpec.KIND, maxSpecBuilder).apply {
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

        private val minSpec = ConfigMapSpecBuilder().build()
    }

    @Test
    fun testMaxContent() {
        assertEquals(mapOf("key1" to "value1", "key2" to "value2"), maxSpec.data)
        assertNotNull(maxSpec.binaryData)
        assertContentEquals("test".toByteArray(), maxSpec.binaryData["binKey"])
        assertEquals(true, maxSpec.immutable)
    }

    @Test
    fun testMinContent() {
        assertNull(minSpec.data)
        assertNull(minSpec.binaryData)
        assertNull(minSpec.immutable)
    }

    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/configmap.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testBinaryDataMultipleEntries() {
        val spec = ConfigMapSpecBuilder().apply {
            addBinaryData("bin1", "test1".toByteArray())
            addBinaryData("bin2", "test2".toByteArray())
        }.build()

        assertNotNull(spec.binaryData)
        assertEquals(2, spec.binaryData.size)
        assertContentEquals("test1".toByteArray(), spec.binaryData["bin1"])
        assertContentEquals("test2".toByteArray(), spec.binaryData["bin2"])
        assertNull(spec.data)
    }

    @Test
    fun testBinaryDataBuilderDsl() {
        val spec = ConfigMapSpecBuilder().apply {
            binaryData {
                entry("bin1", "test1".toByteArray())
                entry("bin2", "test2".toByteArray())
            }
        }.build()

        assertNotNull(spec.binaryData)
        assertEquals(2, spec.binaryData.size)
        assertContentEquals("test1".toByteArray(), spec.binaryData["bin1"])
        assertContentEquals("test2".toByteArray(), spec.binaryData["bin2"])
        assertNull(spec.data)
    }

    @Test
    fun testBinaryDataOnlyYaml() {
        val specBuilder = ConfigMapSpecBuilder().apply {
            addBinaryData("bin1", "test1".toByteArray())
            addBinaryData("bin2", "test2".toByteArray())
        }
        val template = FlatTemplateSpecBuilder(ConfigMapSpec.API_VERSION, ConfigMapSpec.KIND, specBuilder).apply {
            metadata("name") {}
        }.build()

        val expectedYaml = IOUtils.resourceToString("/configmap-binaryonly.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = template.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }
}
