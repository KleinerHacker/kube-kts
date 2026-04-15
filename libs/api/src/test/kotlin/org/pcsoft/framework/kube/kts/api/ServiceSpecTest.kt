package org.pcsoft.framework.kube.kts.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.types.PortSpec
import org.pcsoft.framework.kube.kts.api.utils.toYaml

class ServiceSpecTest {
    private val api = serviceSpec {
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
        }
    }

    @Test
    fun testContent() {
        Assertions.assertEquals("name", api.metadata.name)
        Assertions.assertEquals("namespace", api.metadata.namespace)
        Assertions.assertEquals("generated-name", api.metadata.generatedName)

        Assertions.assertEquals(ServiceSpec.Type.LoadBalancer, api.spec.type)

        Assertions.assertEquals(1, api.spec.ports.size)
        Assertions.assertEquals("port", api.spec.ports[0].name)
        Assertions.assertEquals(9999, api.spec.ports[0].port)
        Assertions.assertEquals(8888, api.spec.ports[0].targetPort)
        Assertions.assertEquals(7777, api.spec.ports[0].nodePort)
        Assertions.assertEquals(PortSpec.Protocol.SCTP, api.spec.ports[0].protocol)
        Assertions.assertEquals("https", api.spec.ports[0].appProtocol)
    }

    @Test
    fun testYaml() {
        //val expectedJson = IOUtils.resourceToString("/service-spec.yml", Charsets.UTF_8)
        val actualJson = api.toYaml()

        println(actualJson)
        //JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)

    }

}