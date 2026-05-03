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

package org.pcsoft.framework.kube.kts.core.merge

import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

/**
 * Provides a base implementation for the [YamlMerging] interface, enabling the merging of YAML files
 * by handling validation and delegation of merging logic to subclasses.
 *
 * This abstract class ensures that input YAML files are validated before merging and allows subclasses
 * to implement their own merging strategies through the [doMerge] method.
 */
abstract class YamlMergingBase : YamlMerging {
    final override fun merge(baseYamlIn: Path?, vararg overlayYamlIn: Path): String? {
        if (baseYamlIn == null && overlayYamlIn.isEmpty())
            return null

        val baseYaml = baseYamlIn ?: overlayYamlIn.first()
        val overlayYaml = if (baseYamlIn != null) overlayYamlIn else overlayYamlIn.drop(1).toTypedArray()

        require(baseYaml.exists() && baseYaml.isRegularFile()) { "Base YAML file does not exist: ${baseYaml.toAbsolutePath()}" }
        overlayYaml.forEach {
            require(it.exists() && it.isRegularFile()) { "Overlay YAML file does not exist: ${it.toAbsolutePath()}" }
        }

        return doMerge(baseYaml, overlayYaml)
    }

    /**
     * Merges a base YAML file with one or more overlay YAML files into a single resulting YAML structure.
     *
     * This method is intended to be implemented by subclasses to define specific merging strategies for YAML content.
     *
     * @param baseYaml The path to the base YAML file. Must be an existing and regular file.
     * @param overlayYaml The paths to the overlay YAML files. Each must be an existing and regular file.
     * @return A string representing the merged YAML content.
     * @throws IllegalArgumentException If any of the provided YAML files (base or overlays) do not exist or are not regular files.
     */
    protected abstract fun doMerge(baseYaml: Path, overlayYaml: Array<out Path>): String
}