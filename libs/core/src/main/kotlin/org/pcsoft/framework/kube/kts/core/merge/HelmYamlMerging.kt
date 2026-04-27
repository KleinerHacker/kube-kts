package org.pcsoft.framework.kube.kts.core.merge

import org.pcsoft.framework.kube.kts.logging.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

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