package org.pcsoft.framework.kube.kts.api.json

import org.pcsoft.framework.kube.kts.api.ResourceApi
import org.pcsoft.framework.kube.kts.api.ResourceSpec
import org.pcsoft.framework.kube.kts.api.ServiceSpec
import org.pcsoft.framework.kube.kts.api.types.MetadataSpec
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.node.TreeTraversingParser

class ResourceApiDeserializer : ValueDeserializer<ResourceApi<*>>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): ResourceApi<*> {
        val tree = p.readValueAsTree<JsonNode>()

        val apiVersion = tree["apiVersion"].asString()
        val kind = tree["kind"].asString()
        val metadata = ctxt.readValue(TreeTraversingParser(tree["metadata"]), MetadataSpec::class.java)

        val targetType = when (kind) {
            ServiceSpec.KIND -> ServiceSpec::class.java
            else -> throw NotImplementedError()
        }
        val spec = ctxt.readValue(TreeTraversingParser(tree["spec"]), targetType)
        
        return ResourceApi<ResourceSpec>(apiVersion, kind, metadata, spec)
    }

}