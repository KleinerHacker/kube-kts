package org.pcsoft.framework.kube.kts.core.scanner

import org.pcsoft.framework.kube.kts.core.DefaultKubeKtsFile
import org.pcsoft.framework.kube.kts.core.KubeFile
import org.pcsoft.framework.kube.kts.core.KubeKtsRepository
import org.pcsoft.framework.kube.kts.core.LegacyHelmFile
import org.pcsoft.framework.kube.kts.logging.*
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

internal object DefaultKubeKtsRepositoryScanner : KubeKtsRepositoryScanner {
    private val logger = logger()

    override fun scan(path: Path): KubeKtsRepository {
        require(path.toFile().exists()) { "Path does not exist: ${path.toAbsolutePath()}" }
        require(path.toFile().isDirectory) { "Path is not a directory: ${path.toAbsolutePath()}" }
        logger.atDebug().log { "$symbolProcess Scan repository at path ${path.toAbsolutePath()}" }

        val kubeKtsFiles = Files.walk(path, FileVisitOption.FOLLOW_LINKS)
            .filter { it.isRegularFile() }
            .filter { it.extension.equals("kts", true) }
            .map { path ->
                val subject = path.fileName.nameWithoutExtension
                val script = Files.readString(path)

                DefaultKubeKtsFile(subject, KubeFile.Type.from(subject), script)
            }
            .toList()

        val legacyHelmFiles = Files.walk(path, FileVisitOption.FOLLOW_LINKS)
            .filter { it.isRegularFile() }
            .filter { it.extension.equals("yaml", true) || it.extension.equals("yml", true) }
            .map { path ->
                val subject = path.fileName.nameWithoutExtension
                val yaml = Files.readString(path)

                LegacyHelmFile(subject, KubeFile.Type.from(subject), yaml)
            }
            .toList()

        logger.atDebug().log { "$symbolBullet Found ${kubeKtsFiles.size} KTS files and ${legacyHelmFiles.size} legacy Helm files in repository" }
        logger.atTrace().log { "\t$symbolArrowRight KTS : ${kubeKtsFiles.joinToString(", ") { it.subject }}" }
        logger.atTrace().log { "\t$symbolArrowRight Helm: ${legacyHelmFiles.joinToString(", ") { it.subject }}" }

        if (!kubeKtsFiles.any { it.type == KubeFile.Type.CHART }) {
            throw IllegalArgumentException("No chart file found in repository at path ${path.toAbsolutePath()}")
        }

        logger.atDebug().log { "Scan finished for repository at path ${path.toAbsolutePath()}".successStyle() }
        return KubeKtsRepository(path.parent.fileName.name, kubeKtsFiles, legacyHelmFiles)
    }
}