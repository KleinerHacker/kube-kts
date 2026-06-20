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
 * A builder class for constructing a list of [VolumeClaimTemplateSpec] objects, which define the
 * PersistentVolumeClaim templates of a Kubernetes StatefulSet.
 */
class VolumeClaimTemplateListSpecBuilder internal constructor() {
    private val claims = mutableListOf<VolumeClaimTemplateSpecBuilder>()

    /**
     * Adds a PersistentVolumeClaim template to the StatefulSet.
     *
     * Example:
     * ```kotlin
     * claim("data") {
     *     accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
     *     storageClassName = "standard"
     *     requests {
     *         storage = 1.giBytes
     *     }
     * }
     * ```
     *
     * @param name The name of the claim. Must match a `volumeMount` of the Pod's containers.
     * @param prepare A lambda with a receiver of [VolumeClaimTemplateSpecBuilder] to configure the claim.
     */
    fun claim(name: String, prepare: VolumeClaimTemplateSpecBuilder.() -> Unit = {}) {
        claims.add(VolumeClaimTemplateSpecBuilder(name).apply(prepare))
    }

    internal fun build(): List<VolumeClaimTemplateSpec> {
        require(claims.isNotEmpty()) { "Volume claim templates require at least one claim" }

        return claims.map { it.build() }
    }
}

/**
 * A builder class for constructing a single [VolumeClaimTemplateSpec].
 *
 * @constructor Creates an instance of `VolumeClaimTemplateSpecBuilder` for the claim with the given [name].
 */
class VolumeClaimTemplateSpecBuilder internal constructor(private val name: String) {
    private var labels: MutableMap<String, String>? = null
    private var annotations: MutableMap<String, String>? = null
    private var requests: StorageResourceBuilder? = null
    private var limits: StorageResourceBuilder? = null

    /**
     * The desired access modes of the volume (e.g. `ReadWriteOnce`).
     */
    var accessModes: List<VolumeClaimTemplateSpec.AccessMode>? = null

    /**
     * The name of the StorageClass used to dynamically provision the volume. If omitted, the cluster
     * default StorageClass is used.
     */
    var storageClassName: String? = null

    /**
     * Defines whether the volume is consumed as a `Filesystem` or a raw `Block` device.
     */
    var volumeMode: VolumeClaimTemplateSpec.VolumeMode? = null

    /**
     * Sets the desired access modes of the volume.
     *
     * @param modes The access modes to apply to the claim.
     */
    fun accessModes(vararg modes: VolumeClaimTemplateSpec.AccessMode) {
        accessModes = modes.toList()
    }

    /**
     * Adds a label to the claim metadata.
     *
     * @param key The key of the label.
     * @param value The value of the label.
     */
    fun label(key: String, value: String) {
        if (labels == null) {
            labels = mutableMapOf()
        }
        labels!![key] = value
    }

    /**
     * Configures labels for the claim metadata using a builder pattern.
     *
     * @param prepare A lambda function that configures the [LabelListBuilder] instance.
     */
    fun labels(prepare: LabelListBuilder.() -> Unit) =
        LabelListBuilder().prepare()

    /**
     * Adds an annotation to the claim metadata.
     *
     * @param key The key of the annotation.
     * @param value The value of the annotation.
     */
    fun annotation(key: String, value: String) {
        if (annotations == null) {
            annotations = mutableMapOf()
        }
        annotations!![key] = value
    }

    /**
     * Configures annotations for the claim metadata using a builder pattern.
     *
     * @param prepare A lambda function that configures the [AnnotationListBuilder] instance.
     */
    fun annotations(prepare: AnnotationListBuilder.() -> Unit) =
        AnnotationListBuilder().prepare()

    /**
     * Configures the minimum amount of storage requested for the claim.
     *
     * @param prepare A lambda with a receiver of [StorageResourceBuilder].
     */
    fun requests(prepare: StorageResourceBuilder.() -> Unit) {
        requests = StorageResourceBuilder().apply(prepare)
    }

    /**
     * Configures the maximum amount of storage the claim is allowed to grow to.
     *
     * @param prepare A lambda with a receiver of [StorageResourceBuilder].
     */
    fun limits(prepare: StorageResourceBuilder.() -> Unit) {
        limits = StorageResourceBuilder().apply(prepare)
    }

    internal fun build(): VolumeClaimTemplateSpec {
        require(name.isNotBlank()) { "Volume claim template name is required" }

        val resources = if (requests != null || limits != null) {
            VolumeClaimTemplateSpec.ResourceRequirements(requests?.build(), limits?.build())
        } else {
            null
        }

        return VolumeClaimTemplateSpec(
            metadata = VolumeClaimTemplateSpec.Metadata(name, labels, annotations),
            spec = VolumeClaimTemplateSpec.Spec(
                accessModes = accessModes,
                storageClassName = storageClassName,
                volumeMode = volumeMode,
                resources = resources
            )
        )
    }

    /**
     * Builder for the storage quantities of a [VolumeClaimTemplateSpec.ResourceRequirements] block.
     *
     * @property storage The amount of storage expressed as a [MemoryValue] (e.g. `1.giBytes`).
     */
    class StorageResourceBuilder internal constructor() {
        var storage: MemoryValue? = null

        internal fun build(): VolumeClaimTemplateSpec.StorageResource =
            VolumeClaimTemplateSpec.StorageResource(storage)
    }

    /**
     * Builder for adding labels to the claim metadata.
     */
    inner class LabelListBuilder internal constructor() {
        /**
         * Adds a label to the claim metadata.
         *
         * @param key The label key.
         * @param value The label value.
         */
        fun label(key: String, value: String) {
            this@VolumeClaimTemplateSpecBuilder.label(key, value)
        }
    }

    /**
     * Builder for adding annotations to the claim metadata.
     */
    inner class AnnotationListBuilder internal constructor() {
        /**
         * Adds an annotation to the claim metadata.
         *
         * @param key The annotation key.
         * @param value The annotation value.
         */
        fun annotation(key: String, value: String) {
            this@VolumeClaimTemplateSpecBuilder.annotation(key, value)
        }
    }
}
