package org.pcsoft.framework.kube.kts.core.intern.json

import tools.jackson.databind.json.JsonMapper
import tools.jackson.dataformat.yaml.YAMLMapper

private val yamlMapper = YAMLMapper()
private val jsonMapper = JsonMapper()

fun String.yamlToJson(): String {
    val jsonNode = yamlMapper.readTree(this)
    return jsonMapper.writeValueAsString(jsonNode)
}