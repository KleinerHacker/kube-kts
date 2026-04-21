package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.core.intern.assertions.ChartAssertion
import org.pcsoft.framework.kube.kts.core.intern.assertions.ServiceAssertion
import java.nio.file.Path

class DefaultKotlinScriptProcessorTest {
    companion object {
        private val compiler: KotlinScriptProcessor = DefaultKotlinScriptProcessor
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testChart() {
        val compiledScriptEither = compiler.compile(Path.of(this::class.java.getResource("/helm/chart.kts").toURI()))
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val chartSpecEither = compiler.execute<ChartSpec>(compiledScript)
        Assertions.assertNotNull(chartSpecEither)
        Assertions.assertInstanceOf(Either.Success::class.java, chartSpecEither)

        val chartSpec = (chartSpecEither as Either.Success).value
        ChartAssertion.assertMax(chartSpec)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testService() {
        val compiledScriptEither = compiler.compile(Path.of(this::class.java.getResource("/helm/templates/service.kts").toURI()))
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