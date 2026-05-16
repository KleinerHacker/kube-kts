/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.utils

import com.fasterxml.jackson.annotation.JsonInclude
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
    .changeDefaultPropertyInclusion {
        it.withValueInclusion(JsonInclude.Include.NON_NULL)
    }
    .build()

private val yamlMapper = YAMLMapper.builder()
    .addModule(KotlinModule.Builder().build())
    .addModule(module)
    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
    .changeDefaultPropertyInclusion {
        it.withValueInclusion(JsonInclude.Include.NON_NULL)
    }
    .build()

internal fun Any.toJson() = jsonMapper.writeValueAsString(this)

internal fun convertToJson(yaml: String) : String {
    val jsonNode = yamlMapper.readTree(yaml)

    return ByteArrayOutputStream().use {
        jsonMapper.writeTree(jsonMapper.createGenerator(it), jsonNode)
        it.toString("UTF-8")
    }
}
