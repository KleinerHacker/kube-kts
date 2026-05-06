/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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

import org.pcsoft.framework.kube.kts.api.chart.types.MetadataPodSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

@NoArgs
data class PodSpec(
    val containers: List<ContainerSpec>,
    val restartPolicy: RestartPolicy?,
    val serviceAccountName: String?,
    val nodeSelector: Map<String, String>?,
    val imagePullSecrets: List<String>?,
    val volumes: List<VolumeSpec>?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val terminationGracePeriodSeconds: Int?,
    val readinessGates: List<String>?,
) {
    @Suppress("unused")
    enum class RestartPolicy {
        Always,
        OnFailure,
        Never
    }
}

@NoArgs
data class PodTemplateSpec(
    val metadata: MetadataPodSpec?,
    val spec: PodSpec
)
