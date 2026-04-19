package org.pcsoft.framework.kube.kts.core.compiler

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.core.KubeKtsFile
import org.pcsoft.framework.kube.kts.core.StringKubeKzsFile
import java.net.URI

class DefaultKubeKtsCompilerTest {
    companion object {
        private val compiler: KubeKtsCompiler = DefaultKubeKtsCompiler
    }

    @Test
    fun test() {
        val script = IOUtils.resourceToString("/helm/chart.kts", Charsets.UTF_8)

        val compiledScript = compiler.compile(StringKubeKzsFile(script, KubeKtsFile.Type.CHART))
        Assertions.assertNotNull(compiledScript)

        val chartSpec = compiler.execute<ChartSpec>(compiledScript)
        Assertions.assertNotNull(chartSpec)

        Assertions.assertEquals(ChartSpec.API_VERSION, chartSpec.apiVersion)
        Assertions.assertEquals("name", chartSpec.name)
        Assertions.assertEquals("1.0.0", chartSpec.version)
        Assertions.assertEquals(
            KubeVersion(
                listOf(
                    KubeVersion.Item("1.0.0", KubeVersion.ItemEquality.GREATER_EQUAL),
                    KubeVersion.Item("2.0.0", KubeVersion.ItemEquality.LESS)
                )
            ), chartSpec.kubeVersion
        )
        Assertions.assertEquals("description", chartSpec.description)
        Assertions.assertEquals(ChartSpec.Type.Library, chartSpec.type)
        Assertions.assertEquals(setOf("keyword"), chartSpec.keywords)
        Assertions.assertEquals("home", chartSpec.home)
        Assertions.assertEquals(listOf("source"), chartSpec.sources)

        Assertions.assertNotNull(chartSpec.dependencies)
        Assertions.assertEquals(1, chartSpec.dependencies!!.size)
        Assertions.assertEquals("dependency", chartSpec.dependencies!![0].name)
        Assertions.assertEquals("1.0.0", chartSpec.dependencies!![0].version)
        Assertions.assertEquals(URI("https://repo.example.com"), chartSpec.dependencies!![0].repository)
        Assertions.assertEquals("alias", chartSpec.dependencies!![0].alias)
        Assertions.assertEquals("condition", chartSpec.dependencies!![0].condition)
        Assertions.assertEquals(setOf("tag"), chartSpec.dependencies!![0].tags)
        Assertions.assertNotNull(chartSpec.dependencies!![0].importValues)
        Assertions.assertEquals(2, chartSpec.dependencies!![0].importValues!!.size)
        Assertions.assertInstanceOf(
            DependencySpec.PathImportValue::class.java,
            chartSpec.dependencies!![0].importValues!![0]
        )
        Assertions.assertEquals(
            "path",
            (chartSpec.dependencies!![0].importValues!![0] as DependencySpec.PathImportValue).path
        )
        Assertions.assertInstanceOf(
            DependencySpec.MappingImportValue::class.java,
            chartSpec.dependencies!![0].importValues!![1]
        )
        Assertions.assertEquals(
            "key",
            (chartSpec.dependencies!![0].importValues!![1] as DependencySpec.MappingImportValue).child
        )
        Assertions.assertEquals(
            "value",
            (chartSpec.dependencies!![0].importValues!![1] as DependencySpec.MappingImportValue).parent
        )

        Assertions.assertNotNull(chartSpec.maintainers)
        Assertions.assertEquals(1, chartSpec.maintainers!!.size)
        Assertions.assertEquals("maintainer", chartSpec.maintainers!![0].name)
        Assertions.assertEquals("email", chartSpec.maintainers!![0].email)
        Assertions.assertEquals(URI("https://url.example.com"), chartSpec.maintainers!![0].url)

        Assertions.assertEquals("icon", chartSpec.icon)
        Assertions.assertEquals("appVersion", chartSpec.appVersion)
        Assertions.assertEquals(true, chartSpec.deprecated)
        Assertions.assertEquals(mapOf("annotation" to "value"), chartSpec.annotations)
    }

}