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
 * Builder for a [FlexVolumeSourceSpec].
 *
 * @constructor Creates a builder for the given FlexVolume driver.
 * @param driver The name of the FlexVolume driver.
 */
@Suppress("DEPRECATION")
class FlexVolumeSourceSpecBuilder internal constructor(private val driver: String) :
    SourceSpecBuilder<FlexVolumeSourceSpec> {
    private var options: MutableMap<String, String>? = null

    /**
     * The filesystem to mount, for example `ext4`.
     */
    var fsType: String? = null

    /**
     * The name of a Secret holding credentials passed to the driver.
     */
    var secretRef: String? = null

    /**
     * If true, the volume is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * Adds a driver-specific parameter.
     *
     * @param key   The parameter name.
     * @param value The parameter value.
     */
    fun addOption(key: String, value: String) {
        if (options == null) {
            options = mutableMapOf()
        }
        options!![key] = value
    }

    /**
     * Builds the configured FlexVolume source.
     *
     * @return A [FlexVolumeSourceSpec] carrying the configured values.
     */
    override fun build(): FlexVolumeSourceSpec = FlexVolumeSourceSpec(
        driver, fsType, secretRef?.let { LocalObjectReferenceSpecBuilder(it).build() }, readOnly, options
    )
}
