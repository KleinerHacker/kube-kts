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
 * Builder for a [GcePersistentDiskSourceSpec].
 *
 * @constructor Creates a builder for the given persistent disk.
 * @param pdName The name of the persistent disk.
 */
class GcePersistentDiskSourceSpecBuilder internal constructor(private val pdName: String) :
    SourceSpecBuilder<GcePersistentDiskSourceSpec> {
    /**
     * The filesystem to mount, for example `ext4`.
     */
    var fsType: String? = null

    /**
     * The partition of the disk to mount. Mounts the whole disk when unset.
     */
    var partition: Int? = null

    /**
     * If true, the disk is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * Builds the configured persistent disk source.
     *
     * @return A [GcePersistentDiskSourceSpec] carrying the configured values.
     */
    override fun build(): GcePersistentDiskSourceSpec =
        GcePersistentDiskSourceSpec(pdName, fsType, partition, readOnly)
}
