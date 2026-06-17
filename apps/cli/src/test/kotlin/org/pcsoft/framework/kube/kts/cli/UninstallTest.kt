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
 * Tests for the `uninstall` command using a mocked [HelmExecutor]: the full pipeline (scan, compile,
 * render) runs for real, but Helm itself is never invoked. The executor captures the command line
 * that would have been passed to Helm so it can be asserted.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UninstallTest {

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
    fun releasesPassedAsPositionals(type: RepoType) {
        val exitCode = runCli(
            arrayOf("uninstall", "src/test/resources/${type.path}", "--name", "rel1", "--name", "rel2")
        )

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(1, executor.invocations)
        Assertions.assertEquals(listOf("uninstall", "rel1", "rel2"), executor.capturedArgs)
        Assertions.assertNotNull(executor.capturedWorkingDir)
    }

    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun forwardsFlags(type: RepoType) {
        val exitCode = runCli(
            arrayOf(
                "uninstall", "src/test/resources/${type.path}", "--name", "rel",
                "-n", "ns", "--keep-history", "--timeout", "1m",
            )
        )

        Assertions.assertEquals(0, exitCode)
        val args = executor.capturedArgs!!
        Assertions.assertEquals(listOf("uninstall", "rel"), args.subList(0, 2))
        Assertions.assertTrue(args.containsAll(listOf("--namespace", "ns")), "namespace forwarded: $args")
        Assertions.assertTrue(args.contains("--keep-history"), "flag forwarded: $args")
        Assertions.assertTrue(args.containsAll(listOf("--timeout", "1m")), "timeout forwarded: $args")
    }

    @Test
    fun failsWithoutInvokingHelmWhenRepositoryMissing() {
        val exitCode = runCli(arrayOf("uninstall", "abc", "--name", "rel"))

        Assertions.assertNotEquals(0, exitCode)
        Assertions.assertEquals(0, executor.invocations, "Helm must not be invoked when the repository is missing")
    }
}
