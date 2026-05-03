/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LintTest {

    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun testSuccessfully(type: RepoType) {
        val exitCode = runCli(
            arrayOf(
                "lint",
                "src/test/resources/${type.path}",
                "build/${type.path}/helm",
                "-f",
                Path.of(this::class.java.getResource("/values-overlay.yaml").toURI()).toString()
            )
        )
        Assertions.assertEquals(0, exitCode)
    }

    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun testSuccessfully_Tmp(type: RepoType) {
        val exitCode = runCli(arrayOf("lint", "src/test/resources/${type.path}"))
        Assertions.assertEquals(0, exitCode)
    }

    @Test
    fun testFailed_NotFound() {
        val exitCode = runCli(arrayOf("lint", "abc"))
        Assertions.assertNotEquals(0, exitCode)
    }
}