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

package org.pcsoft.framework.kube.kts.api.types

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.utils.toJson
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

/**
 * Tests for [PortValue], which models the Kubernetes `IntOrString` shape used wherever a port may be
 * given either as a number or as the name of a container port.
 */
class PortValueTest {

    /**
     * Verifies that a numeric port renders as a JSON number rather than a string.
     *
     * The distinction matters: Kubernetes rejects a quoted number where it expects an integer port.
     */
    @Test
    fun testNumberRendersAsNumber() {
        assertEquals("8080", ofPortNumber(8080).toJson())
        assertEquals(8080, ofPortNumber(8080).toYamlValue())
    }

    /**
     * Verifies that a named port renders as a JSON string.
     */
    @Test
    fun testNameRendersAsString() {
        assertEquals("\"http\"", ofPortName("http").toJson())
        assertEquals("http", ofPortName("http").toYamlValue())
    }

    /**
     * Verifies that the numeric and named convenience extensions produce the matching value types.
     */
    @Test
    fun testExtensionsProduceMatchingTypes() {
        assertIs<NumberPortValue>(8080.portNumber)
        assertIs<NamePortValue>("http".portName)
        assertEquals(ofPortNumber(8080), 8080.portNumber)
        assertEquals(ofPortName("http"), "http".portName)
    }

    /**
     * Verifies that the factory recovers the correct variant from a scalar read out of YAML.
     *
     * Numeric input must yield a numeric port and textual input a named one, which is what makes reading
     * an existing manifest back symmetrical to writing it.
     */
    @Test
    fun testFactoryRecoversVariantFromScalar() {
        assertIs<NumberPortValue>(PortValue.of(8080))
        assertIs<NamePortValue>(PortValue.of("http"))
        assertEquals(8080, (PortValue.of(8080) as NumberPortValue).value)
        assertEquals("http", (PortValue.of("http") as NamePortValue).value)
    }

    /**
     * Verifies that the factory rejects a scalar that is neither a number nor a string.
     */
    @Test
    fun testFactoryRejectsUnsupportedType() {
        assertFailsWith<IllegalArgumentException> { PortValue.of(true) }
    }

    /**
     * Verifies that a numeric port must lie within the valid TCP and UDP port range.
     */
    @Test
    fun testNumberRejectsOutOfRange() {
        assertFailsWith<IllegalArgumentException> { ofPortNumber(0) }
        assertFailsWith<IllegalArgumentException> { ofPortNumber(65536) }
        assertFailsWith<IllegalArgumentException> { ofPortNumber(-1) }
    }

    /**
     * Verifies that a port name must satisfy the IANA service name rules Kubernetes enforces.
     *
     * The name has to be lower case, at most 15 characters, free of leading, trailing or doubled
     * hyphens, and must contain at least one letter.
     */
    @Test
    fun testNameRejectsInvalidFormats() {
        assertFailsWith<IllegalArgumentException> { ofPortName("") }
        assertFailsWith<IllegalArgumentException> { ofPortName("this-name-is-too-long") }
        assertFailsWith<IllegalArgumentException> { ofPortName("HTTP") }
        assertFailsWith<IllegalArgumentException> { ofPortName("-http") }
        assertFailsWith<IllegalArgumentException> { ofPortName("http-") }
        assertFailsWith<IllegalArgumentException> { ofPortName("ht--tp") }
        assertFailsWith<IllegalArgumentException> { ofPortName("8080") }
    }

    /**
     * Verifies that names Kubernetes considers valid are accepted.
     */
    @Test
    fun testNameAcceptsValidFormats() {
        assertEquals("http", ofPortName("http").value)
        assertEquals("web-1", ofPortName("web-1").value)
        assertEquals("h2c", ofPortName("h2c").value)
    }
}
