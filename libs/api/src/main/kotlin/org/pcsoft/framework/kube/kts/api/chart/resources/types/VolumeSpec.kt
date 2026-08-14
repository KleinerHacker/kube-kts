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
import org.pcsoft.framework.kube.kts.api.intern.jackson.VolumeSpecDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.VolumeSpecSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Declares a volume on a Pod that its containers can mount.
 *
 * A volume pairs a name with exactly one source. The source determines where the data comes from -
 * a ConfigMap, a Secret, a PersistentVolumeClaim, scratch space on the node, or one of the many
 * network and cloud storage backends.
 *
 * In the rendered YAML the source is not nested under a `source` key; instead it becomes a sibling of
 * `name` whose key identifies the source type, for example:
 * ```yaml
 * - name: config
 *   configMap:
 *     name: my-config
 * ```
 * This flattening is performed by [VolumeSpecSerializer].
 *
 * @property name   The name of the volume. Must be unique within the Pod and is what
 *                  [VolumeMountSpec.name] refers to.
 * @property source The single source providing this volume's data.
 */
@NoArgs
@JsonSerialize(using = VolumeSpecSerializer::class)
@JsonDeserialize(using = VolumeSpecDeserializer::class)
data class VolumeSpec(
    val name: String,
    val source: SourceSpec,
) {
    /**
     * Validates that the volume name is not blank.
     */
    init {
        require(name.isNotBlank()) { "Volume name must not be blank" }
    }

    /**
     * The common contract of every volume source.
     *
     * Implementations are grouped by category across several files of this package: file-based sources
     * (ConfigMap, Secret, projected, downward API), node-local sources (emptyDir, hostPath,
     * PersistentVolumeClaim, ephemeral, image, CSI), network sources (NFS, iSCSI, Fibre Channel, RBD,
     * CephFS, GlusterFS), cloud provider sources, and sources that Kubernetes has since removed.
     *
     * The interface is sealed so that [VolumeSpecSerializer] can dispatch exhaustively; Kotlin therefore
     * requires all implementations to reside in this package.
     */
    sealed interface SourceSpec
}
