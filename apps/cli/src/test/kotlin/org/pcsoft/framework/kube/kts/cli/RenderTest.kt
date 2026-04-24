package org.pcsoft.framework.kube.kts.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.pcsoft.framework.kube.kts.cli.intern.RepoType

class RenderTest {

    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun testSuccessfully(type: RepoType) {
        val exitCode = runCli(arrayOf("render", "src/test/resources/${type.path}", "build/helm"))
        Assertions.assertEquals(0, exitCode)
    }

    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun testSuccessfully_Tmp(type: RepoType) {
        val exitCode = runCli(arrayOf("render", "src/test/resources/${type.path}"))
        Assertions.assertEquals(0, exitCode)
    }

    @Test
    fun testFailed_NotFound() {
        val exitCode = runCli(arrayOf("compile", "abc"))
        Assertions.assertNotEquals(0, exitCode)
    }
}