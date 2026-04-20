package org.pcsoft.framework.kube.kts.core.builder

import org.apache.commons.io.IOUtils
import org.jetbrains.kotlin.incremental.util.Either
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.core.intern.assertions.ChartAssertion
import org.pcsoft.framework.kube.kts.core.intern.assertions.ServiceAssertion

class DefaultKotlinScriptProcessorTest {
    companion object {
        private val compiler: KotlinScriptProcessor = DefaultKotlinScriptProcessor
    }

    @Test
    fun testChart() {
        val script = IOUtils.resourceToString("/helm/chart.kts", Charsets.UTF_8)

        val compiledScriptEither = compiler.compile(script)
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val chartSpecEither = compiler.execute<ChartSpec>(compiledScript)
        Assertions.assertNotNull(chartSpecEither)
        Assertions.assertInstanceOf(Either.Success::class.java, chartSpecEither)

        val chartSpec = (chartSpecEither as Either.Success).value
        ChartAssertion.assertMax(chartSpec)
    }

    @Test
    fun testService() {
        val script = IOUtils.resourceToString("/helm/templates/service.kts", Charsets.UTF_8)

        val compiledScriptEither = compiler.compile(script)
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val serviceSpecEither = compiler.execute<TemplateSpec<ServiceSpec>>(compiledScript)
        Assertions.assertNotNull(serviceSpecEither)
        Assertions.assertInstanceOf(Either.Success::class.java, serviceSpecEither)

        val serviceSpec = (serviceSpecEither as Either.Success).value
        ServiceAssertion.assertMax(serviceSpec)
    }

}