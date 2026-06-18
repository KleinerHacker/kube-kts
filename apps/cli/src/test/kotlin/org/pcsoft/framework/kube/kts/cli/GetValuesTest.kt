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

package org.pcsoft.framework.kube.kts.cli

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.cli.commands.BaseDirectHelmCommand
import org.pcsoft.framework.kube.kts.cli.commands.HelmExecutor
import org.pcsoft.framework.kube.kts.cli.commands.ProcessHelmExecutor
import java.nio.file.Path

/**
 * Tests for the nested `get values` command using a mocked [HelmExecutor]. Verifies that nested
 * sub-commands are resolved and forwarded directly to Helm.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class GetValuesTest {

    private class CapturingHelmExecutor : HelmExecutor {
        var capturedArgs: List<String>? = null
        var invocations = 0

        override fun execute(args: List<String>, workingDir: Path): Int {
            capturedArgs = args
            invocations++
            return 0
        }
    }

    private lateinit var executor: CapturingHelmExecutor

    @BeforeEach
    fun installMock() {
        executor = CapturingHelmExecutor()
        BaseDirectHelmCommand.helmExecutor = executor
    }

    @AfterEach
    fun restoreExecutor() {
        BaseDirectHelmCommand.helmExecutor = ProcessHelmExecutor
    }

    @Test
    fun nestedSubcommandForwarded() {
        val exitCode = runCli(arrayOf("get", "values", "rel", "-a", "-o", "json", "-n", "ns"))

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(1, executor.invocations)
        val args = executor.capturedArgs!!
        Assertions.assertEquals(listOf("get", "values", "rel"), args.subList(0, 3))
        Assertions.assertTrue(args.contains("--all"), "all forwarded: $args")
        Assertions.assertTrue(args.containsAll(listOf("--output", "json")), "output forwarded: $args")
        Assertions.assertTrue(args.containsAll(listOf("--namespace", "ns")), "namespace forwarded: $args")
    }

    @Test
    fun failsWhenReleaseMissing() {
        val exitCode = runCli(arrayOf("get", "values"))

        Assertions.assertNotEquals(0, exitCode)
        Assertions.assertEquals(0, executor.invocations, "Helm must not be invoked when the release name is missing")
    }

    @Test
    fun groupWithoutSubcommandPrintsUsage() {
        // Invoking the group itself must not call Helm.
        val exitCode = runCli(arrayOf("get"))
        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(0, executor.invocations)
    }
}
