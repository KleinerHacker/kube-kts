package org.pcsoft.framework.kube.kts.logging

fun String.successStyle(): String = "\u001B[32m$symbolSuccess ${this}\u001B[0m"

fun String.failedStyle(): String = "\u001B[31m$symbolFailed ${this}\u001B[0m"

fun String.warningStyle(): String = "\u001B[33m$symbolWarning ${this}\u001B[0m"

fun String.subProcessTitleStyle(): String = "\u001B[3;36m${this}\u001B[0m $symbolArrowRight"

fun String.subProcessInfoStyle(): String = "\u001B[3m${this}\u001B[0m"

fun String.subProcessErrorStyle(): String = "\u001B[1;3;31m${this}\u001B[0m"


