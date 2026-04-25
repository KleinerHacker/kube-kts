package org.pcsoft.framework.kube.kts.core.merge

import tools.jackson.databind.JsonNode
import tools.jackson.databind.node.ArrayNode
import tools.jackson.databind.node.ObjectNode
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule


internal class DefaultYamlMerging(
    val arrayMergeStrategy: YamlArrayMergeStrategy,
    val objectMergeStrategy: YamlObjectMergeStrategy,
    val newObjectStrategy: YamlNewObjectStrategy,
    val valueMergeStrategy: YamlValueMergeStrategy
) : YamlMerging {
    private val mapper = YAMLMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .build()

    override fun merge(baseYaml: String, vararg overlayYaml: String): String {
        var result = mapper.readTree(baseYaml)

        for (overlay in overlayYaml) {
            val overlayNode = mapper.readTree(overlay)
            result = mergeNodes(result, overlayNode)
        }

        return mapper.writeValueAsString(result)
    }

    private fun mergeNodes(base: JsonNode, overlay: JsonNode): JsonNode {
        if (overlay.isObject && base.isObject) {
            val baseObject = base as ObjectNode
            val overlayObject = overlay as ObjectNode

            overlayObject.propertyNames().forEach {
                if (baseObject.has(it)) {
                    when (objectMergeStrategy) {
                        YamlObjectMergeStrategy.None -> { /* Do nothing */ }
                        YamlObjectMergeStrategy.Merge -> baseObject.set(it, mergeNodes(baseObject[it], overlayObject[it]))
                        YamlObjectMergeStrategy.Replace -> baseObject.set(it, overlayObject[it])
                    }
                } else {
                    when (newObjectStrategy) {
                        YamlNewObjectStrategy.AddNever -> { /* Do nothing */ }
                        YamlNewObjectStrategy.AddAlways -> baseObject.set(it, overlayObject[it])
                    }
                }
            }

            return baseObject
        } else if (overlay.isArray && base.isArray) {
            val baseArray = base as ArrayNode
            val overlayArray = overlay as ArrayNode

            when (arrayMergeStrategy) {
                YamlArrayMergeStrategy.None -> { /* do nothing */ }
                YamlArrayMergeStrategy.AddFirst -> overlayArray.forEachIndexed { index, element ->
                    baseArray.insert(index, element)
                }
                YamlArrayMergeStrategy.AddLast -> overlayArray.forEach { element ->
                    baseArray.add(element)
                }
                YamlArrayMergeStrategy.Replace -> {
                    baseArray.removeAll()
                    baseArray.addAll(overlayArray)
                }
            }

            return baseArray
        } else {
            return when (valueMergeStrategy) {
                YamlValueMergeStrategy.None -> base
                YamlValueMergeStrategy.Replace -> overlay
            }
        }
    }
}