package org.pcsoft.framework.kube.kts.core.merge

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.core.intern.json.yamlToJson
import org.pcsoft.framework.kube.kts.core.intern.setupTestLogger
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.nio.file.Path

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DefaultYamlMergingTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            setupTestLogger()
        }
    }

    @Test
    fun testWithBase() {
        val base = Path.of(this::class.java.getResource("/merge/base.yaml").toURI())
        val overlay = Path.of(this::class.java.getResource("/merge/overlay.yaml").toURI())
        val expectedEffective = IOUtils.resourceToString("/merge/effective.yaml", Charsets.UTF_8)

        val actualEffective = YamlMerging.createDefault().merge(base, overlay)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(expectedEffective.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testWithoutBase() {
        val base = Path.of(this::class.java.getResource("/merge/base.yaml").toURI())
        val overlay = Path.of(this::class.java.getResource("/merge/overlay.yaml").toURI())
        val expectedEffective = IOUtils.resourceToString("/merge/effective.yaml", Charsets.UTF_8)

        val actualEffective = YamlMerging.createDefault().merge(null, base, overlay)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(expectedEffective.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testNoOverlay() {
        val base = Path.of(this::class.java.getResource("/merge/base.yaml").toURI())
        val baseYaml = IOUtils.resourceToString("/merge/base.yaml", Charsets.UTF_8)

        val actualEffective = YamlMerging.createDefault().merge(base)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(baseYaml.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testNothing() {
        val actualEffective = YamlMerging.createDefault().merge(null)
        Assertions.assertNull(actualEffective)
    }

    @Test
    fun testNoFile() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            YamlMerging.createDefault().merge(Path.of("abc"))
        }
    }

}