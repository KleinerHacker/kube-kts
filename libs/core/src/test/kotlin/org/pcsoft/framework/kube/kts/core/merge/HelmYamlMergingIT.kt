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

/**
 * Integration tests for [YamlMerging.HELM], the merge strategy that delegates to the Helm binary.
 *
 * Unlike the internal implementation this strategy builds a temporary chart and renders it with
 * Helm, so a `helm` binary must be available on the `PATH`. The tests assert that it produces the
 * same effective values as the internal implementation for the fixtures below `/merge`.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HelmYamlMergingIT {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            setupTestLogger()
        }
    }

    /**
     * Verifies that Helm merges a base file and an overlay into the expected effective values.
     *
     * Given `base.yaml` as the base and `overlay.yaml` as the single overlay, the result must equal
     * the fixture `effective.yaml`.
     */
    @Test
    fun testWithBase() {
        val base = Path.of(this::class.java.getResource("/merge/base.yaml").toURI())
        val overlay = Path.of(this::class.java.getResource("/merge/overlay.yaml").toURI())
        val expectedEffective = IOUtils.resourceToString("/merge/effective.yaml", Charsets.UTF_8)

        val actualEffective = YamlMerging.HELM.merge(base, overlay)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(expectedEffective.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the first overlay acts as the base when no base file is given.
     *
     * Passing `null` as the base and `base.yaml`/`overlay.yaml` as overlays must produce the same
     * effective values as the explicit base case.
     */
    @Test
    fun testWithoutBase() {
        val base = Path.of(this::class.java.getResource("/merge/base.yaml").toURI())
        val overlay = Path.of(this::class.java.getResource("/merge/overlay.yaml").toURI())
        val expectedEffective = IOUtils.resourceToString("/merge/effective.yaml", Charsets.UTF_8)

        val actualEffective = YamlMerging.HELM.merge(null, base, overlay)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(expectedEffective.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that a base file without any overlay is returned unchanged.
     *
     * Helm is still invoked, but with nothing to merge in the result must equal `base.yaml`.
     */
    @Test
    fun testNoOverlay() {
        val base = Path.of(this::class.java.getResource("/merge/base.yaml").toURI())
        val baseYaml = IOUtils.resourceToString("/merge/base.yaml", Charsets.UTF_8)

        val actualEffective = YamlMerging.HELM.merge(base)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(baseYaml.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that merging without any file yields `null` and never invokes Helm.
     *
     * A chart without values must not produce an empty document but the explicit absence of values.
     */
    @Test
    fun testNothing() {
        val actualEffective = YamlMerging.HELM.merge(null)
        Assertions.assertNull(actualEffective)
    }

    /**
     * Verifies that a non-existing file is rejected before Helm is invoked.
     *
     * The path check must fail fast with an [IllegalArgumentException].
     */
    @Test
    fun testNoFile() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            YamlMerging.HELM.merge(Path.of("abc"))
        }
    }

}