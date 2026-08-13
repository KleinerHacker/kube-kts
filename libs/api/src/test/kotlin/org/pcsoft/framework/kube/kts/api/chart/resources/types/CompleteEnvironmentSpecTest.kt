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

    /**
     * Verifies that the maximal CompleteEnvironmentSpec definition of the config map ref flavour
     * is built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testConfigMapRefContentMax() {
        assertEquals("prefix", configMapMaxSpec.prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, configMapMaxSpec.source.type)
        assertEquals("config-map", configMapMaxSpec.source.name)
        assertEquals(true, configMapMaxSpec.source.optional)
    }

    /**
     * Verifies that the maximal CompleteEnvironmentSpec definition of the config map ref flavour
     * is serialised into the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
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

    /**
     * Verifies that the minimal CompleteEnvironmentSpec definition of the config map ref flavour
     * is built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testConfigMapRefContentMin() {
        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, configMapMinSpec.source.type)
        assertNull(configMapMinSpec.prefix)
        assertEquals("config-map", configMapMinSpec.source.name)
        assertNull(configMapMinSpec.source.optional)
    }

    /**
     * Verifies that the minimal CompleteEnvironmentSpec definition of the config map ref flavour
     * is serialised into the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testConfigMapRefYamlMin() {
        val expectedJson = """{"configMapRef":{"name":"config-map"}}"""

        JSONAssert.assertEquals(expectedJson, configMapMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the maximal CompleteEnvironmentSpec definition of the secret ref flavour is
     * built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testSecretRefContentMax() {
        assertEquals("prefix", secretMaxSpec.prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.Secret, secretMaxSpec.source.type)
        assertEquals("secret", secretMaxSpec.source.name)
        assertEquals(true, secretMaxSpec.source.optional)
    }

    /**
     * Verifies that the maximal CompleteEnvironmentSpec definition of the secret ref flavour is
     * serialised into the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
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

    /**
     * Verifies that the minimal CompleteEnvironmentSpec definition of the secret ref flavour is
     * built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testSecretRefContentMin() {
        assertEquals(CompleteEnvironmentSpec.SourceType.Secret, secretMinSpec.source.type)
        assertNull(secretMinSpec.prefix)
        assertEquals("secret", secretMinSpec.source.name)
        assertNull(secretMinSpec.source.optional)
    }

    /**
     * Verifies that the minimal CompleteEnvironmentSpec definition of the secret ref flavour is
     * serialised into the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testSecretRefYamlMin() {
        val expectedJson = """{"secretRef":{"name":"secret"}}"""

        JSONAssert.assertEquals(expectedJson, secretMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

}
