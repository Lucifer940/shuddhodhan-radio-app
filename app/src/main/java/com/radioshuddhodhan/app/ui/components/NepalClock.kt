package com.radioshuddhodhan.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.radioshuddhodhan.app.core.language.AppStrings
import com.radioshuddhodhan.app.core.nepalidate.BsCalendar
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Live Nepal clock + Bikram Sambat and Gregorian dates.
 *
 * The clock is always computed in the official Asia/Kathmandu timezone
 * (UTC+05:45) regardless of where the user's phone is, and ticks every
 * second.
 */
@Composable
fun NepalClockBar(L: AppStrings, modifier: Modifier = Modifier) {
    var now by remember { mutableStateOf(LocalTime.now(BsCalendar.NEPAL_ZONE)) }
    // Recomputed on every tick below so the date rolls over at Nepal midnight
    // even when the app stays open across midnight (structural equality keeps
    // recomposition cheap on ticks where nothing changed).
    var bsToday by remember { mutableStateOf(BsCalendar.todayBs()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000)
            now = LocalTime.now(BsCalendar.NEPAL_ZONE)
            bsToday = BsCalendar.todayBs()
        }
    }

    val timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a")
    val adDate = BsCalendar.toAd(bsToday)
    val weekdayNe = BsCalendar.weekdayNamesNe[BsCalendar.weekdayOf(bsToday) - 1]
    val weekdayEn = BsCalendar.weekdayNamesEn[BsCalendar.weekdayOf(bsToday) - 1]

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🇳🇵",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (L.nepali) {
                            "${weekdayNe}, ${BsCalendar.monthNamesNe[bsToday.month - 1]} " +
                                BsCalendar.toNepaliDigits("${bsToday.day}") +
                                ", " + BsCalendar.toNepaliDigits("${bsToday.year}")
                        } else {
                            "${weekdayEn}, ${BsCalendar.monthNamesEn[bsToday.month - 1]} ${bsToday.day}, ${bsToday.year} BS"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (L.nepali) {
                            "ई.सं. " + BsCalendar.toNepaliDigits(
                                adDate.dayOfMonth.toString() + "/" + adDate.monthValue + "/" + adDate.year
                            )
                        } else {
                            "AD ${adDate.dayOfMonth}/${adDate.monthValue}/${adDate.year}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (L.nepali) {
                            BsCalendar.toNepaliDigits(now.format(timeFormat))
                        } else {
                            now.format(timeFormat)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (L.nepali) "नेपालको समय" else "Nepal time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
