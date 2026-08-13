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
 * Developer tests for the symbol constants declared in `SymbolUtils.kt`.
 *
 * The constants are part of the public API and end up verbatim in the console output of the CLI, so
 * an accidental change of a code point would silently change the rendered output. These tests pin
 * every constant to its exact Unicode code point.
 */
class SymbolUtilsTest {

    /**
     * Verifies that the success symbol is the Unicode check mark (U+2713).
     *
     * Used by [String.successStyle] to mark a successfully finished operation.
     */
    @Test
    fun successSymbolIsCheckMark() {
        Assertions.assertEquals("✓", symbolSuccess)
    }

    /**
     * Verifies that the failure symbol is the Unicode ballot X (U+2717).
     *
     * Used by [String.failedStyle] to mark a failed operation.
     */
    @Test
    fun failedSymbolIsBallotX() {
        Assertions.assertEquals("✗", symbolFailed)
    }

    /**
     * Verifies that the warning symbol is the Unicode warning sign (U+26A0).
     *
     * Used by [String.warningStyle] to mark a non-fatal problem.
     */
    @Test
    fun warningSymbolIsWarningSign() {
        Assertions.assertEquals("⚠", symbolWarning)
    }

    /**
     * Verifies that the arrow symbol is the Unicode rightwards arrow (U+2192).
     *
     * Used by [String.subProcessTitleStyle] as the separator behind a sub-process title.
     */
    @Test
    fun arrowRightSymbolIsRightwardsArrow() {
        Assertions.assertEquals("→", symbolArrowRight)
    }

    /**
     * Verifies that the bullet symbol is the Unicode bullet (U+2022).
     *
     * Used to itemise entries in debug and trace output.
     */
    @Test
    fun bulletSymbolIsBullet() {
        Assertions.assertEquals("•", symbolBullet)
    }

    /**
     * Verifies that the process symbol is the Unicode clockwise gapped circle arrow (U+27F3).
     *
     * Used to indicate a running rendering or transformation step.
     */
    @Test
    fun processSymbolIsCircleArrow() {
        Assertions.assertEquals("⟳", symbolProcess)
    }

    /**
     * Verifies that the sub-process symbol is the Unicode anticlockwise open circle arrow (U+21BB).
     *
     * Used to indicate a nested step, for example a single overlay during a YAML merge.
     */
    @Test
    fun subProcessSymbolIsOpenCircleArrow() {
        Assertions.assertEquals("↻", symbolSubProcess)
    }

    /**
     * Verifies that the main process symbol is the Unicode dotted circle (U+25CC).
     *
     * Used to indicate the start of a primary operation.
     */
    @Test
    fun mainProcessSymbolIsDottedCircle() {
        Assertions.assertEquals("◌", symbolMainProcess)
    }

    /**
     * Verifies that all symbols are distinct from each other.
     *
     * Two identical symbols would make different log states indistinguishable on the console.
     */
    @Test
    fun allSymbolsAreDistinct() {
        val symbols = listOf(
            symbolSuccess, symbolFailed, symbolWarning, symbolArrowRight,
            symbolBullet, symbolProcess, symbolSubProcess, symbolMainProcess,
        )
        Assertions.assertEquals(symbols.size, symbols.toSet().size, "Symbols must be unique: $symbols")
    }
}
