package org.pcsoft.framework.kube.kts.core.intern.assertions

import org.junit.jupiter.api.Assertions
import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.types.MailAddress
import java.net.URI

object ChartAssertion {

    fun assertMax(chartSpec: ChartSpec) {
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
        Assertions.assertEquals(listOf(URI("https://source.example.com")), chartSpec.sources)

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
        Assertions.assertEquals(MailAddress.parse("maintainer@mail.com"), chartSpec.maintainers!![0].email)
        Assertions.assertEquals(URI("https://url.example.com"), chartSpec.maintainers!![0].url)

        Assertions.assertEquals(URI("https://icon.example.com"), chartSpec.icon)
        Assertions.assertEquals("appVersion", chartSpec.appVersion)
        Assertions.assertEquals(true, chartSpec.deprecated)
        Assertions.assertEquals(mapOf("annotation" to "value"), chartSpec.annotations)
    }

}