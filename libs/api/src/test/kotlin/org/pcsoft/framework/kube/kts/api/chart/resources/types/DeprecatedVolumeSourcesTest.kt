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

@file:Suppress("DEPRECATION")

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.utils.roundTrip
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * Tests for the volume sources Kubernetes has deprecated or removed.
 *
 * They remain supported for users targeting older clusters, so they must still be rendered correctly
 * even though newer API servers reject them.
 */
class DeprecatedVolumeSourcesTest {
    companion object {
        private val gitRepoSpec = VolumeSpecBuilder("source").apply {
            fromGitRepo("https://github.com/example/repo.git") {
                revision = "22f1d8406d464b0c0874075539c1f2e96c253775"
                directory = "checkout"
            }
        }.build()

        private val flexVolumeSpec = VolumeSpecBuilder("flex").apply {
            fromFlexVolume("vendor/driver") {
                fsType = "ext4"
                readOnly = true
                secretRef = "flex-credentials"
                addOption("mountOptions", "noatime")
            }
        }.build()

        private val flockerSpec = VolumeSpecBuilder("flocker").apply {
            fromFlocker { datasetName = "app-dataset" }
        }.build()

        private val quobyteSpec = VolumeSpecBuilder("quobyte").apply {
            fromQuobyte("registry.example.com:7861", "app-volume") {
                user = "root"
                group = "root"
                tenant = "tenant-1"
                readOnly = true
            }
        }.build()

        private val scaleIoSpec = VolumeSpecBuilder("scaleio").apply {
            fromScaleIo("https://gateway.example.com", "scaleio-system", "scaleio-credentials") {
                protectionDomain = "domain-1"
                storagePool = "pool-1"
                storageMode = ScaleIoSourceSpec.StorageMode.ThinProvisioned
                volumeName = "app-volume"
                fsType = "ext4"
                sslEnabled = true
            }
        }.build()

        private val storageOsSpec = VolumeSpecBuilder("storageos").apply {
            fromStorageOs("app-volume") {
                volumeNamespace = "default"
                fsType = "ext4"
                secretRef = "storageos-credentials"
            }
        }.build()

        private val photonSpec = VolumeSpecBuilder("photon").apply {
            fromPhotonPersistentDisk("disk-1") { fsType = "ext4" }
        }.build()
    }

    /**
     * Verifies that a gitRepo volume carries repository, revision and target directory.
     */
    @Test
    fun testGitRepoContent() {
        val source = assertIs<GitRepoSourceSpec>(gitRepoSpec.source)
        assertEquals("https://github.com/example/repo.git", source.repository)
        assertEquals("22f1d8406d464b0c0874075539c1f2e96c253775", source.revision)
        assertEquals("checkout", source.directory)
    }

    /**
     * Verifies that a FlexVolume carries its driver, credentials and driver-specific options.
     */
    @Test
    fun testFlexVolumeContent() {
        val source = assertIs<FlexVolumeSourceSpec>(flexVolumeSpec.source)
        assertEquals("vendor/driver", source.driver)
        assertEquals(LocalObjectReferenceSpec("flex-credentials"), source.secretRef)
        assertEquals(mapOf("mountOptions" to "noatime"), source.options)
    }

    /**
     * Verifies that a Flocker volume addressed by dataset name leaves the identifier unset.
     */
    @Test
    fun testFlockerContent() {
        val source = assertIs<FlockerSourceSpec>(flockerSpec.source)
        assertEquals("app-dataset", source.datasetName)
        assertNull(source.datasetUUID)
    }

    /**
     * Verifies that a Quobyte volume carries registry, volume and tenant mapping.
     */
    @Test
    fun testQuobyteContent() {
        val source = assertIs<QuobyteSourceSpec>(quobyteSpec.source)
        assertEquals("registry.example.com:7861", source.registry)
        assertEquals("app-volume", source.volume)
        assertEquals("tenant-1", source.tenant)
    }

    /**
     * Verifies that a ScaleIO volume carries gateway, system and its mandatory credential reference.
     */
    @Test
    fun testScaleIoContent() {
        val source = assertIs<ScaleIoSourceSpec>(scaleIoSpec.source)
        assertEquals("https://gateway.example.com", source.gateway)
        assertEquals("scaleio-system", source.system)
        assertEquals(LocalObjectReferenceSpec("scaleio-credentials"), source.secretRef)
        assertEquals(ScaleIoSourceSpec.StorageMode.ThinProvisioned, source.storageMode)
    }

    /**
     * Verifies that a StorageOS volume carries its volume name and namespace.
     */
    @Test
    fun testStorageOsContent() {
        val source = assertIs<StorageOsSourceSpec>(storageOsSpec.source)
        assertEquals("app-volume", source.volumeName)
        assertEquals("default", source.volumeNamespace)
    }

    /**
     * Verifies that a Photon Controller disk carries its identifier and filesystem.
     */
    @Test
    fun testPhotonContent() {
        val source = assertIs<PhotonPersistentDiskSourceSpec>(photonSpec.source)
        assertEquals("disk-1", source.pdID)
        assertEquals("ext4", source.fsType)
    }

    /**
     * Verifies that every deprecated source is rendered under the property name Kubernetes used for it.
     *
     * These keys must stay stable so manifests remain readable by the older clusters that support them.
     */
    @Test
    fun testDeprecatedSourceKeysYaml() {
        JSONAssert.assertEquals(
            """{"name":"source","gitRepo":{"repository":"https://github.com/example/repo.git","directory":"checkout"}}""",
            gitRepoSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"flex","flexVolume":{"driver":"vendor/driver","options":{"mountOptions":"noatime"}}}""",
            flexVolumeSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"flocker","flocker":{"datasetName":"app-dataset"}}""",
            flockerSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"quobyte","quobyte":{"registry":"registry.example.com:7861","volume":"app-volume"}}""",
            quobyteSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"scaleio","scaleIO":{"gateway":"https://gateway.example.com","system":"scaleio-system"}}""",
            scaleIoSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"storageos","storageos":{"volumeName":"app-volume","volumeNamespace":"default"}}""",
            storageOsSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"photon","photonPersistentDisk":{"pdID":"disk-1"}}""",
            photonSpec.toJson(), JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that each deprecated source survives a full serialize and deserialize cycle.
     */
    @Test
    fun testRoundTrip() {
        listOf(
            gitRepoSpec, flexVolumeSpec, flockerSpec, quobyteSpec,
            scaleIoSpec, storageOsSpec, photonSpec
        ).forEach { original ->
            val result = roundTrip(original)
            assertEquals(original.name, result.name)
            assertEquals(original.source::class, result.source::class)
        }
    }

    /**
     * Verifies that a Flocker volume rejects having neither or both dataset identifiers.
     *
     * Exactly one of name and identifier addresses the dataset, so both other combinations are invalid.
     */
    @Test
    fun testFlockerRequiresExactlyOneIdentifier() {
        assertFailsWith<IllegalArgumentException> { FlockerSourceSpec(null, null) }
        assertFailsWith<IllegalArgumentException> { FlockerSourceSpec("name", "uuid") }
    }

    /**
     * Verifies that a gitRepo volume rejects a checkout directory escaping the volume root.
     */
    @Test
    fun testGitRepoRejectsEscapingDirectory() {
        assertFailsWith<IllegalArgumentException> {
            GitRepoSourceSpec("https://github.com/example/repo.git", null, "../outside")
        }
        assertFailsWith<IllegalArgumentException> {
            GitRepoSourceSpec("https://github.com/example/repo.git", null, "/absolute")
        }
    }
}
