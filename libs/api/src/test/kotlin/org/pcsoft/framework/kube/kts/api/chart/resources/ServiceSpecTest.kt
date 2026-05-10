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
import org.pcsoft.framework.kube.kts.api.chart.resources.types.Protocol
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.util.*
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
        private val maxTemplate = TemplateSpecBuilder(ServiceSpec.API_VERSION, ServiceSpec.KIND, maxSpecBuilder).apply {
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
        Assertions.assertEquals(ServiceSpec.Type.LoadBalancer, maxSpec.type)

        Assertions.assertEquals(1, maxSpec.ports.size)
        Assertions.assertEquals("port", maxSpec.ports[0].name)
        Assertions.assertEquals(9999, maxSpec.ports[0].port)
        Assertions.assertEquals(8888, maxSpec.ports[0].targetPort)
        Assertions.assertEquals(7777, maxSpec.ports[0].nodePort)
        Assertions.assertEquals(Protocol.SCTP, maxSpec.ports[0].protocol)
        Assertions.assertEquals("https", maxSpec.ports[0].appProtocol)

        Assertions.assertEquals("clusterIP", maxSpec.clusterIP)
        Assertions.assertEquals(listOf("clusterIP"), maxSpec.clusterIPs)

        Assertions.assertEquals(setOf(ServiceSpec.IPFamily.IPv4, ServiceSpec.IPFamily.IPv6), maxSpec.ipFamilies)
        Assertions.assertEquals(ServiceSpec.FamilyPolicy.RequireDualStack, maxSpec.ipFamilyPolicy)

        Assertions.assertEquals(listOf("externalIP"), maxSpec.externalIPs)
        Assertions.assertEquals("externalName", maxSpec.externalName)

        Assertions.assertEquals(ServiceSpec.TrafficPolicy.Local, maxSpec.externalTrafficPolicy)
        Assertions.assertEquals(ServiceSpec.TrafficPolicy.Local, maxSpec.internalTrafficPolicy)

        Assertions.assertEquals(false, maxSpec.allocateLoadBalancerNodePorts)
        Assertions.assertEquals("loadBalancerIP", maxSpec.loadBalancerIP)
        Assertions.assertEquals("loadBalancerClass", maxSpec.loadBalancerClass)
        Assertions.assertEquals(listOf("loadBalancerSourceRange"), maxSpec.loadBalancerSourceRanges)

        Assertions.assertEquals(ServiceSpec.SessionAffinity.None, maxSpec.sessionAffinity)
        Assertions.assertEquals(
            ServiceSpec.SessionAffinityConfig(ServiceSpec.ClientIPConfig(30.seconds.toJavaDuration())),
            maxSpec.sessionAffinityConfig
        )

        Assertions.assertEquals(true, maxSpec.publishNotReadyAddresses)
        Assertions.assertEquals(3000, maxSpec.healthCheckNodePort)
        Assertions.assertEquals(ServiceSpec.TrafficDistribution.PreferClose, maxSpec.trafficDistribution)
    }

    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/service.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        println(actualJson)
        println(expectedJson)

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)

    }

}