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
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import tools.jackson.dataformat.yaml.YAMLMapper
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.scriptsInstancesSharing

/**
 * Developer tests for [KubeKtsSpecEvaluationConfiguration] and
 * [KubeKtsLibEvaluationConfiguration].
 *
 * The evaluation configuration decides what a script sees at *runtime*. For spec scripts this is
 * the concrete [ValueAccess] instance built from the merged `values.yaml`; getting it wrong means
 * every value lookup in a rendered chart resolves against the wrong data.
 */
class EvaluationConfigurationTest {

    /** Creates an empty but valid value access, as used for a chart without any values. */
    private fun emptyValueAccess(): ValueAccess = ValueAccess.ofRoot(YAMLMapper().createObjectNode())

    /**
     * Verifies that the spec configuration passes the given [ValueAccess] to the script.
     *
     * The instance handed to the constructor must show up as the implicit receiver, since the
     * script body is compiled as an extension of exactly that receiver.
     */
    @Test
    fun specConfigurationPassesTheGivenValueAccess() {
        val valueAccess = emptyValueAccess()
        val configuration = KubeKtsSpecEvaluationConfiguration(valueAccess)

        val receivers = configuration[ScriptEvaluationConfiguration.implicitReceivers]
        Assertions.assertNotNull(receivers, "The value access must be provided as implicit receiver")
        Assertions.assertSame(valueAccess, receivers!!.single())
    }

    /**
     * Verifies that the no-argument constructor falls back to an empty value access.
     *
     * IntelliJ instantiates the configuration reflectively without arguments; the fallback keeps
     * code completion working in the IDE instead of failing with a missing receiver.
     */
    @Test
    fun specConfigurationFallsBackToAnEmptyValueAccess() {
        val receivers = KubeKtsSpecEvaluationConfiguration()[ScriptEvaluationConfiguration.implicitReceivers]
        Assertions.assertNotNull(receivers, "The fallback must still provide a receiver")
        Assertions.assertInstanceOf(ValueAccess::class.java, receivers!!.single())
    }

    /**
     * Verifies that the spec configuration enables script instance sharing.
     *
     * A repository compiles many spec scripts against the same library scripts; without sharing,
     * every spec script would re-evaluate its libraries and produce its own instances.
     */
    @Test
    fun specConfigurationSharesScriptInstances() {
        Assertions.assertNotNull(
            KubeKtsSpecEvaluationConfiguration(emptyValueAccess())[ScriptEvaluationConfiguration.scriptsInstancesSharing],
        )
    }

    /**
     * Verifies that the library configuration enables script instance sharing as well.
     *
     * A library script included by several spec scripts must be evaluated exactly once, otherwise
     * top-level state in a helper file would differ per spec script.
     */
    @Test
    fun libConfigurationSharesScriptInstances() {
        Assertions.assertNotNull(
            KubeKtsLibEvaluationConfiguration[ScriptEvaluationConfiguration.scriptsInstancesSharing],
        )
    }

    /**
     * Verifies that the library configuration provides no implicit receiver.
     *
     * Library scripts are evaluated as part of the including spec script and inherit its receiver;
     * a second one would shadow the values of the spec script.
     */
    @Test
    fun libConfigurationHasNoImplicitReceiver() {
        val receivers = KubeKtsLibEvaluationConfiguration[ScriptEvaluationConfiguration.implicitReceivers]
        Assertions.assertTrue(receivers.isNullOrEmpty(), "Library scripts must not bring their own receiver")
    }

    /**
     * Verifies that two configurations built from different value accesses stay independent.
     *
     * Rendering several charts in one process must not leak the values of one chart into another.
     */
    @Test
    fun configurationsDoNotShareTheirValueAccess() {
        val first = emptyValueAccess()
        val second = emptyValueAccess()

        val firstReceiver = KubeKtsSpecEvaluationConfiguration(first)[ScriptEvaluationConfiguration.implicitReceivers]
        val secondReceiver = KubeKtsSpecEvaluationConfiguration(second)[ScriptEvaluationConfiguration.implicitReceivers]

        Assertions.assertSame(first, firstReceiver!!.single())
        Assertions.assertSame(second, secondReceiver!!.single())
    }
}
