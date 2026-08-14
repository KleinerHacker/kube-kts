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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Mounts an AWS Elastic Block Store volume.
 *
 * The volume must already exist in the same AWS availability zone as the node running the Pod, and it
 * can only be attached to a single node at a time.
 *
 * In current Kubernetes versions this in-tree source is superseded by the `ebs.csi.aws.com` CSI driver,
 * which is addressed through a [CsiSourceSpec] or a PersistentVolumeClaim.
 *
 * @property volumeID  The identifier of the EBS volume, for example `vol-0123456789abcdef0`.
 * @property fsType    The filesystem to mount, for example `ext4`.
 * @property partition The partition of the volume to mount. Mounts the whole volume if unset.
 * @property readOnly  If true, the volume is mounted read-only.
 */
@NoArgs
data class AwsElasticBlockStoreSourceSpec(
    val volumeID: String,
    val fsType: String?,
    val partition: Int?,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the volume identifier and the partition number.
     */
    init {
        require(volumeID.isNotBlank()) { "EBS volume ID must not be blank" }
        partition?.let { require(it > 0) { "Partition must be positive, but was $it" } }
    }
}

/**
 * Mounts a Google Compute Engine persistent disk.
 *
 * The disk must already exist in the same GCE project and zone as the node running the Pod. It may be
 * mounted read-only by several nodes at once, but read-write by only one.
 *
 * In current Kubernetes versions this in-tree source is superseded by the `pd.csi.storage.gke.io` CSI
 * driver.
 *
 * @property pdName    The name of the persistent disk in GCE.
 * @property fsType    The filesystem to mount, for example `ext4`.
 * @property partition The partition of the disk to mount. Mounts the whole disk if unset.
 * @property readOnly  If true, the disk is mounted read-only.
 */
@NoArgs
data class GcePersistentDiskSourceSpec(
    val pdName: String,
    val fsType: String?,
    val partition: Int?,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the disk name and the partition number.
     */
    init {
        require(pdName.isNotBlank()) { "GCE persistent disk name must not be blank" }
        partition?.let { require(it > 0) { "Partition must be positive, but was $it" } }
    }
}

/**
 * Mounts an Azure managed or unmanaged data disk.
 *
 * In current Kubernetes versions this in-tree source is superseded by the `disk.csi.azure.com` CSI driver.
 *
 * @property diskName    The name of the data disk in Azure.
 * @property diskURI     The resource URI of the data disk.
 * @property cachingMode The host caching mode used for the disk.
 * @property fsType      The filesystem to mount, for example `ext4`.
 * @property readOnly    If true, the disk is mounted read-only.
 * @property kind        Whether the disk is a managed disk, a shared blob disk or a dedicated blob disk.
 */
@NoArgs
data class AzureDiskSourceSpec(
    val diskName: String,
    val diskURI: String,
    val cachingMode: CachingMode?,
    val fsType: String?,
    val readOnly: Boolean?,
    val kind: Kind?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the disk name and URI.
     */
    init {
        require(diskName.isNotBlank()) { "Azure disk name must not be blank" }
        require(diskURI.isNotBlank()) { "Azure disk URI must not be blank" }
    }

    /**
     * The host caching mode applied to an Azure data disk.
     */
    @Suppress("unused")
    enum class CachingMode {
        /**
         * Host caching is disabled.
         */
        None,

        /**
         * The host caches reads only.
         */
        ReadOnly,

        /**
         * The host caches both reads and writes.
         */
        ReadWrite
    }

    /**
     * The storage model backing an Azure data disk.
     */
    @Suppress("unused")
    enum class Kind {
        /**
         * A dedicated blob disk within a storage account owned by the user.
         */
        Dedicated,

        /**
         * A blob disk shared with other disks in the same storage account.
         */
        Shared,

        /**
         * An Azure managed data disk. This is the recommended model.
         */
        Managed
    }
}

/**
 * Mounts an Azure Files share.
 *
 * In current Kubernetes versions this in-tree source is superseded by the `file.csi.azure.com` CSI driver.
 *
 * @property secretName The name of the Secret holding the Azure storage account name and key.
 * @property shareName  The name of the Azure Files share to mount.
 * @property readOnly   If true, the share is mounted read-only.
 */
@NoArgs
data class AzureFileSourceSpec(
    val secretName: String,
    val shareName: String,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the credential Secret and the share name.
     */
    init {
        require(secretName.isNotBlank()) { "Azure file secret name must not be blank" }
        require(shareName.isNotBlank()) { "Azure file share name must not be blank" }
    }
}

/**
 * Mounts an OpenStack Cinder volume.
 *
 * In current Kubernetes versions this in-tree source is superseded by the `cinder.csi.openstack.org`
 * CSI driver.
 *
 * @property volumeID  The identifier of the Cinder volume.
 * @property fsType    The filesystem to mount, for example `ext4`.
 * @property readOnly  If true, the volume is mounted read-only.
 * @property secretRef A Secret holding the OpenStack credentials used to connect to Cinder.
 */
@NoArgs
data class CinderSourceSpec(
    val volumeID: String,
    val fsType: String?,
    val readOnly: Boolean?,
    val secretRef: LocalObjectReferenceSpec?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that the volume identifier is not blank.
     */
    init {
        require(volumeID.isNotBlank()) { "Cinder volume ID must not be blank" }
    }
}

/**
 * Mounts a Portworx volume.
 *
 * @property volumeID The identifier of the Portworx volume.
 * @property fsType   The filesystem to mount, for example `ext4`.
 * @property readOnly If true, the volume is mounted read-only.
 */
@NoArgs
data class PortworxVolumeSourceSpec(
    val volumeID: String,
    val fsType: String?,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that the volume identifier is not blank.
     */
    init {
        require(volumeID.isNotBlank()) { "Portworx volume ID must not be blank" }
    }
}

/**
 * Mounts a vSphere virtual machine disk.
 *
 * In current Kubernetes versions this in-tree source is superseded by the `csi.vsphere.vmware.com`
 * CSI driver.
 *
 * @property volumePath        The datastore path of the VMDK, for example `[datastore1] volumes/disk.vmdk`.
 * @property fsType            The filesystem to mount, for example `ext4`.
 * @property storagePolicyName The name of the storage policy profile applied to the disk.
 * @property storagePolicyID   The identifier of the storage policy profile, associated with
 *                             [storagePolicyName].
 */
@NoArgs
data class VsphereVolumeSourceSpec(
    val volumePath: String,
    val fsType: String?,
    val storagePolicyName: String?,
    val storagePolicyID: String?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that the datastore path is not blank.
     */
    init {
        require(volumePath.isNotBlank()) { "vSphere volume path must not be blank" }
    }
}
