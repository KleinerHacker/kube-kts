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

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RulesSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.ServiceBackendSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.utils.KotlinAssertions
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("DEPRECATION")
class IngressSpecTest {
    companion object {
        private val maxSpecBuilder = IngressSpecBuilder().apply {
            ingressClassName = "className"

            defaultServiceBackend("service") {
                port(9999)
            }

            addTls {
                secretName = "secretName"
                addHost("host.example.com")
            }

            addRule {
                host = "rule.example.com"
                addHttpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
                    path = "path"
                    serviceBackend("ruleService") {
                        port(7777)
                    }
                }
            }
        }

        private val maxSpec = maxSpecBuilder.build()
        private val maxTemplate = ExplicitTemplateSpecBuilder(IngressSpec.API_VERSION, IngressSpec.KIND, maxSpecBuilder).apply {
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
                ownerReferences {
                    ownerReference("apiVersion", "kind", "name", UUID.fromString("2fade68b-1f49-403a-b5e8-4e640d3c6594")) {
                        blockOwnerDeletion = true
                        controller = true
                    }
                }
            }
        }.build()

        private val minSpec = IngressSpecBuilder().build()

        private val minTlsSpec = IngressSpecBuilder().apply {
            addTls { }
        }.build()

        private val minRuleSpec = IngressSpecBuilder().apply {
            addRule {
                addHttpPath(RulesSpec.HttpPathConfig.PathType.ImplementationSpecific) {
                    serviceBackend("ruleService") {
                        port(80)
                    }
                }
            }
        }.build()
    }

    @Test
    fun testMaxContent() {
        assertEquals("className", maxSpec.ingressClassName)

        assertNotNull(maxSpec.defaultBackend)
        KotlinAssertions.assertInstanceOf<ServiceBackendSpec>(maxSpec.defaultBackend) {
            assertEquals("service", it.name)
            assertNotNull(it.port)
            assertEquals(9999, it.port.number)
            assertNull(it.port.name)
        }

        assertNotNull(maxSpec.tls)
        KotlinAssertions.assertList(1, maxSpec.tls) {
            assertEquals("secretName", it.secretName)
            assertNotNull(it.hosts)
            KotlinAssertions.assertList(1, it.hosts) {
                assertEquals("host.example.com", it)
            }
        }

        assertNotNull(maxSpec.rules)
        KotlinAssertions.assertList(1, maxSpec.rules) {
            assertEquals("rule.example.com", it.host)
            KotlinAssertions.assertList(1, it.http) {
                assertEquals("path", it.path)
                assertEquals(RulesSpec.HttpPathConfig.PathType.Exact, it.pathType)
                assertNotNull(it.backend)
                KotlinAssertions.assertInstanceOf<ServiceBackendSpec>(it.backend) {
                    assertEquals("ruleService", it.name)
                    assertNotNull(it.port)
                    assertEquals(7777, it.port.number)
                    assertNull(it.port.name)
                }
            }
        }
    }

    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/ingress.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        assertNull(minSpec.ingressClassName)
        assertNull(minSpec.defaultBackend)
        assertNull(minSpec.tls)
        assertNull(minSpec.rules)
    }

    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals("""{}""", minSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinTlsContent() {
        assertNotNull(minTlsSpec.tls)
        KotlinAssertions.assertList(1, minTlsSpec.tls) {
            assertNull(it.secretName)
            assertNull(it.hosts)
        }
    }

    @Test
    fun testMinTlsYaml() {
        JSONAssert.assertEquals(
            """{
              |  "tls": [
              |    {}
              |  ]
              |}""".trimMargin(),
            minTlsSpec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    @Test
    fun testMinRuleContent() {
        assertNotNull(minRuleSpec.rules)
        KotlinAssertions.assertList(1, minRuleSpec.rules) {
            assertNull(it.host)
            KotlinAssertions.assertList(1, it.http) {
                assertNull(it.path)
                assertEquals(RulesSpec.HttpPathConfig.PathType.ImplementationSpecific, it.pathType)
                KotlinAssertions.assertInstanceOf<ServiceBackendSpec>(it.backend) {
                    assertEquals("ruleService", it.name)
                    assertEquals(80, it.port.number)
                    assertNull(it.port.name)
                }
            }
        }
    }

    @Test
    fun testMinRuleYaml() {
        JSONAssert.assertEquals(
            """{
              |  "rules": [
              |    {
              |      "http": {
              |        "paths": [
              |          {
              |            "pathType": "ImplementationSpecific",
              |            "backend": {
              |              "service": {
              |                "name": "ruleService",
              |                "port": {
              |                  "number": 80
              |                }
              |              }
              |            }
              |          }
              |        ]
              |      }
              |    }
              |  ]
              |}""".trimMargin(),
            minRuleSpec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

}
