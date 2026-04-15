package org.pcsoft.framework.kube.kts.api

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.types.PortSpec
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.pcsoft.framework.kube.kts.api.utils.toYaml
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.time.Duration.Companion.seconds

@Suppress("DEPRECATION")
class ServiceSpecTest {
    private val apiMax = serviceSpec {
        metadata {
            name = "name"
            namespace = "namespace"
            generatedName = "generated-name"
        }

        spec {
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
            sessionAffinityClientTimeout = 30.seconds

            publishNotReadyAddresses = true
            healthCheckNodePort = 3000
            trafficDistribution = ServiceSpec.TrafficDistribution.PreferClose
        }
    }

    @Test
    fun testContent() {
        Assertions.assertEquals("name", apiMax.metadata.name)
        Assertions.assertEquals("namespace", apiMax.metadata.namespace)
        Assertions.assertEquals("generated-name", apiMax.metadata.generatedName)

        Assertions.assertEquals(ServiceSpec.Type.LoadBalancer, apiMax.spec.type)

        Assertions.assertEquals(1, apiMax.spec.ports.size)
        Assertions.assertEquals("port", apiMax.spec.ports[0].name)
        Assertions.assertEquals(9999, apiMax.spec.ports[0].port)
        Assertions.assertEquals(8888, apiMax.spec.ports[0].targetPort)
        Assertions.assertEquals(7777, apiMax.spec.ports[0].nodePort)
        Assertions.assertEquals(PortSpec.Protocol.SCTP, apiMax.spec.ports[0].protocol)
        Assertions.assertEquals("https", apiMax.spec.ports[0].appProtocol)

        Assertions.assertEquals("clusterIP", apiMax.spec.clusterIP)
        Assertions.assertEquals(listOf("clusterIP"), apiMax.spec.clusterIPs)

        Assertions.assertEquals(setOf(ServiceSpec.IPFamily.IPv4, ServiceSpec.IPFamily.IPv6), apiMax.spec.ipFamilies)
        Assertions.assertEquals(ServiceSpec.FamilyPolicy.RequireDualStack, apiMax.spec.ipFamilyPolicy)

        Assertions.assertEquals(listOf("externalIP"), apiMax.spec.externalIPs)
        Assertions.assertEquals("externalName", apiMax.spec.externalName)

        Assertions.assertEquals(ServiceSpec.TrafficPolicy.Local, apiMax.spec.externalTrafficPolicy)
        Assertions.assertEquals(ServiceSpec.TrafficPolicy.Local, apiMax.spec.internalTrafficPolicy)

        Assertions.assertEquals(false, apiMax.spec.allocateLoadBalancerNodePorts)
        Assertions.assertEquals("loadBalancerIP", apiMax.spec.loadBalancerIP)
        Assertions.assertEquals("loadBalancerClass", apiMax.spec.loadBalancerClass)
        Assertions.assertEquals(listOf("loadBalancerSourceRange"), apiMax.spec.loadBalancerSourceRanges)

        Assertions.assertEquals(ServiceSpec.SessionAffinity.None, apiMax.spec.sessionAffinity)
        Assertions.assertEquals(ServiceSpec.SessionAffinityConfig(ServiceSpec.ClientIPConfig(30.seconds)), apiMax.spec.sessionAffinityConfig)

        Assertions.assertEquals(true, apiMax.spec.publishNotReadyAddresses)
        Assertions.assertEquals(3000, apiMax.spec.healthCheckNodePort)
        Assertions.assertEquals(ServiceSpec.TrafficDistribution.PreferClose, apiMax.spec.trafficDistribution)
    }

    @Test
    fun testYaml() {
        val expectedYaml = IOUtils.resourceToString("/service.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson<ResourceApi<ServiceSpec>>(expectedYaml)
        val actualJson = apiMax.toJson()

        println(apiMax.toYaml())
        println(expectedJson)
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)

    }

}