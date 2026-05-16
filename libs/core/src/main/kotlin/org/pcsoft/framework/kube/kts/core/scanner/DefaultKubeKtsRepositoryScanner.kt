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

package org.pcsoft.framework.kube.kts.core.scanner

import org.pcsoft.framework.kube.kts.core.DefaultKubeKtsFile
import org.pcsoft.framework.kube.kts.core.KubeKtsRepository
import org.pcsoft.framework.kube.kts.core.LegacyHelmFile
import org.pcsoft.framework.kube.kts.logging.*
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

/**
 * Default implementation of the [KubeKtsRepositoryScanner] interface.
 *
 * This object scans a specified directory path to identify Kubernetes-related files,
 * including both modern Kubernetes Kotlin Script (KTS) files and legacy Helm YAML templates.
 * It processes the directory structure, filters for supported file types, and organizes
 * the results into a [KubeKtsRepository] instance.
 *
 * The scanning process includes the following steps:
 * - Validating that the provided path exists and is a directory.
 * - Searching for `.kts` files, extracting their content, and encapsulating them
 *   as [DefaultKubeKtsFile] instances.
 * - Searching for legacy `.yaml`, `.yml`, and `.tpl` files, reading their content,
 *   and representing them as [LegacyHelmFile] instances.
 * - Logging details about the files discovered during the scan.
 * - Verifying the presence of at least one chart file in the repository.
 * - Creating and returning a [KubeKtsRepository] object to represent the scanned repository.
 *
 * In case of missing required chart files or if the provided path is invalid (non-existent
 * or not a directory), the scanner will throw an [IllegalArgumentException].
 */
internal object DefaultKubeKtsRepositoryScanner : KubeKtsRepositoryScanner {
    private val logger = logger()

    override fun scan(path: Path): KubeKtsRepository {
        require(path.toFile().exists()) { "Path does not exist: ${path.toAbsolutePath()}" }
        require(path.toFile().isDirectory) { "Path is not a directory: ${path.toAbsolutePath()}" }
        logger.atDebug().log { "$symbolProcess Scan repository at path ${path.toAbsolutePath()}" }

        val kubeKtsFiles = Files.walk(path, FileVisitOption.FOLLOW_LINKS)
            .filter { it.isRegularFile() }
            .filter { it.extension.equals("kts", true) }
            .map { file ->
                val subject = file.fileName.nameWithoutExtension
                val script = Files.readString(file)

                DefaultKubeKtsFile(subject, file.parent.relativeTo(path), script)
            }
            .toList()

        val legacyHelmFiles = Files.walk(path, FileVisitOption.FOLLOW_LINKS)
            .filter { it.isRegularFile() }
            .filter {
                it.extension.equals("yaml", true)
                        || it.extension.equals("yml", true)
                        || it.extension.equals("tpl", true)
            }
            .map { file ->
                val subject = file.fileName.nameWithoutExtension
                val yaml = Files.readString(file)

                LegacyHelmFile(subject, file.parent.relativeTo(path), file.extension, yaml)
            }
            .toList()

        logger.atDebug()
            .log { "$symbolBullet Found ${kubeKtsFiles.size} KTS files and ${legacyHelmFiles.size} legacy Helm files in repository" }
        logger.atTrace().log { "\t$symbolArrowRight KTS : ${kubeKtsFiles.joinToString(", ") { it.subject }}" }
        logger.atTrace().log { "\t$symbolArrowRight Helm: ${legacyHelmFiles.joinToString(", ") { it.subject }}" }

        if (!kubeKtsFiles.any { it.isChart }) {
            throw IllegalArgumentException("No chart file found in repository at path ${path.toAbsolutePath()}")
        }

        logger.atDebug().log { "Scan finished for repository at path ${path.toAbsolutePath()}".successStyle() }
        return KubeKtsRepository(path.parent.fileName.name, kubeKtsFiles, legacyHelmFiles)
    }
}