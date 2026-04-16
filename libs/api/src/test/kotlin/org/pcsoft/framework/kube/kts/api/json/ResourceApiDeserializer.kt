package org.pcsoft.framework.kube.kts.api.json

import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.types.MetadataSpec
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
        val metadata = ctxt.readValue(TreeTraversingParser(tree["metadata"]), MetadataSpec::class.java)

        val targetType = when (kind) {
            ServiceSpec.KIND -> ServiceSpec::class.java
            else -> throw NotImplementedError()
        }
        val spec = ctxt.readValue(TreeTraversingParser(tree["spec"]), targetType)
        
        return TemplateSpec<ResourceSpec>(apiVersion, kind, metadata, spec)
    }

}