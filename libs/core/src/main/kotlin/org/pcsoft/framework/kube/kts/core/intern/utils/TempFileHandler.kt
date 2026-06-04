package org.pcsoft.framework.kube.kts.core.intern.utils

import java.nio.file.Path

internal class TempFileHandler(private val tempFileList: MutableList<Path> = mutableListOf()) {
    fun Path.toTempFile() : Path {
        tempFileList.add(this)
        return this
    }

    fun deleteTempFiles() {
        tempFileList.forEach { it.toFile().delete() }
    }
}

internal fun <T> withTempFileHandler(action: TempFileHandler.() -> T): T =
    TempFileHandler().let {
        try {
            action(it)
        } finally {
            it.deleteTempFiles()
        }
    }