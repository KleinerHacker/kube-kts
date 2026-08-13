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

/**
 * Integration tests for the script safety model implemented by [DefaultKotlinScriptProcessor].
 *
 * Spec scripts must stay declarative: neither `import` statements nor fully qualified class names
 * are allowed, unless the user explicitly opts out with the `--unsafe` flag. The check runs as part
 * of a real script compilation, which is why these are integration tests.
 */
class ScriptSafetyCheckIT {
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

    /**
     * Verifies that an `import` statement is rejected in safe mode.
     *
     * Compiling a script that imports `java.io.File` must fail with an [IllegalArgumentException],
     * so a template cannot reach outside the DSL.
     */
    @Test
    fun testImportBlockedInSafeMode() {
        val script = tempScriptFile("""import java.io.File
chart("test", "1.0.0") {}""")
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            compiler.compile("test", script, emptyList(), false)
        }
    }

    /**
     * Verifies that a fully qualified class name is rejected in safe mode.
     *
     * Without this check `java.lang.Runtime.getRuntime()` would bypass the missing import and allow
     * arbitrary code execution from a template.
     */
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
    /**
     * Verifies that a fully qualified name inside a string literal is not mistaken for code.
     *
     * A chart may legitimately contain a value such as the name of a Java class; compiling it in
     * safe mode must succeed instead of reporting a false positive.
     */
    @Test
    fun testFqnInStringLiteralNotBlocked() {
        val path = Path.of(this::class.java.getResource("/kts_safety/helm/chart_with_fqn_string.spec.kts").toURI())
        val result = compiler.compile("safety-string", path, emptyList(), false)
        Assertions.assertInstanceOf(Either.Success::class.java, result)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    /**
     * Verifies that a fully qualified name inside a line comment does not trigger the check.
     *
     * Comments are not executable, so a documented class name must not block the compilation.
     */
    @Test
    fun testFqnInLineCommentNotBlocked() {
        val path = Path.of(this::class.java.getResource("/kts_safety/helm/chart_with_fqn_line_comment.spec.kts").toURI())
        val result = compiler.compile("safety-line-comment", path, emptyList(), false)
        Assertions.assertInstanceOf(Either.Success::class.java, result)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    /**
     * Verifies that a fully qualified name inside a block comment does not trigger the check.
     *
     * Same reasoning as for line comments, but for the multi-line comment syntax.
     */
    @Test
    fun testFqnInBlockCommentNotBlocked() {
        val path = Path.of(this::class.java.getResource("/kts_safety/helm/chart_with_fqn_block_comment.spec.kts").toURI())
        val result = compiler.compile("safety-block-comment", path, emptyList(), false)
        Assertions.assertInstanceOf(Either.Success::class.java, result)
    }

    // --- Unsafe mode: safety checks bypassed ---

    /**
     * Verifies that `import` statements are accepted when unsafe mode is enabled.
     *
     * With `--unsafe` the user deliberately gives up the safety guarantees, so the same script that
     * fails in safe mode must compile without an exception.
     */
    @Test
    fun testImportAllowedInUnsafeMode() {
        val script = tempScriptFile("""import java.io.File
chart("test", "1.0.0") {}""")
        Assertions.assertDoesNotThrow {
            compiler.compile("test", script, emptyList(), true)
        }
    }

    /**
     * Verifies that fully qualified class names are accepted when unsafe mode is enabled.
     *
     * Counterpart to the safe mode case: with `--unsafe` the check must be bypassed entirely.
     */
    @Test
    fun testFqnAllowedInUnsafeMode() {
        val script = tempScriptFile("""val r = java.lang.Runtime.getRuntime()
chart("test", "1.0.0") {}""")
        Assertions.assertDoesNotThrow {
            compiler.compile("test", script, emptyList(), true)
        }
    }
}
