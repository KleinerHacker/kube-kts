package org.pcsoft.framework.kube.kts.api.utils

import tools.jackson.databind.module.SimpleModule
import tools.jackson.dataformat.yaml.YAMLMapper
import java.io.PrintStream

private val module = SimpleModule().apply {

}

private val mapper = YAMLMapper.builder()
    .addModule(module)
    .build()

internal fun Any.toYaml(printer: PrintStream) = mapper.writeValue(printer, this)
internal fun Any.toYaml() = mapper.writeValueAsString(this)

