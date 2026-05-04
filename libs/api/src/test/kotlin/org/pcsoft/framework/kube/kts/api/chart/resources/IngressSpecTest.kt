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

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RulesSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.ServiceBackendSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.utils.KotlinAssertions
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.util.UUID

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
        private val maxTemplate = TemplateSpecBuilder(IngressSpec.API_VERSION, IngressSpec.KIND, maxSpecBuilder).apply {
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
    }

    @Test
    fun testMaxContent() {
        Assertions.assertEquals("className", maxSpec.ingressClassName)

        Assertions.assertNotNull(maxSpec.defaultBackend)
        KotlinAssertions.assertInstanceOf<ServiceBackendSpec>(maxSpec.defaultBackend!!) {
            Assertions.assertEquals("service", it.name)
            Assertions.assertNotNull(it.port)
            Assertions.assertEquals(9999, it.port.number)
            Assertions.assertNull(it.port.name)
        }

        Assertions.assertNotNull(maxSpec.tls)
        KotlinAssertions.assertList(1, maxSpec.tls!!) {
            Assertions.assertEquals("secretName", it.secretName)
            Assertions.assertNotNull(it.hosts)
            KotlinAssertions.assertList(1, it.hosts!!) {
                Assertions.assertEquals("host.example.com", it)
            }
        }

        Assertions.assertNotNull(maxSpec.rules)
        KotlinAssertions.assertList(1, maxSpec.rules!!) {
            Assertions.assertEquals("rule.example.com", it.host)
            KotlinAssertions.assertList(1, it.http) {
                Assertions.assertEquals("path", it.path)
                Assertions.assertEquals(RulesSpec.HttpPathConfig.PathType.Exact, it.pathType)
                Assertions.assertNotNull(it.backend)
                KotlinAssertions.assertInstanceOf<ServiceBackendSpec>(it.backend) {
                    Assertions.assertEquals("ruleService", it.name)
                    Assertions.assertNotNull(it.port)
                    Assertions.assertEquals(7777, it.port.number)
                    Assertions.assertNull(it.port.name)
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

}