package org.pcsoft.framework.kube.kts.cli.intern.utils

import org.apache.log4j.Level.*
import org.apache.log4j.PatternLayout
import org.apache.log4j.spi.LoggingEvent

class ColorPatternLayout : PatternLayout() {

    override fun format(event: LoggingEvent): String {
        val original = super.format(event)
        val color = when (event.level) {
            TRACE -> "\u001B[90m" // gray
            DEBUG -> "\u001B[36m" // cyan
            INFO -> "\u001B[0m"  // reset
            WARN -> "\u001B[33m"  // yellow
            ERROR, FATAL -> "\u001B[31m" // red
            else -> "\u001B[0m" // reset
        }
        return "$color$original\u001B[0m"
    }
}