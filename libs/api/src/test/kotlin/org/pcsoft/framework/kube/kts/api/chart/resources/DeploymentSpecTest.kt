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
import kotlin.test.assertFailsWith
import org.pcsoft.framework.kube.kts.api.chart.resources.types.DeploymentStrategySpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.LabelSelectorRequirementSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.types.absolute
import org.pcsoft.framework.kube.kts.api.types.percent
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class DeploymentSpecTest {
    companion object {
        private val maxSpecBuilder = DeploymentSpecBuilder().apply {
            replicas = 3

            selector {
                matchLabels {
                    label("app", "demo")
                }
                matchExpressions {
                    expression("tier", LabelSelectorRequirementSpec.Operator.In) {
                        values {
                            value("backend")
                        }
                    }
                }
            }

            template {
                metadata {
                    labels {
                        label("app", "demo")
                    }
                    annotations {
                        annotation("description", "Demo pod template")
                    }
                }

                spec {
                    containers {
                        container("name", "image") {

                        }
                    }

                    restartPolicy = PodSpec.RestartPolicy.Always
                }
            }

            strategy {
                type = DeploymentStrategySpec.Type.RollingUpdate
                rollingUpdate {
                    maxSurge = 10.percent
                    maxUnavailable = 3.absolute
                }
            }

            minReadySeconds = 30.seconds.toJavaDuration()
            revisionHistoryLimit = 5
            paused = true
            progressDeadlineSeconds = 600.seconds.toJavaDuration()
        }

        private val maxSpec = maxSpecBuilder.build()
        private val maxTemplate = ExplicitTemplateSpecBuilder(DeploymentSpec.API_VERSION, DeploymentSpec.KIND, maxSpecBuilder).apply {
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

        private val minSpec = DeploymentSpecBuilder().apply {
            selector {
                matchLabels {
                    label("app", "demo")
                }
            }

            template {
                spec {
                    containers {
                        container("name", "image") {

                        }
                    }
                }
            }
        }.build()
    }

    @Test
    fun testMaxContent() {
        val deploymentSpec = maxSpec

        assertEquals(3, deploymentSpec.replicas)

        assertEquals("demo", deploymentSpec.selector.matchLabels!!["app"])
        assertNotNull(deploymentSpec.selector.matchExpressions)
        assertEquals("tier", deploymentSpec.selector.matchExpressions.first().key)
        assertEquals(LabelSelectorRequirementSpec.Operator.In, deploymentSpec.selector.matchExpressions.first().operator)
        assertEquals("backend", deploymentSpec.selector.matchExpressions.first().values!!.first())

        assertNotNull(deploymentSpec.template.metadata)
        assertEquals("demo", deploymentSpec.template.metadata.labels!!["app"])
        assertEquals("Demo pod template", deploymentSpec.template.metadata.annotations!!["description"])
        assertEquals("name", deploymentSpec.template.spec.containers.first().name)
        assertEquals("image", deploymentSpec.template.spec.containers.first().image)
        assertEquals(PodSpec.RestartPolicy.Always, deploymentSpec.template.spec.restartPolicy)

        assertNotNull(deploymentSpec.strategy)
        assertEquals(DeploymentStrategySpec.Type.RollingUpdate, deploymentSpec.strategy.type)
        assertNotNull(deploymentSpec.strategy.rollingUpdate)
        assertEquals(10.percent, deploymentSpec.strategy.rollingUpdate.maxSurge)
        assertEquals(3.absolute, deploymentSpec.strategy.rollingUpdate.maxUnavailable)

        assertEquals(30.seconds.toJavaDuration(), deploymentSpec.minReadySeconds)
        assertEquals(5, deploymentSpec.revisionHistoryLimit)
        assertEquals(true, deploymentSpec.paused)
        assertEquals(600.seconds.toJavaDuration(), deploymentSpec.progressDeadlineSeconds)
    }

    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/deployment.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        val deploymentSpec = minSpec

        assertNull(deploymentSpec.replicas)
        assertEquals("demo", deploymentSpec.selector.matchLabels!!["app"])
        assertNull(deploymentSpec.selector.matchExpressions)
        assertNull(deploymentSpec.template.metadata)
        assertEquals("name", deploymentSpec.template.spec.containers.first().name)
        assertEquals("image", deploymentSpec.template.spec.containers.first().image)
        assertNull(deploymentSpec.template.spec.restartPolicy)
        assertNull(deploymentSpec.strategy)
        assertNull(deploymentSpec.minReadySeconds)
        assertNull(deploymentSpec.revisionHistoryLimit)
        assertNull(deploymentSpec.paused)
        assertNull(deploymentSpec.progressDeadlineSeconds)
    }

    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals(
            """{
              |  "selector": {
              |    "matchLabels": {
              |      "app": "demo"
              |    }
              |  },
              |  "template": {
              |    "spec": {
              |      "containers": [
              |        {
              |          "name": "name",
              |          "image": "image"
              |        }
              |      ]
              |    }
              |  }
              |}""".trimMargin(),
            minSpec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    @Test
    fun testMissingSelectorContent() {
        assertFailsWith<IllegalArgumentException> {
            DeploymentSpecBuilder().apply {
                template {
                    spec {
                        containers {
                            container("name", "image") {

                            }
                        }
                    }
                }
            }.build()
        }
    }

    @Test
    fun testMissingTemplateContent() {
        assertFailsWith<IllegalArgumentException> {
            DeploymentSpecBuilder().apply {
                selector {
                    matchLabels {
                        label("app", "demo")
                    }
                }
            }.build()
        }
    }

}