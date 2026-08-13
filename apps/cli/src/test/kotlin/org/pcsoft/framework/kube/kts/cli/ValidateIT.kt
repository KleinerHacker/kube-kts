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

/**
 * Integration tests for the `validate` command.
 *
 * `validate` only checks the structure of a repository — it neither compiles the scripts nor
 * renders anything. Both repository layouts are covered via the [RepoType] parameter.
 */
class ValidateIT {

    /**
     * Verifies that a structurally valid repository passes validation.
     *
     * The parameter selects the repository layout — pure KTS and mixed KTS/Helm must both exit with
     * code 0.
     */
    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun testSuccessfully(type: RepoType) {
        val exitCode = runCli(arrayOf("validate", "src/test/resources/${type.path}"))
        Assertions.assertEquals(0, exitCode)
    }

    /**
     * Verifies that validating a non-existing repository fails.
     *
     * The missing directory must be reported through a non-zero exit code.
     */
    @Test
    fun testFailed_NotFound() {
        val exitCode = runCli(arrayOf("validate", "abc"))
        Assertions.assertNotEquals(0, exitCode)
    }
}