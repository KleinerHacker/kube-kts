package org.pcsoft.framework.kube.kts.api.chart.resources

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Suppress("DEPRECATION")
class ServiceSpecTest {
    companion object {
        private val maxSpecBuilder = ServiceSpecBuilder().apply {
            type = ServiceSpec.Type.LoadBalancer

            addPort("port") {
                port = 9999
                targetPort = 8888
                nodePort = 7777
                protocol = PortSpec.Protocol.SCTP
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
        Assertions.assertEquals(PortSpec.Protocol.SCTP, maxSpec.ports[0].protocol)
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
        val expectedJson = convertToJson<TemplateSpec<ServiceSpec>>(expectedYaml)
        val actualJson = maxTemplate.toJson()

        println("Expect: $expectedJson")
        println("Actual: $actualJson")

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)

    }

}