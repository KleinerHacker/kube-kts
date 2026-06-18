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

package org.pcsoft.framework.kube.kts.cli.intern.utils

import org.apache.log4j.Level

/**
 * Returns a string representation of the `Level` with ANSI escape codes for fatal error styling.
 *
 * The output is formatted as blinking red text (ANSI code `\u001B[5;31m`) followed by a reset
 * (ANSI code `\u001B[0m`).
 *
 * @return A styled string representing the `Level` in fatal error format.
 */
val Level.fatalStyle: String
    get() = "\u001B[5;31m${this}\u001B[0m"

/**
 * Returns a string representation of the `Level` with ANSI escape codes for error styling.
 *
 * The output is formatted as bold red text (ANSI code `\u001B[1;31m`) followed by a reset
 * (ANSI code `\u001B[0m`).
 *
 * @return A styled string representing the `Level` in error format.
 */
val Level.errorStyle: String
    get() = "\u001B[1;31m${this}\u001B[0m"

/**
 * Returns a string representation of the `Level` with ANSI escape codes for warning styling.
 *
 * The output is formatted as bold yellow text (ANSI code `\u001B[1;33m`) followed by a reset
 * (ANSI code `\u001B[0m`).
 *
 * @return A styled string representing the `Level` in warning format.
 */
val Level.warnStyle: String
    get() = "\u001B[1;33m${this}\u001B[0m"

/**
 * Returns a string representation of the `Level` with ANSI escape codes for debug styling.
 *
 * The output is formatted as faint blue text (ANSI code `\u001B[2;34m`) followed by a reset
 * (ANSI code `\u001B[0m`).
 *
 * @return A styled string representing the `Level` in debug format.
 */
val Level.debugStyle: String
    get() = "\u001B[2;34m${this}\u001B[0m"

/**
 * Returns a string representation of the `Level` with ANSI escape codes for trace styling.
 *
 * The output is formatted as faint gray text (ANSI code `\u001B[2;90m`) followed by a reset
 * (ANSI code `\u001B[0m`).
 *
 * @return A styled string representing the `Level` in trace format.
 */
val Level.traceStyle: String
    get() = "\u001B[2;90m${this}\u001B[0m"

/**
 * Help marker for options that are (only) forwarded to Helm.
 *
 * Prepended to the picocli option description so users can see, directly in `--help`, which flags
 * are passed through to the underlying Helm CLI.
 */
const val HELM_MARKER = "@|yellow ---->|@"

/**
 * Help marker for options that are experimental.
 *
 * Prepended to the picocli option description so users can see, directly in `--help`, which flags are
 * experimental, even when they are already grouped in the experimental section.
 */
const val EXPERIMENTAL_MARKER = "@|red *|@"

/**
 * Help marker for options that are dangerous (security relevant).
 *
 * Prepended to the picocli option description so users can see, directly in `--help`, which flags may
 * compromise safety or security.
 */
const val DANGER_MARKER = "@|red !!!|@"


