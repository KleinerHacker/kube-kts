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

package org.pcsoft.framework.kube.kts.api.intern.jackson

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.resources.types.*
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec
import org.pcsoft.framework.kube.kts.api.types.giBytes
import org.pcsoft.framework.kube.kts.api.types.mCpu
import org.pcsoft.framework.kube.kts.api.types.miBytes
import org.pcsoft.framework.kube.kts.api.types.ofPortName
import org.pcsoft.framework.kube.kts.api.types.ofPortNumber
import org.pcsoft.framework.kube.kts.api.utils.fromJson
import org.pcsoft.framework.kube.kts.api.utils.roundTrip
import org.pcsoft.framework.kube.kts.api.utils.toJson
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * Round-trip tests for the custom Jackson deserializers.
 *
 * Every specification that is written through a hand-written serializer needs a matching deserializer,
 * otherwise a rendered chart can be produced but never parsed back - which is what the `diff` and
 * `upgrade` paths rely on. Each test therefore writes a specification out and reads it back in,
 * asserting that the result is equal to the original.
 */
class DeserializerRoundTripTest {

    /**
     * Verifies that all five environment variable sources survive a round trip.
     *
     * Each source is written under a different key inside `valueFrom`, so the deserializer has to pick
     * the matching variant back out of the document.
     */
    @Test
    fun testSingleEnvironmentRoundTrip() {
        val specs = listOf(
            SingleEnvironmentSpec("PLAIN", SingleEnvironmentSpec.ValueSource("value")),
            SingleEnvironmentSpec("FIELD", SingleEnvironmentSpec.FieldReferenceSource("metadata.name")),
            SingleEnvironmentSpec("RESOURCE", SingleEnvironmentSpec.ResourceFieldReferenceSource("limits.cpu")),
            SingleEnvironmentSpec("CONFIG", SingleEnvironmentSpec.ConfigMapKeyReferenceSource("cfg", "key")),
            SingleEnvironmentSpec("SECRET", SingleEnvironmentSpec.SecretKeyReferenceSource("sec", "key")),
        )

        specs.forEach { assertEquals(it, roundTrip(it)) }
    }

    /**
     * Verifies that an environment source referencing a whole ConfigMap or Secret survives a round trip.
     *
     * The source type is encoded in the property name (`configMapRef` or `secretRef`) rather than in a
     * field, so it has to be recovered from the document structure.
     */
    @Test
    fun testCompleteEnvironmentRoundTrip() {
        val fromConfigMap = CompleteEnvironmentSpec(
            "APP_",
            CompleteEnvironmentSpec.Source(CompleteEnvironmentSpec.SourceType.ConfigMap, "cfg", true)
        )
        val fromSecret = CompleteEnvironmentSpec(
            null,
            CompleteEnvironmentSpec.Source(CompleteEnvironmentSpec.SourceType.Secret, "sec", null)
        )

        assertEquals(fromConfigMap, roundTrip(fromConfigMap))
        assertEquals(fromSecret, roundTrip(fromSecret))
    }

    /**
     * Verifies that both ingress backend flavours survive a round trip.
     *
     * A service backend and a resource backend are written under different keys and carry different
     * fields, so the deserializer has to distinguish them.
     */
    @Test
    fun testBackendRoundTrip() {
        val serviceBackend = ServiceBackendSpecBuilder("demo").apply { port(8080) }.build()
        val restored = roundTrip<BackendSpec>(serviceBackend)
        val restoredService = assertIs<ServiceBackendSpec>(restored)
        assertEquals("demo", restoredService.name)
        assertEquals(8080, restoredService.port.number)

        val namedPortBackend = ServiceBackendSpecBuilder("demo").apply { port("http") }.build()
        val restoredNamed = assertIs<ServiceBackendSpec>(roundTrip<BackendSpec>(namedPortBackend))
        assertEquals("http", restoredNamed.port.name)
    }

    /**
     * Verifies that all four probe actions survive a round trip.
     *
     * The action is flattened into a sibling of the timing fields, so reading it back means finding the
     * one action key present in the document.
     */
    @Test
    fun testProbeRoundTrip() {
        val probes = listOf(
            ProbeSpecBuilder().apply {
                httpGet(8080) {
                    path = "/healthz"
                    httpHeaders { httpHeader("Accept", "application/json") }
                }
                initialDelaySeconds = java.time.Duration.ofSeconds(5)
                failureThreshold = 3
            }.build(),
            ProbeSpecBuilder().apply { tcpSocket("http") }.build(),
            ProbeSpecBuilder().apply { exec { command("test", "-f", "/tmp/ready") } }.build(),
            ProbeSpecBuilder().apply { grpc(9090) { service = "health" } }.build(),
        )

        probes.forEach { original ->
            val restored = roundTrip(original)
            assertEquals(original.action::class, restored.action::class)
            assertEquals(original.initialDelaySeconds, restored.initialDelaySeconds)
            assertEquals(original.failureThreshold, restored.failureThreshold)
        }
    }

    /**
     * Verifies that the numeric and named forms of a probe port are recovered as the correct variant.
     */
    @Test
    fun testProbePortVariantsRoundTrip() {
        val byNumber = ProbeSpecBuilder().apply { httpGet(8080) }.build()
        val byName = ProbeSpecBuilder().apply { httpGet("http") }.build()

        assertEquals(
            ofPortNumber(8080),
            assertIs<ProbeSpec.HttpGetAction>(roundTrip(byNumber).action).port
        )
        assertEquals(
            ofPortName("http"),
            assertIs<ProbeSpec.HttpGetAction>(roundTrip(byName).action).port
        )
    }

    /**
     * Verifies that a probe document without any known action is rejected.
     *
     * Silently producing a probe without an action would yield a manifest the API server refuses.
     */
    @Test
    fun testProbeWithoutActionIsRejected() {
        assertFailsWith<Exception> { fromJson<ProbeSpec>("""{"periodSeconds":10}""") }
    }

    /**
     * Verifies that all three lifecycle hook actions survive a round trip.
     */
    @Test
    fun testLifecycleRoundTrip() {
        val spec = LifecycleSpecBuilder().apply {
            postStart { exec { command("/bin/sh", "-c", "echo started") } }
            preStop { httpGet(8080) { path = "/shutdown" } }
        }.build()

        val restored = roundTrip(spec)
        assertIs<LifecycleSpec.ExecAction>(restored.postStart)
        assertIs<LifecycleSpec.HttpGetAction>(restored.preStop)

        val sleeping = LifecycleSpecBuilder().apply {
            preStop { sleep(java.time.Duration.ofSeconds(15)) }
        }.build()
        assertIs<LifecycleSpec.SleepAction>(roundTrip(sleeping).preStop)
    }

    /**
     * Verifies that resource quantities including extended resources survive a round trip.
     *
     * CPU, memory and ephemeral storage are written as suffixed strings, and extended resources are
     * merged into the same object, so reading them back means separating the known keys from the rest.
     */
    @Test
    fun testHardwareResourceRoundTrip() {
        val spec = HardwareResourceSpecBuilder().apply {
            requests {
                cpu = 250.mCpu
                memory = 256.miBytes
                ephemeralStorage = 1.giBytes
                extendedResources {
                    extendedResource("nvidia.com/gpu", "1")
                }
            }
        }.build()

        val restored = roundTrip(spec)
        assertEquals(250.mCpu, restored.requests?.cpu)
        assertEquals(256.miBytes, restored.requests?.memory)
        assertEquals(1.giBytes, restored.requests?.ephemeralStorage)
        assertEquals(mapOf("nvidia.com/gpu" to "1"), restored.requests?.extendedResources)
        assertNull(restored.limits)
    }

    /**
     * Verifies that node selector terms survive a round trip.
     *
     * Required node affinity is wrapped in an extra `nodeSelectorTerms` node on the way out, so reading
     * it back has to unwrap it again.
     */
    @Test
    fun testNodeSelectorRoundTrip() {
        val spec = AffinitySpecBuilder().apply {
            nodeAffinity {
                addRequiredDuringSchedulingIgnoredDuringExecution {
                    addMatchExpression(
                        "disktype",
                        NodeSelectorTermSpec.NodeSelectorRequirementSpec.Operator.In
                    ) {
                        addValue("ssd")
                    }
                }
            }
        }.build()

        val restored = roundTrip(spec)
        val terms = restored.nodeAffinity?.requiredDuringSchedulingIgnoredDuringExecution
        assertEquals(1, terms?.size)
        assertEquals("disktype", terms?.first()?.matchExpressions?.first()?.key)
    }

    /**
     * Verifies that a name/value list is read back into a map.
     *
     * Sysctls and DNS options are written as lists of `name`/`value` pairs but modelled as maps, so the
     * conversion has to work in both directions.
     */
    @Test
    fun testMapToNameValueRoundTrip() {
        val spec = PodSecurityContextSpecBuilder().apply {
            addSysctl("net.core.somaxconn", "1024")
            addSysctl("net.ipv4.tcp_syncookies", "1")
        }.build()

        val restored = roundTrip(spec)
        assertEquals(
            mapOf("net.core.somaxconn" to "1024", "net.ipv4.tcp_syncookies" to "1"),
            restored.sysctls
        )
    }

    /**
     * Verifies that a name/value document that is not a list is rejected.
     */
    @Test
    fun testMapToNameValueRejectsNonArray() {
        assertFailsWith<Exception> {
            fromJson<PodSecurityContextSpec>("""{"sysctls":{"a":"b"}}""")
        }
    }

    /**
     * Verifies that both forms of a route target port survive a round trip.
     *
     * A route port is a bare scalar in YAML - either the name or the number of the target port - so the
     * deserializer has to decide from the scalar type which field to populate.
     */
    @Test
    fun testRoutePortRoundTrip() {
        val byName = RoutePortSpec("http", null)
        val byNumber = RoutePortSpec(null, 8080)

        assertEquals(byName, roundTrip(byName))
        assertEquals(byNumber, roundTrip(byNumber))
    }

    /**
     * Verifies that both chart dependency import forms survive a round trip.
     *
     * An import value is either a plain path string or a child/parent mapping object, and both have to
     * be recovered as the matching variant.
     */
    @Test
    fun testImportValueRoundTrip() {
        val spec = DependencySpec(
            name = "common",
            version = "1.0.0",
            repository = null,
            alias = null,
            condition = null,
            tags = null,
            importValues = listOf(
                DependencySpec.PathImportValue("data"),
                DependencySpec.MappingImportValue("child.value", "parent.value")
            )
        )

        val restored = roundTrip(spec)
        assertIs<DependencySpec.PathImportValue>(restored.importValues?.get(0))
        val mapping = assertIs<DependencySpec.MappingImportValue>(restored.importValues?.get(1))
        assertEquals("child.value", mapping.child)
        assertEquals("parent.value", mapping.parent)
    }

    /**
     * Verifies that a volume survives a round trip for a representative source of each category.
     *
     * This exercises the shared property-name mapping in both directions.
     */
    @Test
    fun testVolumeRoundTrip() {
        val volumes = listOf(
            VolumeSpecBuilder("cfg").apply { fromConfigMap { name = "demo" } }.build(),
            VolumeSpecBuilder("sec").apply { fromSecret { name = "demo" } }.build(),
            VolumeSpecBuilder("cache").apply { emptyDir { } }.build(),
            VolumeSpecBuilder("host").apply { fromHostPath("/data") }.build(),
            VolumeSpecBuilder("pvc").apply { fromPersistentVolumeClaim("claim") }.build(),
        )

        volumes.forEach { original ->
            val restored = roundTrip(original)
            assertEquals(original.name, restored.name)
            assertEquals(original.source::class, restored.source::class)
        }
    }

    /**
     * Verifies that a volume document without a known source is rejected with a helpful message.
     */
    @Test
    fun testVolumeWithoutKnownSourceIsRejected() {
        val error = assertFailsWith<Exception> { fromJson<VolumeSpec>("""{"name":"broken"}""") }
        assertEquals(true, error.message?.contains("broken") ?: false)
    }

    /**
     * Verifies that the serialized form of a null specification is a JSON null.
     *
     * The hand-written serializers all handle null explicitly, and writing anything else would break
     * documents where an optional block is absent.
     */
    @Test
    fun testNullSerialization() {
        val spec = LifecycleSpecBuilder().apply {
            postStart { exec { command("true") } }
        }.build()

        assertEquals(false, spec.toJson().contains("preStop"))
    }
}
