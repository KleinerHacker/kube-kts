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

package org.pcsoft.framework.kube.kts.cli.intern.logging

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.Level
import org.apache.log4j.Priority
import org.apache.log4j.spi.LoggingEvent
import org.pcsoft.framework.kube.kts.cli.intern.utils.*

/**
 * A console appender for logging events with optional log level display and ANSI color styling.
 *
 * This appender outputs log messages to the console. When [showLogLevel] is enabled, it prefixes each message
 * with the log level in colored format (using ANSI escape codes). The log levels are styled as follows:
 * - TRACE: faint gray
 * - DEBUG: faint blue
 * - INFO: default color
 * - WARN: bold yellow
 * - ERROR: bold red
 * - FATAL: blinking red
 *
 * If the logging event contains a throwable, its stack trace is printed to the console.
 */
class KubeKtsConsoleAppender(
    var showLogLevel: Boolean = false
) : AppenderSkeleton() {
    /**
     * Appends a logging event to the console output. If [showLogLevel] is enabled, the log level is displayed
     * in colored format before the message. The message itself is printed to the standard output, and if the
     * event contains a throwable, its stack trace is also printed.
     *
     * @param event The logging event to be appended, containing the log level, message, and optional throwable information.
     */
    @Suppress("DEPRECATION")
    override fun append(event: LoggingEvent) {
        if (showLogLevel) {
            print("[${event.level.toColoredString()}] ")
        }

        println(event.message)
        event.throwableInformation?.throwable?.printStackTrace()
    }

    override fun close() {

    }

    override fun requiresLayout(): Boolean = false

    private fun Priority.toColoredString(): String = when(this as Level) {
        Level.TRACE -> this.traceStyle()
        Level.DEBUG -> this.debugStyle()
        Level.INFO -> this.toString()
        Level.WARN -> this.warnStyle()
        Level.ERROR -> this.errorStyle()
        Level.FATAL -> this.fatalStyle()
        else -> "UNKNOWN"
    }.padEnd(5)
}