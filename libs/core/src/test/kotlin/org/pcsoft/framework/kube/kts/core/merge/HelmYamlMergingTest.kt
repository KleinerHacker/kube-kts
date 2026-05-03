/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

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
class HelmYamlMergingTest {

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

        val actualEffective = YamlMerging.HELM.merge(base, overlay)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(expectedEffective.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testWithoutBase() {
        val base = Path.of(this::class.java.getResource("/merge/base.yaml").toURI())
        val overlay = Path.of(this::class.java.getResource("/merge/overlay.yaml").toURI())
        val expectedEffective = IOUtils.resourceToString("/merge/effective.yaml", Charsets.UTF_8)

        val actualEffective = YamlMerging.HELM.merge(null, base, overlay)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(expectedEffective.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testNoOverlay() {
        val base = Path.of(this::class.java.getResource("/merge/base.yaml").toURI())
        val baseYaml = IOUtils.resourceToString("/merge/base.yaml", Charsets.UTF_8)

        val actualEffective = YamlMerging.HELM.merge(base)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(baseYaml.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testNothing() {
        val actualEffective = YamlMerging.HELM.merge(null)
        Assertions.assertNull(actualEffective)
    }

    @Test
    fun testNoFile() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            YamlMerging.HELM.merge(Path.of("abc"))
        }
    }

}