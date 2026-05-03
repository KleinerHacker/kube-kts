/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.core.renderer

import com.fasterxml.jackson.annotation.JsonInclude
import tools.jackson.databind.MapperFeature
import tools.jackson.databind.module.SimpleModule
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule

/**
 * Abstract base class for implementing renderers that transform Kubernetes Helm files into YAML format.
 *
 * This class serves as a foundation for creating specific renderer implementations by providing core 
 * utilities such as a pre-configured [YAMLMapper] for processing YAML serialization. The mapper is 
 * customized to handle Kotlin data classes, include non-null fields, and support case-insensitive enums.
 *
 * Subclasses can optionally customize the behavior of the [YAMLMapper] by overriding the 
 * [yamlMapperCustomizer] function, which allows further configuration of the mapper used during 
 * serialization.
 *
 * The primary role of this class is to streamline the rendering of Kubernetes Helm files in a consistent 
 * and extensible manner. Implementations should focus on overriding the rendering logic in the concrete 
 * subclass as needed.
 */
abstract class KubeHelmRendererBase : KubeHelmRenderer {
    private val module = SimpleModule().apply {
    }

    /**
     * Configured instance of [YAMLMapper] used for serializing objects into YAML format.
     *
     * This mapper is pre-configured with the following features to facilitate YAML rendering:
     * - Includes a Kotlin module for seamless handling of Kotlin data classes and nullability.
     * - Supports additional Jackson modules for customized serialization, as specified within the class.
     * - Enables case-insensitive enum handling by turning on [MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS].
     * - Configures default property inclusion to exclude `null` values, ensuring cleaner and more concise output.
     *
     * Subclasses of [KubeHelmRendererBase] can further customize the mapper by overriding 
     * the [yamlMapperCustomizer] function, which is invoked during mapper initialization to apply 
     * additional configurations.
     *
     * The intended use of this mapper is within the rendering process, where Kubernetes Helm files are
     * serialized into YAML representations.
     */
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

    /**
     * Provides a customizer function for configuring a [YAMLMapper.Builder] instance.
     *
     * This method allows subclasses to define additional customizations or configurations
     * to tailor the behavior of the YAML mapper used for rendering processes.
     *
     * Example usage:
     * ```kotlin
     * class MyCustomRenderer : KubeHelmRendererBase() {
     *     override fun yamlMapperCustomizer(): (YAMLMapper.Builder) -> Unit = {
     *         it.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
     *           .disable(MapperFeature.AUTO_DETECT_GETTERS)
     *     }
     * }
     * ```
     *
     * @return A function that applies customization logic to a [YAMLMapper.Builder] instance.
     */
    protected open fun yamlMapperCustomizer(): (YAMLMapper.Builder) -> Unit = { }
}