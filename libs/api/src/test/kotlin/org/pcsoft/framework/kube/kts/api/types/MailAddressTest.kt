package org.pcsoft.framework.kube.kts.api.types

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MailAddressTest {

    @Test
    fun testParseSuccessfully() {
        Assertions.assertEquals(MailAddress("test", "example.com"), MailAddress.parse("test@example.com"))
        Assertions.assertEquals(MailAddress("a", "b.c"), MailAddress.parse("a@b.c"))
        Assertions.assertEquals(MailAddress("a.b.c.d.e", "a.c"), MailAddress.parse("a.b.c.d.e@a.c"))
        Assertions.assertEquals(MailAddress("a", "com.de"), MailAddress.parse("a@com.de"))
    }

    @Test
    fun testCreateSuccessfully() {
        Assertions.assertEquals("test@example.com", MailAddress("test", "example.com").toString())
        Assertions.assertEquals("a@b.c", MailAddress("a", "b.c").toString())
        Assertions.assertEquals("a.b.c.d.e@a.c", MailAddress("a.b.c.d.e", "a.c").toString())
        Assertions.assertEquals("a@com.de", MailAddress("a", "com.de").toString())
    }

    @Test
    fun testParseFailure() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress.parse("test")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress.parse("test@")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress.parse("@example.com")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress.parse("test@example")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress.parse("test@example@e.de")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress.parse("test.@example")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress.parse(".test@example")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress.parse("test@.example")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress.parse("test@example.")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress.parse("test@example..de")
        }
    }

    @Test
    fun testCreateFailure() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress("test", "")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress("", "example.com")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress("test", "example")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress("test", "example@com.de")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress("test.", "example")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress(".test", "example")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress("test", ".example")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress("test", "example.")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MailAddress("test", "example..de")
        }
    }

}