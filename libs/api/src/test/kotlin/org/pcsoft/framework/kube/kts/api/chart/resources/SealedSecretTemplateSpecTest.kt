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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

class SealedSecretTemplateSpecTest {

    // ============ Template — Maximal ============

    @Test
    fun testTemplateMaxContent() {
        val spec = SealedSecretTemplateSpecBuilder().apply {
            type = SecretSpec.Type.Tls
            immutable = true
            metadata {
                labels {
                    label("app", "demo")
                }
                annotations {
                    annotation("description", "secret template")
                }
            }
        }.build()

        Assertions.assertEquals(SecretSpec.Type.Tls, spec.type)
        Assertions.assertEquals(true, spec.immutable)
        Assertions.assertNotNull(spec.metadata)
        Assertions.assertEquals(mapOf("app" to "demo"), spec.metadata!!.labels)
        Assertions.assertEquals(mapOf("description" to "secret template"), spec.metadata.annotations)
    }

    @Test
    fun testTemplateMaxYaml() {
        val spec = SealedSecretTemplateSpecBuilder().apply {
            type = SecretSpec.Type.Tls
            immutable = true
            metadata {
                labels {
                    label("app", "demo")
                }
                annotations {
                    annotation("description", "secret template")
                }
            }
        }.build()

        JSONAssert.assertEquals(
            """{
              |  "type": "kubernetes.io/tls",
              |  "immutable": true,
              |  "metadata": {
              |    "labels": {
              |      "app": "demo"
              |    },
              |    "annotations": {
              |      "description": "secret template"
              |    }
              |  }
              |}""".trimMargin(),
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    // ============ Template — Minimal ============

    @Test
    fun testTemplateMinContent() {
        val spec = SealedSecretTemplateSpecBuilder().build()

        Assertions.assertNull(spec.type)
        Assertions.assertNull(spec.immutable)
        Assertions.assertNull(spec.metadata)
    }

    @Test
    fun testTemplateMinYaml() {
        val spec = SealedSecretTemplateSpecBuilder().build()

        JSONAssert.assertEquals("{}", spec.toJson(), JSONCompareMode.LENIENT)
    }

    // ============ Template Metadata — Maximal ============

    @Test
    fun testMetadataMaxContent() {
        val spec = SealedSecretTemplateMetadataSpecBuilder().apply {
            labels {
                label("app", "demo")
            }
            annotations {
                annotation("description", "secret template")
            }
        }.build()

        Assertions.assertEquals(mapOf("app" to "demo"), spec.labels)
        Assertions.assertEquals(mapOf("description" to "secret template"), spec.annotations)
    }

    @Test
    fun testMetadataMaxYaml() {
        val spec = SealedSecretTemplateMetadataSpecBuilder().apply {
            labels {
                label("app", "demo")
            }
            annotations {
                annotation("description", "secret template")
            }
        }.build()

        JSONAssert.assertEquals(
            """{
              |  "labels": {
              |    "app": "demo"
              |  },
              |  "annotations": {
              |    "description": "secret template"
              |  }
              |}""".trimMargin(),
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    // ============ Template Metadata — Minimal ============

    @Test
    fun testMetadataMinContent() {
        val spec = SealedSecretTemplateMetadataSpecBuilder().build()

        Assertions.assertNull(spec.labels)
        Assertions.assertNull(spec.annotations)
    }

    @Test
    fun testMetadataMinYaml() {
        val spec = SealedSecretTemplateMetadataSpecBuilder().build()

        JSONAssert.assertEquals("{}", spec.toJson(), JSONCompareMode.LENIENT)
    }
}
