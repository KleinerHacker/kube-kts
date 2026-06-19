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
        assertEquals("ENVIRONMENT", maxSpec.env.name)
        assertEquals("production", (maxSpec.env.source as SingleEnvironmentSpec.ValueSource).value)

        assertNotNull(maxSpec.envFrom)
        assertEquals("APP_", maxSpec.envFrom.prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, maxSpec.envFrom.source.type)
        assertEquals("app-config", maxSpec.envFrom.source.name)
        assertEquals(true, maxSpec.envFrom.source.optional)

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
          |  "env": {
          |    "name": "ENVIRONMENT",
          |    "value": "production"
          |  },
          |  "envFrom": {
          |    "prefix": "APP_",
          |    "configMapRef": {
          |      "name": "app-config",
          |      "optional": true
          |    }
          |  },
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

    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals("""{"name":"container","image":"nginx:latest"}""", minSpec.toJson(), JSONCompareMode.LENIENT)
    }

}
