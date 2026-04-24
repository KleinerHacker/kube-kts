package org.pcsoft.framework.kube.kts.core.scanner

import org.pcsoft.framework.kube.kts.core.DefaultKubeKtsFile
import org.pcsoft.framework.kube.kts.core.KubeKtsRepository
import org.pcsoft.framework.kube.kts.core.LegacyHelmFile
import org.pcsoft.framework.kube.kts.logging.*
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

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