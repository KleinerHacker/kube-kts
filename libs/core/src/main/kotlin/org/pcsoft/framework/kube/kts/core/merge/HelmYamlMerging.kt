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

package org.pcsoft.framework.kube.kts.core.merge

import org.pcsoft.framework.kube.kts.logging.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

/**
 * Implements YAML merging logic using Helm's templating engine.
 *
 * This class extends `YamlMergingBase` and provides a concrete implementation of the Helm-based merging
 * algorithm for YAML files. It relies on the Helm CLI to merge the base and overlay YAML files into a
 * single resulting YAML structure. The process involves creating a temporary Helm chart, adding the YAML
 * files as values files, rendering the templates, and returning the merged output.
 *
 * Key features of the Helm-based merging algorithm include:
 * - Rendering templates using Helm's template engine.
 * - Support for merging multiple YAML files by applying overlays on top of a base file.
 * - Creation of temporary Helm charts during the merging process, with minimal configuration.
 *
 * This implementation is suitable for scenarios where Helm-specific logic is required, such as merging
 * configurations for Helm charts or maintaining compatibility with Helm's values.yaml structure.
 */
internal class HelmYamlMerging : YamlMergingBase() {
    companion object {
        private val logger = logger()
    }

    override fun doMerge(baseYaml: Path, overlayYaml: Array<out Path>): String {
        logger.atDebug().log { "$symbolArrowRight Use Helm merge algorithm" }
        logger.atDebug().log { "$symbolSubProcess Merge ${overlayYaml.size + 1} YAML files..." }
        logger.atTrace().log { "\t$symbolArrowRight Base: ${baseYaml.fileName.name}" }
        logger.atTrace().log { "\t$symbolArrowRight Overlay(s): ${overlayYaml.joinToString(", ") { it.fileName.name }}" }

        logger.atDebug().log { "$symbolBullet Create temporary chart" }
        val rootDir = Files.createTempDirectory("helm")
        val chartDir = rootDir.resolve("chart")
        Files.createDirectories(chartDir)
        val templatesDir = chartDir.resolve("templates")
        Files.createDirectories(templatesDir)
        val yamlFile = templatesDir.resolve("values.tpl")
        val chartFile = chartDir.resolve("Chart.yaml")

        Files.writeString(yamlFile, "{{ toYaml .Values }}")
        Files.writeString(chartFile, "apiVersion: v1\nname: tmp\nversion: 0.0.0")
        logger.atTrace().log { "Temporary chart created".successStyle() }

        val valueFiles = listOf(baseYaml) + overlayYaml
        logger.atTrace().log { "$symbolBullet Value files to merge: ${valueFiles.joinToString { it.fileName.name }}" }

        logger.atDebug().log { "$symbolBullet Run Helm merge..." }
        val process = ProcessBuilder("helm", "template", "tmp", chartDir.toString(),
            *valueFiles.flatMap { listOf("-f", it.toString()) }.toTypedArray(), "--show-only", "templates/values.tpl")
            .directory(rootDir.toFile())
            .start()
        val exitCode = process.waitFor()
        if (exitCode != 0)
            throw IllegalStateException("Helm merge failed with exit code $exitCode")

        val yaml = process.inputStream.readAllBytes().decodeToString()
        logger.atTrace().log { "Helm merge finished".successStyle() }

        logger.atDebug().log { "Merging all value files successful".successStyle() }
        return yaml
    }
}