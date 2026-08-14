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
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.utils.roundTrip
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

/**
 * Tests for the parsing side of the typed value classes.
 *
 * These are the code paths taken when an existing chart is read back in: every quantity is written as a
 * suffixed string and has to be recovered from it, so parsing has to be the exact inverse of rendering.
 */
class ValueParsingTest {

    /**
     * Verifies that a CPU quantity is parsed from both notations Kubernetes accepts.
     *
     * Values ending in `m` are milli-CPU, everything else is a plain core count.
     */
    @Test
    fun testCpuParsing() {
        assertEquals(CpuValue(0.5f), CpuValue.parse("500m"))
        assertEquals(CpuValue(1f), CpuValue.parse("1"))
        assertEquals(CpuValue(2.5f), CpuValue.parse("2.5"))
        assertEquals(CpuValue(0.001f), CpuValue.parse("1m"))
    }

    /**
     * Verifies that a CPU quantity survives a full render and parse cycle.
     */
    @Test
    fun testCpuRoundTrip() {
        listOf(250.mCpu, 1f.cpu, 2f.cpu).forEach {
            assertEquals(it, roundTrip(it))
        }
    }

    /**
     * Verifies that an unparseable CPU quantity is rejected instead of silently becoming zero.
     */
    @Test
    fun testCpuParsingRejectsGarbage() {
        assertFailsWith<NumberFormatException> { CpuValue.parse("abc") }
    }

    /**
     * Verifies that a memory quantity is parsed from every supported binary suffix.
     *
     * A value without a suffix is interpreted as plain bytes.
     */
    @Test
    fun testMemoryParsing() {
        assertEquals(1024L, MemoryValue.parse("1Ki").value)
        assertEquals(1024L * 1024, MemoryValue.parse("1Mi").value)
        assertEquals(1024L * 1024 * 1024, MemoryValue.parse("1Gi").value)
        assertEquals(512L, MemoryValue.parse("512").value)
    }

    /**
     * Verifies that a memory quantity survives a full render and parse cycle.
     */
    @Test
    fun testMemoryRoundTrip() {
        listOf(512.miBytes, 1.giBytes, 256.miBytes).forEach {
            assertEquals(it, roundTrip(it))
        }
    }

    /**
     * Verifies that the relative value factory recovers the correct variant from a scalar.
     *
     * A number is an absolute count, a percentage string a relative share - the distinction a rolling
     * update relies on for `maxSurge` and `maxUnavailable`.
     */
    @Test
    fun testRelativeValueFactory() {
        assertIs<AbsoluteValue>(RelativeValue.of(3))
        assertIs<PercentageValue>(RelativeValue.of("25%"))
        assertEquals(3, (RelativeValue.of(3) as AbsoluteValue).value)
        assertEquals("25%", (RelativeValue.of("25%") as PercentageValue).toYamlValue())
    }

    /**
     * Verifies that the relative value factory rejects input it cannot interpret.
     *
     * A bare string without a percent sign is ambiguous, and an unsupported type has no meaning at all.
     */
    @Test
    fun testRelativeValueFactoryRejectsInvalidInput() {
        assertFailsWith<IllegalArgumentException> { RelativeValue.of("25") }
        assertFailsWith<IllegalArgumentException> { RelativeValue.of(true) }
    }

    /**
     * Verifies that a Kubernetes version constraint is parsed from its string form.
     *
     * Helm expresses the supported cluster range as a comma separated list of comparisons, so each part
     * has to be recovered with its own operator.
     */
    @Test
    fun testKubeVersionParsing() {
        val version = KubeVersion.parse(">=1.25.0 <2.0.0")

        assertEquals(2, version.items.size)
        assertEquals("1.25.0", version.items[0].version)
        assertEquals(KubeVersion.ItemEquality.GREATER_EQUAL, version.items[0].equality)
        assertEquals("2.0.0", version.items[1].version)
        assertEquals(KubeVersion.ItemEquality.LESS, version.items[1].equality)
    }

    /**
     * Verifies that a version constraint survives a full render and parse cycle.
     */
    @Test
    fun testKubeVersionRoundTrip() {
        val version = KubeVersion.parse(">=1.25.0 <2.0.0")
        assertEquals(version, roundTrip(version))
    }

    /**
     * Verifies that each supported comparison operator is recognised.
     */
    @Test
    fun testKubeVersionOperators() {
        assertEquals(KubeVersion.ItemEquality.EQUAL, KubeVersion.parse("=1.0.0").items[0].equality)
        assertEquals(KubeVersion.ItemEquality.GREATER, KubeVersion.parse(">1.0.0").items[0].equality)
        assertEquals(KubeVersion.ItemEquality.LESS, KubeVersion.parse("<1.0.0").items[0].equality)
        assertEquals(
            KubeVersion.ItemEquality.GREATER_EQUAL,
            KubeVersion.parse(">=1.0.0").items[0].equality
        )
        assertEquals(
            KubeVersion.ItemEquality.LESS_EQUAL,
            KubeVersion.parse("<=1.0.0").items[0].equality
        )
    }
}
