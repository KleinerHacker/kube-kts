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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import kotlin.script.experimental.api.ScriptAcceptedLocation
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.acceptedLocations
import kotlin.script.experimental.api.compilerOptions
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.refineConfigurationBeforeCompiling

/**
 * Developer tests for [KubeKtsSpecCompilationConfiguration] and
 * [KubeKtsLibCompilationConfiguration].
 *
 * Both configurations are the contract between the KTS files in a repository and the DSL of
 * `libs/api`: they decide which types are available without an `import` statement. Since spec
 * scripts may not use `import` at all (script safety model), a missing default import would make
 * a part of the DSL unreachable — these tests pin the set of imports and the remaining compiler
 * settings.
 */
class CompilationConfigurationTest {

    private val specImports =
        KubeKtsSpecCompilationConfiguration[ScriptCompilationConfiguration.defaultImports].orEmpty()
    private val libImports =
        KubeKtsLibCompilationConfiguration[ScriptCompilationConfiguration.defaultImports].orEmpty()

    /**
     * Verifies that the spec configuration imports every DSL package of `libs/api` by wildcard.
     *
     * The parameter provides the packages holding the chart specs, the resource specs and the
     * shared sub-specs; each must be present as a `<package>.*` entry.
     */
    @ParameterizedTest
    @ValueSource(
        strings = [
            "org.pcsoft.framework.kube.kts.api.chart.*",
            "org.pcsoft.framework.kube.kts.api.chart.resources.*",
            "org.pcsoft.framework.kube.kts.api.chart.resources.types.*",
            "org.pcsoft.framework.kube.kts.api.chart.template.*",
            "org.pcsoft.framework.kube.kts.api.chart.types.*",
            "org.pcsoft.framework.kube.kts.api.types.*",
            "org.pcsoft.framework.kube.kts.api.values.*",
        ]
    )
    fun specConfigurationImportsAllDslPackages(import: String) {
        Assertions.assertTrue(specImports.contains(import), "Missing default import '$import' in: $specImports")
    }

    /**
     * Verifies that the spec configuration imports the standard Java and Kotlin helpers used by
     * the DSL.
     *
     * The parameter provides the URL/URI types, the `java.time` package and the Kotlin duration
     * API — all of them are used in spec scripts (for example for probe timeouts) and cannot be
     * imported manually because of the script safety model.
     */
    @ParameterizedTest
    @ValueSource(strings = ["java.net.URL", "java.net.URI", "java.time.*", "kotlin.time.*"])
    fun specConfigurationImportsTheStandardHelpers(import: String) {
        Assertions.assertTrue(specImports.contains(import), "Missing default import '$import' in: $specImports")
    }

    /**
     * Verifies that the packages of the anchor DSL types are actually covered by the imports.
     *
     * Instead of comparing literal strings this derives the expected entries from
     * [ChartSpec] and [ResourceSpec], so moving a type to another package makes the test fail
     * instead of silently dropping its import.
     */
    @Test
    fun specImportsAreDerivedFromTheApiPackages() {
        Assertions.assertTrue(specImports.contains("${ChartSpec::class.java.packageName}.*"))
        Assertions.assertTrue(specImports.contains("${ResourceSpec::class.java.packageName}.*"))
    }

    /**
     * Verifies that the library configuration provides exactly the same default imports as the
     * spec configuration.
     *
     * Helper functions in `*.lib.kts` build the same DSL objects as the spec scripts, so any
     * difference between the two import sets would make a helper uncompilable.
     */
    @Test
    fun libAndSpecConfigurationShareTheirImports() {
        Assertions.assertEquals(specImports.toSet(), libImports.toSet())
    }

    /**
     * Verifies that the spec configuration exposes [ValueAccess] as an implicit receiver.
     *
     * This is what makes `value(...)`, `array(...)` and `exists(...)` callable at the top level of
     * a spec script without any qualifier.
     */
    @Test
    fun specConfigurationProvidesTheValueAccessReceiver() {
        val receivers = KubeKtsSpecCompilationConfiguration[ScriptCompilationConfiguration.implicitReceivers]
        Assertions.assertNotNull(receivers, "Spec scripts must declare an implicit receiver")
        Assertions.assertTrue(
            receivers!!.any { it.typeName.contains(ValueAccess::class.simpleName!!) },
            "Expected a ValueAccess receiver in: $receivers",
        )
    }

    /**
     * Verifies that the library configuration declares no implicit receiver.
     *
     * Library scripts are compiled as dependencies of spec scripts; an additional receiver there
     * would shadow the receiver of the including spec script.
     */
    @Test
    fun libConfigurationHasNoImplicitReceiver() {
        val receivers = KubeKtsLibCompilationConfiguration[ScriptCompilationConfiguration.implicitReceivers]
        Assertions.assertTrue(receivers.isNullOrEmpty(), "Library scripts must not declare a receiver: $receivers")
    }

    /**
     * Verifies that both configurations compile scripts against the same JVM target as the modules.
     *
     * A mismatch between the JVM target of the compiled scripts and of `libs/api` would fail at
     * class loading time, not at compile time.
     */
    @Test
    fun bothConfigurationsPinTheJvmTarget() {
        val expected = listOf("-jvm-target", "25")
        Assertions.assertEquals(expected, KubeKtsSpecCompilationConfiguration[ScriptCompilationConfiguration.compilerOptions])
        Assertions.assertEquals(expected, KubeKtsLibCompilationConfiguration[ScriptCompilationConfiguration.compilerOptions])
    }

    /**
     * Verifies that both configurations accept scripts in every IDE location.
     *
     * Without this, IntelliJ would only resolve the DSL inside recognised source roots, so a
     * `helm` directory next to the sources would show unresolved references.
     */
    @Test
    fun bothConfigurationsAreAcceptedEverywhereInTheIde() {
        listOf(KubeKtsSpecCompilationConfiguration, KubeKtsLibCompilationConfiguration).forEach { configuration ->
            val locations = configuration[ScriptCompilationConfiguration.ide.acceptedLocations]
            Assertions.assertNotNull(locations, "IDE locations must be configured for $configuration")
            Assertions.assertTrue(
                locations!!.contains(ScriptAcceptedLocation.Everywhere),
                "Expected 'Everywhere' in: $locations",
            )
        }
    }

    /**
     * Verifies that only the spec configuration refines itself before compiling.
     *
     * The refinement discovers the sibling `*.lib.kts` files and adds them as imported scripts;
     * doing the same for library scripts would create an import cycle.
     */
    @Test
    fun onlyTheSpecConfigurationRefinesBeforeCompiling() {
        Assertions.assertFalse(
            KubeKtsSpecCompilationConfiguration[ScriptCompilationConfiguration.refineConfigurationBeforeCompiling]
                .isNullOrEmpty(),
            "Spec scripts must discover their library scripts",
        )
        Assertions.assertTrue(
            KubeKtsLibCompilationConfiguration[ScriptCompilationConfiguration.refineConfigurationBeforeCompiling]
                .isNullOrEmpty(),
            "Library scripts must not import other scripts",
        )
    }
}
