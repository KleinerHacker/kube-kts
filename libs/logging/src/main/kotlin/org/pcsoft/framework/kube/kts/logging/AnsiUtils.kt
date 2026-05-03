/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.logging

/**
 * Applies a success style formatting to the string by prefixing it with a green checkmark symbol (✓).
 *
 * @return The formatted string with ANSI escape codes for green text and reset.
 */
fun String.successStyle(): String = "\u001B[32m$symbolSuccess ${this}\u001B[0m"

/**
 * Applies a failed style formatting to the string by prefixing it with a red cross symbol (✗)
 * and resets the terminal color at the end.
 *
 * @return The formatted string with ANSI escape codes for red text and reset.
 */
fun String.failedStyle(): String = "\u001B[31m$symbolFailed ${this}\u001B[0m"

/**
 * Applies a warning style formatting to the string by prefixing it with a yellow warning symbol (⚠)
 * and resets the terminal color at the end.
 *
 * @return The formatted string with ANSI escape codes for yellow text and reset.
 */
fun String.warningStyle(): String = "\u001B[33m$symbolWarning ${this}\u001B[0m"

/**
 * Applies sub-process title styling to the string by wrapping it with ANSI escape codes for italic and cyan text,
 * followed by a right arrow symbol. This is used to visually distinguish sub-process titles in log output.
 *
 * @return The styled string with sub-process title formatting applied.
 */
fun String.subProcessTitleStyle(): String = "\u001B[3;36m${this}\u001B[0m $symbolArrowRight"

/**
 * Applies sub-process information styling to the string.
 *
 * This function wraps the input string with ANSI escape codes to display it in italic style,
 * which is typically used for logging sub-process or nested task information. The styling helps
 * visually distinguish sub-process output from main process logs.
 *
 * @return The styled string wrapped with ANSI escape codes for italic formatting.
 */
fun String.subProcessInfoStyle(): String = "\u001B[3m${this}\u001B[0m"

/**
 * Applies error styling to the string for sub-process output.
 *
 * This function wraps the input string with ANSI escape codes to display it in bold,
 * italic, and red color, which is typically used to highlight error messages from subprocesses
 * such as Helm command executions. The styling helps visually distinguish errors in logs.
 *
 * @return The styled string with ANSI formatting for error display.
 */
fun String.subProcessErrorStyle(): String = "\u001B[1;3;31m${this}\u001B[0m"


