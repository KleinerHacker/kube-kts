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
 * A builder class for constructing a list of [VolumeClaimTemplateSpec] objects, which define the
 * PersistentVolumeClaim templates of a Kubernetes StatefulSet.
 */
class VolumeClaimTemplateListSpecBuilder internal constructor() {
    private val claims = mutableListOf<VolumeClaimTemplateSpecBuilder>()

    /**
     * Adds a PersistentVolumeClaim template to the StatefulSet.
     *
     * Example:
     * ```kotlin
     * claim("data") {
     *     accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
     *     storageClassName = "standard"
     *     requests {
     *         storage = 1.giBytes
     *     }
     * }
     * ```
     *
     * @param name The name of the claim. Must match a `volumeMount` of the Pod's containers.
     * @param prepare A lambda with a receiver of [VolumeClaimTemplateSpecBuilder] to configure the claim.
     */
    fun claim(name: String, prepare: VolumeClaimTemplateSpecBuilder.() -> Unit = {}) {
        claims.add(VolumeClaimTemplateSpecBuilder(name).apply(prepare))
    }

    internal fun build(): List<VolumeClaimTemplateSpec> {
        require(claims.isNotEmpty()) { "Volume claim templates require at least one claim" }

        return claims.map { it.build() }
    }
}
