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
import org.pcsoft.framework.kube.kts.api.utils.roundTrip
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * Tests for the cloud provider volume sources: AWS EBS, GCE persistent disks, Azure disks and files,
 * OpenStack Cinder, Portworx and vSphere.
 */
class CloudVolumeSourcesTest {
    companion object {
        private val awsMaxSpec = VolumeSpecBuilder("ebs").apply {
            from {
                awsElasticBlockStore("vol-0123456789abcdef0") {
                    fsType = "ext4"
                    partition = 1
                    readOnly = true
                }
            }
        }.build()

        private val awsMinSpec = VolumeSpecBuilder("ebs").apply {
            from { awsElasticBlockStore("vol-0123456789abcdef0") }
        }.build()

        private val gceMaxSpec = VolumeSpecBuilder("pd").apply {
            from {
                gcePersistentDisk("my-disk") {
                    fsType = "ext4"
                    partition = 2
                    readOnly = true
                }
            }
        }.build()

        private val azureDiskSpec = VolumeSpecBuilder("azure-disk").apply {
            from {
                azureDisk("my-disk", "https://account.blob.core.windows.net/vhds/my-disk.vhd") {
                    cachingMode = AzureDiskSourceSpec.CachingMode.ReadWrite
                    kind = AzureDiskSourceSpec.Kind.Managed
                    fsType = "ext4"
                    readOnly = false
                }
            }
        }.build()

        private val azureFileSpec = VolumeSpecBuilder("azure-file").apply {
            from { azureFile("azure-credentials", "my-share") { readOnly = true } }
        }.build()

        private val cinderSpec = VolumeSpecBuilder("cinder").apply {
            from {
                cinder("vol-1") {
                    fsType = "ext4"
                    readOnly = true
                    secretRef = "cinder-credentials"
                }
            }
        }.build()

        private val portworxSpec = VolumeSpecBuilder("portworx").apply {
            from { portworx("pwx-vol") { fsType = "ext4" } }
        }.build()

        private val vsphereSpec = VolumeSpecBuilder("vsphere").apply {
            from {
                vsphereVolume("[datastore1] volumes/disk.vmdk") {
                    fsType = "ext4"
                    storagePolicyName = "gold"
                    storagePolicyID = "policy-1"
                }
            }
        }.build()
    }

    /**
     * Verifies that an AWS EBS volume carries identifier, filesystem and partition.
     */
    @Test
    fun testAwsMaxContent() {
        val source = assertIs<AwsElasticBlockStoreSourceSpec>(awsMaxSpec.source)
        assertEquals("vol-0123456789abcdef0", source.volumeID)
        assertEquals("ext4", source.fsType)
        assertEquals(1, source.partition)
        assertEquals(true, source.readOnly)
    }

    /**
     * Verifies that a minimal AWS EBS volume mounts the whole device read-write.
     *
     * With no partition set the entire volume is used, which is the common case.
     */
    @Test
    fun testAwsMinContent() {
        val source = assertIs<AwsElasticBlockStoreSourceSpec>(awsMinSpec.source)
        assertNull(source.partition)
        assertNull(source.readOnly)
        assertNull(source.fsType)
    }

    /**
     * Verifies that a GCE persistent disk carries its name, filesystem and partition.
     */
    @Test
    fun testGceMaxContent() {
        val source = assertIs<GcePersistentDiskSourceSpec>(gceMaxSpec.source)
        assertEquals("my-disk", source.pdName)
        assertEquals(2, source.partition)
    }

    /**
     * Verifies that an Azure data disk carries its caching mode and storage kind.
     */
    @Test
    fun testAzureDiskContent() {
        val source = assertIs<AzureDiskSourceSpec>(azureDiskSpec.source)
        assertEquals("my-disk", source.diskName)
        assertEquals("https://account.blob.core.windows.net/vhds/my-disk.vhd", source.diskURI)
        assertEquals(AzureDiskSourceSpec.CachingMode.ReadWrite, source.cachingMode)
        assertEquals(AzureDiskSourceSpec.Kind.Managed, source.kind)
    }

    /**
     * Verifies that an Azure Files share carries its credential secret and share name.
     */
    @Test
    fun testAzureFileContent() {
        val source = assertIs<AzureFileSourceSpec>(azureFileSpec.source)
        assertEquals("azure-credentials", source.secretName)
        assertEquals("my-share", source.shareName)
        assertEquals(true, source.readOnly)
    }

    /**
     * Verifies that a Cinder volume renders its credential secret as a nested reference.
     */
    @Test
    fun testCinderContent() {
        val source = assertIs<CinderSourceSpec>(cinderSpec.source)
        assertEquals("vol-1", source.volumeID)
        assertEquals(LocalObjectReferenceSpec("cinder-credentials"), source.secretRef)
    }

    /**
     * Verifies that a Portworx volume carries its identifier and filesystem.
     */
    @Test
    fun testPortworxContent() {
        val source = assertIs<PortworxVolumeSourceSpec>(portworxSpec.source)
        assertEquals("pwx-vol", source.volumeID)
        assertEquals("ext4", source.fsType)
    }

    /**
     * Verifies that a vSphere volume carries its datastore path and storage policy.
     */
    @Test
    fun testVsphereContent() {
        val source = assertIs<VsphereVolumeSourceSpec>(vsphereSpec.source)
        assertEquals("[datastore1] volumes/disk.vmdk", source.volumePath)
        assertEquals("gold", source.storagePolicyName)
        assertEquals("policy-1", source.storagePolicyID)
    }

    /**
     * Verifies that every cloud source is rendered under the property name Kubernetes expects.
     *
     * The keys differ noticeably in spelling between providers, so each one is asserted explicitly.
     */
    @Test
    fun testCloudSourceKeysYaml() {
        JSONAssert.assertEquals(
            """{"name":"ebs","awsElasticBlockStore":{"volumeID":"vol-0123456789abcdef0","partition":1}}""",
            awsMaxSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"pd","gcePersistentDisk":{"pdName":"my-disk","partition":2}}""",
            gceMaxSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"azure-disk","azureDisk":{"diskName":"my-disk","kind":"Managed"}}""",
            azureDiskSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"azure-file","azureFile":{"secretName":"azure-credentials","shareName":"my-share"}}""",
            azureFileSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"cinder","cinder":{"volumeID":"vol-1","secretRef":{"name":"cinder-credentials"}}}""",
            cinderSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"portworx","portworxVolume":{"volumeID":"pwx-vol"}}""",
            portworxSpec.toJson(), JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"vsphere","vsphereVolume":{"volumePath":"[datastore1] volumes/disk.vmdk"}}""",
            vsphereSpec.toJson(), JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that each cloud source survives a full serialize and deserialize cycle.
     */
    @Test
    fun testRoundTrip() {
        listOf(
            awsMaxSpec, awsMinSpec, gceMaxSpec, azureDiskSpec,
            azureFileSpec, cinderSpec, portworxSpec, vsphereSpec
        ).forEach { original ->
            val result = roundTrip(original)
            assertEquals(original.name, result.name)
            assertEquals(original.source::class, result.source::class)
        }
    }

    /**
     * Verifies that the block device sources reject a partition number of zero.
     *
     * Kubernetes counts partitions from one; zero would silently be interpreted as "whole device".
     */
    @Test
    fun testBlockDeviceSourcesRejectZeroPartition() {
        assertFailsWith<IllegalArgumentException> {
            AwsElasticBlockStoreSourceSpec("vol-1", null, 0, null)
        }
        assertFailsWith<IllegalArgumentException> {
            GcePersistentDiskSourceSpec("disk", null, 0, null)
        }
    }

    /**
     * Verifies that the cloud sources reject a blank volume identifier.
     */
    @Test
    fun testCloudSourcesRejectBlankIdentifier() {
        assertFailsWith<IllegalArgumentException> { AwsElasticBlockStoreSourceSpec("", null, null, null) }
        assertFailsWith<IllegalArgumentException> { GcePersistentDiskSourceSpec(" ", null, null, null) }
        assertFailsWith<IllegalArgumentException> { CinderSourceSpec("", null, null, null) }
        assertFailsWith<IllegalArgumentException> { PortworxVolumeSourceSpec("", null, null) }
        assertFailsWith<IllegalArgumentException> { VsphereVolumeSourceSpec("", null, null, null) }
    }
}
