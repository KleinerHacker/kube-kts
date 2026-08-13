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
 * Tests for the `status` command using a mocked [HelmExecutor]. Unlike the render-based commands,
 * `status` needs neither a repository nor a rendering step: it is forwarded directly to Helm. The
 * executor captures the command line that would have been passed to Helm so it can be asserted.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class StatusTest {

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
        BaseDirectHelmCommand.helmExecutor = executor
    }

    @AfterEach
    fun restoreExecutor() {
        BaseDirectHelmCommand.helmExecutor = ProcessHelmExecutor
    }

    /**
     * Verifies that the release name is forwarded to Helm as a plain positional argument.
     *
     * `status` is a direct command: no repository is scanned and nothing is rendered before Helm is
     * invoked.
     */
    @Test
    fun releaseForwardedWithoutRepository() {
        val exitCode = runCli(arrayOf("status", "my-release"))

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(1, executor.invocations)
        Assertions.assertEquals(listOf("status", "my-release"), executor.capturedArgs)
        Assertions.assertNotNull(executor.capturedWorkingDir)
    }

    /**
     * Verifies that namespace, `--revision` and `--output` are forwarded to Helm.
     *
     * The short option `-n` must be expanded to `--namespace`, matching Helm's own spelling.
     */
    @Test
    fun forwardsFlags() {
        val exitCode = runCli(
            arrayOf("status", "rel", "-n", "ns", "--revision", "2", "--output", "json")
        )

        Assertions.assertEquals(0, exitCode)
        val args = executor.capturedArgs!!
        Assertions.assertEquals(listOf("status", "rel"), args.subList(0, 2))
        Assertions.assertTrue(args.containsAll(listOf("--namespace", "ns")), "namespace forwarded: $args")
        Assertions.assertTrue(args.containsAll(listOf("--revision", "2")), "revision forwarded: $args")
        Assertions.assertTrue(args.containsAll(listOf("--output", "json")), "output forwarded: $args")
    }

    /**
     * Verifies that a missing release name fails without invoking Helm.
     *
     * Picocli rejects the incomplete command line, so the exit code must be non-zero and the
     * executor must not have been called.
     */
    @Test
    fun failsWhenReleaseMissing() {
        val exitCode = runCli(arrayOf("status"))

        Assertions.assertNotEquals(0, exitCode)
        Assertions.assertEquals(0, executor.invocations, "Helm must not be invoked when the release name is missing")
    }
}
