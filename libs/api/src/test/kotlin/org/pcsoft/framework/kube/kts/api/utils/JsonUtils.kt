package org.pcsoft.framework.kube.kts.api.utils

import org.pcsoft.framework.kube.kts.api.ResourceApi
import org.pcsoft.framework.kube.kts.api.json.ResourceApiDeserializer
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleModule
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule

private val module = SimpleModule().apply {
    addDeserializer(ResourceApi::class.java, ResourceApiDeserializer())
}

private val jsonMapper = JsonMapper.builder()
    .addModule(KotlinModule.Builder().build())
    .addModule(module)
    .build()

private val yamlMapper = YAMLMapper.builder()
    .addModule(KotlinModule.Builder().build())
    .addModule(module)
    .build()

internal fun Any.toJson() = jsonMapper.writeValueAsString(this)

internal inline fun <reified T> convertToJson(yaml: String) : String {
    val obj = fromYaml<T>(yaml)
    return obj!!.toJson()
}

private inline fun <reified T> fromYaml(yaml: String): T = yamlMapper.readValue(yaml, T::class.java)
