package org.pcsoft.framework.kube.kts.api.utils

import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleModule
import tools.jackson.module.kotlin.KotlinModule

private val module = SimpleModule().apply {

}

private val mapper = JsonMapper.builder()
    .addModule(KotlinModule.Builder().build())
    .addModule(module)
    .build()

internal fun Any.toJson() = mapper.writeValueAsString(this)

internal inline fun <reified T> convertToJson(yaml: String) : String {
    val obj = fromYaml<T>(yaml)
    return obj!!.toJson()
}