package org.pcsoft.framework.kube.kts.core.renderer

import com.fasterxml.jackson.annotation.JsonInclude
import tools.jackson.databind.MapperFeature
import tools.jackson.databind.module.SimpleModule
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule

abstract class KubeHelmRendererBase : KubeHelmRenderer {
    private val module = SimpleModule().apply {
    }

    protected val mapper: YAMLMapper = YAMLMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .addModule(module)
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .changeDefaultPropertyInclusion {
            it.withValueInclusion(JsonInclude.Include.NON_NULL)
        }
        .customizeMapper()
        .build()

    private fun YAMLMapper.Builder.customizeMapper(): YAMLMapper.Builder =
        this.apply(yamlMapperCustomizer())

    protected open fun yamlMapperCustomizer(): (YAMLMapper.Builder) -> Unit = { }
}