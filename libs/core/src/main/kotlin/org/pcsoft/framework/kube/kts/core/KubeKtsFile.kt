package org.pcsoft.framework.kube.kts.core

import org.jetbrains.kotlin.resolve.lazy.FileScopes
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name
import kotlin.script.experimental.api.CompiledScript

sealed interface KubeKtsFile {
    val file: Path
    val type: Type

    enum class Type(val fileName: String) {
        CHART("chart.kts"),
        TEMPLATE("");

        companion object {
            fun fromPath(file: Path): Type =
                entries.firstOrNull { it.fileName.equals(file.fileName.name, true) } ?: TEMPLATE
        }
    }
}

sealed interface PlainKubeKtsFile : KubeKtsFile

data class DefaultKubeKtsFile(override val file: Path, override val type: KubeKtsFile.Type) : PlainKubeKtsFile {
    init {
        require(file.toFile().exists()) { "File does not exist: ${file.toAbsolutePath()}" }
        require(file.toFile().isFile) { "File is not a file: ${file.toAbsolutePath()}" }
    }
}

data class StringKubeKzsFile(val content: String, override val type: KubeKtsFile.Type) : PlainKubeKtsFile {
    override val file: Path = Files.createTempFile(type.name, ".kts")

    init {
        file.toFile().writeText(content)
    }
}

data class CompiledKubeKtsFile(val kubeFile: PlainKubeKtsFile, val compiledScript: CompiledScript) : KubeKtsFile by kubeFile
