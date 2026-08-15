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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeMountSpec.MountPropagationMode
import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeMountSpec.RecursiveReadOnlyMode

/**
 * Builder for a [VolumeMountSpec], describing how a Pod volume is mounted into a container.
 *
 * Example:
 * ```kotlin
 * addVolumeMount("config", "/etc/app") {
 *     readOnly = true
 *     subPath = "application.yaml"
 * }
 * ```
 *
 * @constructor Creates a builder for the given volume name and mount path.
 * @param name      The name of the volume to mount, matching a volume declared on the Pod.
 * @param mountPath The absolute path inside the container the volume is mounted at.
 */
class VolumeMountSpecBuilder internal constructor(private val name: String, private val mountPath: String) {
    /**
     * If true, the volume is mounted read-only. Defaults to read-write when unset.
     */
    var readOnly: Boolean? = null

    /**
     * Mounts only this sub-directory or file of the volume instead of its root.
     *
     * Mutually exclusive with [subPathExpr].
     */
    var subPath: String? = null

    /**
     * Like [subPath], but may reference container environment variables using `$(VAR_NAME)` syntax.
     *
     * Mutually exclusive with [subPath].
     */
    var subPathExpr: String? = null

    /**
     * Controls how mounts are propagated between the host and this container.
     */
    var mountPropagation: MountPropagationMode? = null

    /**
     * Controls whether a read-only mount is applied recursively to all of its submounts.
     *
     * Requires [readOnly] to be set to true.
     */
    var recursiveReadOnly: RecursiveReadOnlyMode? = null

    /**
     * Builds the configured volume mount.
     *
     * @return A [VolumeMountSpec] carrying the configured values.
     */
    internal fun build() = VolumeMountSpec(
        name = name,
        mountPath = mountPath,
        readOnly = readOnly,
        subPath = subPath,
        subPathExpr = subPathExpr,
        mountPropagation = mountPropagation,
        recursiveReadOnly = recursiveReadOnly
    )
}
