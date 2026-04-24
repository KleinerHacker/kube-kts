package org.pcsoft.framework.kube.kts.api.utils

import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.intern.json.ResourceApiDeserializer
import tools.jackson.databind.MapperFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleModule
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule
import java.io.ByteArrayOutputStream

private val module = SimpleModule().apply {
    addDeserializer(TemplateSpec::class.java, ResourceApiDeserializer())
}

private val jsonMapper = JsonMapper.builder()
    .addModule(KotlinModule.Builder().build())
    .addModule(module)
    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
    .build()

private val yamlMapper = YAMLMapper.builder()
    .addModule(KotlinModule.Builder().build())
    .addModule(module)
    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
    .build()

internal fun Any.toJson() = jsonMapper.writeValueAsString(this)

internal fun convertToJson(yaml: String) : String {
    val jsonNode = yamlMapper.readTree(yaml)

    return ByteArrayOutputStream().use {
        jsonMapper.writeTree(jsonMapper.createGenerator(it), jsonNode)
        it.toString("UTF-8")
    }
}
