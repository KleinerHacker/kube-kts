package org.pcsoft.framework.kube.kts.core.scanner

import org.pcsoft.framework.kube.kts.core.DefaultKubeKtsFile
import org.pcsoft.framework.kube.kts.core.KubeFile
import org.pcsoft.framework.kube.kts.core.KubeKtsRepository
import org.pcsoft.framework.kube.kts.core.intern.utils.logger
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
        logger.atDebug().log("Scan repository at path {}", path.toAbsolutePath())

        val files = Files.walk(path, FileVisitOption.FOLLOW_LINKS)
            .filter { it.isRegularFile() }
            .filter { it.extension.equals("kts", true) }
            .map { path ->
                val subject = path.fileName.nameWithoutExtension
                val script = Files.readString(path)

                DefaultKubeKtsFile(subject, KubeFile.Type.fromPath(subject), script)
            }
            .toList()
        logger.atDebug().log("> Found {} files in repository", files.size)

        if (!files.any { it.type == KubeFile.Type.CHART }) {
            throw IllegalArgumentException("No chart file found in repository at path ${path.toAbsolutePath()}")
        }

        return KubeKtsRepository(path.fileName.name, files)
    }
}