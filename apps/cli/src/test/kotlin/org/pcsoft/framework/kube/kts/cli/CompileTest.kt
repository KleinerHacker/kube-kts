package org.pcsoft.framework.kube.kts.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.pcsoft.framework.kube.kts.cli.intern.RepoType

class CompileTest {

    @ParameterizedTest
    @EnumSource(RepoType::class)
    fun testSuccessfully(type: RepoType) {
        val exitCode = runCli(arrayOf("compile", "src/test/resources/${type.path}", "--exception"))
        Assertions.assertEquals(0, exitCode)
    }

    @Test
    fun testFailed_NotFound() {
        val exitCode = runCli(arrayOf("compile", "abc"))
        Assertions.assertNotEquals(0, exitCode)
    }
}