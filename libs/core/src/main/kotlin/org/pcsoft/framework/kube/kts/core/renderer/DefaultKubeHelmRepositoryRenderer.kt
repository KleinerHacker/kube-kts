package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmRepository
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

class DefaultKubeHelmRepositoryRenderer(
    val renderer: KubeHelmRenderer = KubeHelmRenderer.DEFAULT
) : KubeHelmRepositoryRenderer {
    override fun render(repository: KubeHelmRepository, targetPath: Path) {
        validateDirectory(targetPath, "Target")

        repository.files.forEach {
            val yaml = renderer.render(it)
            val subPath = targetPath.resolve(it.type.relativePath)
            validateDirectory(subPath, "Sub")

            val file = targetPath.resolve(it.type.relativePath + "/" + it.subject + ".yaml")
            validateFile(file, it.subject)

            Files.writeString(file, yaml, Charsets.UTF_8, StandardOpenOption.CREATE_NEW)
        }
    }

    private fun validateDirectory(path: Path, name: String) {
        if (!path.exists())
            path.toFile().mkdirs()
        else if (!path.isDirectory())
            throw IllegalArgumentException("$name path is not a directory: ${path.toAbsolutePath()}")
        else if (Files.list(path).count() > 0)
            throw IllegalStateException("$name path is not empty: ${path.toAbsolutePath()}")
    }

    private fun validateFile(path: Path, name: String) {
        if (path.exists())
            throw IllegalStateException("$name file already exists: ${path.toAbsolutePath()}")
    }
}