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
 * Builder for a [VsphereVolumeSourceSpec].
 *
 * @constructor Creates a builder for the given VMDK.
 * @param volumePath The datastore path of the VMDK.
 */
class VsphereVolumeSourceSpecBuilder internal constructor(private val volumePath: String) :
    SourceSpecBuilder<VsphereVolumeSourceSpec> {
    /**
     * The filesystem to mount, for example `ext4`.
     */
    var fsType: String? = null

    /**
     * The name of the storage policy profile applied to the disk.
     */
    var storagePolicyName: String? = null

    /**
     * The identifier of the storage policy profile.
     */
    var storagePolicyID: String? = null

    /**
     * Builds the configured vSphere source.
     *
     * @return A [VsphereVolumeSourceSpec] carrying the configured values.
     */
    override fun build(): VsphereVolumeSourceSpec =
        VsphereVolumeSourceSpec(volumePath, fsType, storagePolicyName, storagePolicyID)
}
