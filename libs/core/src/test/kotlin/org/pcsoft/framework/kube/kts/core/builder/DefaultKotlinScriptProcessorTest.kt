package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import org.pcsoft.framework.kube.kts.core.intern.assertions.ChartAssertion
import org.pcsoft.framework.kube.kts.core.intern.assertions.ServiceAssertion
import org.pcsoft.framework.kube.kts.core.intern.setupTestLogger
import tools.jackson.dataformat.yaml.YAMLMapper
import java.nio.file.Path

class DefaultKotlinScriptProcessorTest {
    companion object {
        private val compiler: KotlinScriptProcessor = DefaultKotlinScriptProcessor

        @BeforeAll
        @JvmStatic
        fun setup() {
            setupTestLogger()
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testChart() {
        val compiledScriptEither =
            compiler.compile("chart", Path.of(this::class.java.getResource("/kts/helm/Chart.kts").toURI()), false)
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val chartSpecEither =
            compiler.execute<ChartSpec>("chart", compiledScript, ValueAccess(YAMLMapper().createObjectNode()))
        Assertions.assertNotNull(chartSpecEither)
        Assertions.assertInstanceOf(Either.Success::class.java, chartSpecEither)

        val chartSpec = (chartSpecEither as Either.Success).value
        ChartAssertion.assertMax(chartSpec)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testService() {
        val compiledScriptEither =
            compiler.compile(
                "service",
                Path.of(this::class.java.getResource("/kts/helm/templates/service.kts").toURI()),
                false
            )
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val serviceSpecEither = compiler.execute<TemplateSpec<ServiceSpec>>(
            "service",
            compiledScript,
            ValueAccess(YAMLMapper().createObjectNode())
        )
        Assertions.assertNotNull(serviceSpecEither)
        Assertions.assertInstanceOf(Either.Success::class.java, serviceSpecEither)

        val serviceSpec = (serviceSpecEither as Either.Success).value
        ServiceAssertion.assertMax(serviceSpec)
    }

}