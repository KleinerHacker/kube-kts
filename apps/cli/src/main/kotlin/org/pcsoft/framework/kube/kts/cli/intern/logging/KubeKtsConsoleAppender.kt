package org.pcsoft.framework.kube.kts.cli.intern.logging

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.Level
import org.apache.log4j.Priority
import org.apache.log4j.spi.LoggingEvent
import org.pcsoft.framework.kube.kts.cli.intern.utils.*

class KubeKtsConsoleAppender(
    var showLogLevel: Boolean = false
) : AppenderSkeleton() {
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