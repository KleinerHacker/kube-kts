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

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec.IPFamily
import kotlin.test.assertEquals

/**
 * Tests the collection oriented parts of [ServiceSpecBuilder].
 *
 * Every list-valued property offers three ways to be filled - a single `addX`, a vararg `addXs` and a
 * nested list block - and all of them have to end up in the same specification.
 */
class ServiceSpecBuilderListTest {

    /**
     * Verifies that cluster IPs can be added individually, as varargs and through a list block.
     */
    @Test
    fun testClusterIps() {
        val spec = ServiceSpecBuilder().apply {
            addPort("http", 80) {}
            addClusterIP("10.0.0.1")
            addClusterIPs("10.0.0.2", "10.0.0.3")
            clusterIPs {
                clusterIP("10.0.0.4")
            }
        }.build()

        assertEquals(listOf("10.0.0.1", "10.0.0.2", "10.0.0.3", "10.0.0.4"), spec.clusterIPs)
    }

    /**
     * Verifies that IP families can be added individually, as varargs and through a list block.
     *
     * `addAllIpFamilies` is the shorthand for a dual-stack service and must add both families.
     */
    @Test
    fun testIpFamilies() {
        val single = ServiceSpecBuilder().apply {
            addPort("http", 80) {}
            addIpFamily(IPFamily.IPv4)
        }.build()
        assertEquals(setOf(IPFamily.IPv4), single.ipFamilies)

        val varargs = ServiceSpecBuilder().apply {
            addPort("http", 80) {}
            addIpFamilies(IPFamily.IPv4, IPFamily.IPv6)
        }.build()
        assertEquals(setOf(IPFamily.IPv4, IPFamily.IPv6), varargs.ipFamilies)

        val all = ServiceSpecBuilder().apply {
            addPort("http", 80) {}
            addAllIpFamilies()
        }.build()
        assertEquals(setOf(IPFamily.IPv4, IPFamily.IPv6), all.ipFamilies)

        val block = ServiceSpecBuilder().apply {
            addPort("http", 80) {}
            ipFamilies {
                ipFamily(IPFamily.IPv6)
            }
        }.build()
        assertEquals(setOf(IPFamily.IPv6), block.ipFamilies)
    }

    /**
     * Verifies that external IPs can be added individually, as varargs and through a list block.
     */
    @Test
    fun testExternalIps() {
        val spec = ServiceSpecBuilder().apply {
            addPort("http", 80) {}
            addExternalIP("192.0.2.1")
            addExternalIPs("192.0.2.2", "192.0.2.3")
            externalIPs {
                externalIP("192.0.2.4")
            }
        }.build()

        assertEquals(listOf("192.0.2.1", "192.0.2.2", "192.0.2.3", "192.0.2.4"), spec.externalIPs)
    }

    /**
     * Verifies that load balancer source ranges can be added in all three supported ways.
     *
     * These ranges restrict who may reach a load balancer, so dropping one would silently widen access.
     */
    @Test
    fun testLoadBalancerSourceRanges() {
        val spec = ServiceSpecBuilder().apply {
            addPort("http", 80) {}
            type = ServiceSpec.Type.LoadBalancer
            addLoadBalancerSourceRange("10.0.0.0/8")
            addLoadBalancerSourceRanges("192.168.0.0/16", "172.16.0.0/12")
            loadBalancerSourceRanges {
                loadBalancerSourceRange("203.0.113.0/24")
            }
        }.build()

        assertEquals(
            listOf("10.0.0.0/8", "192.168.0.0/16", "172.16.0.0/12", "203.0.113.0/24"),
            spec.loadBalancerSourceRanges
        )
    }

    /**
     * Verifies that several ports can be declared through the nested list block.
     */
    @Test
    fun testPortsBlock() {
        val spec = ServiceSpecBuilder().apply {
            ports {
                port("http", 80) { targetPortName = "http" }
                port("https", 443) { targetPort = 8443 }
            }
        }.build()

        assertEquals(2, spec.ports?.size)
        assertEquals("http", spec.ports?.get(0)?.name)
        assertEquals("https", spec.ports?.get(1)?.name)
    }
}
