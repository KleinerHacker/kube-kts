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

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RouteTargetSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RouteTlsSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.utils.KotlinAssertions
import org.pcsoft.framework.kube.kts.api.utils.convertToJson
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RouteSpecTest {
    companion object {
        private val maxSpecBuilder = RouteSpecBuilder().apply {
            host = "www.example.com"
            path = "/app"

            to("main-service") {
                weight = 100
            }

            addAlternateBackend("canary-service") {
                weight = 20
            }

            port(8080)

            tls(RouteTlsSpec.Termination.Edge) {
                insecureEdgeTerminationPolicy = RouteTlsSpec.InsecureEdgeTerminationPolicy.Redirect
                key = "key"
                certificate = "certificate"
                caCertificate = "caCertificate"
            }

            wildcardPolicy = RouteSpec.WildcardPolicy.None
        }

        private val maxSpec = maxSpecBuilder.build()
        private val maxTemplate =
            ExplicitTemplateSpecBuilder(RouteSpec.API_VERSION, RouteSpec.KIND, maxSpecBuilder).apply {
                metadata("name") {
                    namespace = "namespace"
                    generateName = "generateName"
                    labels {
                        label("key", "value")
                    }
                    annotations {
                        annotation("key", "value")
                    }
                    finalizers {
                        finalizer("finalizer")
                    }
                    ownerReferences {
                        ownerReference(
                            "apiVersion",
                            "kind",
                            "name",
                            UUID.fromString("2fade68b-1f49-403a-b5e8-4e640d3c6594")
                        ) {
                            blockOwnerDeletion = true
                            controller = true
                        }
                    }
                }
            }.build()

        private val minSpec = RouteSpecBuilder().apply {
            to("service")
        }.build()

        private val namedPortSpec = RouteSpecBuilder().apply {
            to("main-service")
            port("http")
        }.build()
    }

    /**
     * Verifies that the maximal RouteSpec definition is built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMaxContent() {
        assertEquals("www.example.com", maxSpec.host)
        assertEquals("/app", maxSpec.path)
        assertEquals(RouteSpec.WildcardPolicy.None, maxSpec.wildcardPolicy)

        KotlinAssertions.assertNotNull(maxSpec.to) {
            assertEquals(RouteTargetSpec.Kind.Service, it.kind)
            assertEquals("main-service", it.name)
            assertEquals(100, it.weight)
        }

        assertNotNull(maxSpec.alternateBackends)
        KotlinAssertions.assertList(1, maxSpec.alternateBackends) {
            assertEquals(RouteTargetSpec.Kind.Service, it.kind)
            assertEquals("canary-service", it.name)
            assertEquals(20, it.weight)
        }

        KotlinAssertions.assertNotNull(maxSpec.port) {
            assertEquals(8080, it.targetPortNumber)
            assertNull(it.targetPortName)
        }

        KotlinAssertions.assertNotNull(maxSpec.tls) {
            assertEquals(RouteTlsSpec.Termination.Edge, it.termination)
            assertEquals(RouteTlsSpec.InsecureEdgeTerminationPolicy.Redirect, it.insecureEdgeTerminationPolicy)
            assertEquals("key", it.key)
            assertEquals("certificate", it.certificate)
            assertEquals("caCertificate", it.caCertificate)
            assertNull(it.destinationCACertificate)
        }
    }

    /**
     * Verifies that the maximal RouteSpec definition is serialised into the expected YAML
     * document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testMaxYaml() {
        val expectedYaml = IOUtils.resourceToString("/route.yaml", Charsets.UTF_8)
        val expectedJson = convertToJson(expectedYaml)
        val actualJson = maxTemplate.toJson()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the minimal RouteSpec definition is built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testMinContent() {
        assertNull(minSpec.host)
        assertNull(minSpec.path)
        assertEquals(RouteTargetSpec.Kind.Service, minSpec.to.kind)
        assertEquals("service", minSpec.to.name)
        assertNull(minSpec.alternateBackends)
        assertNull(minSpec.port)
        assertNull(minSpec.tls)
        assertNull(minSpec.wildcardPolicy)
    }

    /**
     * Verifies that the minimal RouteSpec definition is serialised into the expected YAML
     * document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals("""{}""", minSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that the RouteSpec definition of the named port flavour is built into the expected
     * spec object.
     *
     * The fields relevant for this case are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testNamedPortContent() {
        KotlinAssertions.assertNotNull(namedPortSpec.port) {
            assertEquals("http", it.targetPortName)
            assertNull(it.targetPortNumber)
        }
    }

    /**
     * Verifies that the RouteSpec definition of the named port flavour is serialised into the
     * expected YAML document.
     *
     * The fields relevant for this case are set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
    @Test
    fun testNamedPortYaml() {
        JSONAssert.assertEquals(
            """{
              |  "to": {
              |    "kind": "Service",
              |    "name": "main-service"
              |  },
              |  "port": {
              |    "targetPort": "http"
              |  }
              |}""".trimMargin(),
            namedPortSpec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that a route published under a subdomain carries it instead of an explicit host.
     *
     * The router combines the subdomain with the domain of the matching Ingress Controller, which is
     * how a route stays portable across clusters with different base domains.
     */
    @Test
    fun testSubdomainContent() {
        val spec = RouteSpecBuilder().apply {
            subdomain = "apps"
            to("main-service")
        }.build()

        assertEquals("apps", spec.subdomain)
        assertNull(spec.host)
        JSONAssert.assertEquals("""{"subdomain":"apps"}""", spec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that a route rejects being given both a host and a subdomain.
     *
     * The two ways of publishing a route are mutually exclusive in OpenShift.
     */
    @Test
    fun testRejectsHostAndSubdomain() {
        assertFailsWith<IllegalArgumentException> {
            RouteSpecBuilder().apply {
                host = "www.example.com"
                subdomain = "apps"
                to("main-service")
            }.build()
        }
    }

    /**
     * Verifies that header actions are rendered under the nested request and response lists.
     *
     * OpenShift groups them under `httpHeaders.actions`, separated by direction, and expresses a
     * removal as an action without a value.
     */
    @Test
    fun testHttpHeadersContent() {
        val spec = RouteSpecBuilder().apply {
            to("main-service")
            httpHeaders {
                setRequestHeader("X-Forwarded-Proto", "https")
                deleteResponseHeader("Server")
            }
        }.build()

        val actions = spec.httpHeaders!!.actions
        assertEquals("X-Forwarded-Proto", actions.request!!.first().name)
        assertEquals("Server", actions.response!!.first().name)

        val expectedJson = """
          |{
          |  "httpHeaders": {
          |    "actions": {
          |      "request": [
          |        { "name": "X-Forwarded-Proto", "action": { "type": "Set", "set": { "value": "https" } } }
          |      ],
          |      "response": [
          |        { "name": "Server", "action": { "type": "Delete" } }
          |      ]
          |    }
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, spec.toJson(), JSONCompareMode.LENIENT)
    }
}
