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