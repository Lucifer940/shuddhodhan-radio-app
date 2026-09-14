package com.radioshuddhodhan.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.data.db.AnnouncementEntity
import com.radioshuddhodhan.app.data.db.EventEntity
import com.radioshuddhodhan.app.data.db.HelpdeskTicketEntity
import com.radioshuddhodhan.app.data.db.NewsEntity
import com.radioshuddhodhan.app.data.db.PostEntity
import com.radioshuddhodhan.app.data.db.SocialLinkEntity
import com.radioshuddhodhan.app.data.db.StationEntity
import com.radioshuddhodhan.app.data.remote.AiRequest
import com.radioshuddhodhan.app.data.remote.ApiClient
import com.radioshuddhodhan.app.data.remote.AppConfigDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Admin data model shared by the admin console screens. Writes go to the
 * local Room database first (instant effect on user screens) and are then
 * pushed to the backend when configured.
 */
class AdminDataViewModel(private val app: RadioApp) : ViewModel() {

    val news = app.contentRepository.observeAllNews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val posts = app.contentRepository.observeAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val stations = app.contentRepository.observeAllStations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val events = app.contentRepository.observeEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val announcements = app.contentRepository.observeAllAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val socialLinks = app.contentRepository.observeAllSocialLinks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val helpdeskTickets = app.helpdeskRepository.observeAllTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val config: StateFlow<AppConfigDto> = app.configRepository.config
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppConfigDto())

    // ---------- Result feedback ----------

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast

    fun showToast(message: String) { _toast.value = message }
    fun consumeToast() { _toast.value = null }

    // ---------- News & posts ----------

    fun saveNews(
        id: String?,
        title: String,
        summary: String,
        content: String,
        category: String,
        imageUrl: String,
        isPublished: Boolean,
        isFeatured: Boolean,
        isBreaking: Boolean
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val item = NewsEntity(
                id = id ?: UUID.randomUUID().toString(),
                title = title.trim(),
                summary = summary.trim(),
                content = content.trim(),
                category = category.trim().ifBlank { "General" },
                imageUrl = imageUrl.trim().ifBlank { null },
                author = "Admin",
                publishedAt = now,
                updatedAt = now,
                isPublished = isPublished,
                isFeatured = isFeatured,
                isBreaking = isBreaking,
                source = "local"
            )
            app.contentRepository.upsertNewsLocal(item, pushRemote = true)
            _toast.value = "saved"
        }
    }

    fun toggleNewsFlags(news: NewsEntity) {
        viewModelScope.launch {
            app.contentRepository.upsertNewsLocal(news.copy(updatedAt = System.currentTimeMillis()), pushRemote = true)
        }
    }

    fun deleteNews(id: String) {
        viewModelScope.launch { app.contentRepository.deleteNewsLocal(id) }
    }

    fun savePost(
        id: String?,
        title: String,
        summary: String,
        content: String,
        imageUrl: String,
        isPublished: Boolean,
        isFeatured: Boolean
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val item = PostEntity(
                id = id ?: UUID.randomUUID().toString(),
                title = title.trim(),
                summary = summary.trim(),
                content = content.trim(),
                imageUrl = imageUrl.trim().ifBlank { null },
                author = "Admin",
                publishedAt = now,
                updatedAt = now,
                isPublished = isPublished,
                isFeatured = isFeatured,
                source = "local"
            )
            app.database.postDao().upsert(item)
            _toast.value = "saved"
        }
    }

    fun deletePost(id: String) {
        viewModelScope.launch { app.database.postDao().delete(id) }
    }

    // ---------- Stations ----------

    fun saveStation(
        id: String?,
        name: String,
        nameNe: String,
        description: String,
        streamUrl: String,
        logoUrl: String,
        isEnabled: Boolean,
        isFeatured: Boolean,
        sortOrder: Int
    ) {
        viewModelScope.launch {
            val item = StationEntity(
                id = id ?: UUID.randomUUID().toString(),
                name = name.trim(),
                nameNe = nameNe.trim(),
                description = description.trim(),
                streamUrl = streamUrl.trim(),
                logoUrl = logoUrl.trim().ifBlank { null },
                isEnabled = isEnabled,
                isFeatured = isFeatured,
                sortOrder = sortOrder,
                source = "local"
            )
            app.contentRepository.upsertStationLocal(item, pushRemote = true)
            _toast.value = "saved"
        }
    }

    fun deleteStation(id: String) {
        viewModelScope.launch { app.contentRepository.deleteStationLocal(id) }
    }

    // ---------- Events ----------

    fun saveEvent(
        id: String?,
        title: String,
        description: String,
        adDate: String,
        timeLabel: String,
        location: String
    ) {
        viewModelScope.launch {
            val item = EventEntity(
                id = id ?: UUID.randomUUID().toString(),
                title = title.trim(),
                titleNe = title.trim(),
                description = description.trim(),
                adDate = adDate,
                timeLabel = timeLabel.trim(),
                location = location.trim(),
                isFeatured = false,
                source = "local"
            )
            app.contentRepository.upsertEventLocal(item, pushRemote = true)
            _toast.value = "saved"
        }
    }

    fun deleteEvent(id: String) {
        viewModelScope.launch { app.contentRepository.deleteEventLocal(id) }
    }

    // ---------- Announcements ----------

    fun saveAnnouncement(id: String?, title: String, message: String) {
        viewModelScope.launch {
            val item = AnnouncementEntity(
                id = id ?: UUID.randomUUID().toString(),
                title = title.trim(),
                message = message.trim(),
                createdAt = System.currentTimeMillis(),
                activeUntil = System.currentTimeMillis() + 90L * 24 * 3600 * 1000,
                isActive = true,
                source = "local"
            )
            app.contentRepository.upsertAnnouncementLocal(item, pushRemote = true)
            _toast.value = "saved"
        }
    }

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch { app.contentRepository.deleteAnnouncementLocal(id) }
    }

    // ---------- Social links ----------

    fun saveSocialLink(id: String?, platform: String, label: String, url: String, sortOrder: Int) {
        viewModelScope.launch {
            val item = SocialLinkEntity(
                id = id ?: UUID.randomUUID().toString(),
                platform = platform,
                label = label.trim(),
                url = url.trim(),
                sortOrder = sortOrder,
                isEnabled = true,
                source = "local"
            )
            app.contentRepository.upsertSocialLinkLocal(item, pushRemote = true)
            _toast.value = "saved"
        }
    }

    fun deleteSocialLink(id: String) {
        viewModelScope.launch { app.contentRepository.deleteSocialLinkLocal(id) }
    }

    // ---------- Helpdesk ----------

    fun replyTicket(ticket: HelpdeskTicketEntity, reply: String) {
        viewModelScope.launch {
            app.helpdeskRepository.replyTicket(ticket, reply)
            _toast.value = "saved"
        }
    }

    fun deleteTicket(id: String) {
        viewModelScope.launch { app.helpdeskRepository.deleteTicket(id) }
    }

    // ---------- Remote configuration ----------

    fun saveConfig(newConfig: AppConfigDto) {
        viewModelScope.launch {
            app.configRepository.save(newConfig)
            // Push to server when configured (server stays source of truth).
            val url = app.settings.backendUrl.first()
            if (url.isNotBlank()) {
                runCatching {
                    val token = app.settings.adminToken.first()
                    val api = ApiClient.create(url) { token.ifBlank { null } }
                    api.saveConfig(newConfig)
                }
            }
            _toast.value = "configSaved"
        }
    }

    // ---------- Notifications ----------

    /**
     * Sends a notification. Demo mode: local delivery on this device.
     * Backend mode: also POSTs to the server which fans out via FCM.
     */
    fun sendNotification(title: String, body: String, type: String) {
        viewModelScope.launch {
            val url = app.settings.backendUrl.first()
            if (url.isNotBlank()) {
                val token = app.settings.adminToken.first()
                runCatching {
                    val api = ApiClient.create(url) { token.ifBlank { null } }
                    api.sendNotification(
                        com.radioshuddhodhan.app.data.remote.AdminNotificationRequest(
                            title = title, body = body, type = type
                        )
                    )
                }
            }
            com.radioshuddhodhan.app.notifications.NotificationHelper.show(
                app, app.appScope, app.contentRepository, title, body, type
            )
            _toast.value = "sent"
        }
    }

    // ---------- AI assistant (secure backend integration point) ----------

    /**
     * Calls the backend AI endpoint — the OpenAI key stays on the server.
     * Returns an error message when no backend is configured (demo mode).
     */
    suspend fun aiGenerate(action: String, input: String): Result<String> {
        val url = app.settings.backendUrl.first()
        if (url.isBlank()) return Result.failure(IllegalStateException("no-backend"))
        return runCatching {
            val token = app.settings.adminToken.first()
            val api = ApiClient.create(url) { token.ifBlank { null } }
            api.aiGenerate(AiRequest(action = action, input = input)).output
        }
    }

    /** Saves an AI-generated draft for admin approval in Manage news. */
    fun saveAiDraft(title: String, content: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            app.contentRepository.upsertNewsLocal(
                NewsEntity(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    summary = content.trim().take(140),
                    content = content.trim(),
                    category = "AI Draft",
                    imageUrl = null,
                    author = "AI Assistant",
                    publishedAt = now,
                    updatedAt = now,
                    isPublished = false, // requires administrator approval
                    isFeatured = false,
                    isBreaking = false,
                    source = "local"
                ),
                pushRemote = false
            )
            _toast.value = "draftSaved"
        }
    }
}
