package org.pcsoft.framework.kube.kts.core.intern

fun setupTestLogger() {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
    System.setProperty("org.slf4j.simpleLogger.showThreadName", "false")
    System.setProperty("org.slf4j.simpleLogger.showLogName", "false")
    System.setProperty("org.slf4j.simpleLogger.showShortLogName", "false")
    System.setProperty("org.slf4j.simpleLogger.levelInBrackets", "true")
}