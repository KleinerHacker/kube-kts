package org.pcsoft.framework.kube.kts.core.compiler

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.core.KubeKtsFile
import org.pcsoft.framework.kube.kts.core.StringKubeKzsFile
import java.net.URI
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class DefaultKubeKtsCompilerTest {
    companion object {
        private val compiler: KubeKtsCompiler = DefaultKubeKtsCompiler
    }

    @Test
    fun testChart() {
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

    @Test
    fun testService() {
        val script = IOUtils.resourceToString("/helm/templates/service.kts", Charsets.UTF_8)

        val compiledScript = compiler.compile(StringKubeKzsFile(script, KubeKtsFile.Type.CHART))
        Assertions.assertNotNull(compiledScript)

        val serviceSpec = compiler.execute<TemplateSpec<ServiceSpec>>(compiledScript)
        Assertions.assertNotNull(serviceSpec)

        Assertions.assertNotNull(serviceSpec.metadata)
        Assertions.assertEquals("metadata", serviceSpec.metadata.name)
        Assertions.assertEquals("namespace", serviceSpec.metadata.namespace)
        Assertions.assertEquals("generateName", serviceSpec.metadata.generateName)

        Assertions.assertNotNull(serviceSpec.spec)
        Assertions.assertEquals(ServiceSpec.Type.LoadBalancer, serviceSpec.spec.type)

        Assertions.assertEquals(1, serviceSpec.spec.ports.size)
        Assertions.assertEquals("port", serviceSpec.spec.ports[0].name)
        Assertions.assertEquals(9999, serviceSpec.spec.ports[0].port)
        Assertions.assertEquals(8888, serviceSpec.spec.ports[0].targetPort)
        Assertions.assertEquals(7777, serviceSpec.spec.ports[0].nodePort)
        Assertions.assertEquals(PortSpec.Protocol.SCTP, serviceSpec.spec.ports[0].protocol)
        Assertions.assertEquals("https", serviceSpec.spec.ports[0].appProtocol)

        Assertions.assertEquals("clusterIP", serviceSpec.spec.clusterIP)
        Assertions.assertEquals(listOf("clusterIP"), serviceSpec.spec.clusterIPs)

        Assertions.assertEquals(setOf(ServiceSpec.IPFamily.IPv4, ServiceSpec.IPFamily.IPv6), serviceSpec.spec.ipFamilies)
        Assertions.assertEquals(ServiceSpec.FamilyPolicy.RequireDualStack, serviceSpec.spec.ipFamilyPolicy)

        Assertions.assertEquals(listOf("externalIP"), serviceSpec.spec.externalIPs)
        Assertions.assertEquals("externalName", serviceSpec.spec.externalName)

        Assertions.assertEquals(ServiceSpec.TrafficPolicy.Local, serviceSpec.spec.externalTrafficPolicy)
        Assertions.assertEquals(ServiceSpec.TrafficPolicy.Local, serviceSpec.spec.internalTrafficPolicy)

        Assertions.assertEquals(false, serviceSpec.spec.allocateLoadBalancerNodePorts)
        Assertions.assertEquals("loadBalancerIP", serviceSpec.spec.loadBalancerIP)
        Assertions.assertEquals("loadBalancerClass", serviceSpec.spec.loadBalancerClass)
        Assertions.assertEquals(listOf("loadBalancerSourceRange"), serviceSpec.spec.loadBalancerSourceRanges)

        Assertions.assertEquals(ServiceSpec.SessionAffinity.None, serviceSpec.spec.sessionAffinity)
        Assertions.assertEquals(
            ServiceSpec.SessionAffinityConfig(ServiceSpec.ClientIPConfig(30.seconds.toJavaDuration())),
            serviceSpec.spec.sessionAffinityConfig
        )

        Assertions.assertEquals(true, serviceSpec.spec.publishNotReadyAddresses)
        Assertions.assertEquals(3000, serviceSpec.spec.healthCheckNodePort)
        Assertions.assertEquals(ServiceSpec.TrafficDistribution.PreferClose, serviceSpec.spec.trafficDistribution)
    }

}