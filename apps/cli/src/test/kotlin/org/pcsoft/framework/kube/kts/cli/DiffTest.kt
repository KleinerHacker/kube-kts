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
import org.pcsoft.framework.kube.kts.cli.commands.ProcessHelmExecutor
import org.pcsoft.framework.kube.kts.cli.intern.RepoType
import java.nio.file.Path

/**
 * Tests for the nested, render based `diff upgrade` command (helm-diff plugin) using a mocked
 * [HelmExecutor]. The release name is passed via `--name` and forwarded as the positional RELEASE.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DiffTest {

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
        BaseRenderedHelmCommand.helmExecutor = executor
    }

    @AfterEach
    fun restoreExecutor() {
        BaseRenderedHelmCommand.helmExecutor = ProcessHelmExecutor
    }

    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun rendersThenDiffsUpgrade(type: RepoType) {
        val exitCode = runCli(
            arrayOf("diff", "upgrade", "src/test/resources/${type.path}", "--name", "demo", "--detailed-exitcode")
        )

        Assertions.assertEquals(0, exitCode)
        Assertions.assertEquals(1, executor.invocations)
        val args = executor.capturedArgs!!
        Assertions.assertEquals(listOf("diff", "upgrade", "demo", "."), args.subList(0, 4))
        Assertions.assertTrue(args.contains("--detailed-exitcode"), "detailed-exitcode forwarded: $args")
    }

    @Test
    fun failsWithoutInvokingHelmWhenRepositoryMissing() {
        val exitCode = runCli(arrayOf("diff", "upgrade", "abc", "--name", "demo"))

        Assertions.assertNotEquals(0, exitCode)
        Assertions.assertEquals(0, executor.invocations, "Helm must not be invoked when the repository is missing")
    }
}
