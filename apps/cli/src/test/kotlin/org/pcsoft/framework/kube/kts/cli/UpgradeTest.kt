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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.pcsoft.framework.kube.kts.cli.commands.BaseRenderedHelmCommand
import org.pcsoft.framework.kube.kts.cli.commands.HelmExecutor
import org.pcsoft.framework.kube.kts.cli.intern.RepoType
import java.nio.file.Path

/**
 * Tests for the `upgrade` command using a mocked [HelmExecutor]: the full pipeline (scan, compile,
 * render) runs for real, but Helm itself is never invoked. The executor captures the command line
 * that would have been passed to Helm so it can be asserted.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UpgradeTest {

    /** Records the arguments and working directory of the last (mocked) Helm invocation. */
    private class CapturingHelmExecutor : HelmExecutor {
        var capturedArgs: List<String>? = null
        var capturedWorkingDir: Path? = null
        var invocations = 0

        override fun execute(args: List<String>, workingDir: Path): Int {
            capturedArgs = args
            capturedWorkingDir = workingDir
            invocations++
            return 0
        }
    }

    private lateinit var executor: CapturingHelmExecutor

    @BeforeEach
    fun installMock() {
        executor = CapturingHelmExecutor()
        BaseRenderedHelmCommand.helmExecutor = executor
    }

    @AfterEach
    fun restoreExecutor() {
        BaseRenderedHelmCommand.helmExecutor = org.pcsoft.framework.kube.kts.cli.commands.ProcessHelmExecutor
    }

    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun namePassedAsPositional(type: RepoType) {
        val exitCode = runCli(arrayOf("upgrade", "src/test/resources/${type.path}", "--name", "demo"))

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(1, executor.invocations)
        Assertions.assertEquals(listOf("upgrade", "demo", "."), executor.capturedArgs)
        Assertions.assertNotNull(executor.capturedWorkingDir)
    }

    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun forwardsFlagsAndValues(type: RepoType) {
        val valuesFile = Path.of(this::class.java.getResource("/values-overlay.yaml").toURI()).toString()

        val exitCode = runCli(
            arrayOf(
                "upgrade", "src/test/resources/${type.path}", "--name", "demo",
                "-n", "ns", "--set", "a=1", "--install", "--atomic", "--reuse-values",
                "--history-max", "5", "-f", valuesFile,
            )
        )

        Assertions.assertEquals(0, exitCode)
        val args = executor.capturedArgs!!
        Assertions.assertEquals(listOf("upgrade", "demo", "."), args.subList(0, 3))
        Assertions.assertTrue(args.containsAll(listOf("--namespace", "ns")), "namespace forwarded: $args")
        Assertions.assertTrue(args.containsAll(listOf("--set", "a=1")), "set forwarded: $args")
        Assertions.assertTrue(args.contains("--install"), "flag forwarded: $args")
        Assertions.assertTrue(args.contains("--atomic"), "flag forwarded: $args")
        Assertions.assertTrue(args.contains("--reuse-values"), "flag forwarded: $args")
        Assertions.assertTrue(args.containsAll(listOf("--history-max", "5")), "history-max forwarded: $args")
        Assertions.assertTrue(args.containsAll(listOf("-f", valuesFile)), "values forwarded: $args")
    }

    @Test
    fun failsWithoutInvokingHelmWhenRepositoryMissing() {
        val exitCode = runCli(arrayOf("upgrade", "abc", "--name", "demo"))

        Assertions.assertNotEquals(0, exitCode)
        Assertions.assertEquals(0, executor.invocations, "Helm must not be invoked when the repository is missing")
    }
}
