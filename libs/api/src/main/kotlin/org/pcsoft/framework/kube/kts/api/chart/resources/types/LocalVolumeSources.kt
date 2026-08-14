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

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.types.MemoryValue

/**
 * Provides scratch space that lives and dies with the Pod.
 *
 * The directory starts out empty when the Pod is assigned to a node and is deleted permanently when the
 * Pod is removed from it. Containers may share the same emptyDir volume.
 *
 * @property medium    Where the storage is backed. Defaults to the node's disk.
 * @property sizeLimit The maximum amount of storage this volume may consume.
 */
@NoArgs
data class EmptyDirSourceSpec(
    val medium: MediumType?,
    val sizeLimit: MemoryValue?
) : VolumeSpec.SourceSpec {
    /**
     * The storage backing an emptyDir volume.
     */
    @Suppress("unused")
    enum class MediumType @JsonCreator constructor(@get:JsonValue val value: String) {
        /**
         * A tmpfs backed by the node's RAM. Fast and wiped on reboot, but counts against the Pod's
         * memory limit.
         */
        Memory("Memory"),

        /**
         * The node's default storage medium, usually its disk. Rendered as an empty string.
         */
        Disk("")
    }
}

/**
 * Mounts a file or directory from the host node's filesystem into the Pod.
 *
 * This source couples the Pod to the node it runs on and can be used to escape container isolation, so
 * it should be avoided outside of system-level workloads.
 *
 * @property path The absolute path on the host node.
 * @property type What the [path] is expected to be, and whether it may be created on demand.
 */
@NoArgs
data class HostPathSourceSpec(
    val path: String,
    val type: Type?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that the host path is absolute.
     */
    init {
        require(path.isNotBlank()) { "Host path must not be blank" }
        require(path.startsWith('/')) { "Host path must be absolute, but was '$path'" }
    }

    /**
     * Declares what a host path is expected to be before it is mounted.
     */
    @Suppress("unused")
    enum class Type {
        /**
         * No check is performed. Rendered as an empty string.
         */
        None {
            override fun toString(): String = ""
        },

        /**
         * A directory is created with permission 0755 if it does not exist.
         */
        DirectoryOrCreate,

        /**
         * An existing directory is required.
         */
        Directory,

        /**
         * A file is created with permission 0644 if it does not exist.
         */
        FileOrCreate,

        /**
         * An existing file is required.
         */
        File,

        /**
         * An existing UNIX socket is required.
         */
        Socket,

        /**
         * An existing character device is required.
         */
        CharDevice,

        /**
         * An existing block device is required.
         */
        BlockDevice
    }
}

/**
 * Mounts storage bound by a PersistentVolumeClaim in the same namespace.
 *
 * @property claimName The name of the PersistentVolumeClaim to mount.
 * @property readOnly  If true, the volume is mounted read-only regardless of the claim's access mode.
 */
@NoArgs
data class PersistentVolumeClaimSourceSpec(
    val claimName: String,
    val readOnly: Boolean?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that the claim name is not blank.
     */
    init {
        require(claimName.isNotBlank()) { "Claim name must not be blank" }
    }
}

/**
 * The PersistentVolumeClaim template an ephemeral volume is provisioned from.
 *
 * @property metadata Optional labels and annotations applied to the generated claim.
 * @property spec     The claim specification describing the requested storage.
 */
@NoArgs
data class EphemeralVolumeClaimTemplateSpec(
    val metadata: Metadata?,
    val spec: VolumeClaimTemplateSpec.Spec
) {
    /**
     * Labels and annotations applied to the claim generated for an ephemeral volume.
     *
     * @property labels      Labels applied to the generated claim.
     * @property annotations Annotations applied to the generated claim.
     */
    @NoArgs
    data class Metadata(
        val labels: Map<String, String>?,
        val annotations: Map<String, String>?
    )
}

/**
 * Provisions a PersistentVolumeClaim that shares the Pod's lifetime.
 *
 * Unlike a [PersistentVolumeClaimSourceSpec], the claim does not have to exist beforehand: it is created
 * from [volumeClaimTemplate] when the Pod starts and deleted again when the Pod is removed. This suits
 * workloads that need real storage semantics - a storage class, a size, block mode - but no data
 * retention.
 *
 * @property volumeClaimTemplate The template the per-Pod claim is generated from.
 */
@NoArgs
data class EphemeralSourceSpec(
    val volumeClaimTemplate: EphemeralVolumeClaimTemplateSpec
) : VolumeSpec.SourceSpec

/**
 * Mounts the contents of an OCI image as a read-only volume.
 *
 * @property reference  The image reference, for example `registry.example.com/data:1.0`.
 * @property pullPolicy Controls when the image is pulled.
 */
@NoArgs
data class ImageSourceSpec(
    val reference: String,
    val pullPolicy: ContainerSpec.ImagePullPolicy?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that the image reference is not blank.
     */
    init {
        require(reference.isNotBlank()) { "Image reference must not be blank" }
    }
}

/**
 * Mounts storage provided by a CSI driver installed in the cluster.
 *
 * @property driver                The name of the CSI driver handling this volume.
 * @property readOnly              If true, the volume is mounted read-only.
 * @property fsType                The filesystem to mount, for example `ext4`. Defaults to the driver's choice.
 * @property volumeAttributes      Driver-specific parameters passed through unchanged.
 * @property nodePublishSecretRef  A Secret in the Pod's namespace holding credentials the driver needs
 *                                 when publishing the volume on the node.
 */
@NoArgs
data class CsiSourceSpec(
    val driver: String,
    val readOnly: Boolean?,
    val fsType: String?,
    val volumeAttributes: Map<String, String>?,
    val nodePublishSecretRef: LocalObjectReferenceSpec?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that the driver name is not blank.
     */
    init {
        require(driver.isNotBlank()) { "CSI driver must not be blank" }
    }
}
