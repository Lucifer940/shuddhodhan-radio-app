package com.radioshuddhodhan.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.core.nepalidate.BsCalendar
import com.radioshuddhodhan.app.data.db.AnnouncementEntity
import com.radioshuddhodhan.app.data.db.EventEntity
import com.radioshuddhodhan.app.data.db.NewsEntity
import com.radioshuddhodhan.app.data.db.PostEntity
import com.radioshuddhodhan.app.data.db.StationEntity
import com.radioshuddhodhan.app.data.remote.AppConfigDto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Home screen data: latest news & posts, featured station, announcements,
 * upcoming events and the remote config (banner, current programme…).
 */
class HomeViewModel(private val app: RadioApp) : ViewModel() {

    data class HomeUiState(
        val isLoading: Boolean = true,
        val config: AppConfigDto = AppConfigDto(),
        val news: List<NewsEntity> = emptyList(),
        val posts: List<PostEntity> = emptyList(),
        val stations: List<StationEntity> = emptyList(),
        val events: List<EventEntity> = emptyList(),
        val announcements: List<AnnouncementEntity> = emptyList()
    )

    val state: StateFlow<HomeUiState> = combine(
        app.contentRepository.observeNews(),
        app.contentRepository.observePosts(),
        app.contentRepository.observeStations(),
        app.contentRepository.observeUpcomingEvents(BsCalendar.todayInNepal().toString(), 5),
        app.contentRepository.observeAnnouncements(System.currentTimeMillis()),
        app.configRepository.config
    ) { news, posts, stations, events, announcements, config ->
        HomeUiState(
            isLoading = false,
            config = config,
            news = news.take(6),
            posts = posts.take(4),
            stations = stations.take(6),
            events = events,
            announcements = announcements
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        viewModelScope.launch { app.syncManager.syncNow() }
    }

    fun refresh() {
        viewModelScope.launch { app.syncManager.syncNow() }
    }
}
