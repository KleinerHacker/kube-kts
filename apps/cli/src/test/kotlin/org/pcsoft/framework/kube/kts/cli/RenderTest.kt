package org.pcsoft.framework.kube.kts.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RenderTest {

    @Test
    fun testSuccessfully() {
        val exitCode = runCli(arrayOf("render", "src/test/resources/helm", "build/helm"))
        Assertions.assertEquals(0, exitCode)
    }

    @Test
    fun testSuccessfully_Tmp() {
        val exitCode = runCli(arrayOf("render", "src/test/resources/helm"))
        Assertions.assertEquals(0, exitCode)
    }

    @Test
    fun testFailed_NotFound() {
        val exitCode = runCli(arrayOf("compile", "abc"))
        Assertions.assertNotEquals(0, exitCode)
    }
}