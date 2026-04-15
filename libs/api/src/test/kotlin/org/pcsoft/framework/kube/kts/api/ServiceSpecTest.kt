package org.pcsoft.framework.kube.kts.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.PrintStream

class ServiceSpecTest {

    @Test
    fun testContent() {
        val spec = serviceSpec {
            metadata {
                name = "name"
                namespace = "namespace"
                generatedName = "generated-name"
            }

            type = ServiceSpec.Type.ClusterIP

            addPort("port") {
                port = 8080
                targetPort = 8080
            }
        }

        Assertions.assertEquals("name", spec.metadata.name)
        Assertions.assertEquals("namespace", spec.metadata.namespace)
        Assertions.assertEquals("generated-name", spec.metadata.generatedName)
//
//        Assertions.assertEquals(ServiceSpec.Type.ClusterIP, spec.type)
//
//        Assertions.assertEquals(1, spec.ports.size)
//        Assertions.assertEquals("port", spec.ports[0].name)
//        Assertions.assertEquals(8080, spec.ports[0].port)
//        Assertions.assertEquals(8080, spec.ports[0].targetPort)
    }

    @Test
    fun testYaml() {
        val spec = serviceSpec {
            metadata {
                name = "name"
                namespace = "namespace"
                generatedName = "generated-name"
            }

            type = ServiceSpec.Type.ClusterIP

            addPort("port") {
                port = 8080
                targetPort = 8080
            }
        }

        PrintStream(System.out).use { spec.printYaml(it) }
    }

}