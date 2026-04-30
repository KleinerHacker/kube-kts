package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmRepository
import org.pcsoft.framework.kube.kts.core.intern.utils.resolve
import org.pcsoft.framework.kube.kts.logging.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

/**
 * Default implementation of the [KubeHelmRepositoryRenderer] interface.
 *
 * This class renders a [KubeHelmRepository] into YAML files and writes the output to a specified
 * target directory. It supports rendering both modern Helm files (KTS) and legacy Helm templates,
 * ensuring compatibility with various Helm-based workflows.
 *
 * The rendering process involves the following steps:
 * - Validating and ensuring the target directory structure exists.
 * - Processing each modern Helm file in the repository, rendering it into a YAML file via the
 *   configured [KubeHelmRenderer].
 * - Writing the rendered YAML files to the appropriate locations within the target directory.
 * - Processing each legacy Helm template, writing its content to the corresponding file in the
 *   target directory.
 *
 * This implementation leverages [KubeHelmRenderer.DEFAULT] to handle the rendering of modern
 * Helm files unless a custom renderer is explicitly provided.
 *
 * Logging is used throughout the process to provide detailed traceability of the rendering
 * operations, including debug and trace-level logs for file rendering activities.
 *
 * @property renderer The [KubeHelmRenderer] instance used to render modern Helm files into YAML format.
 *                    Defaults to [KubeHelmRenderer.DEFAULT].
 */
class DefaultKubeHelmRepositoryRenderer(
    val renderer: KubeHelmRenderer = KubeHelmRenderer.DEFAULT
) : KubeHelmRepositoryRenderer {
    companion object {
        private val logger = logger()
    }

    override fun render(repository: KubeHelmRepository, targetPath: Path) {
        validateDirectory(targetPath, "Target")
        logger.atDebug().log { "$symbolProcess Rendering repository to YAML: ${repository.name}" }

        logger.atDebug()
            .log { "$symbolBullet Render ${repository.files.size} KTS files and ${repository.legacyFiles.size} legacy Helm files..." }
        logger.atTrace()
            .log { "\t$symbolArrowRight KTS : ${repository.files.joinToString(", ") { it.subject }}" }
        logger.atTrace()
            .log { "\t$symbolArrowRight Helm: ${repository.legacyFiles.joinToString(", ") { it.subject }}" }

        repository.files.forEach {
            val yaml = renderer.render(it)

            val file = targetPath.resolve(it.relativePath, "${it.subject}.yaml")
            validateDirectory(file.parent, "Sub")

            Files.writeString(file, yaml, Charsets.UTF_8, StandardOpenOption.CREATE)
        }

        repository.legacyFiles.forEach {
            val file = targetPath.resolve(it.relativePath, "${it.subject}.${it.extension}")
            validateDirectory(file.parent, "Sub")

            Files.writeString(file, it.content, Charsets.UTF_8, StandardOpenOption.CREATE)
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