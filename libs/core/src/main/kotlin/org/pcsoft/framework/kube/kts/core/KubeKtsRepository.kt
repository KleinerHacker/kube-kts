package org.pcsoft.framework.kube.kts.core

import org.jetbrains.kotlin.incremental.util.Either
import org.pcsoft.framework.kube.kts.core.intern.utils.onFailure
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

class KubeKtsRepository(val path: Path) {
    private val files = mutableListOf<KubeKtsFile>()

    var state : State = State.INITIALIZED
        private set

    init {
        require(path.toFile().exists()) { "Path does not exist: ${path.toAbsolutePath()}" }
        require(path.toFile().isDirectory) { "Path is not a directory: ${path.toAbsolutePath()}" }
    }

    fun scan() {
        stateCheck(State.INITIALIZED, State.SCANNED) {
            files.clear()
            files.addAll(
                Files.walk(path, FileVisitOption.FOLLOW_LINKS)
                    .filter { it.isRegularFile() }
                    .filter { it.extension == "kts" }
                    .map { path -> DefaultKubeKtsFile(path, KubeKtsFile.Type.fromPath(path)) }
                    .toList()
            )
        }.onFailure { throw IllegalStateException("Already scanned") }
    }

    fun compile(compiler: (DefaultKubeKtsFile) -> CompiledKubeKtsFile) {
        stateCheck(State.SCANNED, State.COMPILED) {
            files.replaceAll { compiler(it as DefaultKubeKtsFile) }
        }.onFailure { throw IllegalStateException("Not scanned yet or already compiled") }
    }

    private fun stateCheck(shouldState: State, newState: State, block: () -> Unit) : Either<Unit> {
        if (state != shouldState)
            return Either.Error("")

        block()

        state = newState

        return Either.Success(Unit)
    }

    enum class State {
        INITIALIZED,
        SCANNED,
        COMPILED
    }
}