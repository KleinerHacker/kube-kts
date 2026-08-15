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
 * Builder for a [CephFsSourceSpec].
 */
class CephFsSourceSpecBuilder internal constructor() : SourceSpecBuilder<CephFsSourceSpec> {
    private val monitors = mutableListOf<String>()

    /**
     * The path within the filesystem to mount. Defaults to its root when unset.
     */
    var path: String? = null

    /**
     * The user to authenticate as. Defaults to `admin` when unset.
     */
    var user: String? = null

    /**
     * The path of the secret file on the node.
     */
    var secretFile: String? = null

    /**
     * The name of a Secret holding the authentication key, taking precedence over the secret file.
     */
    var secretRef: String? = null

    /**
     * If true, the filesystem is mounted read-only.
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
     * Builds the configured CephFS source.
     *
     * @return A [CephFsSourceSpec] carrying the configured values.
     */
    override fun build(): CephFsSourceSpec = CephFsSourceSpec(
        monitors = monitors.toList(),
        path = path,
        user = user,
        secretFile = secretFile,
        secretRef = secretRef?.let { LocalObjectReferenceSpecBuilder(it).build() },
        readOnly = readOnly
    )
}
