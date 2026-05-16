/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

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