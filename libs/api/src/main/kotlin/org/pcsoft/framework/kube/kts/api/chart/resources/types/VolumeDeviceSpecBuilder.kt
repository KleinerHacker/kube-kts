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
 * Builder for a [VolumeDeviceSpec], exposing a volume to a container as a raw block device.
 *
 * @constructor Creates a builder for the given volume name and device path.
 * @param name       The name of the volume to expose, matching a volume declared on the Pod.
 * @param devicePath The absolute path inside the container the block device is made available at.
 */
class VolumeDeviceSpecBuilder internal constructor(private val name: String, private val devicePath: String) {
    /**
     * Builds the configured volume device.
     *
     * @return A [VolumeDeviceSpec] carrying the configured values.
     */
    internal fun build() = VolumeDeviceSpec(name, devicePath)
}
