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

package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmRepository
import java.nio.file.Path

/**
 * Provides functionality for rendering a Helm repository into a specific output format (e.g., YAML files).
 *
 * Implementations of this interface are responsible for processing a [KubeHelmRepository] and translating
 * its contents (both modern Helm files and legacy Helm files) into a desired form. The rendered output is
 * written to a specified target directory.
 *
 * Use cases for this interface involve exporting Kubernetes Helm resources for deployment or further
 * processing.
 *
 * The companion object offers a default implementation, [DEFAULT], which can be used for standard rendering
 * needs.
 */
interface KubeHelmRepositoryRenderer {
    companion object {
        /**
         * Represents the default implementation of [KubeHelmRepositoryRenderer].
         *
         * This default renderer uses an instance of [DefaultKubeHelmRenderer] to process and render
         * a [KubeHelmRepository] into YAML files. It handles both modern Helm files (KTS) and legacy
         * Helm files by converting them into their respective output formats and writing the results
         * to a given target directory. This implementation is suitable for standard use cases where a
         * predefined configuration for rendering Helm repositories suffices.
         */
        val DEFAULT: KubeHelmRepositoryRenderer = DefaultKubeHelmRepositoryRenderer()
    }

    /**
     * Renders the contents of a [KubeHelmRepository] into the specified target directory.
     *
     * The method processes both modern Helm files ([org.pcsoft.framework.kube.kts.core.KubeHelmFile]s) and legacy Helm templates
     * ([org.pcsoft.framework.kube.kts.core.LegacyHelmFile]s), converting them into their respective output formats (e.g., YAML files).
     * The rendered output is written into the target directory, maintaining an organized structure
     * that matches the repository layout.
     *
     * @param repository The [KubeHelmRepository] containing the Helm files to be rendered. This includes
     * both modern Kubernetes Helm files and legacy files, allowing for seamless rendering and export.
     * @param targetPath The target directory where the rendered files will be written. Must be a valid
     * directory path or will be created if it does not already exist.
     */
    fun render(repository: KubeHelmRepository, targetPath: Path)
}