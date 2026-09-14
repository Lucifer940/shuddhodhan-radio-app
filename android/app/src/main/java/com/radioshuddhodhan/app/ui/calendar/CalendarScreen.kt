package com.radioshuddhodhan.app.ui.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radioshuddhodhan.app.core.nepalidate.BsCalendar
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.components.brandGradient
import com.radioshuddhodhan.app.ui.theme.LiveRed

/**
 * Bikram Sambat calendar: month grid with holidays, festivals and events,
 * previous/next month navigation, today button and a day-detail panel.
 * Full Nepali/English support.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    val L = LocalAppStrings.current
    val viewModel: CalendarViewModel = appViewModel { CalendarViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val nepali = L.nepali

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(L.calendar, fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            // Month header + navigation
            item {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(brandGradient())
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AnimatedContent(
                                    targetState = state.bsYear to state.bsMonth,
                                    transitionSpec = {
                                        if (targetState.second >= initialState.second) {
                                            slideInHorizontally { it / 3 } + fadeIn() togetherWith fadeOut()
                                        } else {
                                            slideInHorizontally { -it / 3 } + fadeIn() togetherWith fadeOut()
                                        }
                                    },
                                    label = "monthTitle"
                                ) { (year, month) ->
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = (if (nepali) BsCalendar.monthNamesNe[month - 1]
                                            else BsCalendar.monthNamesEn[month - 1]) +
                                                " " + (if (nepali) BsCalendar.toNepaliDigits(year.toString()) else year.toString()),
                                            color = androidx.compose.ui.graphics.Color.White,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (nepali) "वि.सं. " + BsCalendar.toNepaliDigits(year.toString())
                                            else "BS $year",
                                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                                IconButton(onClick = { viewModel.previousMonth() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = L.previous,
                                        tint = androidx.compose.ui.graphics.Color.White
                                    )
                                }
                                IconButton(onClick = { viewModel.nextMonth() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = L.next,
                                        tint = androidx.compose.ui.graphics.Color.White
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp),
                                onClick = { viewModel.goToToday() }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Today,
                                        contentDescription = null,
                                        tint = androidx.compose.ui.graphics.Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = L.today,
                                        color = androidx.compose.ui.graphics.Color.White,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Weekday header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    val weekdays = if (nepali) BsCalendar.weekdayShortNe else BsCalendar.weekdayShortEn
                    weekdays.forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Month grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    state.cells.chunked(7).forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            week.forEach { cell ->
                                Box(modifier = Modifier.weight(1f)) {
                                    if (cell != null) {
                                        DayCellView(
                                            cell = cell,
                                            nepali = nepali,
                                            isSelected = state.selectedDay?.bs == cell.bs,
                                            onClick = { viewModel.selectDay(cell.bs) }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(46.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Selected day details
            if (state.selectedDay != null) {
                item {
                    val cell = state.selectedDay!!
                    Card(
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = L.dayDetails,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = if (nepali) {
                                    val wd = BsCalendar.weekdayNamesNe[BsCalendar.weekdayOf(cell.bs) - 1]
                                    "$wd, ${BsCalendar.monthNamesNe[cell.bs.month - 1]} " +
                                        BsCalendar.toNepaliDigits(cell.bs.day.toString()) +
                                        ", " + BsCalendar.toNepaliDigits(cell.bs.year.toString()) + " वि.सं."
                                } else {
                                    val wd = BsCalendar.weekdayNamesEn[BsCalendar.weekdayOf(cell.bs) - 1]
                                    "$wd, ${BsCalendar.monthNamesEn[cell.bs.month - 1]} ${cell.bs.day}, ${cell.bs.year} BS"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "AD: " + cell.ad.dayOfMonth + "/" + cell.ad.monthValue + "/" + cell.ad.year,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(10.dp))

                            if (cell.holiday != null) {
                                HolidayRow(
                                    name = if (nepali) cell.holiday.nameNe else cell.holiday.nameEn,
                                    isPublic = cell.holiday.isPublicHoliday,
                                    publicLabel = L.publicHoliday
                                )
                                Spacer(Modifier.height(6.dp))
                            }
                            if (cell.events.isNotEmpty()) {
                                Text(
                                    text = L.events + ":",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                cell.events.forEach { event ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Event,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (nepali) event.titleNe.ifBlank { event.title } else event.title,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            if (event.timeLabel.isNotBlank()) {
                                                Text(
                                                    text = event.timeLabel + if (event.location.isNotBlank()) " • " + event.location else "",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if (cell.holiday == null && cell.events.isEmpty()) {
                                Text(
                                    text = L.noEventsForDay,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Month events summary
            if (state.monthEvents.isNotEmpty()) {
                item {
                    Text(
                        text = L.radioPrograms + " • " + L.events,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(state.monthEvents.size) { index ->
                    val event = state.monthEvents[index]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (nepali) event.titleNe.ifBlank { event.title } else event.title,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = event.adDate + if (event.timeLabel.isNotBlank()) " • " + event.timeLabel else "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCellView(
    cell: CalendarViewModel.DayCell,
    nepali: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayText = if (nepali) BsCalendar.toNepaliDigits(cell.bs.day.toString()) else cell.bs.day.toString()
    val isSaturday = BsCalendar.weekdayOf(cell.bs) == 7
    val hasHoliday = cell.holiday?.isPublicHoliday == true
    val hasFestival = cell.holiday != null && !cell.holiday.isPublicHoliday
    val hasEvents = cell.events.isNotEmpty()

    val bg = when {
        cell.isToday -> MaterialTheme.colorScheme.primary
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> androidx.compose.ui.graphics.Color.Transparent
    }
    val dayColor = when {
        cell.isToday -> androidx.compose.ui.graphics.Color.White
        hasHoliday -> LiveRed
        isSaturday && !cell.isToday -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(10.dp),
        border = if (isSelected && !cell.isToday) androidx.compose.foundation.BorderStroke(
            1.dp, MaterialTheme.colorScheme.primary
        ) else null,
        modifier = Modifier
            .padding(1.dp)
            .height(46.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (cell.isToday) FontWeight.Bold else FontWeight.Medium,
                color = dayColor
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (hasHoliday) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(LiveRed, CircleShape)
                    )
                }
                if (hasFestival) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                    )
                }
                if (hasEvents) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun HolidayRow(name: String, isPublic: Boolean, publicLabel: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            if (isPublic) {
                Surface(color = LiveRed, shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = publicLabel,
                        color = androidx.compose.ui.graphics.Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            Text(text = name, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
