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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

class ConfigMapSpecTest {
    companion object {
        private val maxSpecBuilder = ConfigMapSpecBuilder().apply {
            addData("key1", "value1")
            addData("key2", "value2")
            addBinaryData("binKey", "test".toByteArray())
            immutable = true
        }

        private val maxSpec = maxSpecBuilder.build()
        private val maxTemplate = TemplateSpecBuilder(ConfigMapSpec.API_VERSION, ConfigMapSpec.KIND, maxSpecBuilder).apply {
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
        Assertions.assertEquals(mapOf("key1" to "value1", "key2" to "value2"), maxSpec.data)
        Assertions.assertNotNull(maxSpec.binaryData)
        Assertions.assertArrayEquals("test".toByteArray(), maxSpec.binaryData!!["binKey"])
        Assertions.assertEquals(true, maxSpec.immutable)
    }

    @Test
    fun testMinContent() {
        Assertions.assertNull(minSpec.data)
        Assertions.assertNull(minSpec.binaryData)
        Assertions.assertNull(minSpec.immutable)
    }

    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/configmap.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        println(actualJson)
        println(expectedJson)

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }
}
