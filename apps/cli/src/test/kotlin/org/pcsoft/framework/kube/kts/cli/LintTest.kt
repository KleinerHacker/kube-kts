package org.pcsoft.framework.kube.kts.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LintTest {

    @Test
    fun testSuccessfully() {
        val exitCode = runCli(arrayOf("lint", "src/test/resources/helm", "build/helm"))
        Assertions.assertEquals(0, exitCode)
    }

    @Test
    fun testSuccessfully_Tmp() {
        val exitCode = runCli(arrayOf("lint", "src/test/resources/helm"))
        Assertions.assertEquals(0, exitCode)
    }

    @Test
    fun testFailed_NotFound() {
        val exitCode = runCli(arrayOf("lint", "abc"))
        Assertions.assertNotEquals(0, exitCode)
    }
}