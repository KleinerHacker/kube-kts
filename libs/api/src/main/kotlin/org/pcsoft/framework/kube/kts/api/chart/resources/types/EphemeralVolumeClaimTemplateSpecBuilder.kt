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

import org.pcsoft.framework.kube.kts.api.types.MemoryValue

/**
 * Builder class for creating instances of [EphemeralVolumeClaimTemplateSpec].
 *
 * The template describes the PersistentVolumeClaim that is provisioned together with the pod and
 * deleted with it again. Unlike a StatefulSet's volume claim template it carries no name, since the
 * name is derived from the pod and the volume.
 *
 * @constructor Creates an instance of [EphemeralVolumeClaimTemplateSpecBuilder] for internal usage.
 */
class EphemeralVolumeClaimTemplateSpecBuilder internal constructor() {
    private var labels: Map<String, String>? = null
    private var annotations: Map<String, String>? = null
    private var spec: ClaimSpecBuilder? = null

    /**
     * Sets the labels and annotations applied to the generated claim.
     *
     * @param labels      Labels applied to the generated claim.
     * @param annotations Annotations applied to the generated claim.
     */
    fun metadata(labels: Map<String, String>? = null, annotations: Map<String, String>? = null) {
        this.labels = labels
        this.annotations = annotations
    }

    /**
     * Configures the claim specification describing the requested storage.
     *
     * @param prepare Configures the [ClaimSpecBuilder].
     */
    fun spec(prepare: ClaimSpecBuilder.() -> Unit) {
        spec = ClaimSpecBuilder().apply(prepare)
    }

    /**
     * Constructs and returns an [EphemeralVolumeClaimTemplateSpec] instance based on the configured values.
     *
     * @return An [EphemeralVolumeClaimTemplateSpec] carrying the configured metadata and claim specification.
     * @throws IllegalArgumentException If no claim specification has been configured.
     */
    internal fun build(): EphemeralVolumeClaimTemplateSpec {
        require(spec != null) { "A volume claim template spec must be set for an ephemeral volume" }

        val metadata = if (labels == null && annotations == null) {
            null
        } else {
            EphemeralVolumeClaimTemplateSpec.Metadata(labels, annotations)
        }

        return EphemeralVolumeClaimTemplateSpec(metadata, spec!!.build())
    }

    /**
     * Builder for the claim specification of an ephemeral volume.
     *
     * Unlike a StatefulSet's volume claim template, an ephemeral claim carries no name - it is
     * derived from the Pod and volume name - so only the storage requirements are configurable here.
     */
    class ClaimSpecBuilder internal constructor() {
        private var requests: VolumeClaimTemplateSpec.StorageResource? = null
        private var limits: VolumeClaimTemplateSpec.StorageResource? = null

        /**
         * The access modes the generated claim requests.
         */
        var accessModes: List<VolumeClaimTemplateSpec.AccessMode>? = null

        /**
         * The storage class the generated claim requests. Uses the cluster default when unset.
         */
        var storageClassName: String? = null

        /**
         * Whether the volume is consumed as a filesystem or as a raw block device.
         */
        var volumeMode: VolumeClaimTemplateSpec.VolumeMode? = null

        /**
         * The VolumeAttributesClass applying mutable QoS parameters such as IOPS or throughput.
         */
        var volumeAttributesClassName: String? = null

        /**
         * Sets the access modes the generated claim requests.
         *
         * @param modes The requested access modes.
         */
        fun accessModes(vararg modes: VolumeClaimTemplateSpec.AccessMode) {
            accessModes = modes.toList()
        }

        /**
         * Sets the amount of storage the generated claim requests.
         *
         * @param storage The requested storage size.
         */
        fun requests(storage: MemoryValue) {
            requests = VolumeClaimTemplateSpec.StorageResource(storage)
        }

        /**
         * Sets the upper bound of storage the generated claim may consume.
         *
         * @param storage The maximum storage size.
         */
        fun limits(storage: MemoryValue) {
            limits = VolumeClaimTemplateSpec.StorageResource(storage)
        }

        /**
         * Builds the configured claim specification.
         *
         * @return A [VolumeClaimTemplateSpec.Spec] carrying the configured values.
         */
        internal fun build(): VolumeClaimTemplateSpec.Spec {
            val resources = if (requests != null || limits != null) {
                VolumeClaimTemplateSpec.ResourceRequirements(requests, limits)
            } else {
                null
            }
            return VolumeClaimTemplateSpec.Spec(
                accessModes = accessModes,
                storageClassName = storageClassName,
                volumeMode = volumeMode,
                resources = resources,
                selector = null,
                volumeName = null,
                dataSource = null,
                dataSourceRef = null,
                volumeAttributesClassName = volumeAttributesClassName
            )
        }
    }
}
