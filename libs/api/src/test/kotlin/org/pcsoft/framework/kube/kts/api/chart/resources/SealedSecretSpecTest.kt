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
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SealedSecretSpecTest {
    companion object {
        private val maxSpecBuilder = SealedSecretSpecBuilder().apply {
            addEncryptedData("password", "AgBy3i4OJSWK+PiTySYZZA9rO")
            addEncryptedData("username", "AgAKv2H8x9Qm0pLrT3uVwX1yZ")
            template {
                type = SecretSpec.Type.Opaque
                immutable = true
                metadata {
                    labels {
                        label("key", "value")
                    }
                    annotations {
                        annotation("key", "value")
                    }
                }
            }
        }

        private val maxSpec = maxSpecBuilder.build()
        private val maxTemplate =
            ExplicitTemplateSpecBuilder(SealedSecretSpec.API_VERSION, SealedSecretSpec.KIND, maxSpecBuilder).apply {
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

        private val minSpecBuilder = SealedSecretSpecBuilder().apply {
            addEncryptedData("password", "AgBy3i4OJSWK+PiTySYZZA9rO")
        }

        private val minSpec = minSpecBuilder.build()
        private val minTemplate =
            ExplicitTemplateSpecBuilder(SealedSecretSpec.API_VERSION, SealedSecretSpec.KIND, minSpecBuilder).apply {
                metadata("name") {}
            }.build()
    }

    /**
     * Verifies that the maximal SealedSecretSpec definition is built into the expected spec
     * object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertEquals(
            mapOf(
                "password" to "AgBy3i4OJSWK+PiTySYZZA9rO",
                "username" to "AgAKv2H8x9Qm0pLrT3uVwX1yZ"
            ),
            maxSpec.encryptedData
        )
        assertNotNull(maxSpec.template)
        assertEquals(SecretSpec.Type.Opaque, maxSpec.template.type)
        assertEquals(true, maxSpec.template.immutable)
        assertNotNull(maxSpec.template.metadata)
        assertEquals(mapOf("key" to "value"), maxSpec.template.metadata.labels)
        assertEquals(mapOf("key" to "value"), maxSpec.template.metadata.annotations)
    }

    /**
     * Verifies that the minimal SealedSecretSpec definition is built into the expected spec
     * object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertEquals(mapOf("password" to "AgBy3i4OJSWK+PiTySYZZA9rO"), minSpec.encryptedData)
        assertNull(minSpec.template)
    }

    /**
     * Verifies that the maximal SealedSecretSpec definition is serialised into the expected YAML
     * document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/sealedsecret.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal SealedSecretSpec definition is serialised into the expected YAML
     * document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals(
            """{
              |  "apiVersion": "bitnami.com/v1alpha1",
              |  "kind": "SealedSecret",
              |  "metadata": {
              |    "name": "name"
              |  },
              |  "spec": {
              |    "encryptedData": {
              |      "password": "AgBy3i4OJSWK+PiTySYZZA9rO"
              |    }
              |  }
              |}""".trimMargin(),
            minTemplate.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies the SealedSecretSpec definition of the encrypted data builder dsl flavour.
     *
     * The fields relevant for this case are set and the resulting specification is checked against
     * the expectation.
     */
    @Test
    fun testEncryptedDataBuilderDsl() {
        val spec = SealedSecretSpecBuilder().apply {
            encryptedData {
                entry("a", "encA")
                entry("b", "encB")
            }
        }.build()

        assertEquals(mapOf("a" to "encA", "b" to "encB"), spec.encryptedData)
        assertNull(spec.template)
    }

    /**
     * Verifies that building a [SealedSecretSpec] fails when a mandatory field is not set.
     *
     * The builder must reject the input for encrypted data with an exception instead of producing
     * an incomplete specification that the API server would refuse later.
     */
    @Test
    fun testMissingEncryptedDataContent() {
        assertFailsWith<IllegalArgumentException> {
            SealedSecretSpecBuilder().build()
        }
    }
}
