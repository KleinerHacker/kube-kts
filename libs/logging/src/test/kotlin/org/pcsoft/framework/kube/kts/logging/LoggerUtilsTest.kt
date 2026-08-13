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

package org.pcsoft.framework.kube.kts.logging

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Developer tests for [logger], the reified helper that creates an SLF4J logger for the receiver
 * type.
 *
 * Because the type parameter is reified, the logger name is derived from the *static* type of the
 * receiver, not from its runtime class. These tests pin that behaviour, as it decides under which
 * name log statements appear in the CLI output.
 */
class LoggerUtilsTest {

    /** Simple receiver type used to check the derived logger name. */
    private class Sample

    /** Base type used to verify that the static type wins over the runtime type. */
    private open class Base

    /** Sub type of [Base] used as the runtime type in [staticTypeDeterminesTheLoggerName]. */
    private class Derived : Base()

    /**
     * Verifies that the logger name is the fully qualified name of the receiver type.
     *
     * Calling [logger] on an instance of a nested class must yield a logger named after that
     * class, including the enclosing class and the package.
     */
    @Test
    fun loggerNameIsTheQualifiedTypeName() {
        val log = Sample().logger()
        Assertions.assertEquals(Sample::class.java.name, log.name)
    }

    /**
     * Verifies that the static type — not the runtime type — determines the logger name.
     *
     * A [Derived] instance held in a [Base] variable must produce a logger named after `Base`,
     * because the type parameter is resolved at compile time.
     */
    @Test
    fun staticTypeDeterminesTheLoggerName() {
        val value: Base = Derived()
        Assertions.assertEquals(Base::class.java.name, value.logger().name)
    }

    /**
     * Verifies that repeated calls return an equivalent logger for the same type.
     *
     * SLF4J caches loggers per name, so two calls on different instances of the same type must
     * resolve to the same logger and never to `null`.
     */
    @Test
    fun repeatedCallsResolveTheSameLogger() {
        val first = Sample().logger()
        val second = Sample().logger()
        Assertions.assertNotNull(first)
        Assertions.assertSame(first, second)
    }

    /**
     * Verifies that the helper also works for types outside this module.
     *
     * Calling [logger] on a plain [String] receiver must produce a logger named `java.lang.String`,
     * proving that no reflection on the receiver instance is involved.
     */
    @Test
    fun worksForArbitraryReceiverTypes() {
        Assertions.assertEquals(String::class.java.name, "any".logger().name)
    }
}
