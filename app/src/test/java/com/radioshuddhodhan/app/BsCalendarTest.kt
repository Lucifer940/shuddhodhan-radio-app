package com.radioshuddhodhan.app

import com.radioshuddhodhan.app.core.nepalidate.BsCalendar
import com.radioshuddhodhan.app.core.nepalidate.NepaliHolidays
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Verification tests for the Bikram Sambat calendar engine.
 * The dataset is cross-checked from two independent open-source projects
 * (nepali_utils MIT / nepali-datetime Apache-2.0) and anchored at
 * BS 2000-01-01 == AD 1943-04-14.
 */
class BsCalendarTest {

    @Test
    fun `anchor is BS 2000-01-01 equals AD 1943-04-14`() {
        val ad = BsCalendar.toAd(BsCalendar.BsDate(2000, 1, 1))
        assertEquals(LocalDate.of(1943, 4, 14), ad)
    }

    @Test
    fun `known date - nepali new year 2083`() {
        // Baisakh 1, 2083 == April 14, 2026
        assertEquals(LocalDate.of(2026, 4, 14), BsCalendar.toAd(BsCalendar.BsDate(2083, 1, 1)))
        assertEquals(BsCalendar.BsDate(2083, 1, 1), BsCalendar.fromAd(LocalDate.of(2026, 4, 14)))
    }

    @Test
    fun `known date - 2026-09-11 is Bhadra 26 2083`() {
        assertEquals(BsCalendar.BsDate(2083, 5, 26), BsCalendar.fromAd(LocalDate.of(2026, 9, 11)))
    }

    @Test
    fun `known festival - gai jatra 2082`() {
        // Holiday dataset: Gai Jatra 2082-04-25 == AD 2025-08-10
        assertEquals(LocalDate.of(2025, 8, 10), BsCalendar.toAd(BsCalendar.BsDate(2082, 4, 25)))
    }

    @Test
    fun `known festival - indra jatra 2083`() {
        // Holiday dataset: Indra Jatra 2083-06-10 == AD 2026-09-26
        assertEquals(LocalDate.of(2026, 9, 26), BsCalendar.toAd(BsCalendar.BsDate(2083, 6, 10)))
    }

    @Test
    fun `round trip over several years`() {
        val start = LocalDate.of(2020, 1, 1)
        var date = start
        // 700 weekly steps stay inside the supported BS 2000–2090 table (~AD 2034-04).
        repeat(700) {
            val bs = BsCalendar.fromAd(date)
            assertEquals(date, BsCalendar.toAd(bs))
            date = date.plusDays(7)
        }
    }

    @Test
    fun `month lengths are within valid range`() {
        for (year in BsCalendar.FIRST_BS_YEAR..BsCalendar.LAST_BS_YEAR) {
            for (month in 1..12) {
                val days = BsCalendar.daysInMonth(year, month)
                assertTrue("BS $year-$month had $days days", days in 29..32)
            }
            val total = BsCalendar.daysInYear(year)
            assertTrue("BS $year had $total days", total in 363..367)
        }
    }

    @Test
    fun `weekday is consistent between BS and AD`() {
        // BS 2083-01-01 (2026-04-14) was a Tuesday: weekday 3 (1 = Sunday)
        assertEquals(3, BsCalendar.weekdayOf(BsCalendar.BsDate(2083, 1, 1)))
        // BS 2000-01-01 (1943-04-14) was a Wednesday: weekday 4
        assertEquals(4, BsCalendar.weekdayOf(BsCalendar.BsDate(2000, 1, 1)))
    }

    @Test
    fun `nepali digits conversion`() {
        assertEquals("२०८३", BsCalendar.toNepaliDigits("2083"))
        assertEquals("2083", BsCalendar.toAsciiDigits("२०८३"))
    }

    @Test
    fun `holiday data contains major festivals`() {
        assertNotNull(NepaliHolidays["2083-01-01"]) // New Year
        assertNotNull(NepaliHolidays["2082-01-01"]) // New Year 2082
        assertTrue(NepaliHolidays.isNotEmpty())
    }
}
