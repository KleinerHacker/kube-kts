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
 * Builder for a [FibreChannelSourceSpec].
 */
class FibreChannelSourceSpecBuilder internal constructor() : SourceSpecBuilder<FibreChannelSourceSpec> {
    private var targetWWNs: MutableList<String>? = null
    private var wwids: MutableList<String>? = null

    /**
     * The logical unit number to mount. Required together with target world wide names.
     */
    var lun: Int? = null

    /**
     * The filesystem to mount, for example `ext4`.
     */
    var fsType: String? = null

    /**
     * If true, the logical unit is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * Adds a world wide name of a target port.
     *
     * @param wwn The world wide name to add.
     */
    fun addTargetWWN(wwn: String) {
        if (targetWWNs == null) {
            targetWWNs = mutableListOf()
        }
        targetWWNs!!.add(wwn)
    }

    /**
     * Adds a world wide identifier of a volume.
     *
     * @param wwid The world wide identifier to add.
     */
    fun addWWID(wwid: String) {
        if (wwids == null) {
            wwids = mutableListOf()
        }
        wwids!!.add(wwid)
    }

    /**
     * Builds the configured Fibre Channel source.
     *
     * @return A [FibreChannelSourceSpec] carrying the configured values.
     */
    override fun build(): FibreChannelSourceSpec =
        FibreChannelSourceSpec(targetWWNs, lun, wwids, fsType, readOnly)
}
