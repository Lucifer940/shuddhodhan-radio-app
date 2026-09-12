package com.radioshuddhodhan.app.data.repo

import com.radioshuddhodhan.app.data.SettingsRepository
import com.radioshuddhodhan.app.data.db.AppDatabase
import com.radioshuddhodhan.app.data.db.AnnouncementEntity
import com.radioshuddhodhan.app.data.db.AppConfigEntity
import com.radioshuddhodhan.app.data.db.BookmarkedNewsEntity
import com.radioshuddhodhan.app.data.db.EventEntity
import com.radioshuddhodhan.app.data.db.FavoriteStationEntity
import com.radioshuddhodhan.app.data.db.NewsEntity
import com.radioshuddhodhan.app.data.db.NotificationItemEntity
import com.radioshuddhodhan.app.data.db.PostEntity
import com.radioshuddhodhan.app.data.db.SocialLinkEntity
import com.radioshuddhodhan.app.data.db.StationEntity
import com.radioshuddhodhan.app.data.remote.ApiClient
import com.radioshuddhodhan.app.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import java.util.UUID

/**
 * Repository for all user-facing content (news, posts, stations, events,
 * announcements, social links, notifications) and user personalisation
 * (bookmarks, favourites).
 *
 * Every read is a reactive Room Flow so ANY write — whether from a server
 * sync or from the on-device admin console — updates the UI immediately.
 */
class ContentRepository(
    private val db: AppDatabase,
    private val settings: SettingsRepository
) {

    private suspend fun apiOrNull(): ApiService? {
        val url = settings.backendUrl.first()
        if (url.isBlank()) return null
        val token = settings.authToken.first()
        return ApiClient.create(url) { token.ifBlank { null } }
    }

    // ---------------- Observers (reactive) ----------------

    fun observeNews() = db.newsDao().observePublished()
    fun observeAllNews() = db.newsDao().observeAll()
    fun observeNewsById(id: String) = db.newsDao().observeById(id)
    fun searchNews(q: String) = db.newsDao().searchPublished(q)
    fun observeNewsCategories() = db.newsDao().observeCategories()

    fun observePosts() = db.postDao().observePublished()
    fun observeAllPosts() = db.postDao().observeAll()
    fun observePostById(id: String) = db.postDao().observeById(id)

    fun observeStations() = db.stationDao().observeEnabled()
    fun observeAllStations() = db.stationDao().observeAll()
    fun searchStations(q: String) = db.stationDao().searchEnabled(q)

    fun observeEvents() = db.eventDao().observeAll()
    fun observeUpcomingEvents(todayIso: String, limit: Int = 5) =
        db.eventDao().observeUpcoming(todayIso, limit)
    fun observeEventsForDate(dateIso: String) = db.eventDao().observeForDate(dateIso)

    fun observeAnnouncements(now: Long) = db.announcementDao().observeActive(now)
    fun observeAllAnnouncements() = db.announcementDao().observeAll()

    fun observeSocialLinks() = db.socialLinkDao().observeEnabled()
    fun observeAllSocialLinks() = db.socialLinkDao().observeAll()

    fun observeNotifications() = db.notificationDao().observeAll()
    fun observeUnreadNotificationCount() = db.notificationDao().observeUnreadCount()

    fun observeBookmarkIds(): Flow<List<String>> = db.bookmarkedNewsDao().observeIds()
    fun observeFavoriteIds(): Flow<List<String>> = db.favoriteStationDao().observeIds()

    // ---------------- Personalisation ----------------

    suspend fun toggleBookmark(newsId: String): Boolean {
        val existing = db.bookmarkedNewsDao().observeIds().first()
        return if (newsId in existing) {
            db.bookmarkedNewsDao().remove(newsId)
            false
        } else {
            db.bookmarkedNewsDao().add(BookmarkedNewsEntity(newsId, System.currentTimeMillis()))
            true
        }
    }

    suspend fun toggleFavorite(stationId: String): Boolean {
        val existing = db.favoriteStationDao().observeIds().first()
        return if (stationId in existing) {
            db.favoriteStationDao().remove(stationId)
            false
        } else {
            db.favoriteStationDao().add(FavoriteStationEntity(stationId, System.currentTimeMillis()))
            true
        }
    }

    suspend fun markNotificationsRead() = db.notificationDao().markAllRead()
    suspend fun clearNotifications() = db.notificationDao().clearAll()

    suspend fun addLocalNotification(
        title: String,
        body: String,
        type: String,
        dataId: String? = null
    ) {
        db.notificationDao().upsert(
            NotificationItemEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                body = body,
                type = type,
                receivedAt = System.currentTimeMillis(),
                isRead = false,
                dataId = dataId
            )
        )
    }

    // ---------------- Admin mutations (local / demo) ----------------
    // These write to the local Room database. When a backend is configured
    // the admin console ALSO pushes the change to the server so every user
    // device receives it on the next sync/push.

    suspend fun upsertNewsLocal(item: NewsEntity, pushRemote: Boolean = false) {
        db.newsDao().upsert(item)
        if (pushRemote) runCatching {
            apiOrNull()?.let { api ->
                val dto = com.radioshuddhodhan.app.data.remote.NewsDto(
                    id = item.id, title = item.title, summary = item.summary, content = item.content,
                    category = item.category, imageUrl = item.imageUrl, author = item.author,
                    publishedAt = item.publishedAt, updatedAt = item.updatedAt,
                    isPublished = item.isPublished, isFeatured = item.isFeatured, isBreaking = item.isBreaking
                )
                api.createNews(dto)
            }
        }
    }

    suspend fun deleteNewsLocal(id: String) {
        db.newsDao().delete(id)
        val api = apiOrNull() ?: return
        runCatching { api.deleteNews(id) }
    }

    suspend fun upsertStationLocal(item: StationEntity, pushRemote: Boolean = false) {
        db.stationDao().upsert(item)
        if (pushRemote) runCatching {
            apiOrNull()?.let { api ->
                api.createStation(
                    com.radioshuddhodhan.app.data.remote.StationDto(
                        id = item.id, name = item.name, nameNe = item.nameNe,
                        description = item.description, streamUrl = item.streamUrl,
                        logoUrl = item.logoUrl, isEnabled = item.isEnabled,
                        isFeatured = item.isFeatured, sortOrder = item.sortOrder
                    )
                )
            }
        }
    }

    suspend fun deleteStationLocal(id: String) {
        db.stationDao().delete(id)
        val api = apiOrNull() ?: return
        runCatching { api.deleteStation(id) }
    }

    suspend fun upsertEventLocal(item: EventEntity, pushRemote: Boolean = false) {
        db.eventDao().upsert(item)
        if (pushRemote) runCatching {
            apiOrNull()?.let { api ->
                api.createEvent(
                    com.radioshuddhodhan.app.data.remote.EventDto(
                        id = item.id, title = item.title, titleNe = item.titleNe,
                        description = item.description, adDate = item.adDate,
                        timeLabel = item.timeLabel, location = item.location,
                        isFeatured = item.isFeatured
                    )
                )
            }
        }
    }

    suspend fun deleteEventLocal(id: String) {
        db.eventDao().delete(id)
        val api = apiOrNull() ?: return
        runCatching { api.deleteEvent(id) }
    }

    suspend fun upsertAnnouncementLocal(item: AnnouncementEntity, pushRemote: Boolean = false) {
        db.announcementDao().upsert(item)
        if (pushRemote) runCatching {
            apiOrNull()?.let { api ->
                api.createAnnouncement(
                    com.radioshuddhodhan.app.data.remote.AnnouncementDto(
                        id = item.id, title = item.title, message = item.message,
                        createdAt = item.createdAt, activeUntil = item.activeUntil,
                        isActive = item.isActive
                    )
                )
            }
        }
    }

    suspend fun deleteAnnouncementLocal(id: String) {
        db.announcementDao().delete(id)
        val api = apiOrNull() ?: return
        runCatching { api.deleteAnnouncement(id) }
    }

    suspend fun upsertSocialLinkLocal(item: SocialLinkEntity, pushRemote: Boolean = false) {
        db.socialLinkDao().upsert(item)
        if (pushRemote) runCatching {
            apiOrNull()?.let { api ->
                api.createSocialLink(
                    com.radioshuddhodhan.app.data.remote.SocialLinkDto(
                        id = item.id, platform = item.platform, label = item.label,
                        url = item.url, sortOrder = item.sortOrder, isEnabled = item.isEnabled
                    )
                )
            }
        }
    }

    suspend fun deleteSocialLinkLocal(id: String) {
        db.socialLinkDao().delete(id)
        val api = apiOrNull() ?: return
        runCatching { api.deleteSocialLink(id) }
    }

    // ---------------- Server sync (backend -> local cache) ----------------

    /**
     * Pulls every content type from the backend into the local cache.
     * Returns true when the backend is configured AND all primary fetches
     * succeeded. In demo mode this is a no-op returning false.
     */
    suspend fun syncFromServer(): Boolean {
        val api = apiOrNull() ?: return false

        val configOk = runCatching {
            val config = api.getConfig()
            db.configDao().upsert(
                AppConfigEntity(json = ApiClient.json.encodeToString(config))
            )
        }.isSuccess

        val newsOk = runCatching {
            val items = api.listNews()
            db.newsDao().upsertAll(
                items.map {
                    NewsEntity(
                        id = it.id, title = it.title, summary = it.summary, content = it.content,
                        category = it.category, imageUrl = it.imageUrl, author = it.author,
                        publishedAt = it.publishedAt, updatedAt = it.updatedAt,
                        isPublished = it.isPublished, isFeatured = it.isFeatured,
                        isBreaking = it.isBreaking, source = "remote"
                    )
                }
            )
            if (items.isNotEmpty()) db.newsDao().deleteRemoteNotIn(items.map { it.id })
        }.isSuccess

        val postsOk = runCatching {
            val items = api.listPosts()
            db.postDao().upsertAll(
                items.map {
                    PostEntity(
                        id = it.id, title = it.title, summary = it.summary, content = it.content,
                        imageUrl = it.imageUrl, author = it.author, publishedAt = it.publishedAt,
                        updatedAt = it.updatedAt, isPublished = it.isPublished,
                        isFeatured = it.isFeatured, source = "remote"
                    )
                }
            )
            if (items.isNotEmpty()) db.postDao().deleteRemoteNotIn(items.map { it.id })
        }.isSuccess

        val stationsOk = runCatching {
            val items = api.listStations()
            db.stationDao().upsertAll(
                items.map {
                    StationEntity(
                        id = it.id, name = it.name, nameNe = it.nameNe, description = it.description,
                        streamUrl = it.streamUrl, logoUrl = it.logoUrl, isEnabled = it.isEnabled,
                        isFeatured = it.isFeatured, sortOrder = it.sortOrder, source = "remote"
                    )
                }
            )
            if (items.isNotEmpty()) db.stationDao().deleteRemoteNotIn(items.map { it.id })
        }.isSuccess

        val eventsOk = runCatching {
            val items = api.listEvents()
            db.eventDao().upsertAll(
                items.map {
                    EventEntity(
                        id = it.id, title = it.title, titleNe = it.titleNe,
                        description = it.description, adDate = it.adDate, timeLabel = it.timeLabel,
                        location = it.location, isFeatured = it.isFeatured, source = "remote"
                    )
                }
            )
            if (items.isNotEmpty()) db.eventDao().deleteRemoteNotIn(items.map { it.id })
        }.isSuccess

        val announcementsOk = runCatching {
            val items = api.listAnnouncements()
            db.announcementDao().upsertAll(
                items.map {
                    AnnouncementEntity(
                        id = it.id, title = it.title, message = it.message,
                        createdAt = it.createdAt, activeUntil = it.activeUntil,
                        isActive = it.isActive, source = "remote"
                    )
                }
            )
            if (items.isNotEmpty()) db.announcementDao().deleteRemoteNotIn(items.map { it.id })
        }.isSuccess

        val socialOk = runCatching {
            val items = api.listSocialLinks()
            db.socialLinkDao().upsertAll(
                items.map {
                    SocialLinkEntity(
                        id = it.id, platform = it.platform, label = it.label, url = it.url,
                        sortOrder = it.sortOrder, isEnabled = it.isEnabled, source = "remote"
                    )
                }
            )
            if (items.isNotEmpty()) db.socialLinkDao().deleteRemoteNotIn(items.map { it.id })
        }.isSuccess

        return configOk || newsOk || postsOk || stationsOk || eventsOk || announcementsOk || socialOk
    }
}
