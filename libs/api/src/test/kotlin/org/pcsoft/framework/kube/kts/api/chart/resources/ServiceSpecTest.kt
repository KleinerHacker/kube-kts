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
import org.pcsoft.framework.kube.kts.api.chart.resources.types.Protocol
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Suppress("DEPRECATION")
class ServiceSpecTest {
    companion object {
        private val maxSpecBuilder = ServiceSpecBuilder().apply {
            type = ServiceSpec.Type.LoadBalancer

            addPort("port", 9999) {
                targetPort = 8888
                nodePort = 7777
                protocol = Protocol.SCTP
                appProtocol = "https"
            }

            addClusterIP("clusterIP")

            addIpFamily(ServiceSpec.IPFamily.IPv4)
            addIpFamily(ServiceSpec.IPFamily.IPv6)
            ipFamilyPolicy = ServiceSpec.FamilyPolicy.RequireDualStack

            addExternalIP("externalIP")
            externalName = "externalName"

            externalTrafficPolicy = ServiceSpec.TrafficPolicy.Local
            internalTrafficPolicy = ServiceSpec.TrafficPolicy.Local

            allocateLoadBalancerNodePorts = false
            loadBalancerIP = "loadBalancerIP"
            loadBalancerClass = "loadBalancerClass"
            addLoadBalancerSourceRange("loadBalancerSourceRange")

            sessionAffinity = ServiceSpec.SessionAffinity.None
            sessionAffinityClientTimeout = 30.seconds.toJavaDuration()

            publishNotReadyAddresses = true
            healthCheckNodePort = 3000
            trafficDistribution = ServiceSpec.TrafficDistribution.PreferClose
        }

        private val maxSpec = maxSpecBuilder.build()
        private val maxTemplate = ExplicitTemplateSpecBuilder(ServiceSpec.API_VERSION, ServiceSpec.KIND, maxSpecBuilder).apply {
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

        private val minSpec = ServiceSpecBuilder().apply {
            addPort("port", 9999) {}
        }.build()
    }

    @Test
    fun testMaxContent() {
        assertEquals(ServiceSpec.Type.LoadBalancer, maxSpec.type)

        assertEquals(1, maxSpec.ports.size)
        assertEquals("port", maxSpec.ports[0].name)
        assertEquals(9999, maxSpec.ports[0].port)
        assertEquals(8888, maxSpec.ports[0].targetPort)
        assertEquals(7777, maxSpec.ports[0].nodePort)
        assertEquals(Protocol.SCTP, maxSpec.ports[0].protocol)
        assertEquals("https", maxSpec.ports[0].appProtocol)

        assertEquals("clusterIP", maxSpec.clusterIP)
        assertEquals(listOf("clusterIP"), maxSpec.clusterIPs)

        assertEquals(setOf(ServiceSpec.IPFamily.IPv4, ServiceSpec.IPFamily.IPv6), maxSpec.ipFamilies)
        assertEquals(ServiceSpec.FamilyPolicy.RequireDualStack, maxSpec.ipFamilyPolicy)

        assertEquals(listOf("externalIP"), maxSpec.externalIPs)
        assertEquals("externalName", maxSpec.externalName)

        assertEquals(ServiceSpec.TrafficPolicy.Local, maxSpec.externalTrafficPolicy)
        assertEquals(ServiceSpec.TrafficPolicy.Local, maxSpec.internalTrafficPolicy)

        assertEquals(false, maxSpec.allocateLoadBalancerNodePorts)
        assertEquals("loadBalancerIP", maxSpec.loadBalancerIP)
        assertEquals("loadBalancerClass", maxSpec.loadBalancerClass)
        assertEquals(listOf("loadBalancerSourceRange"), maxSpec.loadBalancerSourceRanges)

        assertEquals(ServiceSpec.SessionAffinity.None, maxSpec.sessionAffinity)
        assertEquals(
            ServiceSpec.SessionAffinityConfig(ServiceSpec.ClientIPConfig(30.seconds.toJavaDuration())),
            maxSpec.sessionAffinityConfig
        )

        assertEquals(true, maxSpec.publishNotReadyAddresses)
        assertEquals(3000, maxSpec.healthCheckNodePort)
        assertEquals(ServiceSpec.TrafficDistribution.PreferClose, maxSpec.trafficDistribution)
    }

    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/service.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        assertNull(minSpec.type)
        assertNull(minSpec.selector)

        assertEquals(1, minSpec.ports.size)
        assertEquals("port", minSpec.ports[0].name)
        assertEquals(9999, minSpec.ports[0].port)
        assertNull(minSpec.ports[0].targetPort)
        assertNull(minSpec.ports[0].nodePort)
        assertNull(minSpec.ports[0].protocol)
        assertNull(minSpec.ports[0].appProtocol)

        assertNull(minSpec.clusterIP)
        assertNull(minSpec.clusterIPs)
        assertNull(minSpec.ipFamilies)
        assertNull(minSpec.ipFamilyPolicy)
        assertNull(minSpec.externalIPs)
        assertNull(minSpec.externalName)
        assertNull(minSpec.externalTrafficPolicy)
        assertNull(minSpec.internalTrafficPolicy)
        assertNull(minSpec.allocateLoadBalancerNodePorts)
        assertNull(minSpec.loadBalancerIP)
        assertNull(minSpec.loadBalancerClass)
        assertNull(minSpec.loadBalancerSourceRanges)
        assertNull(minSpec.sessionAffinity)
        assertNull(minSpec.sessionAffinityConfig)
        assertNull(minSpec.publishNotReadyAddresses)
        assertNull(minSpec.healthCheckNodePort)
        assertNull(minSpec.trafficDistribution)
    }

    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals(
            """{
              |  "ports": [
              |    {
              |      "name": "port",
              |      "port": 9999
              |    }
              |  ]
              |}""".trimMargin(),
            minSpec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

}
