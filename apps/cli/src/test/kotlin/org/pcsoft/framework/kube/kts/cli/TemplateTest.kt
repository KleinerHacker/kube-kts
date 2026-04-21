package org.pcsoft.framework.kube.kts.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("Incomplete charts")
class TemplateTest {

    @Test
    fun testSuccessfully() {
        val exitCode = runCli(arrayOf("template", "src/test/resources/helm", "build/helm", "-n", "demo"))
        Assertions.assertEquals(0, exitCode)
    }

    @Test
    fun testSuccessfully_Tmp() {
        val exitCode = runCli(arrayOf("template", "src/test/resources/helm", "-n", "demo"))
        Assertions.assertEquals(0, exitCode)
    }

    @Test
    fun testFailed_NotFound() {
        val exitCode = runCli(arrayOf("template", "abc", "-n", "demo"))
        Assertions.assertNotEquals(0, exitCode)
    }
}