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
 * A template a StatefulSet provisions one PersistentVolumeClaim per replica from.
 *
 * Each Pod of the StatefulSet gets its own claim named after the template and the Pod's ordinal, which
 * is what gives a StatefulSet its stable per-replica storage.
 *
 * @property metadata The name and optional labels of the generated claims.
 * @property spec     The storage the generated claims request.
 */
@NoArgs
data class VolumeClaimTemplateSpec(
    val metadata: Metadata,
    val spec: Spec
) {
    /**
     * The identity of the claims generated from a template.
     *
     * @property name        The name of the template. Together with the Pod ordinal it forms the claim name
     *                       and is what a container's volume mount refers to.
     * @property labels      Labels applied to the generated claims.
     * @property annotations Annotations applied to the generated claims.
     */
    @NoArgs
    data class Metadata(
        val name: String,
        val labels: Map<String, String>?,
        val annotations: Map<String, String>?
    ) {
        /**
         * Validates that the template name is not blank.
         */
        init {
            require(name.isNotBlank()) { "Volume claim template name must not be blank" }
        }
    }

    /**
     * The storage a generated PersistentVolumeClaim requests.
     *
     * @property accessModes               How the volume may be mounted. Most dynamically provisioned
     *                                     volumes only support [AccessMode.ReadWriteOnce].
     * @property storageClassName          The StorageClass to provision from. Uses the cluster default when
     *                                     unset; an empty string disables dynamic provisioning.
     * @property volumeMode                Whether the volume is consumed as a filesystem or a raw block device.
     * @property resources                 The requested and maximum storage size.
     * @property selector                  Restricts binding to PersistentVolumes matching these labels. Only
     *                                     meaningful for statically provisioned volumes.
     * @property volumeName                Binds the claim to one specific PersistentVolume by name.
     * @property dataSource                Populates the new volume from an existing snapshot or claim.
     *                                     Superseded by [dataSourceRef], which can express the same thing.
     * @property dataSourceRef             Populates the new volume from an existing object. Unlike
     *                                     [dataSource] it also accepts custom resources and a namespace.
     * @property volumeAttributesClassName The VolumeAttributesClass applying mutable QoS parameters such as
     *                                     IOPS or throughput.
     */
    @NoArgs
    data class Spec(
        val accessModes: List<AccessMode>?,
        val storageClassName: String?,
        val volumeMode: VolumeMode?,
        val resources: ResourceRequirements?,
        val selector: LabelSelectorSpec?,
        val volumeName: String?,
        val dataSource: TypedObjectReferenceSpec?,
        val dataSourceRef: TypedObjectReferenceSpec?,
        val volumeAttributesClassName: String?
    )

    /**
     * The storage size a claim requests and may grow to.
     *
     * @property requests The storage size the claim requires. Effectively mandatory for dynamic provisioning.
     * @property limits   The maximum storage the claim may consume.
     */
    @NoArgs
    data class ResourceRequirements(
        val requests: StorageResource?,
        val limits: StorageResource?
    )

    /**
     * A storage quantity of a claim.
     *
     * @property storage The amount of storage.
     */
    @NoArgs
    data class StorageResource(
        val storage: MemoryValue?
    )

    /**
     * How a volume may be mounted by the Pods using it.
     */
    @Suppress("unused")
    enum class AccessMode {
        /**
         * Mountable read-write by a single node. The most widely supported mode.
         */
        ReadWriteOnce,

        /**
         * Mountable read-only by many nodes at once.
         */
        ReadOnlyMany,

        /**
         * Mountable read-write by many nodes at once. Requires a shared filesystem such as NFS or CephFS.
         */
        ReadWriteMany,

        /**
         * Mountable read-write by a single Pod, even if other Pods run on the same node.
         */
        ReadWriteOncePod
    }

    /**
     * How the contents of a volume are presented to a container.
     */
    @Suppress("unused")
    enum class VolumeMode {
        /**
         * The volume is formatted and mounted as a filesystem. This is the default.
         */
        Filesystem,

        /**
         * The volume is exposed as an unformatted raw block device.
         */
        Block
    }
}

/**
 * References an object a new volume is populated from.
 *
 * Typically this is a VolumeSnapshot or another PersistentVolumeClaim, but with a custom [apiGroup] it
 * can also address a resource provided by a storage operator.
 *
 * @property kind      The kind of the referenced object, for example `VolumeSnapshot`.
 * @property name      The name of the referenced object.
 * @property apiGroup  The API group of the referenced object. Unset or empty means the core API group,
 *                     which is what a reference to a PersistentVolumeClaim uses.
 * @property namespace The namespace the referenced object lives in. Only honoured for `dataSourceRef` and
 *                     requires a ReferenceGrant allowing the cross-namespace access.
 */
@NoArgs
data class TypedObjectReferenceSpec(
    val kind: String,
    val name: String,
    val apiGroup: String?,
    val namespace: String?
) {
    /**
     * Validates the referenced kind and name.
     */
    init {
        require(kind.isNotBlank()) { "Referenced kind must not be blank" }
        require(name.isNotBlank()) { "Referenced name must not be blank" }
        namespace?.let { require(it.isNotBlank()) { "Referenced namespace must not be blank" } }
    }
}
