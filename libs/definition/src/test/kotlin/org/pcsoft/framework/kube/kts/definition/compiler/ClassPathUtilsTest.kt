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

package org.pcsoft.framework.kube.kts.definition.compiler

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import java.io.File
import java.net.URL

/**
 * Developer tests for the internal helpers of `KubeKtsSpecCompilationConfiguration.kt`:
 * [findHelmRoot], [getJarFromClass] and the URL conversions.
 *
 * [findHelmRoot] drives the discovery of `*.lib.kts` files, so a wrong result silently strips all
 * helper functions from a spec script. The JAR/URL helpers decide which artifacts end up on the
 * script classpath and must never throw for an unexpected URL shape.
 */
class ClassPathUtilsTest {

    /**
     * Verifies that the helm root is found when the file sits directly inside `helm`.
     *
     * A `Chart.spec.kts` in the repository root is the shallowest possible case.
     */
    @Test
    fun findsHelmRootForADirectChild(@TempDir tempDir: File) {
        val helm = File(tempDir, "helm").apply { mkdirs() }
        val script = File(helm, "Chart.spec.kts").apply { writeText("") }

        Assertions.assertEquals(helm, findHelmRoot(script))
    }

    /**
     * Verifies that the helm root is found from an arbitrarily deep sub-directory.
     *
     * Templates may be organised in nested folders; the search must walk up until it hits `helm`.
     */
    @Test
    fun findsHelmRootFromANestedDirectory(@TempDir tempDir: File) {
        val helm = File(tempDir, "helm").apply { mkdirs() }
        val nested = File(helm, "templates/deeply/nested").apply { mkdirs() }
        val script = File(nested, "deployment.spec.kts").apply { writeText("") }

        Assertions.assertEquals(helm, findHelmRoot(script))
    }

    /**
     * Verifies that the directory name is matched case-insensitively.
     *
     * On Windows a directory may well be named `Helm`; it must be recognised all the same.
     */
    @Test
    fun findsHelmRootIgnoringCase(@TempDir tempDir: File) {
        val helm = File(tempDir, "Helm").apply { mkdirs() }
        val script = File(helm, "Chart.spec.kts").apply { writeText("") }

        Assertions.assertEquals(helm, findHelmRoot(script))
    }

    /**
     * Verifies that the innermost `helm` directory wins when several are nested.
     *
     * A chart may contain a sub-chart below `helm/charts/x/helm`; the closest root is the one the
     * script belongs to.
     */
    @Test
    fun findsTheInnermostHelmRoot(@TempDir tempDir: File) {
        val outer = File(tempDir, "helm").apply { mkdirs() }
        val inner = File(outer, "charts/sub/helm").apply { mkdirs() }
        val script = File(inner, "Chart.spec.kts").apply { writeText("") }

        Assertions.assertEquals(inner, findHelmRoot(script))
    }

    /**
     * Verifies that `null` is returned when no `helm` directory is present in the path.
     *
     * The refinement step uses this to skip the library discovery instead of failing.
     */
    @Test
    fun returnsNullWithoutAHelmDirectory(@TempDir tempDir: File) {
        val script = File(tempDir, "other/deployment.spec.kts").apply {
            parentFile.mkdirs()
            writeText("")
        }

        Assertions.assertNull(findHelmRoot(script))
    }

    /**
     * Verifies that a `null` input is tolerated.
     *
     * IntelliJ may hand over a script source without a backing file, in which case the caller
     * passes `null` and must not receive an exception.
     */
    @Test
    fun returnsNullForANullFile() {
        Assertions.assertNull(findHelmRoot(null))
    }

    /**
     * Verifies that the JAR lookup returns either the containing JAR or `null`.
     *
     * During a Gradle test run the classes are loaded from a directory rather than from a JAR, so
     * `null` is the expected result; the call must not throw in either case.
     */
    @Test
    fun jarLookupNeverThrows() {
        val jar = getJarFromClass(ChartSpec::class)
        if (jar != null) {
            Assertions.assertTrue(jar.name.endsWith(".jar"), "Expected a JAR file, got: $jar")
        }
    }

    /**
     * Verifies that a non-JAR URL yields no containing JAR.
     *
     * A class loaded from a plain directory has a `file:` URL, which must map to `null` instead of
     * being cast to a JAR connection.
     */
    @Test
    fun fileUrlHasNoContainingJar() {
        Assertions.assertNull(URL("file:/tmp/classes/Some.class").toContainingJarOrNull())
    }

    /**
     * Verifies that a `file:` URL is converted into a [File].
     *
     * This is the path used when the API classes are loaded from an exploded directory.
     */
    @Test
    fun fileUrlIsConvertedToAFile() {
        val file = File(System.getProperty("java.io.tmpdir"), "kube-kts-url-test.txt")
        Assertions.assertEquals(file.absoluteFile, file.toURI().toURL().toFileOrNull()?.absoluteFile)
    }

    /**
     * Verifies that a non-file URL is rejected without an exception.
     *
     * An `http:` URL cannot denote a local artifact; the conversion must answer `null` rather than
     * propagating the `URISyntaxException`/`IllegalArgumentException` of `File(URI)`.
     */
    @Test
    fun nonFileUrlIsRejected() {
        Assertions.assertNull(URL("http://example.org/lib.jar").toFileOrNull())
    }
}
