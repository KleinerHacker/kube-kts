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
import org.pcsoft.framework.kube.kts.api.chart.resources.types.LabelSelectorRequirementSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PersistentVolumeClaimRetentionPolicySpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.StatefulSetUpdateStrategySpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeClaimTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.types.absolute
import org.pcsoft.framework.kube.kts.api.types.giBytes
import org.pcsoft.framework.kube.kts.api.utils.KotlinAssertions
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class StatefulSetSpecTest {
    companion object {
        private val maxSpecBuilder = StatefulSetSpecBuilder().apply {
            replicas = 3
            serviceName = "my-service"
            podManagementPolicy = StatefulSetSpec.PodManagementPolicy.Parallel
            revisionHistoryLimit = 5
            minReadySeconds = 10.seconds.toJavaDuration()
            ordinals(1)

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

            updateStrategy {
                type = StatefulSetUpdateStrategySpec.Type.RollingUpdate
                rollingUpdate {
                    partition = 1
                    maxUnavailable = 1.absolute
                }
            }

            volumeClaimTemplates {
                claim("data") {
                    accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
                    storageClassName = "standard"
                    volumeMode = VolumeClaimTemplateSpec.VolumeMode.Filesystem
                    labels {
                        label("type", "ssd")
                    }
                    requests {
                        storage = 1.giBytes
                    }
                    limits {
                        storage = 2.giBytes
                    }
                }
            }

            persistentVolumeClaimRetentionPolicy {
                whenDeleted = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Delete
                whenScaled = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Retain
            }

            template {
                metadata {
                    labels {
                        label("app", "demo")
                    }
                }

                spec {
                    containers {
                        container("name", "image") {

                        }
                    }
                }
            }
        }

        private val maxSpec = maxSpecBuilder.build()
        private val maxTemplate = ExplicitTemplateSpecBuilder(StatefulSetSpec.API_VERSION, StatefulSetSpec.KIND, maxSpecBuilder).apply {
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

        private val minSpec = StatefulSetSpecBuilder().apply {
            serviceName = "my-service"
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
        assertEquals(3, maxSpec.replicas)
        assertEquals("my-service", maxSpec.serviceName)
        assertEquals(StatefulSetSpec.PodManagementPolicy.Parallel, maxSpec.podManagementPolicy)
        assertEquals(5, maxSpec.revisionHistoryLimit)
        assertEquals(10.seconds.toJavaDuration(), maxSpec.minReadySeconds)
        assertEquals(1, maxSpec.ordinals!!.start)

        assertEquals("demo", maxSpec.selector.matchLabels!!["app"])
        assertEquals("tier", maxSpec.selector.matchExpressions!!.first().key)

        KotlinAssertions.assertNotNull(maxSpec.updateStrategy) {
            assertEquals(StatefulSetUpdateStrategySpec.Type.RollingUpdate, it.type)
            assertEquals(1, it.rollingUpdate!!.partition)
            assertEquals(1, it.rollingUpdate.maxUnavailable!!.value)
        }

        KotlinAssertions.assertNotNull(maxSpec.volumeClaimTemplates) {
            KotlinAssertions.assertList(1, it) { }
            val claim = it.first()
            assertEquals("data", claim.metadata.name)
            assertEquals("ssd", claim.metadata.labels!!["type"])
            assertEquals(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce, claim.spec.accessModes!!.first())
            assertEquals("standard", claim.spec.storageClassName)
            assertEquals(VolumeClaimTemplateSpec.VolumeMode.Filesystem, claim.spec.volumeMode)
            assertEquals(1.giBytes, claim.spec.resources!!.requests!!.storage)
            assertEquals(2.giBytes, claim.spec.resources.limits!!.storage)
        }

        KotlinAssertions.assertNotNull(maxSpec.persistentVolumeClaimRetentionPolicy) {
            assertEquals(PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Delete, it.whenDeleted)
            assertEquals(PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Retain, it.whenScaled)
        }

        assertEquals("demo", maxSpec.template.metadata!!.labels!!["app"])
        assertEquals("name", maxSpec.template.spec.containers.first().name)
        assertEquals("image", maxSpec.template.spec.containers.first().image)
    }

    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/statefulset.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        assertNull(minSpec.replicas)
        assertEquals("my-service", minSpec.serviceName)
        assertNull(minSpec.podManagementPolicy)
        assertNull(minSpec.updateStrategy)
        assertNull(minSpec.volumeClaimTemplates)
        assertNull(minSpec.persistentVolumeClaimRetentionPolicy)
        assertNull(minSpec.revisionHistoryLimit)
        assertNull(minSpec.minReadySeconds)
        assertNull(minSpec.ordinals)
        assertEquals("demo", minSpec.selector.matchLabels!!["app"])
        assertNull(minSpec.template.metadata)
        assertEquals("name", minSpec.template.spec.containers.first().name)
        assertEquals("image", minSpec.template.spec.containers.first().image)
    }

    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals(
            """{
              |  "serviceName": "my-service",
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
    fun testMissingTemplateContent() {
        assertFailsWith<IllegalArgumentException> {
            StatefulSetSpecBuilder().apply {
                serviceName = "my-service"
                selector {
                    matchLabels {
                        label("app", "demo")
                    }
                }
            }.build()
        }
    }

    @Test
    fun testMissingSelectorContent() {
        assertFailsWith<IllegalArgumentException> {
            StatefulSetSpecBuilder().apply {
                serviceName = "my-service"
                template {
                    spec {
                        containers {
                            container("name", "image") { }
                        }
                    }
                }
            }.build()
        }
    }

    @Test
    fun testMissingServiceNameContent() {
        assertFailsWith<IllegalArgumentException> {
            StatefulSetSpecBuilder().apply {
                selector {
                    matchLabels {
                        label("app", "demo")
                    }
                }
                template {
                    spec {
                        containers {
                            container("name", "image") { }
                        }
                    }
                }
            }.build()
        }
    }
}
