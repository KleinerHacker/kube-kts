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

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SealedSecretTemplateSpecTest {
    companion object {
        private val templateMaxSpec = SealedSecretTemplateSpecBuilder().apply {
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

        private val templateMinSpec = SealedSecretTemplateSpecBuilder().build()

        private val metadataMaxSpec = SealedSecretTemplateMetadataSpecBuilder().apply {
            labels {
                label("app", "demo")
            }
            annotations {
                annotation("description", "secret template")
            }
        }.build()

        private val metadataMinSpec = SealedSecretTemplateMetadataSpecBuilder().build()
    }

    // ============ Template — Maximal ============

    @Test
    fun testTemplateMaxContent() {
        assertEquals(SecretSpec.Type.Tls, templateMaxSpec.type)
        assertEquals(true, templateMaxSpec.immutable)
        assertNotNull(templateMaxSpec.metadata)
        assertEquals(mapOf("app" to "demo"), templateMaxSpec.metadata.labels)
        assertEquals(mapOf("description" to "secret template"), templateMaxSpec.metadata.annotations)
    }

    @Test
    fun testTemplateMaxYaml() {
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
            templateMaxSpec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    // ============ Template — Minimal ============

    @Test
    fun testTemplateMinContent() {
        assertNull(templateMinSpec.type)
        assertNull(templateMinSpec.immutable)
        assertNull(templateMinSpec.metadata)
    }

    @Test
    fun testTemplateMinYaml() {
        JSONAssert.assertEquals("{}", templateMinSpec.toJson(), JSONCompareMode.LENIENT)
    }

    // ============ Template Metadata — Maximal ============

    @Test
    fun testMetadataMaxContent() {
        assertEquals(mapOf("app" to "demo"), metadataMaxSpec.labels)
        assertEquals(mapOf("description" to "secret template"), metadataMaxSpec.annotations)
    }

    @Test
    fun testMetadataMaxYaml() {
        JSONAssert.assertEquals(
            """{
              |  "labels": {
              |    "app": "demo"
              |  },
              |  "annotations": {
              |    "description": "secret template"
              |  }
              |}""".trimMargin(),
            metadataMaxSpec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    // ============ Template Metadata — Minimal ============

    @Test
    fun testMetadataMinContent() {
        assertNull(metadataMinSpec.labels)
        assertNull(metadataMinSpec.annotations)
    }

    @Test
    fun testMetadataMinYaml() {
        JSONAssert.assertEquals("{}", metadataMinSpec.toJson(), JSONCompareMode.LENIENT)
    }
}
