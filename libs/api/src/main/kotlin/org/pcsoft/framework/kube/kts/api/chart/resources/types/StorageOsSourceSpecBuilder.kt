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
 * Builder for a [StorageOsSourceSpec].
 *
 * @constructor Creates a builder for the given StorageOS volume.
 * @param volumeName The name of the StorageOS volume.
 */
class StorageOsSourceSpecBuilder internal constructor(private val volumeName: String) :
    SourceSpecBuilder<StorageOsSourceSpec> {
    /**
     * The StorageOS namespace the volume lives in.
     */
    var volumeNamespace: String? = null

    /**
     * The filesystem to mount, for example `ext4`.
     */
    var fsType: String? = null

    /**
     * If true, the volume is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * The name of a Secret holding the StorageOS API credentials.
     */
    var secretRef: String? = null

    /**
     * Builds the configured StorageOS source.
     *
     * @return A [StorageOsSourceSpec] carrying the configured values.
     */
    override fun build(): StorageOsSourceSpec = StorageOsSourceSpec(
        volumeName, volumeNamespace, fsType, readOnly, secretRef?.let { LocalObjectReferenceSpecBuilder(it).build() }
    )
}
