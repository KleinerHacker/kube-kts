/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

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