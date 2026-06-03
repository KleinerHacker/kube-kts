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

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.HardwareResourceDataSpecDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.HardwareResourceDataSpecSerializer
import org.pcsoft.framework.kube.kts.api.types.CpuValue
import org.pcsoft.framework.kube.kts.api.types.MemoryValue
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Specification for hardware resource requirements and limits for containers.
 * Defines the compute resources required and maximum allowed for a container.
 */
@NoArgs
data class HardwareResourceSpec(
    /**
     * Minimum amount of compute resources required.
     * If requests are omitted for a container, it defaults to limits if that is explicitly specified.
     */
    var requests: Data?,
    /**
     * Maximum amount of compute resources allowed.
     * Limits cannot be exceeded by the container.
     */
    var limits: Data?,
) {

    /**
     * Resource data specifying CPU, memory, and storage requirements.
     * 
     * Uses [HardwareResourceDataSpecSerializer] for serialization and [HardwareResourceDataSpecDeserializer] for deserialization.
     */
    @NoArgs
    @JsonSerialize(using = HardwareResourceDataSpecSerializer::class)
    @JsonDeserialize(using = HardwareResourceDataSpecDeserializer::class)
    data class Data(
        /**
         * CPU resource requirement or limit expressed as a CpuValue.
         */
        val cpu: CpuValue?,
        /**
         * Memory resource requirement or limit expressed as a MemoryValue.
         */
        val memory: MemoryValue?,
        /**
         * Ephemeral storage resource requirement or limit expressed as a MemoryValue.
         */
        @field:JsonProperty("ephemeral-storage")
        val ephemeralStorage: MemoryValue?,
        /**
         * Extended resources mapped by resource name to quantity string.
         */
        val extendedResources: Map<String, String>?
    )

}