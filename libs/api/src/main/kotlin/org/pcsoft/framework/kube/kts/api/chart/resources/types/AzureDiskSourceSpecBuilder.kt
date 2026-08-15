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
 * Builder for an [AzureDiskSourceSpec].
 *
 * @constructor Creates a builder for the given Azure data disk.
 * @param diskName The name of the data disk.
 * @param diskURI  The resource URI of the data disk.
 */
class AzureDiskSourceSpecBuilder internal constructor(
    private val diskName: String,
    private val diskURI: String
) : SourceSpecBuilder<AzureDiskSourceSpec> {
    /**
     * The host caching mode used for the disk.
     */
    var cachingMode: AzureDiskSourceSpec.CachingMode? = null

    /**
     * The filesystem to mount, for example `ext4`.
     */
    var fsType: String? = null

    /**
     * If true, the disk is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * The storage model backing the disk.
     */
    var kind: AzureDiskSourceSpec.Kind? = null

    /**
     * Builds the configured Azure disk source.
     *
     * @return An [AzureDiskSourceSpec] carrying the configured values.
     */
    override fun build(): AzureDiskSourceSpec =
        AzureDiskSourceSpec(diskName, diskURI, cachingMode, fsType, readOnly, kind)
}
