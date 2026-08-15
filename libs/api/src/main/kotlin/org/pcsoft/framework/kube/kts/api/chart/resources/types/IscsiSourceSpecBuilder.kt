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
 * Builder for an [IscsiSourceSpec].
 *
 * @constructor Creates a builder for the given iSCSI target.
 * @param targetPortal The iSCSI target portal.
 * @param iqn          The iSCSI qualified name of the target.
 * @param lun          The logical unit number.
 */
class IscsiSourceSpecBuilder internal constructor(
    private val targetPortal: String,
    private val iqn: String,
    private val lun: Int
) : SourceSpecBuilder<IscsiSourceSpec> {
    private var portals: MutableList<String>? = null

    /**
     * The iSCSI interface name. Defaults to `default` when unset.
     */
    var iscsiInterface: String? = null

    /**
     * The filesystem to mount, for example `ext4`.
     */
    var fsType: String? = null

    /**
     * If true, the logical unit is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * If true, CHAP authentication is used for target discovery.
     */
    var chapAuthDiscovery: Boolean? = null

    /**
     * If true, CHAP authentication is used for the session itself.
     */
    var chapAuthSession: Boolean? = null

    /**
     * The name of a Secret holding the CHAP credentials.
     */
    var secretRef: String? = null

    /**
     * Overrides the initiator name for this connection.
     */
    var initiatorName: String? = null

    /**
     * Adds an additional target portal for multipath access.
     *
     * @param portal The target portal to add.
     */
    fun addPortal(portal: String) {
        if (portals == null) {
            portals = mutableListOf()
        }
        portals!!.add(portal)
    }

    /**
     * Builds the configured iSCSI source.
     *
     * @return An [IscsiSourceSpec] carrying the configured values.
     */
    override fun build(): IscsiSourceSpec = IscsiSourceSpec(
        targetPortal = targetPortal,
        iqn = iqn,
        lun = lun,
        iscsiInterface = iscsiInterface,
        fsType = fsType,
        readOnly = readOnly,
        portals = portals,
        chapAuthDiscovery = chapAuthDiscovery,
        chapAuthSession = chapAuthSession,
        secretRef = secretRef?.let { LocalObjectReferenceSpecBuilder(it).build() },
        initiatorName = initiatorName
    )
}
