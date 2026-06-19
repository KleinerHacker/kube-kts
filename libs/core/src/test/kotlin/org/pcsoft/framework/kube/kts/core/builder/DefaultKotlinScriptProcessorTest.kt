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

package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ConfigMapSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.IngressSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.SealedSecretSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.SecretSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.FlatTemplateSpec
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import org.pcsoft.framework.kube.kts.core.intern.assertions.ChartAssertion
import org.pcsoft.framework.kube.kts.core.intern.assertions.ConfigMapAssertion
import org.pcsoft.framework.kube.kts.core.intern.assertions.SealedSecretAssertion
import org.pcsoft.framework.kube.kts.core.intern.assertions.SecretAssertion
import org.pcsoft.framework.kube.kts.core.intern.assertions.ServiceAssertion
import org.pcsoft.framework.kube.kts.core.intern.setupTestLogger
import tools.jackson.databind.JsonNode
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
            compiler.compile("chart", Path.of(this::class.java.getResource("/kts/helm/Chart.spec.kts").toURI()), emptyList(), false)
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val chartSpecEither =
            compiler.execute<ChartSpec>("chart", compiledScript, ValueAccess.ofRoot(YAMLMapper().createObjectNode()))
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
                Path.of(this::class.java.getResource("/kts/helm/templates/service.spec.kts").toURI()),
                emptyList(),
                false
            )
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val serviceSpecEither = compiler.execute<ExplicitTemplateSpec<ServiceSpec>>(
            "service",
            compiledScript,
            ValueAccess.ofRoot(getValuesNode())
        )
        Assertions.assertNotNull(serviceSpecEither)
        Assertions.assertInstanceOf(Either.Success::class.java, serviceSpecEither)

        val serviceSpec = (serviceSpecEither as Either.Success).value
        ServiceAssertion.assertMax(serviceSpec)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testIngress() {
        val compiledScriptEither =
            compiler.compile(
                "ingress",
                Path.of(this::class.java.getResource("/kts/helm/templates/ingress.spec.kts").toURI()),
                emptyList(),
                false
            )
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val ingressSpecEither = compiler.execute<ExplicitTemplateSpec<IngressSpec>>(
            "ingress",
            compiledScript,
            ValueAccess.ofRoot(getValuesNode())
        )
        Assertions.assertNotNull(ingressSpecEither)
        Assertions.assertInstanceOf(Either.Success::class.java, ingressSpecEither)

        //val ingressSpec = (ingressSpecEither as Either.Success).value
        //ServiceAssertion.assertMax(ingressSpec)
        //TODO: Assertions
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testConfigMap() {
        val compiledScriptEither =
            compiler.compile(
                "configmap",
                Path.of(this::class.java.getResource("/kts/helm/templates/configmap.spec.kts").toURI()),
                emptyList(),
                false
            )
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val configMapSpecEither = compiler.execute<FlatTemplateSpec<ConfigMapSpec>>(
            "configmap",
            compiledScript,
            ValueAccess.ofRoot(getValuesNode())
        )
        Assertions.assertNotNull(configMapSpecEither)
        Assertions.assertInstanceOf(Either.Success::class.java, configMapSpecEither)

        val configMapSpec = (configMapSpecEither as Either.Success).value
        ConfigMapAssertion.assertMax(configMapSpec)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testSecret() {
        val compiledScriptEither =
            compiler.compile(
                "secret",
                Path.of(this::class.java.getResource("/kts/helm/templates/secret.spec.kts").toURI()),
                emptyList(),
                false
            )
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val secretSpecEither = compiler.execute<FlatTemplateSpec<SecretSpec>>(
            "secret",
            compiledScript,
            ValueAccess.ofRoot(getValuesNode())
        )
        Assertions.assertNotNull(secretSpecEither)
        Assertions.assertInstanceOf(Either.Success::class.java, secretSpecEither)

        val secretSpec = (secretSpecEither as Either.Success).value
        SecretAssertion.assertMax(secretSpec)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Test
    fun testSealedSecret() {
        val compiledScriptEither =
            compiler.compile(
                "sealedsecret",
                Path.of(this::class.java.getResource("/kts/helm/templates/sealedsecret.spec.kts").toURI()),
                emptyList(),
                false
            )
        Assertions.assertNotNull(compiledScriptEither)
        Assertions.assertInstanceOf(Either.Success::class.java, compiledScriptEither)

        val compiledScript = (compiledScriptEither as Either.Success).value
        val sealedSecretSpecEither = compiler.execute<ExplicitTemplateSpec<SealedSecretSpec>>(
            "sealedsecret",
            compiledScript,
            ValueAccess.ofRoot(getValuesNode())
        )
        Assertions.assertNotNull(sealedSecretSpecEither)
        Assertions.assertInstanceOf(Either.Success::class.java, sealedSecretSpecEither)

        val sealedSecretSpec = (sealedSecretSpecEither as Either.Success).value
        SealedSecretAssertion.assertMax(sealedSecretSpec)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getValuesNode(): JsonNode =
        YAMLMapper().readTree(Path.of(this::class.java.getResource("/kts/helm/values.yaml").toURI()))

}