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

package org.pcsoft.framework.kube.kts.api.intern.jackson

import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeSpec
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

class VolumeSpecSerializer : ValueSerializer<VolumeSpec>() {
    override fun serialize(
        value: VolumeSpec?,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeObject {
            gen.writeStringProperty("name", value.name)
            when (value.source) {
                is VolumeSpec.SecretSourceSpec -> gen.writePOJOProperty("secret", value.source)
                is VolumeSpec.ConfigMapSourceSpec -> gen.writePOJOProperty("configMap", value.source)
                is VolumeSpec.PersistentVolumeClaimSourceSpec -> gen.writePOJOProperty("persistentVolumeClaim", value.source)
                is VolumeSpec.HostPathSourceSpec -> gen.writePOJOProperty("hostPath", value.source)
                is VolumeSpec.EmptyDirSourceSpec -> gen.writePOJOProperty("emptyDir", value.source)
            }
        }
    }
}