package org.pcsoft.framework.kube.kts.cli.intern.utils

fun String.green(): String = "\u001B[32m${this}\u001B[0m"

fun String.red(): String = "\u001B[31m${this}\u001B[0m"

fun String.yellow(): String = "\u001B[33m${this}\u001B[0m"

fun String.blue(): String = "\u001B[34m${this}\u001B[0m"
