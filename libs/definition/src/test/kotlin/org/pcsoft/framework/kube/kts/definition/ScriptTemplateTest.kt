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

package org.pcsoft.framework.kube.kts.definition

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsLibCompilationConfiguration
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsLibEvaluationConfiguration
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsSpecCompilationConfiguration
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsSpecEvaluationConfiguration
import kotlin.script.experimental.annotations.KotlinScript

/**
 * Developer tests for the two script definitions [SpecTemplate] and [LibTemplate].
 *
 * Both are pure marker classes: everything they contribute lives in their `@KotlinScript`
 * annotation, which IntelliJ and the script host read to decide *which* file is compiled with
 * *which* configuration. A wrong file path pattern silently disables the DSL for a whole file
 * category, so the patterns are verified against concrete paths here.
 */
class ScriptTemplateTest {

    private val specAnnotation = SpecTemplate::class.java.getAnnotation(KotlinScript::class.java)
    private val libAnnotation = LibTemplate::class.java.getAnnotation(KotlinScript::class.java)

    /**
     * Verifies that [SpecTemplate] is annotated with `@KotlinScript` and wires in the spec
     * compilation and evaluation configuration.
     *
     * Without this wiring a `*.spec.kts` file would be compiled as a plain Kotlin script, i.e.
     * without any of the Kube KTS default imports and without the implicit value receiver.
     */
    @Test
    fun specTemplateUsesTheSpecConfigurations() {
        Assertions.assertNotNull(specAnnotation, "SpecTemplate must be annotated with @KotlinScript")
        Assertions.assertEquals(KubeKtsSpecCompilationConfiguration::class, specAnnotation.compilationConfiguration)
        Assertions.assertEquals(KubeKtsSpecEvaluationConfiguration::class, specAnnotation.evaluationConfiguration)
        Assertions.assertEquals("Kube KTS Spec", specAnnotation.displayName)
    }

    /**
     * Verifies that [LibTemplate] is annotated with `@KotlinScript` and wires in the library
     * compilation and evaluation configuration.
     *
     * Library scripts share the default imports of the spec scripts but must not receive the
     * implicit value receiver, which is why they use their own configuration pair.
     */
    @Test
    fun libTemplateUsesTheLibConfigurations() {
        Assertions.assertNotNull(libAnnotation, "LibTemplate must be annotated with @KotlinScript")
        Assertions.assertEquals(KubeKtsLibCompilationConfiguration::class, libAnnotation.compilationConfiguration)
        Assertions.assertEquals(KubeKtsLibEvaluationConfiguration::class, libAnnotation.evaluationConfiguration)
        Assertions.assertEquals("Kube KTS Library", libAnnotation.displayName)
    }

    /**
     * Verifies that the spec pattern matches `*.spec.kts` files below a `helm` directory.
     *
     * The parameter provides the same path with a forward slash and with a backslash separator as
     * well as a nested `templates` directory, covering both Linux/macOS and Windows layouts.
     */
    @ParameterizedTest
    @ValueSource(
        strings = [
            "/project/helm/Chart.spec.kts",
            "/project/helm/templates/deployment.spec.kts",
            "C:\\project\\helm\\templates\\service.spec.kts",
            "project/helm/templates/deeply/nested/route.spec.kts",
        ]
    )
    fun specPatternMatchesSpecScriptsBelowHelm(path: String) {
        Assertions.assertTrue(
            Regex(specAnnotation.filePathPattern).matches(path),
            "Expected the spec pattern to match: $path",
        )
    }

    /**
     * Verifies that the spec pattern rejects files that are not spec scripts below a `helm`
     * directory.
     *
     * The parameter provides a library script, a plain Kotlin script, a spec script outside of
     * `helm` and a Gradle build script — none of them must be compiled as a Kube KTS spec.
     */
    @ParameterizedTest
    @ValueSource(
        strings = [
            "/project/helm/templates/helpers.lib.kts",
            "/project/helm/templates/deployment.kts",
            "/project/other/deployment.spec.kts",
            "/project/build.gradle.kts",
        ]
    )
    fun specPatternRejectsEverythingElse(path: String) {
        Assertions.assertFalse(
            Regex(specAnnotation.filePathPattern).matches(path),
            "Expected the spec pattern to reject: $path",
        )
    }

    /**
     * Verifies that the library pattern matches `*.lib.kts` files below a `helm` directory.
     *
     * The parameter provides both path separators and a nested directory, mirroring the layouts
     * the scanner produces on Linux/macOS and on Windows.
     */
    @ParameterizedTest
    @ValueSource(
        strings = [
            "/project/helm/helpers.lib.kts",
            "/project/helm/templates/helpers.lib.kts",
            "C:\\project\\helm\\templates\\helpers.lib.kts",
        ]
    )
    fun libPatternMatchesLibraryScriptsBelowHelm(path: String) {
        Assertions.assertTrue(
            Regex(libAnnotation.filePathPattern).matches(path),
            "Expected the lib pattern to match: $path",
        )
    }

    /**
     * Verifies that the library pattern rejects spec scripts and files outside of `helm`.
     *
     * Otherwise a spec script would be compiled without its implicit value receiver, which would
     * break every `value(...)` call inside it.
     */
    @ParameterizedTest
    @ValueSource(
        strings = [
            "/project/helm/templates/deployment.spec.kts",
            "/project/other/helpers.lib.kts",
            "/project/build.gradle.kts",
        ]
    )
    fun libPatternRejectsEverythingElse(path: String) {
        Assertions.assertFalse(
            Regex(libAnnotation.filePathPattern).matches(path),
            "Expected the lib pattern to reject: $path",
        )
    }

    /**
     * Verifies that the spec and the library pattern never match the same file.
     *
     * If both matched, the script host could pick either definition for a file and the behaviour
     * would depend on the discovery order.
     */
    @Test
    fun patternsAreMutuallyExclusive() {
        val spec = Regex(specAnnotation.filePathPattern)
        val lib = Regex(libAnnotation.filePathPattern)
        listOf(
            "/project/helm/templates/deployment.spec.kts",
            "/project/helm/templates/helpers.lib.kts",
        ).forEach { path ->
            Assertions.assertFalse(
                spec.matches(path) && lib.matches(path),
                "Patterns must not both match: $path",
            )
        }
    }
}
