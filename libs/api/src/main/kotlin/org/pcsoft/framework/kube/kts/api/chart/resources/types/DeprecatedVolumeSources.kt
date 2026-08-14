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
 * Clones a Git repository into a volume when the Pod starts.
 *
 * Kubernetes deprecated this source in 1.11 and it is disabled by default from 1.33 onwards. The
 * recommended replacement is an init container - or a native sidecar for continuous updates - that
 * clones into a shared [EmptyDirSourceSpec].
 *
 * @property repository The URL of the repository to clone.
 * @property revision   The commit hash to check out. Defaults to the repository's default branch.
 * @property directory  The target directory relative to the volume root. `.` clones into the root.
 */
@Deprecated(
    message = "The gitRepo volume source is deprecated since Kubernetes 1.11 and disabled by default " +
            "since 1.33. Clone into an emptyDir volume from an init container instead.",
    level = DeprecationLevel.WARNING
)
@NoArgs
data class GitRepoSourceSpec(
    val repository: String,
    val revision: String?,
    val directory: String?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the repository URL and the target directory.
     */
    init {
        require(repository.isNotBlank()) { "Repository must not be blank" }
        directory?.let {
            require(it.isNotBlank()) { "Directory must not be blank" }
            require(!it.startsWith('/')) { "Directory must be relative, but was '$it'" }
            require(".." !in it.split('/')) { "Directory must not contain a '..' segment, but was '$it'" }
        }
    }
}

/**
 * Mounts storage through an out-of-tree FlexVolume driver installed on the node.
 *
 * FlexVolume was superseded by CSI and is deprecated since Kubernetes 1.23. New deployments should use
 * a [CsiSourceSpec] instead.
 *
 * @property driver    The name of the FlexVolume driver to invoke.
 * @property fsType    The filesystem to mount, for example `ext4`.
 * @property secretRef A Secret holding credentials passed to the driver.
 * @property readOnly  If true, the volume is mounted read-only.
 * @property options   Driver-specific parameters passed through unchanged.
 */
@Deprecated(
    message = "The FlexVolume source is deprecated since Kubernetes 1.23. Use a CSI driver instead.",
    replaceWith = ReplaceWith("CsiSourceSpec"),
    level = DeprecationLevel.WARNING
)
@NoArgs
data class FlexVolumeSourceSpec(
    val driver: String,
    val fsType: String?,
    val secretRef: LocalObjectReferenceSpec?,
    val readOnly: Boolean?,
    val options: Map<String, String>?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that the driver name is not blank.
     */
    init {
        require(driver.isNotBlank()) { "FlexVolume driver must not be blank" }
    }
}

/**
 * Mounts a Flocker dataset.
 *
 * Flocker support was removed from Kubernetes in 1.25. This source only works against clusters older
 * than that.
 *
 * Exactly one of [datasetName] and [datasetUUID] has to be given.
 *
 * @property datasetName The name of the dataset stored as metadata on the Flocker dataset.
 * @property datasetUUID The unique identifier of the Flocker dataset.
 */
@Deprecated(
    message = "The Flocker volume source was removed in Kubernetes 1.25 and is rejected by newer API servers.",
    level = DeprecationLevel.WARNING
)
@NoArgs
data class FlockerSourceSpec(
    val datasetName: String?,
    val datasetUUID: String?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that exactly one dataset identifier is given.
     */
    init {
        require((datasetName == null) != (datasetUUID == null)) {
            "Exactly one of 'datasetName' and 'datasetUUID' must be set"
        }
        datasetName?.let { require(it.isNotBlank()) { "Dataset name must not be blank" } }
        datasetUUID?.let { require(it.isNotBlank()) { "Dataset UUID must not be blank" } }
    }
}

/**
 * Mounts a Quobyte volume.
 *
 * Quobyte support was removed from Kubernetes in 1.25. This source only works against clusters older
 * than that.
 *
 * @property registry The Quobyte registry, given as one or more `host:port` pairs separated by commas.
 * @property volume   The name of the Quobyte volume to mount.
 * @property readOnly If true, the volume is mounted read-only.
 * @property user     The user to map the mount to. Defaults to the service account.
 * @property group    The group to map the mount to.
 * @property tenant   The tenant owning the volume in a multi-tenant installation.
 */
@Deprecated(
    message = "The Quobyte volume source was removed in Kubernetes 1.25 and is rejected by newer API servers.",
    level = DeprecationLevel.WARNING
)
@NoArgs
data class QuobyteSourceSpec(
    val registry: String,
    val volume: String,
    val readOnly: Boolean?,
    val user: String?,
    val group: String?,
    val tenant: String?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the registry address and the volume name.
     */
    init {
        require(registry.isNotBlank()) { "Quobyte registry must not be blank" }
        require(volume.isNotBlank()) { "Quobyte volume must not be blank" }
    }
}

/**
 * Mounts a Dell EMC ScaleIO volume.
 *
 * ScaleIO support was removed from Kubernetes in 1.26. This source only works against clusters older
 * than that.
 *
 * @property gateway          The address of the ScaleIO API gateway.
 * @property system           The name of the storage system as configured in ScaleIO.
 * @property secretRef        A Secret holding the ScaleIO credentials.
 * @property sslEnabled       If true, the gateway is contacted over TLS.
 * @property protectionDomain The name of the protection domain the storage pool belongs to.
 * @property storagePool      The name of the storage pool the volume lives in.
 * @property storageMode      The redundancy mode of the volume.
 * @property volumeName       The name of the volume already created in the ScaleIO system.
 * @property fsType           The filesystem to mount, for example `ext4`.
 * @property readOnly         If true, the volume is mounted read-only.
 */
@Deprecated(
    message = "The ScaleIO volume source was removed in Kubernetes 1.26 and is rejected by newer API servers.",
    level = DeprecationLevel.WARNING
)
@NoArgs
data class ScaleIoSourceSpec(
    val gateway: String,
    val system: String,
    val secretRef: LocalObjectReferenceSpec,
    val sslEnabled: Boolean?,
    val protectionDomain: String?,
    val storagePool: String?,
    val storageMode: StorageMode?,
    val volumeName: String?,
    val fsType: String?,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the gateway address and the storage system name.
     */
    init {
        require(gateway.isNotBlank()) { "ScaleIO gateway must not be blank" }
        require(system.isNotBlank()) { "ScaleIO system must not be blank" }
    }

    /**
     * The redundancy mode of a ScaleIO volume.
     */
    @Suppress("unused")
    enum class StorageMode {
        /**
         * The volume is stored without redundancy.
         */
        ThinProvisioned,

        /**
         * The volume's capacity is fully allocated up front.
         */
        ThickProvisioned
    }
}

/**
 * Mounts a StorageOS volume.
 *
 * StorageOS support was removed from Kubernetes in 1.25. This source only works against clusters older
 * than that.
 *
 * @property volumeName      The name of the StorageOS volume.
 * @property volumeNamespace The StorageOS namespace the volume lives in. Defaults to the Pod's namespace.
 * @property fsType          The filesystem to mount, for example `ext4`.
 * @property readOnly        If true, the volume is mounted read-only.
 * @property secretRef       A Secret holding the StorageOS API credentials.
 */
@Deprecated(
    message = "The StorageOS volume source was removed in Kubernetes 1.25 and is rejected by newer API servers.",
    level = DeprecationLevel.WARNING
)
@NoArgs
data class StorageOsSourceSpec(
    val volumeName: String,
    val volumeNamespace: String?,
    val fsType: String?,
    val readOnly: Boolean?,
    val secretRef: LocalObjectReferenceSpec?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that the volume name is not blank.
     */
    init {
        require(volumeName.isNotBlank()) { "StorageOS volume name must not be blank" }
    }
}

/**
 * Mounts a Photon Controller persistent disk.
 *
 * Photon Controller support was removed from Kubernetes in 1.24. This source only works against clusters
 * older than that.
 *
 * @property pdID   The identifier of the persistent disk in Photon Controller.
 * @property fsType The filesystem to mount, for example `ext4`.
 */
@Deprecated(
    message = "The Photon Controller volume source was removed in Kubernetes 1.24 and is rejected by " +
            "newer API servers.",
    level = DeprecationLevel.WARNING
)
@NoArgs
data class PhotonPersistentDiskSourceSpec(
    val pdID: String,
    val fsType: String?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that the disk identifier is not blank.
     */
    init {
        require(pdID.isNotBlank()) { "Photon persistent disk ID must not be blank" }
    }
}
