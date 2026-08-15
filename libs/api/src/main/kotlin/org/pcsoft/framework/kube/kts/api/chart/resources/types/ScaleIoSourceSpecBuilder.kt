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
 * Builder for a [ScaleIoSourceSpec].
 *
 * @constructor Creates a builder for the given ScaleIO system.
 * @param gateway   The address of the ScaleIO API gateway.
 * @param system    The name of the storage system.
 * @param secretRef The name of the Secret holding the ScaleIO credentials.
 */
@Suppress("DEPRECATION")
class ScaleIoSourceSpecBuilder internal constructor(
    private val gateway: String,
    private val system: String,
    private val secretRef: String
) : SourceSpecBuilder<ScaleIoSourceSpec> {
    /**
     * If true, the gateway is contacted over TLS.
     */
    var sslEnabled: Boolean? = null

    /**
     * The name of the protection domain the storage pool belongs to.
     */
    var protectionDomain: String? = null

    /**
     * The name of the storage pool the volume lives in.
     */
    var storagePool: String? = null

    /**
     * The redundancy mode of the volume.
     */
    var storageMode: ScaleIoSourceSpec.StorageMode? = null

    /**
     * The name of the volume already created in the ScaleIO system.
     */
    var volumeName: String? = null

    /**
     * The filesystem to mount, for example `ext4`.
     */
    var fsType: String? = null

    /**
     * If true, the volume is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * Builds the configured ScaleIO source.
     *
     * @return A [ScaleIoSourceSpec] carrying the configured values.
     */
    override fun build(): ScaleIoSourceSpec = ScaleIoSourceSpec(
        gateway = gateway,
        system = system,
        secretRef = LocalObjectReferenceSpecBuilder(secretRef).build(),
        sslEnabled = sslEnabled,
        protectionDomain = protectionDomain,
        storagePool = storagePool,
        storageMode = storageMode,
        volumeName = volumeName,
        fsType = fsType,
        readOnly = readOnly
    )
}
