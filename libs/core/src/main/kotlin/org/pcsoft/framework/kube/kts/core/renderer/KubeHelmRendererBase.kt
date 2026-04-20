package org.pcsoft.framework.kube.kts.core.renderer

import tools.jackson.databind.module.SimpleModule
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule

abstract class KubeHelmRendererBase : KubeHelmRenderer {
    private val module = SimpleModule().apply {
    }

    protected val mapper: YAMLMapper = YAMLMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .addModule(module)
        .customizeMapper()
        .build()

    private fun YAMLMapper.Builder.customizeMapper(): YAMLMapper.Builder =
        this.apply(yamlMapperCustomizer())

    protected open fun yamlMapperCustomizer(): (YAMLMapper.Builder) -> Unit = { }
}