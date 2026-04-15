package org.pcsoft.framework.kube.kts.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ServiceSpecTest {

    @Test
    fun test() {
        val spec = serviceSpec {
            metadata {
                name = "name"
                namespace = "namespace"
                generatedName = "generated-name"
            }
        }

        Assertions.assertEquals("name", spec.metadata.name)
        Assertions.assertEquals("namespace", spec.metadata.namespace)
        Assertions.assertEquals("generated-name", spec.metadata.generatedName)
    }

}