package org.pcsoft.framework.kube.kts.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CompileTest {

    @Test
    fun testSuccessfully() {
        val exitCode = runCli(arrayOf("compile", "src/test/resources/helm"))
        Assertions.assertEquals(0, exitCode)
    }

    @Test
    fun testFailed_NotFound() {
        val exitCode = runCli(arrayOf("compile", "abc"))
        Assertions.assertNotEquals(0, exitCode)
    }
}