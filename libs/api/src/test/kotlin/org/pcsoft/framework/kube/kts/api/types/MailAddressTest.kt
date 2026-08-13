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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MailAddressTest {

    /**
     * Verifies that a valid mail address is parsed successfully.
     *
     * Maintainer entries of a chart carry mail addresses, which are validated on parsing.
     */
    @Test
    fun testParseSuccessfully() {
        assertEquals(MailAddress("test", "example.com"), MailAddress.parse("test@example.com"))
        assertEquals(MailAddress("a", "b.c"), MailAddress.parse("a@b.c"))
        assertEquals(MailAddress("a.b.c.d.e", "a.c"), MailAddress.parse("a.b.c.d.e@a.c"))
        assertEquals(MailAddress("a", "com.de"), MailAddress.parse("a@com.de"))
    }

    /**
     * Verifies that a valid mail address can be created from its parts.
     *
     * The constructor takes the local part and the domain separately and must accept a valid
     * combination.
     */
    @Test
    fun testCreateSuccessfully() {
        assertEquals("test@example.com", MailAddress("test", "example.com").toString())
        assertEquals("a@b.c", MailAddress("a", "b.c").toString())
        assertEquals("a.b.c.d.e@a.c", MailAddress("a.b.c.d.e", "a.c").toString())
        assertEquals("a@com.de", MailAddress("a", "com.de").toString())
    }

    /**
     * Verifies that a malformed mail address is rejected on parsing.
     *
     * An invalid address must fail fast instead of ending up in the rendered `Chart.yaml`.
     */
    @Test
    fun testParseFailure() {
        assertFailsWith<IllegalArgumentException> {
            MailAddress.parse("test")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress.parse("test@")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress.parse("@example.com")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress.parse("test@example")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress.parse("test@example@e.de")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress.parse("test.@example")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress.parse(".test@example")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress.parse("test@.example")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress.parse("test@example.")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress.parse("test@example..de")
        }
    }

    /**
     * Verifies that invalid parts are rejected when creating a mail address.
     *
     * The same validation as for parsing must apply to the part based constructor.
     */
    @Test
    fun testCreateFailure() {
        assertFailsWith<IllegalArgumentException> {
            MailAddress("test", "")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress("", "example.com")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress("test", "example")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress("test", "example@com.de")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress("test.", "example")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress(".test", "example")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress("test", ".example")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress("test", "example.")
        }
        assertFailsWith<IllegalArgumentException> {
            MailAddress("test", "example..de")
        }
    }

}
