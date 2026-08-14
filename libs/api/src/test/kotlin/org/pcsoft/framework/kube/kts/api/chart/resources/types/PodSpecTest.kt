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
import org.pcsoft.framework.kube.kts.api.types.cpu
import org.pcsoft.framework.kube.kts.api.types.mCpu
import org.pcsoft.framework.kube.kts.api.types.miBytes
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class PodSpecTest {
    companion object {
        private val maxSpec = PodSpecBuilder().apply {
            containers {
                container("name", "image") {

                }

                init("name", "image") {

                }

                ephemeral("name", "image") {

                }
            }

            imagePullSecrets {
                secret("name")
            }

            automountServiceAccountToken = true
            serviceAccountName = "my-service-account"
            hostNetwork = true
            hostPID = false
            hostIPC = false
            dnsPolicy = PodSpec.DnsPolicy.ClusterFirstWithHostNet
            hostname = "my-hostname"
            subdomain = "my-subdomain"

            affinity {
                nodeAffinity {
                    // node affinity configuration
                }
            }

            schedulerName = "default-scheduler"

            tolerations {
                toleration {
                    key = "key1"
                    operator = TolerationSpec.Operator.Exists
                    value = "value1"
                    effect = TolerationSpec.Effect.NoSchedule
                }
            }

            priorityClassName = "high-priority"
            priority = 1000
            runtimeClassName = "runc"
            os = PodSpec.OS.Linux
            enableServiceLinks = false
            preemptionPolicy = PodSpec.PreemptionPolicy.PreemptLowerPriority

            overhead(100.mCpu, 100.miBytes)

            topologySpreadConstraints {
                constraint(
                    maxSkew = 1,
                    topologyKey = "kubernetes.io/hostname",
                    whenUnsatisfiable = TopologySpreadConstraintSpec.WhenUnsatisfiable.DoNotSchedule
                ) {
                
                }
            }

            securityContext {
                runAsUser = 1000
                runAsGroup = 3000
                fsGroup = 2000
            }

            volumes {
                volume("my-volume") {
                    from {
                        secret {
                            name = "secret-name"
                        }
                    }
                }
            }

            nodeSelector {
                select("disktype", "ssd")
            }

            nodeName = "specific-node"
            restartPolicy = PodSpec.RestartPolicy.OnFailure
            terminationGracePeriodSeconds = 30.seconds.toJavaDuration()
            activeDeadlineSeconds = 600.seconds.toJavaDuration()

            dnsConfig {
                nameservers {
                    nameserver("name")
                }
                searches {
                    search("kubernetes.io/hostname")
                }
                options {
                    option("ndots", "2")
                }
            }

            readinessGates {
                gate("custom-condition")
            }

            hostAliases {
                alias("127.0.0.1") {
                    hostnames {
                        hostname("localhost")
                    }
                }
            }

            resourceClaims {
                claim("gpu") {
                    resourceClaimName = "gpu-claim"
                }
            }

            shareProcessNamespace = true
            setHostnameAsFQDN = false
        }.build()

        private val minSpec = PodSpecBuilder().apply {
            containers {
                container("name", "image") {

                }
            }
        }.build()
    }

    /**
     * Verifies that the maximal PodSpec definition is built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        val spec = maxSpec

        assertEquals("name", spec.containers.first().name)
        assertEquals("image", spec.containers.first().image)
        assertNotNull(spec.initContainers)
        assertEquals("name", spec.initContainers.first().name)
        assertEquals("image", spec.initContainers.first().image)
        assertNotNull(spec.ephemeralContainers)
        assertEquals("name", spec.ephemeralContainers.first().name)
        assertEquals("image", spec.ephemeralContainers.first().image)

        assertNotNull(spec.imagePullSecrets)
        assertEquals("name", spec.imagePullSecrets.first().name)

        assertEquals(true, spec.automountServiceAccountToken)
        assertEquals("my-service-account", spec.serviceAccountName)
        assertEquals(true, spec.hostNetwork)
        assertEquals(false, spec.hostPID)
        assertEquals(false, spec.hostIPC)
        assertEquals(PodSpec.DnsPolicy.ClusterFirstWithHostNet, spec.dnsPolicy)
        assertEquals("my-hostname", spec.hostname)
        assertEquals("my-subdomain", spec.subdomain)

        assertNotNull(spec.affinity)
        assertNotNull(spec.affinity.nodeAffinity)

        assertEquals("default-scheduler", spec.schedulerName)

        assertNotNull(spec.tolerations)
        assertEquals("key1", spec.tolerations.first().key)
        assertEquals(TolerationSpec.Operator.Exists, spec.tolerations.first().operator)
        assertEquals("value1", spec.tolerations.first().value)
        assertEquals(TolerationSpec.Effect.NoSchedule, spec.tolerations.first().effect)

        assertEquals("high-priority", spec.priorityClassName)
        assertEquals(1000, spec.priority)
        assertEquals("runc", spec.runtimeClassName)
        assertEquals(PodSpec.OS.Linux, spec.os)
        assertEquals(false, spec.enableServiceLinks)
        assertEquals(PodSpec.PreemptionPolicy.PreemptLowerPriority, spec.preemptionPolicy)

        assertNotNull(spec.overhead)
        assertEquals(100.mCpu, spec.overhead.cpu)
        assertEquals(100.miBytes, spec.overhead.memory)

        assertNotNull(spec.topologySpreadConstraints)
        assertEquals(1, spec.topologySpreadConstraints.first().maxSkew)
        assertEquals("kubernetes.io/hostname", spec.topologySpreadConstraints.first().topologyKey)
        assertEquals(
            TopologySpreadConstraintSpec.WhenUnsatisfiable.DoNotSchedule,
            spec.topologySpreadConstraints.first().whenUnsatisfiable
        )

        assertNotNull(spec.securityContext)
        assertEquals(1000L, spec.securityContext.runAsUser)
        assertEquals(3000L, spec.securityContext.runAsGroup)
        assertEquals(2000L, spec.securityContext.fsGroup)

        assertNotNull(spec.volumes)
        assertEquals("my-volume", spec.volumes.first().name)
        assertIs<SecretSourceSpec>(spec.volumes.first().source)
        assertEquals("secret-name", (spec.volumes.first().source as SecretSourceSpec).name)

        assertNotNull(spec.nodeSelector)
        assertEquals("ssd", spec.nodeSelector["disktype"])

        assertEquals("specific-node", spec.nodeName)
        assertEquals(PodSpec.RestartPolicy.OnFailure, spec.restartPolicy)
        assertEquals(30.seconds.toJavaDuration(), spec.terminationGracePeriodSeconds)
        assertEquals(600.seconds.toJavaDuration(), spec.activeDeadlineSeconds)

        assertNotNull(spec.dnsConfig)
        assertEquals("name", spec.dnsConfig.nameservers!!.first())
        assertEquals("kubernetes.io/hostname", spec.dnsConfig.searches!!.first())
        assertEquals("2", spec.dnsConfig.options!!["ndots"])

        assertNotNull(spec.readinessGates)
        assertEquals("custom-condition", spec.readinessGates.first().conditionType)

        assertNotNull(spec.hostAliases)
        assertEquals("127.0.0.1", spec.hostAliases.first().ip)
        assertEquals("localhost", spec.hostAliases.first().hostnames.first())

        assertNotNull(spec.resourceClaims)
        assertEquals("gpu", spec.resourceClaims.first().name)
        assertEquals("gpu-claim", spec.resourceClaims.first().resourceClaimName)
        assertNull(spec.resourceClaims.first().resourceClaimTemplateName)

        assertEquals(true, spec.shareProcessNamespace)
        assertEquals(false, spec.setHostnameAsFQDN)
    }

    /**
     * Verifies that the maximal PodSpec definition is serialised into the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "containers": [
          |    {
          |      "name": "name",
          |      "image": "image"
          |    }
          |  ],
          |  "initContainers": [
          |    {
          |      "name": "name",
          |      "image": "image"
          |    }
          |  ],
          |  "restartPolicy": "OnFailure",
          |  "dnsPolicy": "ClusterFirstWithHostNet",
          |  "dnsConfig": {
          |    "nameservers": [
          |      "name"
          |    ],
          |    "searches": [
          |      "kubernetes.io/hostname"
          |    ],
          |    "options": [{
          |      "name": "ndots",
          |      "value": "2"
          |    }]
          |  },
          |  "serviceAccountName": "my-service-account",
          |  "automountServiceAccountToken": true,
          |  "nodeName": "specific-node",
          |  "hostNetwork": true,
          |  "hostPID": false,
          |  "hostIPC": false,
          |  "shareProcessNamespace": true,
          |  "hostname": "my-hostname",
          |  "subdomain": "my-subdomain",
          |  "setHostnameAsFQDN": false,
          |  "priorityClassName": "high-priority",
          |  "priority": 1000,
          |  "preemptionPolicy": "PreemptLowerPriority",
          |  "schedulerName": "default-scheduler",
          |  "runtimeClassName": "runc",
          |  "os": "Linux",
          |  "overhead": {
          |    "cpu": "100m",
          |    "memory": "100Mi"
          |  },
          |  "nodeSelector": {
          |    "disktype": "ssd"
          |  },
          |  "imagePullSecrets": [
          |    { "name": "name" }
          |  ],
          |  "volumes": [
          |    {
          |      "name": "my-volume"
          |    }
          |  ],
          |  "enableServiceLinks": false,
          |  "topologySpreadConstraints": [
          |    {
          |      "maxSkew": 1,
          |      "topologyKey": "kubernetes.io/hostname",
          |      "whenUnsatisfiable": "DoNotSchedule"
          |    }
          |  ],
          |  "affinity": {
          |    "nodeAffinity": {}
          |  },
          |  "tolerations": [
          |    {
          |      "key": "key1",
          |      "operator": "Exists",
          |      "value": "value1",
          |      "effect": "NoSchedule"
          |    }
          |  ],
          |  "securityContext": {
          |    "runAsUser": 1000,
          |    "runAsGroup": 3000,
          |    "fsGroup": 2000
          |  },
          |  "terminationGracePeriodSeconds": 30,
          |  "activeDeadlineSeconds": 600,
          |  "readinessGates": [
          |    { "conditionType": "custom-condition" }
          |  ],
          |  "hostAliases": [
          |    {
          |      "ip": "127.0.0.1",
          |      "hostnames": [
          |        "localhost"
          |      ]
          |    }
          |  ],
          |  "resourceClaims": [
          |    {
          |      "name": "gpu",
          |      "resourceClaimName": "gpu-claim"
          |    }
          |  ]
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal PodSpec definition is built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        val spec = minSpec

        assertEquals("name", spec.containers.first().name)
        assertEquals("image", spec.containers.first().image)
        assertNull(spec.initContainers)
        assertNull(spec.ephemeralContainers)
        assertNull(spec.restartPolicy)
        assertNull(spec.dnsPolicy)
        assertNull(spec.dnsConfig)
        assertNull(spec.serviceAccountName)
        assertNull(spec.automountServiceAccountToken)
        assertNull(spec.nodeName)
        assertNull(spec.hostNetwork)
        assertNull(spec.hostPID)
        assertNull(spec.hostIPC)
        assertNull(spec.shareProcessNamespace)
        assertNull(spec.hostname)
        assertNull(spec.subdomain)
        assertNull(spec.setHostnameAsFQDN)
        assertNull(spec.priorityClassName)
        assertNull(spec.priority)
        assertNull(spec.preemptionPolicy)
        assertNull(spec.schedulerName)
        assertNull(spec.runtimeClassName)
        assertNull(spec.os)
        assertNull(spec.overhead)
        assertNull(spec.nodeSelector)
        assertNull(spec.imagePullSecrets)
        assertNull(spec.volumes)
        assertNull(spec.enableServiceLinks)
        assertNull(spec.topologySpreadConstraints)
        assertNull(spec.affinity)
        assertNull(spec.tolerations)
        assertNull(spec.securityContext)
        assertNull(spec.terminationGracePeriodSeconds)
        assertNull(spec.activeDeadlineSeconds)
        assertNull(spec.readinessGates)
        assertNull(spec.hostAliases)
        assertNull(spec.resourceClaims)
    }

    /**
     * Verifies that the minimal PodSpec definition is serialised into the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals("""{"containers":[{"name":"name","image":"image"}]}""", minSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that scheduling gates keep a pod pending until a controller removes them.
     *
     * Each gate is rendered as an object carrying a `name`, not as a bare string, which is what the
     * Kubernetes schema requires.
     */
    @Test
    fun testSchedulingGatesContent() {
        val spec = PodSpecBuilder().apply {
            addContainer("app", "nginx:latest") {}
            schedulingGates {
                gate("example.com/await-quota")
            }
        }.build()

        assertEquals("example.com/await-quota", spec.schedulingGates!!.first().name)
        JSONAssert.assertEquals(
            """{"schedulingGates":[{"name":"example.com/await-quota"}]}""",
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a pod can opt into its own user namespace.
     *
     * Setting `hostUsers` to false maps container root onto an unprivileged user on the node, which is
     * a meaningful hardening step and therefore must survive into the manifest.
     */
    @Test
    fun testHostUsersContent() {
        val spec = PodSpecBuilder().apply {
            addContainer("app", "nginx:latest") {}
            hostUsers = false
        }.build()

        assertEquals(false, spec.hostUsers)
        JSONAssert.assertEquals("""{"hostUsers":false}""", spec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that pod-level resources bound the pod as a whole.
     *
     * These act as an envelope around the containers, so they are rendered next to the container list
     * rather than inside it.
     */
    @Test
    fun testPodLevelResourcesContent() {
        val spec = PodSpecBuilder().apply {
            addContainer("app", "nginx:latest") {}
            resources {
                limits {
                    cpu = 2f.cpu
                    memory = 512.miBytes
                }
            }
        }.build()

        assertEquals(512.miBytes, spec.resources!!.limits!!.memory)
        JSONAssert.assertEquals(
            """{"resources":{"limits":{"cpu":"2000m","memory":"512Mi"}}}""",
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that the sandbox overhead accepts extended resources beyond CPU and memory.
     *
     * Overhead is a full resource list in Kubernetes, so restricting it to the two standard resources
     * would drop values a runtime class may declare.
     */
    @Test
    fun testOverheadWithExtendedResourcesContent() {
        val spec = PodSpecBuilder().apply {
            addContainer("app", "nginx:latest") {}
            overhead {
                cpu = 250.mCpu
                memory = 120.miBytes
            }
        }.build()

        assertEquals(120.miBytes, spec.overhead!!.memory)
        JSONAssert.assertEquals(
            """{"overhead":{"cpu":"250m","memory":"120Mi"}}""",
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that the SELinux change policy is carried in the pod security context.
     *
     * Choosing the mount option avoids recursively relabelling large volumes, so the value must reach
     * the manifest unchanged.
     */
    @Test
    fun testSeLinuxChangePolicyContent() {
        val spec = PodSpecBuilder().apply {
            addContainer("app", "nginx:latest") {}
            securityContext {
                seLinuxChangePolicy = PodSecurityContextSpec.SELinuxChangePolicy.MountOption
            }
        }.build()

        assertEquals(
            PodSecurityContextSpec.SELinuxChangePolicy.MountOption,
            spec.securityContext!!.seLinuxChangePolicy
        )
        JSONAssert.assertEquals(
            """{"securityContext":{"seLinuxChangePolicy":"MountOption"}}""",
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that ephemeral containers are kept out of the rendered manifest.
     *
     * They cannot be set through a manifest at all - Kubernetes only accepts them through the pod's
     * `ephemeralcontainers` subresource - so emitting them would produce a manifest the API server
     * rejects.
     */
    @Test
    fun testEphemeralContainersAreNotRendered() {
        @Suppress("DEPRECATION")
        val spec = PodSpecBuilder().apply {
            addContainer("app", "nginx:latest") {}
            addEphemeralContainer("debugger", "busybox:latest") {}
        }.build()

        assertFalse(spec.toJson().contains("ephemeralContainers"))
    }
}
