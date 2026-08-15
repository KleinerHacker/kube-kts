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
import org.pcsoft.framework.kube.kts.api.types.miBytes
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ContainerSpecTest {
    companion object {
        private val maxSpec = ContainerSpecBuilder("container", "nginx:latest").apply {
            imagePullPolicy = ContainerSpec.ImagePullPolicy.Always
            ports {
                port(8080) {
                    name = "http"
                    protocol = Protocol.TCP
                }
            }
            env("ENVIRONMENT") {
                from {
                    value("production")
                }
            }
            envFrom {
                prefix = "APP_"
                configMapRef("app-config") {
                    optional = true
                }
            }
            resources {
                limits {
                    cpu = 0.5f.cpu
                    memory = 256.miBytes
                }
            }
            volumeMounts {
                volumeMount("config", "/etc/config") {
                    readOnly = true
                }
            }
            volumeDevices {
                volumeDevice("device", "/dev/xvda")
            }
            livenessProbe {
                httpGet(8080) {
                    path = "/health"
                }
            }
            readinessProbe {
                tcpSocket(8080)
            }
            startupProbe {
                exec {
                    command("test", "-f", "/tmp/started")
                }
            }
            lifecycle {
                postStart {
                    exec {
                        command("echo", "started")
                    }
                }
                preStop {
                    exec {
                        command("echo", "stopped")
                    }
                }
            }
            terminationMessagePath = "/dev/termination-log"
            terminationMessagePolicy = ContainerSpec.TerminationMessagePolicy.FallbackToLogsOnError
            stdin = true
            stdinOnce = true
            tty = true
            securityContext {
                runAsUser = 1000L
            }
            command("/bin/sh", "-c")
            args("echo", "started")
            workingDir = "/app"
        }.build()

        private val minSpec = ContainerSpecBuilder("container", "nginx:latest").build()
    }

    /**
     * Verifies that the maximal ContainerSpec definition is built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertEquals("container", maxSpec.name)
        assertEquals("nginx:latest", maxSpec.image)
        assertEquals(ContainerSpec.ImagePullPolicy.Always, maxSpec.imagePullPolicy)

        assertNotNull(maxSpec.ports)
        assertEquals("http", maxSpec.ports.first().name)
        assertEquals(8080, maxSpec.ports.first().containerPort)
        assertEquals(Protocol.TCP, maxSpec.ports.first().protocol)

        assertNotNull(maxSpec.env)
        assertEquals("ENVIRONMENT", maxSpec.env.first().name)
        assertEquals("production", (maxSpec.env.first().source as SingleEnvironmentSpec.ValueSource).value)

        assertNotNull(maxSpec.envFrom)
        assertEquals("APP_", maxSpec.envFrom.first().prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, maxSpec.envFrom.first().source.type)
        assertEquals("app-config", maxSpec.envFrom.first().source.name)
        assertEquals(true, maxSpec.envFrom.first().source.optional)

        assertNotNull(maxSpec.resources)
        val limits = assertNotNull(maxSpec.resources.limits)
        assertEquals(0.5f.cpu, limits.cpu)
        assertEquals(256.miBytes, limits.memory)

        assertNotNull(maxSpec.volumeMounts)
        assertEquals("config", maxSpec.volumeMounts.first().name)
        assertEquals("/etc/config", maxSpec.volumeMounts.first().mountPath)
        assertEquals(true, maxSpec.volumeMounts.first().readOnly)

        assertNotNull(maxSpec.volumeDevices)
        assertEquals("device", maxSpec.volumeDevices.first().name)
        assertEquals("/dev/xvda", maxSpec.volumeDevices.first().devicePath)

        assertNotNull(maxSpec.livenessProbe)
        assertNotNull(maxSpec.readinessProbe)
        assertNotNull(maxSpec.startupProbe)
        assertNotNull(maxSpec.lifecycle)

        assertEquals("/dev/termination-log", maxSpec.terminationMessagePath)
        assertEquals(ContainerSpec.TerminationMessagePolicy.FallbackToLogsOnError, maxSpec.terminationMessagePolicy)
        assertEquals(true, maxSpec.stdin)
        assertEquals(true, maxSpec.stdinOnce)
        assertEquals(true, maxSpec.tty)

        assertNotNull(maxSpec.securityContext)
        assertEquals(1000L, maxSpec.securityContext.runAsUser)
        assertEquals(listOf("/bin/sh", "-c"), maxSpec.command)
        assertEquals(listOf("echo", "started"), maxSpec.args)
        assertEquals("/app", maxSpec.workingDir)
    }

    /**
     * Verifies that the maximal ContainerSpec definition is serialised into the expected YAML
     * document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val actualJson = maxSpec.toJson()
        val expectedJson = """{
          |  "name": "container",
          |  "image": "nginx:latest",
          |  "imagePullPolicy": "Always",
          |  "ports": [{
          |    "name": "http",
          |    "containerPort": 8080,
          |    "protocol": "TCP"
          |  }],
          |  "env": [{
          |    "name": "ENVIRONMENT",
          |    "value": "production"
          |  }],
          |  "envFrom": [{
          |    "prefix": "APP_",
          |    "configMapRef": {
          |      "name": "app-config",
          |      "optional": true
          |    }
          |  }],
          |  "resources": {
          |    "limits": {
          |      "cpu": "500m",
          |      "memory": "256Mi"
          |    }
          |  },
          |  "volumeMounts": [{
          |    "name": "config",
          |    "mountPath": "/etc/config",
          |    "readOnly": true
          |  }],
          |  "volumeDevices": [{
          |    "name": "device",
          |    "devicePath": "/dev/xvda"
          |  }],
          |  "livenessProbe": {"httpGet":{"path":"/health","port":8080}},
          |  "readinessProbe": {"tcpSocket":{"port":8080}},
          |  "startupProbe": {"exec":{"command":["test","-f","/tmp/started"]}},
          |  "lifecycle": {
          |    "postStart": {"exec":{"command":["echo","started"]}},
          |    "preStop": {"exec":{"command":["echo","stopped"]}}
          |  },
          |  "terminationMessagePath": "/dev/termination-log",
          |  "terminationMessagePolicy": "FallbackToLogsOnError",
          |  "stdin": true,
          |  "stdinOnce": true,
          |  "tty": true,
          |  "securityContext": {"runAsUser": 1000},
          |  "command": ["/bin/sh", "-c"],
          |  "args": ["echo", "started"],
          |  "workingDir": "/app"
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal ContainerSpec definition is built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertEquals("container", minSpec.name)
        assertEquals("nginx:latest", minSpec.image)
        assertNull(minSpec.imagePullPolicy)
        assertNull(minSpec.ports)
        assertNull(minSpec.env)
        assertNull(minSpec.envFrom)
        assertNull(minSpec.resources)
        assertNull(minSpec.volumeMounts)
        assertNull(minSpec.volumeDevices)
        assertNull(minSpec.livenessProbe)
        assertNull(minSpec.readinessProbe)
        assertNull(minSpec.startupProbe)
        assertNull(minSpec.lifecycle)
        assertNull(minSpec.terminationMessagePath)
        assertNull(minSpec.terminationMessagePolicy)
        assertNull(minSpec.stdin)
        assertNull(minSpec.stdinOnce)
        assertNull(minSpec.tty)
        assertNull(minSpec.securityContext)
        assertNull(minSpec.command)
        assertNull(minSpec.args)
        assertNull(minSpec.workingDir)
    }

    /**
     * Verifies that the minimal ContainerSpec definition is serialised into the expected YAML
     * document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals(
            """{"name":"container","image":"nginx:latest"}""",
            minSpec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that an init container declared as a native sidecar carries the restart policy.
     *
     * Setting `restartPolicy` to `Always` on an init container is what turns it into a sidecar that
     * keeps running alongside the pod's main containers instead of running to completion.
     */
    @Test
    fun testSidecarRestartPolicyContent() {
        val sidecar = ContainerSpecBuilder("proxy", "envoy:latest").apply {
            restartPolicy = ContainerSpec.RestartPolicy.Always
        }.build()

        assertEquals(ContainerSpec.RestartPolicy.Always, sidecar.restartPolicy)
        JSONAssert.assertEquals(
            """{"name":"proxy","image":"envoy:latest","restartPolicy":"Always"}""",
            sidecar.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a container declares per-resource in-place resize behaviour.
     *
     * CPU changes are applied to the running process while memory changes require a restart, which is
     * the combination Kubernetes documents as the common case.
     */
    @Test
    fun testResizePolicyContent() {
        val spec = ContainerSpecBuilder("app", "nginx:latest").apply {
            addResizePolicy(
                ResourceResizePolicySpec.ResourceName.Cpu,
                ResourceResizePolicySpec.RestartPolicy.NotRequired
            )
            addResizePolicy(
                ResourceResizePolicySpec.ResourceName.Memory,
                ResourceResizePolicySpec.RestartPolicy.RestartContainer
            )
        }.build()

        assertEquals(2, spec.resizePolicy!!.size)
        JSONAssert.assertEquals(
            """{"resizePolicy":[
               |  {"resourceName":"cpu","restartPolicy":"NotRequired"},
               |  {"resourceName":"memory","restartPolicy":"RestartContainer"}
               |]}""".trimMargin(),
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a container references the pod's resource claims from its resource requirements.
     *
     * The claim is declared on the pod; the container only opts into using it by name, optionally
     * narrowing the usage to a single request of that claim.
     */
    @Test
    fun testResourceClaimsContent() {
        val spec = ContainerSpecBuilder("app", "nginx:latest").apply {
            resources {
                addClaim("gpu")
                addClaim("fast-nic", "primary")
            }
        }.build()

        val claims = spec.resources!!.claims!!
        assertEquals(2, claims.size)
        assertEquals(ResourceClaimReferenceSpec("gpu", null), claims[0])
        assertEquals(ResourceClaimReferenceSpec("fast-nic", "primary"), claims[1])

        JSONAssert.assertEquals(
            """{"resources":{"claims":[{"name":"gpu"},{"name":"fast-nic","request":"primary"}]}}""",
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }
}
