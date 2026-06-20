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

package org.pcsoft.framework.kube.kts.api.chart

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.types.MailAddress
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ChartSpecTest {
    companion object {
        private val maxChart = chart("name", "1.0.0") {
            kubeVersion {
                minInclusive("1.0.0")
                maxExclusive("2.0.0")
            }
            description = "description"
            type = ChartSpec.Type.Library

            addKeyword("keyword")

            home = "home"

            addSource(URI("https://source.example.com"))

            addDependency("dependency", "1.0.0") {
                repository = URI("https://repo.example.com")
                alias = "alias"
                condition = "condition"

                addTag("tag")
                addPathImportValue("path")
                addMappingImportValue("key", "value")
            }

            addMaintainer("maintainer") {
                email = MailAddress.parse("maintainer@mail.com")
                url = URI("https://url.example.com")
            }

            icon = URI("https://icon.example.com")
            appVersion = "appVersion"
            deprecated = true

            addAnnotation("annotation", "value")
        }

        private val minChart = chart("name", "1.0.0") {}
    }

    @Test
    fun testMaxContent() {
        assertEquals(ChartSpec.API_VERSION, maxChart.apiVersion)
        assertEquals("name", maxChart.name)
        assertEquals("1.0.0", maxChart.version)
        assertEquals(
            KubeVersion(
                listOf(
                    KubeVersion.Item("1.0.0", KubeVersion.ItemEquality.GREATER_EQUAL),
                    KubeVersion.Item("2.0.0", KubeVersion.ItemEquality.LESS)
                )
            ), maxChart.kubeVersion
        )
        assertEquals("description", maxChart.description)
        assertEquals(ChartSpec.Type.Library, maxChart.type)
        assertEquals(setOf("keyword"), maxChart.keywords)
        assertEquals("home", maxChart.home)
        assertEquals(listOf(URI("https://source.example.com")), maxChart.sources)

        assertNotNull(maxChart.dependencies)
        assertEquals(1, maxChart.dependencies.size)
        assertEquals("dependency", maxChart.dependencies[0].name)
        assertEquals("1.0.0", maxChart.dependencies[0].version)
        assertEquals(URI("https://repo.example.com"), maxChart.dependencies[0].repository)
        assertEquals("alias", maxChart.dependencies[0].alias)
        assertEquals("condition", maxChart.dependencies[0].condition)
        assertEquals(setOf("tag"), maxChart.dependencies[0].tags)
        assertNotNull(maxChart.dependencies[0].importValues)
        assertEquals(2, maxChart.dependencies[0].importValues!!.size)
        assertIs<DependencySpec.PathImportValue>(maxChart.dependencies[0].importValues!![0])
        assertEquals(
            "path",
            (maxChart.dependencies[0].importValues!![0] as DependencySpec.PathImportValue).path
        )
        assertIs<DependencySpec.MappingImportValue>(maxChart.dependencies[0].importValues!![1])
        assertEquals(
            "key",
            (maxChart.dependencies[0].importValues!![1] as DependencySpec.MappingImportValue).child
        )
        assertEquals(
            "value",
            (maxChart.dependencies[0].importValues!![1] as DependencySpec.MappingImportValue).parent
        )

        assertNotNull(maxChart.maintainers)
        assertEquals(1, maxChart.maintainers.size)
        assertEquals("maintainer", maxChart.maintainers[0].name)
        assertEquals(MailAddress.parse("maintainer@mail.com"), maxChart.maintainers[0].email)
        assertEquals(URI("https://url.example.com"), maxChart.maintainers[0].url)

        assertEquals(URI("https://icon.example.com"), maxChart.icon)
        assertEquals("appVersion", maxChart.appVersion)
        assertEquals(true, maxChart.deprecated)
        assertEquals(mapOf("annotation" to "value"), maxChart.annotations)
    }

    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/chart.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxChart.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        assertEquals(ChartSpec.API_VERSION, minChart.apiVersion)
        assertEquals("name", minChart.name)
        assertEquals("1.0.0", minChart.version)
        assertNull(minChart.kubeVersion)
        assertNull(minChart.description)
        assertNull(minChart.type)
        assertNull(minChart.keywords)
        assertNull(minChart.home)
        assertNull(minChart.sources)
        assertNull(minChart.dependencies)
        assertNull(minChart.maintainers)
        assertNull(minChart.icon)
        assertNull(minChart.appVersion)
        assertNull(minChart.deprecated)
        assertNull(minChart.annotations)
    }

    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals(
            """{
              |  "apiVersion": "v2",
              |  "name": "name",
              |  "version": "1.0.0"
              |}""".trimMargin(),
            minChart.toJson(),
            JSONCompareMode.LENIENT
        )
    }

}
