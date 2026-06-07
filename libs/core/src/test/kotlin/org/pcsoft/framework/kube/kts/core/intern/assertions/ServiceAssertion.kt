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

package org.pcsoft.framework.kube.kts.core.intern.assertions

import org.junit.jupiter.api.Assertions
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec.*
import org.pcsoft.framework.kube.kts.api.chart.resources.types.Protocol
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpec
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object ServiceAssertion {

    fun assertMax(serviceSpec: ExplicitTemplateSpec<ServiceSpec>) {
        Assertions.assertNotNull(serviceSpec.metadata)
        Assertions.assertEquals("metadata", serviceSpec.metadata.name)
        Assertions.assertEquals("namespace", serviceSpec.metadata.namespace)
        Assertions.assertEquals("generateName", serviceSpec.metadata.generateName)

        Assertions.assertNotNull(serviceSpec.spec)
        Assertions.assertEquals(Type.LoadBalancer, serviceSpec.spec.type)

        Assertions.assertEquals(1, serviceSpec.spec.ports.size)
        Assertions.assertEquals("port", serviceSpec.spec.ports[0].name)
        Assertions.assertEquals(9999, serviceSpec.spec.ports[0].port)
        Assertions.assertEquals(8888, serviceSpec.spec.ports[0].targetPort)
        Assertions.assertEquals(7777, serviceSpec.spec.ports[0].nodePort)
        Assertions.assertEquals(Protocol.SCTP, serviceSpec.spec.ports[0].protocol)
        Assertions.assertEquals("https", serviceSpec.spec.ports[0].appProtocol)

        Assertions.assertEquals("clusterIP", serviceSpec.spec.clusterIP)
        Assertions.assertEquals(listOf("clusterIP"), serviceSpec.spec.clusterIPs)

        Assertions.assertEquals(setOf(IPFamily.IPv4, IPFamily.IPv6), serviceSpec.spec.ipFamilies)
        Assertions.assertEquals(FamilyPolicy.RequireDualStack, serviceSpec.spec.ipFamilyPolicy)

        Assertions.assertEquals(listOf("externalIP"), serviceSpec.spec.externalIPs)
        Assertions.assertEquals("externalName", serviceSpec.spec.externalName)

        Assertions.assertEquals(TrafficPolicy.Local, serviceSpec.spec.externalTrafficPolicy)
        Assertions.assertEquals(TrafficPolicy.Local, serviceSpec.spec.internalTrafficPolicy)

        Assertions.assertEquals(false, serviceSpec.spec.allocateLoadBalancerNodePorts)
        Assertions.assertEquals("loadBalancerIP", serviceSpec.spec.loadBalancerIP)
        Assertions.assertEquals("loadBalancerClass", serviceSpec.spec.loadBalancerClass)
        Assertions.assertEquals(listOf("loadBalancerSourceRange"), serviceSpec.spec.loadBalancerSourceRanges)

        Assertions.assertEquals(SessionAffinity.None, serviceSpec.spec.sessionAffinity)
        Assertions.assertEquals(
            SessionAffinityConfig(ClientIPConfig(30.seconds.toJavaDuration())),
            serviceSpec.spec.sessionAffinityConfig
        )

        Assertions.assertEquals(true, serviceSpec.spec.publishNotReadyAddresses)
        Assertions.assertEquals(3000, serviceSpec.spec.healthCheckNodePort)
        Assertions.assertEquals(TrafficDistribution.PreferClose, serviceSpec.spec.trafficDistribution)
    }

}