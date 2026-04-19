package org.pcsoft.framework.kube.kts.api.chart

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.net.URI

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

            addSource("source")

            addDependency("dependency", "1.0.0") {
                repository = URI("https://repo.example.com")
                alias = "alias"
                condition = "condition"

                addTag("tag")
                addPathImportValue("path")
                addMappingImportValue("key", "value")
            }

            addMaintainer("maintainer") {
                email = "email"
                url = URI("https://url.example.com")
            }

            icon = "icon"
            appVersion = "appVersion"
            deprecated = true

            addAnnotation("annotation", "value")
        }
    }

    @Test
    fun testMaxContent() {
        Assertions.assertEquals(ChartSpec.API_VERSION, maxChart.apiVersion)
        Assertions.assertEquals("name", maxChart.name)
        Assertions.assertEquals("1.0.0", maxChart.version)
        Assertions.assertEquals(
            KubeVersion(
                listOf(
                    KubeVersion.Item("1.0.0", KubeVersion.ItemEquality.GREATER_EQUAL),
                    KubeVersion.Item("2.0.0", KubeVersion.ItemEquality.LESS)
                )
            ), maxChart.kubeVersion
        )
        Assertions.assertEquals("description", maxChart.description)
        Assertions.assertEquals(ChartSpec.Type.Library, maxChart.type)
        Assertions.assertEquals(setOf("keyword"), maxChart.keywords)
        Assertions.assertEquals("home", maxChart.home)
        Assertions.assertEquals(listOf("source"), maxChart.sources)

        Assertions.assertNotNull(maxChart.dependencies)
        Assertions.assertEquals(1, maxChart.dependencies!!.size)
        Assertions.assertEquals("dependency", maxChart.dependencies[0].name)
        Assertions.assertEquals("1.0.0", maxChart.dependencies[0].version)
        Assertions.assertEquals(URI("https://repo.example.com"), maxChart.dependencies[0].repository)
        Assertions.assertEquals("alias", maxChart.dependencies[0].alias)
        Assertions.assertEquals("condition", maxChart.dependencies[0].condition)
        Assertions.assertEquals(setOf("tag"), maxChart.dependencies[0].tags)
        Assertions.assertNotNull(maxChart.dependencies[0].importValues)
        Assertions.assertEquals(2, maxChart.dependencies[0].importValues!!.size)
        Assertions.assertInstanceOf(
            DependencySpec.PathImportValue::class.java,
            maxChart.dependencies[0].importValues!![0]
        )
        Assertions.assertEquals(
            "path",
            (maxChart.dependencies[0].importValues!![0] as DependencySpec.PathImportValue).path
        )
        Assertions.assertInstanceOf(
            DependencySpec.MappingImportValue::class.java,
            maxChart.dependencies[0].importValues!![1]
        )
        Assertions.assertEquals(
            "key",
            (maxChart.dependencies[0].importValues!![1] as DependencySpec.MappingImportValue).child
        )
        Assertions.assertEquals(
            "value",
            (maxChart.dependencies[0].importValues!![1] as DependencySpec.MappingImportValue).parent
        )

        Assertions.assertNotNull(maxChart.maintainers)
        Assertions.assertEquals(1, maxChart.maintainers!!.size)
        Assertions.assertEquals("maintainer", maxChart.maintainers[0].name)
        Assertions.assertEquals("email", maxChart.maintainers[0].email)
        Assertions.assertEquals(URI("https://url.example.com"), maxChart.maintainers[0].url)

        Assertions.assertEquals("icon", maxChart.icon)
        Assertions.assertEquals("appVersion", maxChart.appVersion)
        Assertions.assertEquals(true, maxChart.deprecated)
        Assertions.assertEquals(mapOf("annotation" to "value"), maxChart.annotations)
    }

    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/chart.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson<ChartSpec>(expectedYaml)
        val actualJson = maxChart.toJson()

        println("Expect: $expectedJson")
        println("Actual: $actualJson")
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

}