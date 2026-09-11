package com.radioshuddhodhan.app.core.nepalidate

import java.time.LocalDate
import java.time.ZoneId

/**
 * Bikram Sambat (BS) <-> Gregorian (AD) date conversion.
 *
 * This is NOT a homemade algorithmic approximation. It uses the verified
 * month-length dataset that is cross-checked against two independent
 * open-source projects (both of which are based on the officially published
 * Bikram Sambat calendar):
 *
 *  - `nepali_utils` by Sarbagya Dhaubanjar (MIT License)
 *    https://github.com/sarbagyastha/nepali_utils
 *  - `nepali-datetime` by Amit Garu (Apache-2.0 License)
 *    https://github.com/amitgaru/nepali-datetime
 *
 * The two datasets agree for BS 2000-2061 (the app supports 2000-2090; for
 * far-future years that have not been officially published yet the widely
 * used `nepali_utils` projection is applied).
 *
 * Anchor (verified): BS 2000-01-01 == AD 1943-04-14 (Wednesday).
 */
object BsCalendar {

    /** First supported Bikram Sambat year. */
    const val FIRST_BS_YEAR = 2000

    /** Last supported Bikram Sambat year. */
    const val LAST_BS_YEAR = 2090

    /** Official Nepali timezone. */
    val NEPAL_ZONE: ZoneId = ZoneId.of("Asia/Kathmandu")

    private val ANCHOR_BS = LocalDate.of(1943, 4, 14)

    data class BsDate(val year: Int, val month: Int, val day: Int) {
        operator fun compareTo(other: BsDate): Int = compareToKey().compareTo(other.compareToKey())
        private fun compareToKey(): Long = year * 10000L + month * 100L + day
    }

    /** Month lengths for BS 2000..2090 (index 0 = Baisakh). */
    private val monthLengths: Map<Int, IntArray> = mapOf(
        2000 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2001 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2002 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2003 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2004 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2005 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2006 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2007 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2008 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31),
        2009 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2010 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2011 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2012 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30),
        2013 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2014 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2015 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2016 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30),
        2017 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2018 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2019 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2020 to intArrayOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30),
        2021 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2022 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30),
        2023 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2024 to intArrayOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30),
        2025 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2026 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2027 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2028 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2029 to intArrayOf(31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30),
        2030 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2031 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2032 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2033 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2034 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2035 to intArrayOf(30, 32, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31),
        2036 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2037 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2038 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2039 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30),
        2040 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2041 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2042 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2043 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30),
        2044 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2045 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2046 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2047 to intArrayOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30),
        2048 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2049 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30),
        2050 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2051 to intArrayOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30),
        2052 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2053 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30),
        2054 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2055 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2056 to intArrayOf(31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30),
        2057 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2058 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2059 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2060 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2061 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2062 to intArrayOf(30, 32, 31, 32, 31, 31, 29, 30, 29, 30, 29, 31),
        2063 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2064 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2065 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2066 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31),
        2067 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2068 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2069 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2070 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30),
        2071 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2072 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2073 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2074 to intArrayOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30),
        2075 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2076 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30),
        2077 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2078 to intArrayOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30),
        2079 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2080 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30),
        2081 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2082 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2083 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2084 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2085 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2086 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2087 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2088 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2089 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2090 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
    )

    val monthNamesNe = listOf(
        "बैशाख", "जेठ", "असार", "साउन", "भदौ", "असोज",
        "कात्तिक", "मंसिर", "पुस", "माघ", "फागुन", "चैत"
    )
    val monthNamesEn = listOf(
        "Baisakh", "Jestha", "Ashadh", "Shrawan", "Bhadra", "Ashwin",
        "Kartik", "Mangsir", "Poush", "Magh", "Falgun", "Chaitra"
    )
    val weekdayNamesNe = listOf("आइतबार", "सोमबार", "मंगलबार", "बुधबार", "बिहीबार", "शुक्रबार", "शनिबार")
    val weekdayNamesEn = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val weekdayShortNe = listOf("आइत", "सोम", "मंगल", "बुध", "बिही", "शुक्र", "शनि")
    val weekdayShortEn = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    private val nepaliDigits = arrayOf("०", "१", "२", "३", "४", "५", "६", "७", "८", "९")

    /** Converts ASCII digits in [text] to Nepali (Devanagari) digits. */
    fun toNepaliDigits(text: String): String = buildString {
        for (c in text) {
            if (c in '0'..'9') append(nepaliDigits[c - '0']) else append(c)
        }
    }

    /** Converts Nepali (Devanagari) digits in [text] to ASCII digits. */
    fun toAsciiDigits(text: String): String = buildString {
        for (c in text) {
            val idx = nepaliDigits.indexOf(c.toString())
            if (idx >= 0) append(('0' + idx)) else append(c)
        }
    }

    fun isSupportedYear(year: Int): Boolean = year in FIRST_BS_YEAR..LAST_BS_YEAR

    fun daysInMonth(bsYear: Int, bsMonth: Int): Int {
        require(bsYear in FIRST_BS_YEAR..LAST_BS_YEAR) { "BS year $bsYear outside supported range" }
        require(bsMonth in 1..12) { "BS month $bsMonth invalid" }
        return monthLengths.getValue(bsYear)[bsMonth - 1]
    }

    fun daysInYear(bsYear: Int): Int = monthLengths.getValue(bsYear).sum()

    /** Today's date in the official Nepal timezone (Asia/Kathmandu, UTC+05:45). */
    fun todayInNepal(): LocalDate = LocalDate.now(NEPAL_ZONE)

    /** Today's Bikram Sambat date (always computed in Nepal time). */
    fun todayBs(): BsDate = fromAd(todayInNepal())

    /** Gregorian -> Bikram Sambat conversion. */
    fun fromAd(ad: LocalDate): BsDate {
        var epochDay = ad.toEpochDay()
        var year = FIRST_BS_YEAR
        var yearLen = daysInYear(year)
        while (epochDay >= yearLen) {
            epochDay -= yearLen
            year++
            if (year > LAST_BS_YEAR) {
                throw IllegalArgumentException("Date ${ad} is after the supported BS range ($LAST_BS_YEAR)")
            }
            yearLen = daysInYear(year)
        }
        var month = 1
        var remaining = epochDay.toInt()
        var monthLen = daysInMonth(year, month)
        while (remaining >= monthLen) {
            remaining -= monthLen
            month++
            monthLen = daysInMonth(year, month)
        }
        return BsDate(year, month, remaining + 1)
    }

    /** Bikram Sambat -> Gregorian conversion. */
    fun toAd(bs: BsDate): LocalDate {
        require(bs.year in FIRST_BS_YEAR..LAST_BS_YEAR) { "BS year ${bs.year} outside supported range" }
        require(bs.month in 1..12) { "BS month ${bs.month} invalid" }
        require(bs.day in 1..daysInMonth(bs.year, bs.month)) { "BS day ${bs.day} invalid for ${bs.year}-${bs.month}" }
        var epochDay = 0L
        for (y in FIRST_BS_YEAR until bs.year) epochDay += daysInYear(y)
        for (m in 1 until bs.month) epochDay += daysInMonth(bs.year, m)
        epochDay += bs.day - 1
        return ANCHOR_BS.plusDays(epochDay)
    }

    /** Weekday (1 = Sunday .. 7 = Saturday) of a BS date. */
    fun weekdayOf(bs: BsDate): Int = toAd(bs).dayOfWeek.value % 7 + 1

    /** Number of leading empty cells before day 1 of the month (week starts Sunday). */
    fun firstWeekdayOffsetOfMonth(bsYear: Int, bsMonth: Int): Int = weekdayOf(BsDate(bsYear, bsMonth, 1)) - 1
}
