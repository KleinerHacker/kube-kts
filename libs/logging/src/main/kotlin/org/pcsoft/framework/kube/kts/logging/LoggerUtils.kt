package org.pcsoft.framework.kube.kts.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Returns a [Logger] instance for the given type [T].
 *
 * @return Logger instance configured for the specified class.
 */
inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)
