package org.pcsoft.framework.kube.kts.cli.intern.utils

import org.apache.log4j.Level

fun Level.fatalStyle(): String = "\u001B[5;31m${this}\u001B[0m"

fun Level.errorStyle(): String = "\u001B[1;31m${this}\u001B[0m"

fun Level.warnStyle(): String = "\u001B[1;33m${this}\u001B[0m"

fun Level.traceStyle(): String = "\u001B[2;90m${this}\u001B[0m"

fun Level.debugStyle(): String = "\u001B[2;34m${this}\u001B[0m"


