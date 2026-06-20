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
import org.pcsoft.framework.kube.kts.api.types.MemoryValue

/**
 * Represents a PersistentVolumeClaim template used by a Kubernetes StatefulSet.
 *
 * A StatefulSet provisions one PersistentVolumeClaim per Pod for each entry of its
 * `volumeClaimTemplates`. The created claims provide stable, per-Pod storage that survives Pod
 * rescheduling.
 *
 * @property metadata Metadata of the claim. The [Metadata.name] is mandatory and is referenced by a
 * `volumeMount` of the same name inside the Pod's containers.
 * @property spec The desired storage characteristics of the claim such as access modes, storage
 * class and requested size.
 */
@NoArgs
data class VolumeClaimTemplateSpec(
    val metadata: Metadata,
    val spec: Spec
) {
    /**
     * Metadata of a [VolumeClaimTemplateSpec].
     *
     * @property name The name of the claim. This name must match a `volumeMount` inside the Pod's
     * containers to be mounted.
     * @property labels Optional labels used to organize and categorize the claim.
     * @property annotations Optional annotations storing non-identifying metadata of the claim.
     */
    @NoArgs
    data class Metadata(
        val name: String,
        val labels: Map<String, String>?,
        val annotations: Map<String, String>?
    )

    /**
     * The desired specification of a [VolumeClaimTemplateSpec].
     *
     * @property accessModes The desired access modes of the volume (e.g. `ReadWriteOnce`).
     * @property storageClassName The name of the StorageClass used to dynamically provision the volume.
     * If omitted, the cluster default StorageClass is used.
     * @property volumeMode Defines whether the volume is consumed as a `Filesystem` or a raw `Block` device.
     * @property resources The minimum and maximum storage resources of the claim.
     */
    @NoArgs
    data class Spec(
        val accessModes: List<AccessMode>?,
        val storageClassName: String?,
        val volumeMode: VolumeMode?,
        val resources: ResourceRequirements?
    )

    /**
     * The storage resource requirements of a [VolumeClaimTemplateSpec].
     *
     * @property requests The minimum amount of storage requested for the claim.
     * @property limits The maximum amount of storage the claim is allowed to grow to.
     */
    @NoArgs
    data class ResourceRequirements(
        val requests: StorageResource?,
        val limits: StorageResource?
    )

    /**
     * A storage quantity bundle for a [ResourceRequirements] block.
     *
     * @property storage The amount of storage expressed as a [MemoryValue] (e.g. `1.giBytes`).
     */
    @NoArgs
    data class StorageResource(
        val storage: MemoryValue?
    )

    /**
     * The access mode describing how a volume can be mounted by nodes.
     */
    @Suppress("unused")
    enum class AccessMode {
        /**
         * The volume can be mounted as read-write by a single node.
         */
        ReadWriteOnce,

        /**
         * The volume can be mounted as read-only by many nodes.
         */
        ReadOnlyMany,

        /**
         * The volume can be mounted as read-write by many nodes.
         */
        ReadWriteMany,

        /**
         * The volume can be mounted as read-write by a single Pod.
         */
        ReadWriteOncePod
    }

    /**
     * Defines how a volume is consumed.
     */
    @Suppress("unused")
    enum class VolumeMode {
        /**
         * The volume is mounted into Pods as a directory (default).
         */
        Filesystem,

        /**
         * The volume is presented to the Pod as a raw block device.
         */
        Block
    }
}
