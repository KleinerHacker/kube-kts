package org.pcsoft.framework.kube.kts.core.intern.utils

import org.apache.commons.io.FilenameUtils
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.SourceCode

internal fun Iterable<ScriptDiagnostic>.toEffectiveString(): String =
    filter { it.severity == ScriptDiagnostic.Severity.ERROR }
        .joinToString("\n") {
            "${it.message} (at ${toSubject(it.sourcePath)}.kts:${it.location?.toEffectiveString()})"
        }

private fun SourceCode.Location.toEffectiveString(): String =
    "${start.line}:${start.col}"

private fun toSubject(sourcePath: String?): String =
    if (sourcePath == null) "?" else FilenameUtils.getBaseName(sourcePath).split(".").first()