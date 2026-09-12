package com.radioshuddhodhan.app.data.remote

import kotlinx.serialization.Serializable

/**
 * Remote configuration — the server is the source of truth for every flag.
 * Bundled defaults mirror this model; when a backend is connected the server
 * copy always wins (see docs/BACKEND_API.md for the full contract).
 */
@Serializable
data class AppConfigDto(
    val liveRadioEnabled: Boolean = true,
    val newsEnabled: Boolean = true,
    val calendarEnabled: Boolean = true,
    val helpdeskEnabled: Boolean = true,
    val postsEnabled: Boolean = true,
    val stationsEnabled: Boolean = true,
    val socialEnabled: Boolean = true,
    val maintenanceMode: Boolean = false,
    val featuredStationId: String? = null,
    val primaryStreamUrl: String = "",
    val primaryStationName: String = "",
    val homeBannerText: String = "",
    val homeBannerTextNe: String = "",
    val currentProgram: String = "",
    val currentProgramNe: String = "",
    val contactPhone: String = "+977 984-7036945",
    val contactEmail: String = "Radiosuddhodhan95.1@gmail.com",
    val contactWhatsapp: String = "+977 984-7036945",
    val contactWebsite: String = "https://www.facebook.com/share/1BjjUPuPdx/",
    val aboutText: String = "",
    val aboutTextNe: String = "",
    val appLogoUrl: String = "",
    val googleLoginEnabled: Boolean = false,
    val facebookLoginEnabled: Boolean = false,
    val phoneLoginEnabled: Boolean = true,
    // ---- Station identity & team (shown on Home > Station details) ----
    val stationFrequency: String = "95.1 MHz",
    val stationAddress: String = "Shuddhodhan-4, Pharsatikar, Rupandehi, Nepal",
    val taglineNe: String = "हरेक नेपालीको मन रेडियो शुद्धोधन 95.1 मेगाहर्ज.",
    val taglineEn: String = "In every Nepali's heart — Radio Shuddhodhan 95.1 MHz.",
    val teamMembers: List<TeamMemberDto> = listOf(
        TeamMemberDto(
            roleKey = "manager", role = "Station Manager", roleNe = "स्टेशन प्रमुख",
            name = "Ravi Rana", contact = "", sortOrder = 0
        ),
        TeamMemberDto(
            roleKey = "technician", role = "Technician", roleNe = "प्राविधिक",
            name = "", contact = "", sortOrder = 1
        ),
        TeamMemberDto(
            roleKey = "marketing", role = "Marketing Manager", roleNe = "मार्केटिङ प्रमुख",
            name = "", contact = "", sortOrder = 2
        )
    ),
    val updatedAt: Long = 0
)

/**
 * A member of the station team shown in the Station details section
 * (Station Manager, Technician, Marketing Manager, …). The admin can add,
 * edit and remove these at any time; users see the changes instantly.
 */
@Serializable
data class TeamMemberDto(
    /** Machine role key: "manager" | "technician" | "marketing" | custom. */
    val roleKey: String = "",
    /** Display name of the role. */
    val role: String = "",
    val roleNe: String = "",
    val name: String = "",
    val contact: String = "",
    /** Smaller cards render first (0 = top). */
    val sortOrder: Int = 0
)

@Serializable
data class NewsDto(
    val id: String,
    val title: String = "",
    val summary: String = "",
    val content: String = "",
    val category: String = "General",
    val imageUrl: String? = null,
    val author: String = "",
    val publishedAt: Long = 0,
    val updatedAt: Long = 0,
    val isPublished: Boolean = true,
    val isFeatured: Boolean = false,
    val isBreaking: Boolean = false
)

@Serializable
data class PostDto(
    val id: String,
    val title: String = "",
    val summary: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val author: String = "",
    val publishedAt: Long = 0,
    val updatedAt: Long = 0,
    val isPublished: Boolean = true,
    val isFeatured: Boolean = false
)

@Serializable
data class StationDto(
    val id: String,
    val name: String = "",
    val nameNe: String = "",
    val description: String = "",
    val streamUrl: String = "",
    val logoUrl: String? = null,
    val isEnabled: Boolean = true,
    val isFeatured: Boolean = false,
    val sortOrder: Int = 0
)

@Serializable
data class EventDto(
    val id: String,
    val title: String = "",
    val titleNe: String = "",
    val description: String = "",
    val adDate: String = "",
    val timeLabel: String = "",
    val location: String = "",
    val isFeatured: Boolean = false
)

@Serializable
data class AnnouncementDto(
    val id: String,
    val title: String = "",
    val message: String = "",
    val createdAt: Long = 0,
    val activeUntil: Long = 0,
    val isActive: Boolean = true
)

@Serializable
data class SocialLinkDto(
    val id: String,
    val platform: String = "",
    val label: String = "",
    val url: String = "",
    val sortOrder: Int = 0,
    val isEnabled: Boolean = true
)

@Serializable
data class HelpdeskTicketDto(
    val id: String,
    val name: String = "",
    val contact: String = "",
    val category: String = "General",
    val message: String = "",
    val createdAt: Long = 0,
    val status: String = "open",
    val reply: String? = null,
    val repliedAt: Long? = null
)

@Serializable
data class NotificationDto(
    val id: String,
    val title: String = "",
    val body: String = "",
    val type: String = "general",
    val receivedAt: Long = 0,
    val dataId: String? = null
)

@Serializable
data class AuthRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val password: String = ""
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: String = "",
    val name: String = "",
    val email: String? = null,
    val phone: String? = null,
    val isAdmin: Boolean = false
)

@Serializable
data class HelpdeskRequest(
    val name: String,
    val contact: String,
    val category: String,
    val message: String
)

@Serializable
data class DeviceRegistration(
    val pushToken: String,
    val platform: String = "android",
    val preferences: NotificationPreferencesDto = NotificationPreferencesDto()
)

@Serializable
data class NotificationPreferencesDto(
    val breaking: Boolean = true,
    val news: Boolean = true,
    val events: Boolean = true,
    val announcements: Boolean = true
)

@Serializable
data class AdminNotificationRequest(
    val title: String,
    val body: String,
    val type: String = "general"
)

@Serializable
data class AiRequest(
    val action: String,
    val input: String,
    val language: String = "ne"
)

@Serializable
data class AiResponse(
    val output: String = ""
)

@Serializable
data class SimpleResult(
    val ok: Boolean = false,
    val message: String = ""
)

/** Live listener count for a station, provided by the backend. */
@Serializable
data class ListenerCountDto(
    val stationId: String = "",
    val count: Int = 0,
    val updatedAt: Long = 0
)
