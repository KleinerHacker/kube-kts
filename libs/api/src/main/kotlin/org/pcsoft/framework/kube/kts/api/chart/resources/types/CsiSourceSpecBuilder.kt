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
 * Builder for a [CsiSourceSpec].
 *
 * @constructor Creates a builder for the given CSI driver.
 * @param driver The name of the CSI driver.
 */
class CsiSourceSpecBuilder internal constructor(private val driver: String) : SourceSpecBuilder<CsiSourceSpec> {
    private var volumeAttributes: MutableMap<String, String>? = null

    /**
     * If true, the volume is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * The filesystem to mount, for example `ext4`.
     */
    var fsType: String? = null

    /**
     * The name of a Secret holding credentials the driver needs when publishing the volume.
     */
    var nodePublishSecretRef: String? = null

    /**
     * Adds a driver-specific parameter.
     *
     * @param key   The parameter name.
     * @param value The parameter value.
     */
    fun addVolumeAttribute(key: String, value: String) {
        if (volumeAttributes == null) {
            volumeAttributes = mutableMapOf()
        }
        volumeAttributes!![key] = value
    }

    /**
     * Builds the configured CSI source.
     *
     * @return A [CsiSourceSpec] carrying the configured values.
     */
    override fun build(): CsiSourceSpec = CsiSourceSpec(
        driver = driver,
        readOnly = readOnly,
        fsType = fsType,
        volumeAttributes = volumeAttributes,
        nodePublishSecretRef = nodePublishSecretRef?.let { LocalObjectReferenceSpecBuilder(it).build() }
    )
}
