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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Describes how a volume declared on the Pod is mounted into a container's filesystem.
 *
 * @property name             The name of the volume to mount. Must match the name of a volume declared
 *                            on the Pod.
 * @property mountPath        The absolute path inside the container the volume is mounted at. Must not
 *                            contain `:`.
 * @property readOnly         If true, the volume is mounted read-only. Defaults to read-write.
 * @property subPath          Mounts only the given sub-directory or file of the volume instead of its
 *                            root. Mutually exclusive with [subPathExpr].
 * @property subPathExpr      Like [subPath], but the value may reference container environment variables
 *                            using `$(VAR_NAME)` syntax. Mutually exclusive with [subPath].
 * @property mountPropagation Controls how mounts are propagated between the host and this container.
 * @property recursiveReadOnly Controls whether a read-only mount is applied recursively to all submounts.
 *                            Only valid together with [readOnly] set to true.
 */
@NoArgs
data class VolumeMountSpec(
    val name: String,
    val mountPath: String,
    val readOnly: Boolean?,
    val subPath: String?,
    val subPathExpr: String?,
    val mountPropagation: MountPropagationMode?,
    val recursiveReadOnly: RecursiveReadOnlyMode?
) {
    /**
     * Validates the mount paths and the mutually exclusive sub-path fields.
     */
    init {
        require(name.isNotBlank()) { "Volume mount name must not be blank" }
        require(mountPath.isNotBlank()) { "Mount path must not be blank" }
        require(!mountPath.contains(':')) { "Mount path must not contain ':', but was '$mountPath'" }
        require(subPath == null || subPathExpr == null) {
            "Only one of 'subPath' and 'subPathExpr' may be set"
        }
        subPath?.let { require(it.isNotBlank()) { "Sub path must not be blank" } }
        subPathExpr?.let { require(it.isNotBlank()) { "Sub path expression must not be blank" } }
        require(recursiveReadOnly == null || readOnly == true) {
            "'recursiveReadOnly' requires 'readOnly' to be true"
        }
    }

    /**
     * Controls how mount points are propagated between the host and a container.
     */
    @Suppress("unused")
    enum class MountPropagationMode {
        /**
         * The container receives no submounts created after the mount, and none of its own are visible
         * to the host. This is the default.
         */
        None,

        /**
         * The container receives submounts created by the host, but the host does not receive the
         * container's own submounts.
         */
        HostToContainer,

        /**
         * Mounts propagate in both directions between the host and the container. Requires a privileged
         * container.
         */
        Bidirectional
    }

    /**
     * Controls whether a read-only mount is enforced recursively for all of its submounts.
     */
    @Suppress("unused")
    enum class RecursiveReadOnlyMode {
        /**
         * Recursive read-only is not applied; only the mount itself is read-only.
         */
        Disabled,

        /**
         * Recursive read-only is applied if the runtime supports it, otherwise the mount falls back to
         * a plain read-only mount.
         */
        IfPossible,

        /**
         * Recursive read-only is required; the container fails to start if the runtime cannot provide it.
         */
        Enabled
    }
}

/**
 * Describes a raw block device from a volume that is made available inside a container.
 *
 * Unlike a [VolumeMountSpec], the volume is not mounted as a filesystem but exposed as a block device
 * the application reads and writes directly.
 *
 * @property name       The name of the volume to expose. Must match the name of a volume declared on the Pod.
 * @property devicePath The absolute path inside the container the block device is made available at.
 */
@NoArgs
data class VolumeDeviceSpec(
    val name: String,
    val devicePath: String
) {
    /**
     * Validates that name and device path are not blank.
     */
    init {
        require(name.isNotBlank()) { "Volume device name must not be blank" }
        require(devicePath.isNotBlank()) { "Device path must not be blank" }
    }
}
