package com.arturojas32.todoapp.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate
import java.time.ZoneId

@RunWith(RobolectricTestRunner::class)
class UtilsTest {

    // --- emailAndPasswordValidator ---

    @Test
    fun `valid email and password longer than 8 chars returns true`() {
        assertTrue(emailAndPasswordValidator("test@example.com", "password123"))
    }

    @Test
    fun `invalid email returns false`() {
        assertFalse(emailAndPasswordValidator("not-an-email", "password123"))
    }

    @Test
    fun `empty email returns false`() {
        assertFalse(emailAndPasswordValidator("", "password123"))
    }

    @Test
    fun `password with 8 or fewer chars returns false`() {
        assertFalse(emailAndPasswordValidator("test@example.com", "short"))
    }

    @Test
    fun `password with exactly 8 chars returns false`() {
        assertFalse(emailAndPasswordValidator("test@example.com", "12345678"))
    }

    @Test
    fun `password with 9 chars returns true`() {
        assertTrue(emailAndPasswordValidator("test@example.com", "123456789"))
    }

    @Test
    fun `empty password returns false`() {
        assertFalse(emailAndPasswordValidator("test@example.com", ""))
    }

    @Test
    fun `both empty returns false`() {
        assertFalse(emailAndPasswordValidator("", ""))
    }

    @Test
    fun `email without domain returns false`() {
        assertFalse(emailAndPasswordValidator("user@", "password123"))
    }

    @Test
    fun `email without at sign returns false`() {
        assertFalse(emailAndPasswordValidator("userexample.com", "password123"))
    }

    // --- getCurrentDate ---

    @Test
    fun `getCurrentDate returns date in dd MM yyyy format`() {
        val result = getCurrentDate()
        val regex = Regex("""\d{2}/\d{2}/\d{4}""")
        assertTrue("Expected dd/MM/yyyy format but got: $result", regex.matches(result))
    }

    @Test
    fun `getCurrentDate returns today's date`() {
        val result = getCurrentDate()
        val expected = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        assertEquals(expected, result)
    }

    // --- getSelectedDate ---

    @Test
    fun `getSelectedDate converts epoch millis to dd MM yyyy`() {
        // 2024-01-15 00:00:00 UTC
        val epochMillis = java.time.LocalDate.of(2024, 1, 15)
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        val result = getSelectedDate(epochMillis)
        assertEquals("15/01/2024", result)
    }

    @Test
    fun `getSelectedDate handles epoch millis for 2025-12-31`() {
        val epochMillis = java.time.LocalDate.of(2025, 12, 31)
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        val result = getSelectedDate(epochMillis)
        assertEquals("31/12/2025", result)
    }

    @Test
    fun `getSelectedDate handles epoch millis for 2000-01-01`() {
        val epochMillis = java.time.LocalDate.of(2000, 1, 1)
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        val result = getSelectedDate(epochMillis)
        assertEquals("01/01/2000", result)
    }
}
