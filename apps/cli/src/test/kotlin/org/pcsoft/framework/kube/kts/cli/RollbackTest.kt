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
 * Tests for the `rollback` command using a mocked [HelmExecutor]. Forwarded directly to Helm.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RollbackTest {

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
    fun releaseAndRevisionForwarded() {
        val exitCode = runCli(arrayOf("rollback", "rel", "2", "--force"))

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(1, executor.invocations)
        val args = executor.capturedArgs!!
        Assertions.assertEquals(listOf("rollback", "rel", "2"), args.subList(0, 3))
        Assertions.assertTrue(args.contains("--force"), "force forwarded: $args")
    }

    @Test
    fun revisionOptional() {
        val exitCode = runCli(arrayOf("rollback", "rel"))

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(listOf("rollback", "rel"), executor.capturedArgs)
    }

    @Test
    fun failsWhenReleaseMissing() {
        val exitCode = runCli(arrayOf("rollback"))

        Assertions.assertNotEquals(0, exitCode)
        Assertions.assertEquals(0, executor.invocations, "Helm must not be invoked when the release name is missing")
    }
}
