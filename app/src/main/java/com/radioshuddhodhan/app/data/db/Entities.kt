package com.radioshuddhodhan.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entities. The local Room database is the single source of truth for the
 * UI: everything the backend sends is cached here, and every screen observes
 * the database reactively. That is what makes admin changes appear instantly
 * on user devices once the backend is connected (and inside the demo mode
 * where the admin console writes to the same database).
 *
 * [source] is "remote" (synced from the backend) or "local" (created on this
 * device by the demo admin console / helpdesk queue).
 */

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val category: String,
    val imageUrl: String?,
    val author: String,
    val publishedAt: Long,
    val updatedAt: Long,
    val isPublished: Boolean,
    val isFeatured: Boolean,
    val isBreaking: Boolean,
    val source: String
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val imageUrl: String?,
    val author: String,
    val publishedAt: Long,
    val updatedAt: Long,
    val isPublished: Boolean,
    val isFeatured: Boolean,
    val source: String
)

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameNe: String,
    val description: String,
    val streamUrl: String,
    val logoUrl: String?,
    val isEnabled: Boolean,
    val isFeatured: Boolean,
    val sortOrder: Int,
    val source: String
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleNe: String,
    val description: String,
    /** ISO yyyy-MM-dd in the Bikram Sambat calendar sense (converted from BS by admin UI). */
    val adDate: String,
    val timeLabel: String,
    val location: String,
    val isFeatured: Boolean,
    val source: String
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val createdAt: Long,
    val activeUntil: Long,
    val isActive: Boolean,
    val source: String
)

@Entity(tableName = "helpdesk_tickets")
data class HelpdeskTicketEntity(
    @PrimaryKey val id: String,
    val name: String,
    val contact: String,
    val category: String,
    val message: String,
    val createdAt: Long,
    val status: String, // open | pending | resolved
    val reply: String?,
    val repliedAt: Long?,
    val source: String
)

@Entity(tableName = "social_links")
data class SocialLinkEntity(
    @PrimaryKey val id: String,
    /** facebook | facebook_live | youtube | website | whatsapp | twitter | instagram | telegram */
    val platform: String,
    val label: String,
    val url: String,
    val sortOrder: Int,
    val isEnabled: Boolean,
    val source: String
)

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey val id: Int = 1,
    val json: String
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val isGuest: Boolean,
    val createdAt: Long,
    /** Local (demo) password hash — never used once backend auth is connected. */
    val passwordHash: String?,
    val remoteToken: String?,
    val avatarUrl: String?
)

@Entity(tableName = "favorite_stations")
data class FavoriteStationEntity(
    @PrimaryKey val stationId: String,
    val addedAt: Long
)

@Entity(tableName = "bookmarked_news")
data class BookmarkedNewsEntity(
    @PrimaryKey val newsId: String,
    val addedAt: Long
)

@Entity(tableName = "notifications")
data class NotificationItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    /** breaking | news | event | announcement | general */
    val type: String,
    val receivedAt: Long,
    val isRead: Boolean,
    val dataId: String?
)
