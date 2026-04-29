package org.pcsoft.framework.kube.kts.core.merge

import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.symbolArrowRight
import org.pcsoft.framework.kube.kts.logging.symbolSubProcess
import tools.jackson.databind.JsonNode
import tools.jackson.databind.node.ArrayNode
import tools.jackson.databind.node.ObjectNode
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule
import java.nio.file.Path


internal class DefaultYamlMerging(
    val arrayMergeStrategy: YamlArrayMergeStrategy
) : YamlMergingBase() {
    companion object {
        private val logger = logger()
    }

    private val mapper = YAMLMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .build()

    override fun doMerge(baseYaml: Path, overlayYaml: Array<out Path>): String {
        logger.atDebug().log { "$symbolArrowRight Use internal merge algorithm" }
        logger.atDebug().log { "$symbolSubProcess Merge ${overlayYaml.size} YAML files..." }

        var result = mapper.readTree(baseYaml)

        for (overlay in overlayYaml) {
            val overlayNode = mapper.readTree(overlay)
            result = mergeNodes(result, overlayNode)
        }

        return mapper.writeValueAsString(result)
    }

    private fun mergeNodes(base: JsonNode, overlay: JsonNode): JsonNode {
        return if (overlay.isObject && base.isObject) {
            val baseObject = base as ObjectNode
            val overlayObject = overlay as ObjectNode

            overlayObject.propertyNames().forEach {
                if (!baseObject.has(it))
                    baseObject.set(it, overlayObject[it])
                else
                    baseObject.set(it, mergeNodes(baseObject[it], overlayObject[it]))
            }

            baseObject
        } else if (overlay.isArray && base.isArray) {
            val baseArray = base as ArrayNode
            val overlayArray = overlay as ArrayNode

            when (arrayMergeStrategy) {
                YamlArrayMergeStrategy.None -> { /* do nothing */
                }

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

            baseArray
        } else {
            overlay
        }
    }
}