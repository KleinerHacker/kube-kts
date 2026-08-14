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

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Mounts an export of an NFS server.
 *
 * The export's contents survive the Pod and may be mounted read-write by several Pods at once.
 *
 * @property server   The hostname or IP address of the NFS server.
 * @property path     The absolute path of the export on the server.
 * @property readOnly If true, the export is mounted read-only.
 */
@NoArgs
data class NfsSourceSpec(
    val server: String,
    val path: String,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the server address and the export path.
     */
    init {
        require(server.isNotBlank()) { "NFS server must not be blank" }
        require(path.isNotBlank()) { "NFS path must not be blank" }
        require(path.startsWith('/')) { "NFS path must be absolute, but was '$path'" }
    }
}

/**
 * Mounts an iSCSI logical unit.
 *
 * @property targetPortal       The iSCSI target portal, given as `host` or `host:port`.
 * @property iqn                The iSCSI qualified name of the target.
 * @property lun                The logical unit number to mount.
 * @property iscsiInterface     The iSCSI interface name. Defaults to `default`.
 * @property fsType             The filesystem to mount, for example `ext4`.
 * @property readOnly           If true, the logical unit is mounted read-only.
 * @property portals            Additional target portals for multipath access.
 * @property chapAuthDiscovery  If true, CHAP authentication is used for target discovery.
 * @property chapAuthSession    If true, CHAP authentication is used for the session itself.
 * @property secretRef          A Secret holding the CHAP credentials.
 * @property initiatorName      Overrides the initiator name for this connection.
 */
@NoArgs
data class IscsiSourceSpec(
    val targetPortal: String,
    val iqn: String,
    val lun: Int,
    @field:JsonProperty("iscsiInterface")
    val iscsiInterface: String?,
    val fsType: String?,
    val readOnly: Boolean?,
    val portals: List<String>?,
    val chapAuthDiscovery: Boolean?,
    val chapAuthSession: Boolean?,
    val secretRef: LocalObjectReferenceSpec?,
    val initiatorName: String?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the target portal, the qualified name and the logical unit number.
     */
    init {
        require(targetPortal.isNotBlank()) { "Target portal must not be blank" }
        require(iqn.isNotBlank()) { "IQN must not be blank" }
        require(lun >= 0) { "LUN must not be negative, but was $lun" }
    }
}

/**
 * Mounts a Fibre Channel logical unit.
 *
 * Either [targetWWNs] together with [lun] or [wwids] has to be given.
 *
 * @property targetWWNs The world wide names of the target ports.
 * @property lun        The logical unit number to mount. Required together with [targetWWNs].
 * @property wwids      The world wide identifiers of the volumes. Mutually exclusive with [targetWWNs].
 * @property fsType     The filesystem to mount, for example `ext4`.
 * @property readOnly   If true, the logical unit is mounted read-only.
 */
@NoArgs
data class FibreChannelSourceSpec(
    val targetWWNs: List<String>?,
    val lun: Int?,
    val wwids: List<String>?,
    val fsType: String?,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that exactly one addressing scheme is used and completely specified.
     */
    init {
        val byTarget = !targetWWNs.isNullOrEmpty()
        val byWwid = !wwids.isNullOrEmpty()
        require(byTarget != byWwid) { "Exactly one of 'targetWWNs' and 'wwids' must be set" }
        require(!byTarget || lun != null) { "'lun' is required together with 'targetWWNs'" }
        lun?.let { require(it >= 0) { "LUN must not be negative, but was $it" } }
    }
}

/**
 * Mounts a Ceph RADOS block device.
 *
 * @property monitors The addresses of the Ceph monitors.
 * @property image    The name of the RADOS image.
 * @property pool     The RADOS pool the image lives in. Defaults to `rbd`.
 * @property user     The RADOS user to authenticate as. Defaults to `admin`.
 * @property keyring  The path of the keyring file on the node. Defaults to `/etc/ceph/keyring`.
 * @property secretRef A Secret holding the authentication key, taking precedence over [keyring].
 * @property fsType   The filesystem to mount, for example `ext4`.
 * @property readOnly If true, the device is mounted read-only.
 */
@NoArgs
data class RbdSourceSpec(
    val monitors: List<String>,
    val image: String,
    val pool: String?,
    val user: String?,
    val keyring: String?,
    val secretRef: LocalObjectReferenceSpec?,
    val fsType: String?,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that monitors and image are given.
     */
    init {
        require(monitors.isNotEmpty()) { "At least one Ceph monitor is required" }
        require(monitors.all { it.isNotBlank() }) { "Ceph monitors must not be blank" }
        require(image.isNotBlank()) { "RBD image must not be blank" }
    }
}

/**
 * Mounts a CephFS filesystem.
 *
 * @property monitors   The addresses of the Ceph monitors.
 * @property path       The path within the filesystem to mount. Defaults to its root.
 * @property user       The user to authenticate as. Defaults to `admin`.
 * @property secretFile The path of the secret file on the node. Defaults to
 *                      `/etc/ceph/user.secret`.
 * @property secretRef  A Secret holding the authentication key, taking precedence over [secretFile].
 * @property readOnly   If true, the filesystem is mounted read-only.
 */
@NoArgs
data class CephFsSourceSpec(
    val monitors: List<String>,
    val path: String?,
    val user: String?,
    val secretFile: String?,
    val secretRef: LocalObjectReferenceSpec?,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that at least one monitor is given.
     */
    init {
        require(monitors.isNotEmpty()) { "At least one Ceph monitor is required" }
        require(monitors.all { it.isNotBlank() }) { "Ceph monitors must not be blank" }
    }
}

/**
 * Mounts a GlusterFS volume.
 *
 * @property endpoints The name of the Endpoints object describing the Gluster cluster.
 * @property path      The name of the Gluster volume to mount.
 * @property readOnly  If true, the volume is mounted read-only.
 */
@NoArgs
data class GlusterFsSourceSpec(
    val endpoints: String,
    val path: String,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the endpoints reference and the volume name.
     */
    init {
        require(endpoints.isNotBlank()) { "Endpoints must not be blank" }
        require(path.isNotBlank()) { "Gluster volume path must not be blank" }
    }
}
