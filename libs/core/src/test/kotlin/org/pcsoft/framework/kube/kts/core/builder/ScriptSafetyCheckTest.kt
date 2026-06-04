/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.core.intern.setupTestLogger
import java.nio.file.Files
import java.nio.file.Path

class ScriptSafetyCheckTest {
    companion object {
        private val compiler: KotlinScriptProcessor = DefaultKotlinScriptProcessor

        @BeforeAll
        @JvmStatic
        fun setup() {
            setupTestLogger()
        }
    }

    private fun tempScriptFile(content: String): Path {
        val file = Files.createTempFile("safety-test-", ".spec.kts")
        Files.writeString(file, content)
        file.toFile().deleteOnExit()
        return file
    }

    // --- Safe mode: blocked cases ---

    @Test
    fun testImportBlockedInSafeMode() {
        val script = tempScriptFile("""import java.io.File
chart("test", "1.0.0") {}""")
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            compiler.compile("test", script, emptyList(), false)
        }
    }

    @Test
    fun testFqnBlockedInSafeMode() {
        val script = tempScriptFile("""val r = java.lang.Runtime.getRuntime()
chart("test", "1.0.0") {}""")
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            compiler.compile("test", script, emptyList(), false)
        }
    }

    // --- Safe mode: false-positive prevention ---

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testFqnInStringLiteralNotBlocked() {
        val path = Path.of(this::class.java.getResource("/kts_safety/helm/chart_with_fqn_string.spec.kts").toURI())
        val result = compiler.compile("safety-string", path, emptyList(), false)
        Assertions.assertInstanceOf(Either.Success::class.java, result)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testFqnInLineCommentNotBlocked() {
        val path = Path.of(this::class.java.getResource("/kts_safety/helm/chart_with_fqn_line_comment.spec.kts").toURI())
        val result = compiler.compile("safety-line-comment", path, emptyList(), false)
        Assertions.assertInstanceOf(Either.Success::class.java, result)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testFqnInBlockCommentNotBlocked() {
        val path = Path.of(this::class.java.getResource("/kts_safety/helm/chart_with_fqn_block_comment.spec.kts").toURI())
        val result = compiler.compile("safety-block-comment", path, emptyList(), false)
        Assertions.assertInstanceOf(Either.Success::class.java, result)
    }

    // --- Unsafe mode: safety checks bypassed ---

    @Test
    fun testImportAllowedInUnsafeMode() {
        val script = tempScriptFile("""import java.io.File
chart("test", "1.0.0") {}""")
        Assertions.assertDoesNotThrow {
            compiler.compile("test", script, emptyList(), true)
        }
    }

    @Test
    fun testFqnAllowedInUnsafeMode() {
        val script = tempScriptFile("""val r = java.lang.Runtime.getRuntime()
chart("test", "1.0.0") {}""")
        Assertions.assertDoesNotThrow {
            compiler.compile("test", script, emptyList(), true)
        }
    }
}
