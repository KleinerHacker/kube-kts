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
 * Builder for an [RbdSourceSpec].
 *
 * @constructor Creates a builder for the given RADOS image.
 * @param image The name of the RADOS image.
 */
class RbdSourceSpecBuilder internal constructor(private val image: String) : SourceSpecBuilder<RbdSourceSpec> {
    private val monitors = mutableListOf<String>()

    /**
     * The RADOS pool the image lives in. Defaults to `rbd` when unset.
     */
    var pool: String? = null

    /**
     * The RADOS user to authenticate as. Defaults to `admin` when unset.
     */
    var user: String? = null

    /**
     * The path of the keyring file on the node.
     */
    var keyring: String? = null

    /**
     * The name of a Secret holding the authentication key, taking precedence over the keyring.
     */
    var secretRef: String? = null

    /**
     * The filesystem to mount, for example `ext4`.
     */
    var fsType: String? = null

    /**
     * If true, the device is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * Adds the address of a Ceph monitor.
     *
     * @param monitor The monitor address to add.
     */
    fun addMonitor(monitor: String) {
        monitors += monitor
    }

    /**
     * Builds the configured RBD source.
     *
     * @return An [RbdSourceSpec] carrying the configured values.
     */
    override fun build(): RbdSourceSpec = RbdSourceSpec(
        monitors = monitors.toList(),
        image = image,
        pool = pool,
        user = user,
        keyring = keyring,
        secretRef = secretRef?.let { LocalObjectReferenceSpecBuilder(it).build() },
        fsType = fsType,
        readOnly = readOnly
    )
}
