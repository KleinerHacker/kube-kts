package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CompleteEnvironmentSpecTest {

    @Test
    fun testConfigMapRefContentMax() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            prefix = "prefix"
            configMapRef("config-map") {
                optional = true
            }
        }.build()

        assertEquals("prefix", spec.prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, spec.source.type)
        assertEquals("config-map", spec.source.name)
        assertEquals(true, spec.source.optional)
    }

    @Test
    fun testConfigMapRefYamlMax() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            prefix = "prefix"
            configMapRef("config-map") {
                optional = true
            }
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{"prefix":"prefix","configMapRef":{"name":"config-map","optional":true}}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testConfigMapRefContentMin() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            configMapRef("config-map")
        }.build()

        assertEquals(CompleteEnvironmentSpec.SourceType.ConfigMap, spec.source.type)
        assertNull(spec.prefix)
        assertEquals("config-map", spec.source.name)
        assertNull(spec.source.optional)
    }

    @Test
    fun testConfigMapRefYamlMin() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            configMapRef("config-map")
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{"configMapRef":{"name":"config-map"}}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testSecretRefContentMax() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            prefix = "prefix"
            secretRef("secret") {
                optional = true
            }
        }.build()

        assertEquals("prefix", spec.prefix)
        assertEquals(CompleteEnvironmentSpec.SourceType.Secret, spec.source.type)
        assertEquals("secret", spec.source.name)
        assertEquals(true, spec.source.optional)
    }

    @Test
    fun testSecretRefYamlMax() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            prefix = "prefix"
            secretRef("secret") {
                optional = true
            }
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{"prefix":"prefix","secretRef":{"name":"secret","optional":true}}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testSecretRefContentMin() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            secretRef("secret")
        }.build()

        assertEquals(CompleteEnvironmentSpec.SourceType.Secret, spec.source.type)
        assertNull(spec.prefix)
        assertEquals("secret", spec.source.name)
        assertNull(spec.source.optional)
    }

    @Test
    fun testSecretRefYamlMin() {
        val spec = CompleteEnvironmentSpecBuilder().apply {
            secretRef("secret")
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{"secretRef":{"name":"secret"}}"""

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

}