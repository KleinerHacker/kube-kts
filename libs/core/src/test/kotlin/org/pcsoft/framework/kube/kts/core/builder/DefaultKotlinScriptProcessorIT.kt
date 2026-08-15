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
import org.pcsoft.framework.kube.kts.api.chart.resources.*
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.FlatTemplateSpec
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import org.pcsoft.framework.kube.kts.core.intern.assertions.*
import org.pcsoft.framework.kube.kts.core.intern.setupTestLogger
import tools.jackson.databind.JsonNode
import tools.jackson.dataformat.yaml.YAMLMapper
import java.nio.file.Path

/**
 * Integration tests for [DefaultKotlinScriptProcessor], the component that compiles a `*.spec.kts`
 * file with the Kotlin scripting host and evaluates it into a spec object.
 *
 * Every test runs the real two-step pipeline - `compile` followed by `execute` - against the
 * fixture repository below `/kts/helm` and checks the resulting spec with the shared assertion
 * helpers. Both steps return an `Either`, so success is asserted explicitly before the value is
 * unwrapped.
 */
class DefaultKotlinScriptProcessorIT {
    companion object {
        private val compiler: KotlinScriptProcessor = DefaultKotlinScriptProcessor

        @BeforeAll
        @JvmStatic
        fun setup() {
            setupTestLogger()
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    /**
     * Verifies that `Chart.spec.kts` is compiled and evaluated into a complete [ChartSpec].
     *
     * The chart script is evaluated against empty values, since chart metadata does not depend on
     * `values.yaml`. The result is checked against the maximal chart fixture.
     */
    @Test
    fun testChart() {
        val compiledScriptEither =
            compiler.compile(
                "chart",
                Path.of(this::class.java.getResource("/kts/helm/Chart.spec.kts").toURI()),
                emptyList(),
                false
            )
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
    /**
     * Verifies that `service.spec.kts` is compiled and evaluated into a [ServiceSpec].
     *
     * The script is evaluated against the values of the test repository, so the value lookups
     * inside the template are exercised as well. The result is checked against the maximal service
     * fixture.
     */
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
    /**
     * Verifies that `ingress.spec.kts` is compiled and evaluated into an [IngressSpec].
     *
     * Only the successful compilation and evaluation are asserted; the detailed content assertions
     * for the ingress are still open (see the TODO in the test body).
     */
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
    /**
     * Verifies that `configmap.spec.kts` is compiled and evaluated into a [ConfigMapSpec].
     *
     * A ConfigMap is rendered flat, so it is executed as a [FlatTemplateSpec]. The result is
     * checked against the maximal ConfigMap fixture, including its binary data.
     */
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
    /**
     * Verifies that `secret.spec.kts` is compiled and evaluated into a [SecretSpec].
     *
     * Like the ConfigMap, a Secret is rendered flat and is therefore executed as a
     * [FlatTemplateSpec].
     */
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
    /**
     * Verifies that `sealedsecret.spec.kts` is compiled and evaluated into a [SealedSecretSpec].
     *
     * The SealedSecret is a custom resource with an explicit `spec` node and is therefore executed
     * as an [ExplicitTemplateSpec].
     */
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