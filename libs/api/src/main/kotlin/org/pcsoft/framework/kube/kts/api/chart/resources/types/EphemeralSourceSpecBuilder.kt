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

/**
 * Builder for an [EphemeralSourceSpec], provisioning a per-Pod PersistentVolumeClaim.
 */
class EphemeralSourceSpecBuilder internal constructor() : SourceSpecBuilder<EphemeralSourceSpec> {
    private val volumeClaimTemplate = EphemeralVolumeClaimTemplateSpecBuilder()

    /**
     * Configures the volume claim template of the ephemeral volume.
     *
     * @param prepare Configures the [EphemeralVolumeClaimTemplateSpecBuilder].
     */
    fun volumeClaimTemplate(prepare: EphemeralVolumeClaimTemplateSpecBuilder.() -> Unit) {
        volumeClaimTemplate.apply(prepare)
    }

    /**
     * Sets the labels and annotations applied to the generated claim.
     *
     * @param labels      Labels applied to the generated claim.
     * @param annotations Annotations applied to the generated claim.
     */
    fun metadata(labels: Map<String, String>? = null, annotations: Map<String, String>? = null) =
        volumeClaimTemplate.metadata(labels, annotations)

    /**
     * Configures the claim specification describing the requested storage.
     *
     * @param prepare Configures the [EphemeralVolumeClaimTemplateSpecBuilder.ClaimSpecBuilder].
     */
    fun spec(prepare: EphemeralVolumeClaimTemplateSpecBuilder.ClaimSpecBuilder.() -> Unit) =
        volumeClaimTemplate.spec(prepare)

    /**
     * Builds the configured ephemeral source.
     *
     * @return An [EphemeralSourceSpec] carrying the configured values.
     * @throws IllegalArgumentException If no claim specification has been configured.
     */
    override fun build(): EphemeralSourceSpec = EphemeralSourceSpec(volumeClaimTemplate.build())
}
