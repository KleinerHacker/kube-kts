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
 * Developer tests for the internal YAML merge implementation created by [YamlMerging.createDefault].
 *
 * The merge combines a base `values.yaml` with an arbitrary number of overlays and produces the
 * effective values a chart is rendered with. All cases are verified against the fixtures below
 * `/merge` and compared leniently as JSON, so key order does not matter.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DefaultYamlMergingTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            setupTestLogger()
        }
    }

    /**
     * Verifies that a base file and an overlay are merged into the expected effective values.
     *
     * Given `base.yaml` as the base and `overlay.yaml` as the single overlay, the result must equal
     * the fixture `effective.yaml`: overlay keys win, keys only present in the base survive.
     */
    @Test
    fun testWithBase() {
        val base = Path.of(this::class.java.getResource("/merge/base.yaml").toURI())
        val overlay = Path.of(this::class.java.getResource("/merge/overlay.yaml").toURI())
        val expectedEffective = IOUtils.resourceToString("/merge/effective.yaml", Charsets.UTF_8)

        val actualEffective = YamlMerging.createDefault().merge(base, overlay)
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

        val actualEffective = YamlMerging.createDefault().merge(null, base, overlay)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(expectedEffective.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that a base file without any overlay is returned unchanged.
     *
     * With nothing to merge in, the effective values must be identical to `base.yaml`.
     */
    @Test
    fun testNoOverlay() {
        val base = Path.of(this::class.java.getResource("/merge/base.yaml").toURI())
        val baseYaml = IOUtils.resourceToString("/merge/base.yaml", Charsets.UTF_8)

        val actualEffective = YamlMerging.createDefault().merge(base)
        Assertions.assertNotNull(actualEffective)

        JSONAssert.assertEquals(baseYaml.yamlToJson(), actualEffective!!.yamlToJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that merging without any file yields `null`.
     *
     * A chart without values must not produce an empty document but the explicit absence of values.
     */
    @Test
    fun testNothing() {
        val actualEffective = YamlMerging.createDefault().merge(null)
        Assertions.assertNull(actualEffective)
    }

    /**
     * Verifies that a non-existing file is rejected.
     *
     * Merging a path that does not point to a regular file must fail fast with an
     * [IllegalArgumentException] instead of silently ignoring the file.
     */
    @Test
    fun testNoFile() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            YamlMerging.createDefault().merge(Path.of("abc"))
        }
    }

}