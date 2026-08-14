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

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.types.giBytes
import org.pcsoft.framework.kube.kts.api.utils.roundTrip
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for the node-local volume sources that were added beyond the original set: ephemeral claims,
 * OCI images and CSI driven storage.
 *
 * The emptyDir, hostPath and PersistentVolumeClaim sources are covered by [VolumeSpecTest].
 */
class LocalVolumeSourcesTest {
    companion object {
        private val ephemeralMaxSpec = VolumeSpecBuilder("scratch").apply {
            from {
                ephemeral {
                    metadata(labels = mapOf("tier" to "cache"), annotations = mapOf("owner" to "team"))
                    spec {
                        accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
                        storageClassName = "fast"
                        volumeMode = VolumeClaimTemplateSpec.VolumeMode.Filesystem
                        volumeAttributesClassName = "gold"
                        requests(1.giBytes)
                        limits(2.giBytes)
                    }
                }
            }
        }.build()

        private val ephemeralMinSpec = VolumeSpecBuilder("scratch").apply {
            from {
                ephemeral {
                    spec {
                        requests(1.giBytes)
                    }
                }
            }
        }.build()

        private val imageMaxSpec = VolumeSpecBuilder("assets").apply {
            from {
                image("registry.example.com/assets:1.0") {
                    pullPolicy = ContainerSpec.ImagePullPolicy.IfNotPresent
                }
            }
        }.build()

        private val imageMinSpec = VolumeSpecBuilder("assets").apply {
            from {
                image("registry.example.com/assets:1.0")
            }
        }.build()

        private val csiMaxSpec = VolumeSpecBuilder("data").apply {
            from {
                csi("ebs.csi.aws.com") {
                    readOnly = true
                    fsType = "ext4"
                    nodePublishSecretRef = "csi-credentials"
                    addVolumeAttribute("encrypted", "true")
                }
            }
        }.build()

        private val csiMinSpec = VolumeSpecBuilder("data").apply {
            from {
                csi("ebs.csi.aws.com")
            }
        }.build()
    }

    /**
     * Verifies that an ephemeral volume maps the full claim template onto the specification.
     *
     * Metadata and every storage attribute are set, so the generated claim template must carry all of
     * them while remaining nameless - the name is derived by Kubernetes from Pod and volume.
     */
    @Test
    fun testEphemeralMaxContent() {
        val source = assertIs<EphemeralSourceSpec>(ephemeralMaxSpec.source)
        val metadata = source.volumeClaimTemplate.metadata
        assertNotNull(metadata)
        assertEquals(mapOf("tier" to "cache"), metadata.labels)
        assertEquals(mapOf("owner" to "team"), metadata.annotations)

        val spec = source.volumeClaimTemplate.spec
        assertEquals(listOf(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce), spec.accessModes)
        assertEquals("fast", spec.storageClassName)
        assertEquals(VolumeClaimTemplateSpec.VolumeMode.Filesystem, spec.volumeMode)
        assertEquals("gold", spec.volumeAttributesClassName)

        val resources = spec.resources
        assertNotNull(resources)
        assertEquals(1.giBytes, resources.requests?.storage)
        assertEquals(2.giBytes, resources.limits?.storage)
    }

    /**
     * Verifies that a minimal ephemeral volume only carries the requested storage.
     *
     * No metadata is configured, so the template must omit the metadata block entirely.
     */
    @Test
    fun testEphemeralMinContent() {
        val source = assertIs<EphemeralSourceSpec>(ephemeralMinSpec.source)
        assertNull(source.volumeClaimTemplate.metadata)
        assertNull(source.volumeClaimTemplate.spec.storageClassName)
        assertEquals(1.giBytes, source.volumeClaimTemplate.spec.resources?.requests?.storage)
    }

    /**
     * Verifies that an ephemeral volume is rendered under the `ephemeral` key.
     */
    @Test
    fun testEphemeralMaxYaml() {
        val expectedJson = """
          |{
          |  "name": "scratch",
          |  "ephemeral": {
          |    "volumeClaimTemplate": {
          |      "metadata": {
          |        "labels": { "tier": "cache" },
          |        "annotations": { "owner": "team" }
          |      },
          |      "spec": {
          |        "accessModes": [ "ReadWriteOnce" ],
          |        "storageClassName": "fast",
          |        "volumeMode": "Filesystem",
          |        "volumeAttributesClassName": "gold",
          |        "resources": {
          |          "requests": { "storage": "1Gi" },
          |          "limits": { "storage": "2Gi" }
          |        }
          |      }
          |    }
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, ephemeralMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that an image volume carries its reference and pull policy.
     */
    @Test
    fun testImageMaxContent() {
        val source = assertIs<ImageSourceSpec>(imageMaxSpec.source)
        assertEquals("registry.example.com/assets:1.0", source.reference)
        assertEquals(ContainerSpec.ImagePullPolicy.IfNotPresent, source.pullPolicy)
    }

    /**
     * Verifies that a minimal image volume leaves the pull policy to Kubernetes.
     */
    @Test
    fun testImageMinContent() {
        val source = assertIs<ImageSourceSpec>(imageMinSpec.source)
        assertEquals("registry.example.com/assets:1.0", source.reference)
        assertNull(source.pullPolicy)
    }

    /**
     * Verifies that an image volume is rendered under the `image` key.
     */
    @Test
    fun testImageMaxYaml() {
        val expectedJson = """
          |{
          |  "name": "assets",
          |  "image": {
          |    "reference": "registry.example.com/assets:1.0",
          |    "pullPolicy": "IfNotPresent"
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, imageMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that a CSI volume carries the driver, its attributes and the credential reference.
     *
     * The secret reference must be rendered as a nested object rather than a bare string.
     */
    @Test
    fun testCsiMaxContent() {
        val source = assertIs<CsiSourceSpec>(csiMaxSpec.source)
        assertEquals("ebs.csi.aws.com", source.driver)
        assertEquals(true, source.readOnly)
        assertEquals("ext4", source.fsType)
        assertEquals(mapOf("encrypted" to "true"), source.volumeAttributes)
        assertEquals(LocalObjectReferenceSpec("csi-credentials"), source.nodePublishSecretRef)
    }

    /**
     * Verifies that a minimal CSI volume only carries the driver name.
     */
    @Test
    fun testCsiMinContent() {
        val source = assertIs<CsiSourceSpec>(csiMinSpec.source)
        assertEquals("ebs.csi.aws.com", source.driver)
        assertNull(source.readOnly)
        assertNull(source.volumeAttributes)
        assertNull(source.nodePublishSecretRef)
    }

    /**
     * Verifies that a CSI volume is rendered under the `csi` key with a nested secret reference.
     */
    @Test
    fun testCsiMaxYaml() {
        val expectedJson = """
          |{
          |  "name": "data",
          |  "csi": {
          |    "driver": "ebs.csi.aws.com",
          |    "readOnly": true,
          |    "fsType": "ext4",
          |    "volumeAttributes": { "encrypted": "true" },
          |    "nodePublishSecretRef": { "name": "csi-credentials" }
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, csiMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that each of these sources survives a full serialize and deserialize cycle.
     *
     * This exercises the mapping between source class and YAML property in both directions, so a source
     * that is written under one key but read back under another would be caught here.
     */
    @Test
    fun testRoundTrip() {
        listOf(ephemeralMaxSpec, imageMaxSpec, csiMaxSpec, imageMinSpec, csiMinSpec).forEach { original ->
            val result = roundTrip(original)
            assertEquals(original.name, result.name)
            assertEquals(original.source::class, result.source::class)
        }
    }

    /**
     * Verifies that an image volume rejects a blank reference.
     */
    @Test
    fun testImageRejectsBlankReference() {
        assertFailsWith<IllegalArgumentException> { ImageSourceSpec("  ", null) }
    }

    /**
     * Verifies that a CSI volume rejects a blank driver name.
     */
    @Test
    fun testCsiRejectsBlankDriver() {
        assertFailsWith<IllegalArgumentException> { CsiSourceSpec("", null, null, null, null) }
    }

    /**
     * Verifies that an ephemeral volume cannot be built without a claim specification.
     *
     * The claim template is what defines the storage, so omitting it must fail at build time.
     */
    @Test
    fun testEphemeralRequiresSpec() {
        assertFailsWith<IllegalArgumentException> {
            VolumeSpecBuilder("scratch").apply {
                from { ephemeral { } }
            }.build()
        }
    }
}
