package org.pcsoft.framework.kube.kts.api.utils

import tools.jackson.databind.module.SimpleModule
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule
import java.io.PrintStream
import kotlin.jvm.java

private val module = SimpleModule().apply {

}

private val mapper = YAMLMapper.builder()
    .addModule(KotlinModule.Builder().build())
    .addModule(module)
    .build()

internal fun Any.toYaml(printer: PrintStream) = mapper.writeValue(printer, this)
internal fun Any.toYaml() = mapper.writeValueAsString(this)

internal inline fun <reified T> fromYaml(yaml: String): T = mapper.readValue(yaml, T::class.java)

