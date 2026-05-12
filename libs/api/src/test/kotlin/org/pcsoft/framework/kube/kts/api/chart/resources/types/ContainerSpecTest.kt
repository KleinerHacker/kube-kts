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

    @Test
    fun testMaxContent() {
        val spec = buildMaxSpec()

        assertEquals("container", spec.name)
        assertEquals("nginx:latest", spec.image)
        assertEquals(ContainerSpec.ImagePullPolicy.Always, spec.imagePullPolicy)

        assertNotNull(spec.ports)
        assertEquals("http", spec.ports.first().name)
        assertEquals(8080, spec.ports.first().containerPort)
        assertEquals(Protocol.TCP, spec.ports.first().protocol)

        assertNotNull(spec.env)
        assertEquals("ENVIRONMENT", spec.env.name)
        assertEquals("production", (spec.env.source as SingleEnvironmentSpec.ValueSource).value)

        assertNotNull(spec.envFrom)
        assertEquals("APP_", spec.envFrom.prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, spec.envFrom.source.type)
        assertEquals("app-config", spec.envFrom.source.name)
        assertEquals(true, spec.envFrom.source.optional)

        assertNotNull(spec.resources)
        assertNotNull(spec.resources.limits)
        assertEquals(0.5f.cpu, spec.resources.limits!!.cpu)
        assertEquals(256.miBytes, spec.resources.limits!!.memory)

        assertNotNull(spec.volumeMounts)
        assertEquals("config", spec.volumeMounts.first().name)
        assertEquals("/etc/config", spec.volumeMounts.first().mountPath)
        assertEquals(true, spec.volumeMounts.first().readOnly)

        assertNotNull(spec.volumeDevices)
        assertEquals("device", spec.volumeDevices.first().name)
        assertEquals("/dev/xvda", spec.volumeDevices.first().devicePath)

        assertNotNull(spec.livenessProbe)
        assertNotNull(spec.readinessProbe)
        assertNotNull(spec.startupProbe)
        assertNotNull(spec.lifecycle)

        assertEquals("/dev/termination-log", spec.terminationMessagePath)
        assertEquals(ContainerSpec.TerminationMessagePolicy.FallbackToLogsOnError, spec.terminationMessagePolicy)
        assertEquals(true, spec.stdin)
        assertEquals(true, spec.stdinOnce)
        assertEquals(true, spec.tty)

        assertNotNull(spec.securityContext)
        assertEquals(1000L, spec.securityContext.runAsUser)
        assertEquals(listOf("/bin/sh", "-c"), spec.command)
        assertEquals(listOf("echo", "started"), spec.args)
        assertEquals("/app", spec.workingDir)
    }

    @Test
    fun testMaxYaml() {
        val actualJson = buildMaxSpec().toJson()
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
        val spec = ContainerSpecBuilder("container", "nginx:latest").build()

        assertEquals("container", spec.name)
        assertEquals("nginx:latest", spec.image)
        assertNull(spec.imagePullPolicy)
        assertNull(spec.ports)
        assertNull(spec.env)
        assertNull(spec.envFrom)
        assertNull(spec.resources)
        assertNull(spec.volumeMounts)
        assertNull(spec.volumeDevices)
        assertNull(spec.livenessProbe)
        assertNull(spec.readinessProbe)
        assertNull(spec.startupProbe)
        assertNull(spec.lifecycle)
        assertNull(spec.terminationMessagePath)
        assertNull(spec.terminationMessagePolicy)
        assertNull(spec.stdin)
        assertNull(spec.stdinOnce)
        assertNull(spec.tty)
        assertNull(spec.securityContext)
        assertNull(spec.command)
        assertNull(spec.args)
        assertNull(spec.workingDir)
    }

    @Test
    fun testMinYaml() {
        val spec = ContainerSpecBuilder("container", "nginx:latest").build()

        JSONAssert.assertEquals("""{"name":"container","image":"nginx:latest"}""", spec.toJson(), JSONCompareMode.LENIENT)
    }

    private fun buildMaxSpec() = ContainerSpecBuilder("container", "nginx:latest").apply {
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

}