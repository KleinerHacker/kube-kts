package org.pcsoft.framework.kube.kts.core

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.pcsoft.framework.kube.kts.core.intern.json.yamlToJson
import org.pcsoft.framework.kube.kts.core.intern.setupTestLogger
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.nio.file.Files
import java.nio.file.Path

sealed class RepositoryTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            setupTestLogger()
        }
    }

    protected fun assertYaml(actualFile: Path, expectedFile: Path) {
        val content = getContent(actualFile, expectedFile)
        val actualJson = content.first.yamlToJson()
        val expectedJson = content.second.yamlToJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    protected fun assertContent(actualFile: Path, expectedFile: Path) {
        val content = getContent(actualFile, expectedFile)
        Assertions.assertEquals(content.first, content.second)
    }

    private fun getContent(actualFile: Path, expectedFile: Path): Pair<String, String> {
        Assertions.assertTrue { Files.exists(actualFile) }
        assert(Files.exists(expectedFile))

        val actualContent = Files.readString(actualFile)
        val expectedContent = Files.readString(expectedFile)

        return Pair(actualContent, expectedContent)
    }

}