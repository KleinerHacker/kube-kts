package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmRepository
import org.pcsoft.framework.kube.kts.logging.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

class DefaultKubeHelmRepositoryRenderer(
    val renderer: KubeHelmRenderer = KubeHelmRenderer.DEFAULT
) : KubeHelmRepositoryRenderer {
    companion object {
        private val logger = logger()
    }

    override fun render(repository: KubeHelmRepository, targetPath: Path) {
        validateDirectory(targetPath, "Target")
        logger.atDebug().log { "$symbolProcess Rendering repository to YAML: ${repository.name}" }

        logger.atDebug().log { "$symbolBullet Render ${repository.files.size} files..." }
        logger.atTrace()
            .log { "\t$symbolArrowRight ${repository.files.joinToString(", ") { it.subject }}" }
        repository.files.forEach {
            val yaml = renderer.render(it)
            val subPath = targetPath.resolve(it.type.relativePath)
            validateDirectory(subPath, "Sub")

            val file = targetPath.resolve(it.type.relativePath + "/" + it.subject + ".yaml")
            Files.writeString(file, yaml, Charsets.UTF_8, StandardOpenOption.CREATE)
        }

        logger.atDebug().log { "Render finished for repository: ${repository.name}".successStyle() }
    }

    private fun validateDirectory(path: Path, name: String) {
        if (!path.exists())
            path.toFile().mkdirs()
        else if (!path.isDirectory())
            throw IllegalArgumentException("$name path is not a directory: ${path.toAbsolutePath()}")
    }
}