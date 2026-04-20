package org.pcsoft.framework.kube.kts.core.intern.assertions

import org.junit.jupiter.api.Assertions
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object ServiceAssertion {

    fun assertMax(serviceSpec: TemplateSpec<ServiceSpec>) {
        Assertions.assertNotNull(serviceSpec.metadata)
        Assertions.assertEquals("metadata", serviceSpec.metadata.name)
        Assertions.assertEquals("namespace", serviceSpec.metadata.namespace)
        Assertions.assertEquals("generateName", serviceSpec.metadata.generateName)

        Assertions.assertNotNull(serviceSpec.spec)
        Assertions.assertEquals(ServiceSpec.Type.LoadBalancer, serviceSpec.spec.type)

        Assertions.assertEquals(1, serviceSpec.spec.ports.size)
        Assertions.assertEquals("port", serviceSpec.spec.ports[0].name)
        Assertions.assertEquals(9999, serviceSpec.spec.ports[0].port)
        Assertions.assertEquals(8888, serviceSpec.spec.ports[0].targetPort)
        Assertions.assertEquals(7777, serviceSpec.spec.ports[0].nodePort)
        Assertions.assertEquals(PortSpec.Protocol.SCTP, serviceSpec.spec.ports[0].protocol)
        Assertions.assertEquals("https", serviceSpec.spec.ports[0].appProtocol)

        Assertions.assertEquals("clusterIP", serviceSpec.spec.clusterIP)
        Assertions.assertEquals(listOf("clusterIP"), serviceSpec.spec.clusterIPs)

        Assertions.assertEquals(setOf(ServiceSpec.IPFamily.IPv4, ServiceSpec.IPFamily.IPv6), serviceSpec.spec.ipFamilies)
        Assertions.assertEquals(ServiceSpec.FamilyPolicy.RequireDualStack, serviceSpec.spec.ipFamilyPolicy)

        Assertions.assertEquals(listOf("externalIP"), serviceSpec.spec.externalIPs)
        Assertions.assertEquals("externalName", serviceSpec.spec.externalName)

        Assertions.assertEquals(ServiceSpec.TrafficPolicy.Local, serviceSpec.spec.externalTrafficPolicy)
        Assertions.assertEquals(ServiceSpec.TrafficPolicy.Local, serviceSpec.spec.internalTrafficPolicy)

        Assertions.assertEquals(false, serviceSpec.spec.allocateLoadBalancerNodePorts)
        Assertions.assertEquals("loadBalancerIP", serviceSpec.spec.loadBalancerIP)
        Assertions.assertEquals("loadBalancerClass", serviceSpec.spec.loadBalancerClass)
        Assertions.assertEquals(listOf("loadBalancerSourceRange"), serviceSpec.spec.loadBalancerSourceRanges)

        Assertions.assertEquals(ServiceSpec.SessionAffinity.None, serviceSpec.spec.sessionAffinity)
        Assertions.assertEquals(
            ServiceSpec.SessionAffinityConfig(ServiceSpec.ClientIPConfig(30.seconds.toJavaDuration())),
            serviceSpec.spec.sessionAffinityConfig
        )

        Assertions.assertEquals(true, serviceSpec.spec.publishNotReadyAddresses)
        Assertions.assertEquals(3000, serviceSpec.spec.healthCheckNodePort)
        Assertions.assertEquals(ServiceSpec.TrafficDistribution.PreferClose, serviceSpec.spec.trafficDistribution)
    }

}