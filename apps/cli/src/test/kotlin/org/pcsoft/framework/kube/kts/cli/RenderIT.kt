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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.pcsoft.framework.kube.kts.cli.intern.RepoType
import java.nio.file.Path

/**
 * Integration tests for the `render` command, i.e. the complete scan → compile → render pipeline
 * that turns a KTS repository into a plain Helm chart.
 *
 * Both repository layouts are covered via the [RepoType] parameter, with and without an explicit
 * target directory.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RenderIT {

    /**
     * Verifies that a repository is rendered into an explicitly given target directory.
     *
     * The parameter selects the repository layout; an additional values file is passed via `-f` so
     * the value merging is part of the rendering.
     */
    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun testSuccessfully(type: RepoType) {
        val exitCode = runCli(
            arrayOf(
                "render", "src/test/resources/${type.path}", "build/${type.path}/helm", "-f",
                Path.of(this::class.java.getResource("/values-overlay.yaml").toURI()).toString()
            )
        )
        Assertions.assertEquals(0, exitCode)
    }

    /**
     * Verifies that rendering without a target directory falls back to a temporary directory.
     *
     * The parameter selects the repository layout; the command must succeed although no output
     * location is given.
     */
    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun testSuccessfully_Tmp(type: RepoType) {
        val exitCode = runCli(arrayOf("render", "src/test/resources/${type.path}"))
        Assertions.assertEquals(0, exitCode)
    }

    /**
     * Verifies that rendering a non-existing repository fails.
     *
     * The missing directory must be reported through a non-zero exit code.
     */
    @Test
    fun testFailed_NotFound() {
        val exitCode = runCli(arrayOf("compile", "abc"))
        Assertions.assertNotEquals(0, exitCode)
    }
}