package com.radioshuddhodhan.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.core.nepalidate.BsCalendar
import com.radioshuddhodhan.app.core.nepalidate.HolidayInfo
import com.radioshuddhodhan.app.core.nepalidate.NepaliHolidays
import com.radioshuddhodhan.app.data.db.EventEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Calendar state: current BS month being viewed, holidays/festivals for the
 * month, admin events for the month and the selected day.
 */
class CalendarViewModel(private val app: RadioApp) : ViewModel() {

    data class DayCell(
        val bs: BsCalendar.BsDate,
        val ad: java.time.LocalDate,
        val isToday: Boolean,
        val holiday: HolidayInfo?,
        val events: List<EventEntity>
    )

    data class CalendarUiState(
        val bsYear: Int = BsCalendar.todayBs().year,
        val bsMonth: Int = BsCalendar.todayBs().month,
        val cells: List<DayCell?> = emptyList(),
        val selectedDay: DayCell? = null,
        val monthEvents: List<EventEntity> = emptyList()
    )

    private val viewYear = MutableStateFlow(BsCalendar.todayBs().year)
    private val viewMonth = MutableStateFlow(BsCalendar.todayBs().month)
    private val selectedDate = MutableStateFlow<BsCalendar.BsDate?>(null)

    private val allEvents = app.contentRepository.observeEvents()

    val state: StateFlow<CalendarUiState> = combine(
        viewYear,
        viewMonth,
        selectedDate,
        allEvents
    ) { year, month, selected, events ->
        val todayBs = BsCalendar.todayBs()
        val daysInMonth = BsCalendar.daysInMonth(year, month)
        val offset = BsCalendar.firstWeekdayOffsetOfMonth(year, month)

        val cells = mutableListOf<DayCell?>()
        repeat(offset) { cells.add(null) }
        for (day in 1..daysInMonth) {
            val bs = BsCalendar.BsDate(year, month, day)
            val ad = BsCalendar.toAd(bs)
            val holiday = NepaliHolidays["$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"]
            val dayEvents = events.filter { it.adDate == ad.toString() }
            cells.add(
                DayCell(
                    bs = bs,
                    ad = ad,
                    isToday = bs == todayBs,
                    holiday = holiday,
                    events = dayEvents
                )
            )
        }
        while (cells.size % 7 != 0) cells.add(null)

        val monthAdStart = BsCalendar.toAd(BsCalendar.BsDate(year, month, 1))
        val monthAdEnd = BsCalendar.toAd(BsCalendar.BsDate(year, month, daysInMonth))

        CalendarUiState(
            bsYear = year,
            bsMonth = month,
            cells = cells,
            selectedDay = selected?.let { sel ->
                cells.filterNotNull().find { it.bs == sel }
            },
            monthEvents = events.filter {
                it.adDate >= monthAdStart.toString() && it.adDate <= monthAdEnd.toString()
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())

    fun previousMonth() = shiftMonth(-1)

    fun nextMonth() = shiftMonth(+1)

    fun goToToday() {
        val today = BsCalendar.todayBs()
        viewYear.value = today.year
        viewMonth.value = today.month
        selectedDate.value = today
    }

    fun selectDay(bs: BsCalendar.BsDate) {
        selectedDate.value = bs
    }

    private fun shiftMonth(delta: Int) {
        var month = viewMonth.value + delta
        var year = viewYear.value
        if (month < 1) { month = 12; year-- }
        if (month > 12) { month = 1; year++ }
        if (BsCalendar.isSupportedYear(year)) {
            viewYear.value = year
            viewMonth.value = month
            selectedDate.value = null
        }
    }

    /** Returns true while the given preference loop should continue. Kept for clarity. */
    fun refreshOnSync() {
        viewModelScope.launch { app.syncManager.syncNow() }
    }
}
