package org.pcsoft.framework.kube.kts.api

import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.pcsoft.framework.kube.kts.api.types.MetadataSpec
import org.pcsoft.framework.kube.kts.api.utils.toYaml
import java.io.PrintStream

sealed class ResourceSpec<M : MetadataSpec>(val metadata: M) {
    @JsonUnwrapped val header: ResourceSpecHeaderWrapper

    init {
        val header = this::class.annotations.first { it is ResourceSpecHeader } as? ResourceSpecHeader ?:
            throw IllegalStateException("${this::class.qualifiedName} must be annotated with ${ResourceSpecHeader::class.simpleName}")
        this.header = ResourceSpecHeaderWrapper(header)
    }

    internal fun printYaml(printer: PrintStream) {
        this.toYaml(printer)
    }
}