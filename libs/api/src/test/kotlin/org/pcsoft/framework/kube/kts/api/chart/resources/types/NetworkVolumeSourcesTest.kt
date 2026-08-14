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
 * Tests for the network attached volume sources: NFS, iSCSI, Fibre Channel, Ceph RBD, CephFS and
 * GlusterFS.
 */
class NetworkVolumeSourcesTest {
    companion object {
        private val nfsMaxSpec = VolumeSpecBuilder("share").apply {
            from { nfs("nfs.example.com", "/exports/data") { readOnly = true } }
        }.build()

        private val nfsMinSpec = VolumeSpecBuilder("share").apply {
            from { nfs("nfs.example.com", "/exports/data") }
        }.build()

        private val iscsiMaxSpec = VolumeSpecBuilder("block").apply {
            from {
                iscsi("10.0.0.1:3260", "iqn.2001-04.com.example:storage", 0) {
                    iscsiInterface = "default"
                    fsType = "ext4"
                    readOnly = true
                    chapAuthDiscovery = true
                    chapAuthSession = true
                    secretRef = "iscsi-chap"
                    initiatorName = "iqn.2001-04.com.example:initiator"
                    addPortal("10.0.0.2:3260")
                }
            }
        }.build()

        private val iscsiMinSpec = VolumeSpecBuilder("block").apply {
            from { iscsi("10.0.0.1:3260", "iqn.2001-04.com.example:storage", 0) }
        }.build()

        private val fibreChannelSpec = VolumeSpecBuilder("fc").apply {
            from {
                fibreChannel {
                    addTargetWWN("500a0982991b8dc5")
                    lun = 2
                    fsType = "ext4"
                    readOnly = true
                }
            }
        }.build()

        private val fibreChannelWwidSpec = VolumeSpecBuilder("fc").apply {
            from {
                fibreChannel {
                    addWWID("3600508b400105e210000900000490000")
                }
            }
        }.build()

        private val rbdSpec = VolumeSpecBuilder("ceph-block").apply {
            from {
                rbd("app-image") {
                    addMonitor("10.16.154.78:6789")
                    pool = "kube"
                    user = "admin"
                    secretRef = "ceph-secret"
                    fsType = "ext4"
                    readOnly = true
                }
            }
        }.build()

        private val cephFsSpec = VolumeSpecBuilder("ceph-fs").apply {
            from {
                cephFs {
                    addMonitor("10.16.154.78:6789")
                    path = "/volumes/app"
                    user = "admin"
                    secretRef = "ceph-secret"
                    readOnly = true
                }
            }
        }.build()

        private val glusterFsSpec = VolumeSpecBuilder("gluster").apply {
            from { glusterFs("glusterfs-cluster", "app-volume") { readOnly = true } }
        }.build()
    }

    /**
     * Verifies that an NFS volume carries server, export path and the read-only flag.
     */
    @Test
    fun testNfsMaxContent() {
        val source = assertIs<NfsSourceSpec>(nfsMaxSpec.source)
        assertEquals("nfs.example.com", source.server)
        assertEquals("/exports/data", source.path)
        assertEquals(true, source.readOnly)
    }

    /**
     * Verifies that a minimal NFS volume leaves the read-only flag unset so the export is writable.
     */
    @Test
    fun testNfsMinContent() {
        val source = assertIs<NfsSourceSpec>(nfsMinSpec.source)
        assertNull(source.readOnly)
    }

    /**
     * Verifies that an NFS volume is rendered under the `nfs` key.
     */
    @Test
    fun testNfsMaxYaml() {
        val expectedJson = """
          |{
          |  "name": "share",
          |  "nfs": {
          |    "server": "nfs.example.com",
          |    "path": "/exports/data",
          |    "readOnly": true
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, nfsMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that an iSCSI volume maps target, logical unit and the CHAP configuration.
     *
     * The additional portal must be collected into the `portals` list and the CHAP secret rendered as a
     * nested reference.
     */
    @Test
    fun testIscsiMaxContent() {
        val source = assertIs<IscsiSourceSpec>(iscsiMaxSpec.source)
        assertEquals("10.0.0.1:3260", source.targetPortal)
        assertEquals("iqn.2001-04.com.example:storage", source.iqn)
        assertEquals(0, source.lun)
        assertEquals("default", source.iscsiInterface)
        assertEquals(listOf("10.0.0.2:3260"), source.portals)
        assertEquals(true, source.chapAuthDiscovery)
        assertEquals(true, source.chapAuthSession)
        assertEquals(LocalObjectReferenceSpec("iscsi-chap"), source.secretRef)
        assertEquals("iqn.2001-04.com.example:initiator", source.initiatorName)
    }

    /**
     * Verifies that a minimal iSCSI volume only carries target portal, qualified name and logical unit.
     */
    @Test
    fun testIscsiMinContent() {
        val source = assertIs<IscsiSourceSpec>(iscsiMinSpec.source)
        assertNull(source.portals)
        assertNull(source.secretRef)
        assertNull(source.chapAuthDiscovery)
    }

    /**
     * Verifies that an iSCSI volume is rendered under the `iscsi` key.
     */
    @Test
    fun testIscsiMaxYaml() {
        val expectedJson = """
          |{
          |  "name": "block",
          |  "iscsi": {
          |    "targetPortal": "10.0.0.1:3260",
          |    "iqn": "iqn.2001-04.com.example:storage",
          |    "lun": 0,
          |    "iscsiInterface": "default",
          |    "fsType": "ext4",
          |    "readOnly": true,
          |    "portals": [ "10.0.0.2:3260" ],
          |    "chapAuthDiscovery": true,
          |    "chapAuthSession": true,
          |    "secretRef": { "name": "iscsi-chap" },
          |    "initiatorName": "iqn.2001-04.com.example:initiator"
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, iscsiMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that a Fibre Channel volume addressed by target world wide names carries its logical unit.
     */
    @Test
    fun testFibreChannelByTargetContent() {
        val source = assertIs<FibreChannelSourceSpec>(fibreChannelSpec.source)
        assertEquals(listOf("500a0982991b8dc5"), source.targetWWNs)
        assertEquals(2, source.lun)
        assertNull(source.wwids)
    }

    /**
     * Verifies that a Fibre Channel volume addressed by world wide identifiers needs no logical unit.
     */
    @Test
    fun testFibreChannelByWwidContent() {
        val source = assertIs<FibreChannelSourceSpec>(fibreChannelWwidSpec.source)
        assertEquals(listOf("3600508b400105e210000900000490000"), source.wwids)
        assertNull(source.targetWWNs)
        assertNull(source.lun)
    }

    /**
     * Verifies that a Fibre Channel volume is rendered under the short `fc` key Kubernetes uses.
     */
    @Test
    fun testFibreChannelYaml() {
        val expectedJson = """
          |{
          |  "name": "fc",
          |  "fc": {
          |    "targetWWNs": [ "500a0982991b8dc5" ],
          |    "lun": 2,
          |    "fsType": "ext4",
          |    "readOnly": true
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, fibreChannelSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that a Ceph RBD volume carries monitors, image and credentials.
     */
    @Test
    fun testRbdContent() {
        val source = assertIs<RbdSourceSpec>(rbdSpec.source)
        assertEquals(listOf("10.16.154.78:6789"), source.monitors)
        assertEquals("app-image", source.image)
        assertEquals("kube", source.pool)
        assertEquals(LocalObjectReferenceSpec("ceph-secret"), source.secretRef)
    }

    /**
     * Verifies that a CephFS volume carries monitors and the mounted sub-path.
     */
    @Test
    fun testCephFsContent() {
        val source = assertIs<CephFsSourceSpec>(cephFsSpec.source)
        assertEquals(listOf("10.16.154.78:6789"), source.monitors)
        assertEquals("/volumes/app", source.path)
        assertEquals(LocalObjectReferenceSpec("ceph-secret"), source.secretRef)
    }

    /**
     * Verifies that a GlusterFS volume carries its endpoints object and volume name.
     */
    @Test
    fun testGlusterFsContent() {
        val source = assertIs<GlusterFsSourceSpec>(glusterFsSpec.source)
        assertEquals("glusterfs-cluster", source.endpoints)
        assertEquals("app-volume", source.path)
        assertEquals(true, source.readOnly)
    }

    /**
     * Verifies that the Ceph and Gluster sources are rendered under their respective keys.
     *
     * Kubernetes spells these keys entirely in lower case, unlike most other sources, so they are
     * asserted explicitly.
     */
    @Test
    fun testCephAndGlusterYaml() {
        JSONAssert.assertEquals(
            """{"name":"ceph-block","rbd":{"image":"app-image","pool":"kube"}}""",
            rbdSpec.toJson(),
            JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"ceph-fs","cephfs":{"path":"/volumes/app"}}""",
            cephFsSpec.toJson(),
            JSONCompareMode.LENIENT
        )
        JSONAssert.assertEquals(
            """{"name":"gluster","glusterfs":{"endpoints":"glusterfs-cluster","path":"app-volume"}}""",
            glusterFsSpec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    /**
     * Verifies that each network source survives a full serialize and deserialize cycle.
     */
    @Test
    fun testRoundTrip() {
        listOf(
            nfsMaxSpec, nfsMinSpec, iscsiMaxSpec, iscsiMinSpec,
            fibreChannelSpec, fibreChannelWwidSpec, rbdSpec, cephFsSpec, glusterFsSpec
        ).forEach { original ->
            val result = roundTrip(original)
            assertEquals(original.name, result.name)
            assertEquals(original.source::class, result.source::class)
        }
    }

    /**
     * Verifies that an NFS volume rejects a relative export path.
     *
     * NFS exports are always absolute paths on the server, so a relative one must be refused.
     */
    @Test
    fun testNfsRejectsRelativePath() {
        assertFailsWith<IllegalArgumentException> { NfsSourceSpec("nfs.example.com", "exports/data", null) }
    }

    /**
     * Verifies that a Fibre Channel volume rejects mixing both addressing schemes.
     *
     * Kubernetes expects either target world wide names with a logical unit or world wide identifiers,
     * never both at once.
     */
    @Test
    fun testFibreChannelRejectsBothAddressingSchemes() {
        assertFailsWith<IllegalArgumentException> {
            FibreChannelSourceSpec(listOf("500a0982991b8dc5"), 0, listOf("3600508b4"), null, null)
        }
    }

    /**
     * Verifies that a Fibre Channel volume addressed by target requires a logical unit number.
     */
    @Test
    fun testFibreChannelRequiresLunWithTarget() {
        assertFailsWith<IllegalArgumentException> {
            FibreChannelSourceSpec(listOf("500a0982991b8dc5"), null, null, null, null)
        }
    }

    /**
     * Verifies that the Ceph sources reject an empty monitor list.
     *
     * Without at least one monitor the cluster cannot be reached, so the specification is invalid.
     */
    @Test
    fun testCephSourcesRequireMonitors() {
        assertFailsWith<IllegalArgumentException> {
            RbdSourceSpec(emptyList(), "image", null, null, null, null, null, null)
        }
        assertFailsWith<IllegalArgumentException> {
            CephFsSourceSpec(emptyList(), null, null, null, null, null)
        }
    }
}
