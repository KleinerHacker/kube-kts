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
 * Developer tests for the ANSI styling extensions declared in `AnsiUtils.kt`.
 *
 * Every extension wraps the receiver into a fixed pair of ANSI escape sequences and — for the
 * status styles — prefixes it with a symbol from `SymbolUtils.kt`. The tests pin the exact
 * resulting string, since the console output of the CLI depends on it byte for byte.
 */
class AnsiUtilsTest {

    private companion object {
        /** The ASCII escape character that introduces every ANSI sequence. */
        const val ESC = "\u001B"

        /** ANSI reset sequence that terminates every styled string. */
        const val RESET = "$ESC[0m"

        /** Arbitrary payload used as the receiver of the extensions under test. */
        const val TEXT = "message"
    }

    /**
     * Verifies that [String.successStyle] renders green text prefixed with the success symbol.
     *
     * Applied to a plain message, the result must start with the green colour code, contain the
     * check mark followed by the message and end with the reset sequence.
     */
    @Test
    fun successStyleUsesGreenAndSuccessSymbol() {
        Assertions.assertEquals("$ESC[32m$symbolSuccess $TEXT$RESET", TEXT.successStyle())
    }

    /**
     * Verifies that [String.failedStyle] renders red text prefixed with the failure symbol.
     *
     * Applied to a plain message, the result must start with the red colour code, contain the
     * ballot X followed by the message and end with the reset sequence.
     */
    @Test
    fun failedStyleUsesRedAndFailedSymbol() {
        Assertions.assertEquals("$ESC[31m$symbolFailed $TEXT$RESET", TEXT.failedStyle())
    }

    /**
     * Verifies that [String.warningStyle] renders yellow text prefixed with the warning symbol.
     *
     * Applied to a plain message, the result must start with the yellow colour code, contain the
     * warning sign followed by the message and end with the reset sequence.
     */
    @Test
    fun warningStyleUsesYellowAndWarningSymbol() {
        Assertions.assertEquals("$ESC[33m$symbolWarning $TEXT$RESET", TEXT.warningStyle())
    }

    /**
     * Verifies that [String.subProcessTitleStyle] renders italic cyan text followed by an arrow.
     *
     * Unlike the status styles the symbol is appended *after* the reset sequence, so the arrow is
     * printed unstyled behind the title.
     */
    @Test
    fun subProcessTitleStyleUsesItalicCyanAndTrailingArrow() {
        Assertions.assertEquals("$ESC[3;36m$TEXT$RESET $symbolArrowRight", TEXT.subProcessTitleStyle())
    }

    /**
     * Verifies that [String.subProcessInfoStyle] renders italic text without any symbol.
     *
     * Sub-process output is only visually distinguished from the main log, so no colour and no
     * symbol are added.
     */
    @Test
    fun subProcessInfoStyleUsesItalicOnly() {
        Assertions.assertEquals("$ESC[3m$TEXT$RESET", TEXT.subProcessInfoStyle())
    }

    /**
     * Verifies that [String.subProcessErrorStyle] renders bold, italic and red text.
     *
     * Used for the stderr output of an external process such as Helm.
     */
    @Test
    fun subProcessErrorStyleUsesBoldItalicRed() {
        Assertions.assertEquals("$ESC[1;3;31m$TEXT$RESET", TEXT.subProcessErrorStyle())
    }

    /**
     * Verifies that all styles keep the receiver content intact.
     *
     * A message containing spaces and non-ASCII characters must appear unchanged inside the styled
     * result, so styling never mangles the payload.
     */
    @Test
    fun allStylesKeepThePayloadIntact() {
        val payload = "Wert mit Ümläuten und Leerzeichen"
        val styled = listOf(
            payload.successStyle(),
            payload.failedStyle(),
            payload.warningStyle(),
            payload.subProcessTitleStyle(),
            payload.subProcessInfoStyle(),
            payload.subProcessErrorStyle(),
        )
        styled.forEach { Assertions.assertTrue(it.contains(payload), "Payload missing in: $it") }
    }

    /**
     * Verifies that all styles terminate the escape sequence with a reset.
     *
     * Without the reset the styling would bleed into all following console output.
     */
    @Test
    fun allStylesContainAReset() {
        val styled = listOf(
            TEXT.successStyle(),
            TEXT.failedStyle(),
            TEXT.warningStyle(),
            TEXT.subProcessTitleStyle(),
            TEXT.subProcessInfoStyle(),
            TEXT.subProcessErrorStyle(),
        )
        styled.forEach { Assertions.assertTrue(it.contains(RESET), "Reset missing in: $it") }
    }

    /**
     * Verifies that styling an empty string produces only the escape sequences.
     *
     * This is an edge case of the string templates used by the extensions: no payload must not
     * break the escape sequence structure.
     */
    @Test
    fun emptyPayloadProducesOnlyTheEscapeSequences() {
        Assertions.assertEquals("$ESC[3m$RESET", "".subProcessInfoStyle())
        Assertions.assertEquals("$ESC[32m$symbolSuccess $RESET", "".successStyle())
    }
}
