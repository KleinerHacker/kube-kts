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
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodFailurePolicySpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.utils.KotlinAssertions
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class JobSpecTest {
    companion object {
        private val maxSpecBuilder = JobSpecBuilder().apply {
            parallelism = 2
            completions = 5
            completionMode = JobSpec.CompletionMode.Indexed
            backoffLimit = 4
            backoffLimitPerIndex = 2
            maxFailedIndexes = 3
            activeDeadlineSeconds = 100.seconds.toJavaDuration()
            ttlSecondsAfterFinished = 200.seconds.toJavaDuration()
            suspend = false
            manualSelector = true
            podReplacementPolicy = JobSpec.PodReplacementPolicy.Failed

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

            podFailurePolicy {
                rule(PodFailurePolicySpec.Action.FailJob) {
                    onExitCodes(PodFailurePolicySpec.OnExitCodes.Operator.In) {
                        containerName = "worker"
                        values(1, 42)
                    }
                }
                rule(PodFailurePolicySpec.Action.Ignore) {
                    onPodCondition("DisruptionTarget", PodFailurePolicySpec.ConditionStatus.True)
                }
            }

            successPolicy {
                rule {
                    succeededIndexes = "0-2"
                    succeededCount = 2
                }
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

                    restartPolicy = PodSpec.RestartPolicy.Never
                }
            }
        }

        private val maxSpec = maxSpecBuilder.build()
        private val maxTemplate = ExplicitTemplateSpecBuilder(JobSpec.API_VERSION, JobSpec.KIND, maxSpecBuilder).apply {
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
                    ownerReference(
                        "apiVersion",
                        "kind",
                        "name",
                        UUID.fromString("2fade68b-1f49-403a-b5e8-4e640d3c6594")
                    ) {
                        blockOwnerDeletion = true
                        controller = true
                    }
                }
            }
        }.build()

        private val minSpec = JobSpecBuilder().apply {
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

    /**
     * Verifies that the maximal JobSpec definition is built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertEquals(2, maxSpec.parallelism)
        assertEquals(5, maxSpec.completions)
        assertEquals(JobSpec.CompletionMode.Indexed, maxSpec.completionMode)
        assertEquals(4, maxSpec.backoffLimit)
        assertEquals(2, maxSpec.backoffLimitPerIndex)
        assertEquals(3, maxSpec.maxFailedIndexes)
        assertEquals(100.seconds.toJavaDuration(), maxSpec.activeDeadlineSeconds)
        assertEquals(200.seconds.toJavaDuration(), maxSpec.ttlSecondsAfterFinished)
        assertEquals(false, maxSpec.suspend)
        assertEquals(true, maxSpec.manualSelector)
        assertEquals(JobSpec.PodReplacementPolicy.Failed, maxSpec.podReplacementPolicy)

        assertEquals("demo", maxSpec.selector!!.matchLabels!!["app"])
        assertEquals("tier", maxSpec.selector.matchExpressions!!.first().key)

        KotlinAssertions.assertNotNull(maxSpec.podFailurePolicy) {
            KotlinAssertions.assertList(2, it.rules) { }
            assertEquals(PodFailurePolicySpec.Action.FailJob, it.rules.first().action)
            assertEquals("worker", it.rules.first().onExitCodes!!.containerName)
            assertEquals(listOf(1, 42), it.rules.first().onExitCodes!!.values)
            assertEquals("DisruptionTarget", it.rules[1].onPodConditions!!.first().type)
        }

        KotlinAssertions.assertNotNull(maxSpec.successPolicy) {
            assertEquals("0-2", it.rules.first().succeededIndexes)
            assertEquals(2, it.rules.first().succeededCount)
        }

        assertEquals("demo", maxSpec.template.metadata!!.labels!!["app"])
        assertEquals("name", maxSpec.template.spec.containers.first().name)
        assertEquals("image", maxSpec.template.spec.containers.first().image)
        assertEquals(PodSpec.RestartPolicy.Never, maxSpec.template.spec.restartPolicy)
    }

    /**
     * Verifies that the maximal JobSpec definition is serialised into the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/job.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal JobSpec definition is built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertNull(minSpec.parallelism)
        assertNull(minSpec.completions)
        assertNull(minSpec.completionMode)
        assertNull(minSpec.backoffLimit)
        assertNull(minSpec.backoffLimitPerIndex)
        assertNull(minSpec.maxFailedIndexes)
        assertNull(minSpec.activeDeadlineSeconds)
        assertNull(minSpec.ttlSecondsAfterFinished)
        assertNull(minSpec.suspend)
        assertNull(minSpec.manualSelector)
        assertNull(minSpec.podReplacementPolicy)
        assertNull(minSpec.selector)
        assertNull(minSpec.podFailurePolicy)
        assertNull(minSpec.successPolicy)
        assertNull(minSpec.template.metadata)
        assertEquals("name", minSpec.template.spec.containers.first().name)
        assertEquals("image", minSpec.template.spec.containers.first().image)
        assertNull(minSpec.template.spec.restartPolicy)
    }

    /**
     * Verifies that the minimal JobSpec definition is serialised into the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals(
            """{
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

    /**
     * Verifies that building a [JobSpec] fails when a mandatory field is not set.
     *
     * The builder must reject the input for template with an exception instead of producing an
     * incomplete specification that the API server would refuse later.
     */
    @Test
    fun testMissingTemplateContent() {
        assertFailsWith<IllegalArgumentException> {
            JobSpecBuilder().build()
        }
    }

    /**
     * Verifies that a job can be handed over to an external controller.
     *
     * Setting `managedBy` tells the built-in job controller to stay out of the way so that another
     * controller, for example a multi-cluster queue, becomes responsible for the job's pods and status.
     */
    @Test
    fun testManagedByContent() {
        val spec = JobSpecBuilder().apply {
            managedBy = "kueue.x-k8s.io/multikueue"
            template {
                spec {
                    addContainer("worker", "busybox:latest") {}
                    restartPolicy = PodSpec.RestartPolicy.Never
                }
            }
        }.build()

        assertEquals("kueue.x-k8s.io/multikueue", spec.managedBy)
        JSONAssert.assertEquals(
            """{"managedBy":"kueue.x-k8s.io/multikueue"}""",
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }
}
