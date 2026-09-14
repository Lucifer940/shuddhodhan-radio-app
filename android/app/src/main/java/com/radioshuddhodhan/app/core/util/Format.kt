package com.radioshuddhodhan.app.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Small formatting helpers shared across the app.
 */
object Format {

    private val nepalZone: ZoneId = ZoneId.of("Asia/Kathmandu")

    private val adLong = DateTimeFormatter.ofPattern("d MMMM yyyy")
    private val adLongNp = DateTimeFormatter.ofPattern("d MMMM yyyy")
    private val adShort = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTime = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a")

    fun adDateLong(date: LocalDate): String = adLong.format(date)

    fun adDateShort(date: LocalDate): String = adShort.format(date)

    fun dateTime(millis: Long): String = dateTime.format(
        LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), nepalZone)
    )

    /**
     * Compact relative time ("अभैले", "५ मिनेट अघि", …) in the given language.
     */
    fun relativeTime(millis: Long, nepali: Boolean): String {
        val diff = System.currentTimeMillis() - millis
        val minutes = diff / 60_000
        val hours = diff / 3_600_000
        val days = diff / 86_400_000
        fun ne(n: Long): String =
            com.radioshuddhodhan.app.core.nepalidate.BsCalendar.toNepaliDigits(n.toString())
        return when {
            minutes < 1 -> if (nepali) "अहिले" else "just now"
            minutes < 60 -> if (nepali) "${ne(minutes)} मिनेट अघि" else "${minutes}m ago"
            hours < 24 -> if (nepali) "${ne(hours)} घण्टा अघि" else "${hours}h ago"
            days < 7 -> if (nepali) "${ne(days)} दिन अघि" else "${days}d ago"
            else -> if (nepali) {
                // dateTime() renders Western digits; convert for Nepali.
                com.radioshuddhodhan.app.core.nepalidate.BsCalendar.toNepaliDigits(dateTime(millis))
            } else dateTime(millis)
        }
    }
}
