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

package org.pcsoft.framework.kube.kts.api.intern.json

import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.types.MetadataTemplateSpec
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.node.TreeTraversingParser

class ResourceApiDeserializer : ValueDeserializer<TemplateSpec<*>>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): TemplateSpec<*> {
        val tree = p.readValueAsTree<JsonNode>()

        val apiVersion = tree["apiVersion"].asString()
        val kind = tree["kind"].asString()
        val metadata = ctxt.readValue(TreeTraversingParser(tree["metadata"]), MetadataTemplateSpec::class.java)

        val targetType = when (kind) {
            ServiceSpec.KIND -> ServiceSpec::class.java
            else -> throw NotImplementedError()
        }
        val spec = ctxt.readValue(TreeTraversingParser(tree["spec"]), targetType)
        
        return TemplateSpec<ResourceSpec>(apiVersion, kind, metadata, spec)
    }

}