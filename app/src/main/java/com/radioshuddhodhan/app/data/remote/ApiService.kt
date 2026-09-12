package com.radioshuddhodhan.app.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Backend API contract for Radio Shuddhodhan.
 *
 * IMPORTANT: This interface is the single integration point between the
 * Android client and the server. The base URL is configured at runtime
 * (Settings → Backend server). When no server is configured the app runs in
 * fully functional DEMO mode backed by the local Room database.
 *
 * Full contract documentation: docs/BACKEND_API.md
 */
interface ApiService {

    // ---------- Public content (user app) ----------

    @GET("api/v1/config")
    suspend fun getConfig(): AppConfigDto

    @GET("api/v1/news")
    suspend fun listNews(
        @Query("category") category: String? = null,
        @Query("q") query: String? = null,
        @Query("limit") limit: Int = 100
    ): List<NewsDto>

    @GET("api/v1/news/{id}")
    suspend fun getNews(@Path("id") id: String): NewsDto

    @GET("api/v1/posts")
    suspend fun listPosts(@Query("limit") limit: Int = 100): List<PostDto>

    @GET("api/v1/stations")
    suspend fun listStations(): List<StationDto>

    /** Live listener count for a station (drives the "x listening" badge). */
    @GET("api/v1/stations/{id}/listeners")
    suspend fun getListenerCount(@Path("id") id: String): ListenerCountDto

    @GET("api/v1/events")
    suspend fun listEvents(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): List<EventDto>

    @GET("api/v1/announcements")
    suspend fun listAnnouncements(): List<AnnouncementDto>

    @GET("api/v1/social-links")
    suspend fun listSocialLinks(): List<SocialLinkDto>

    @GET("api/v1/notifications")
    suspend fun listNotifications(@Query("since") since: Long = 0): List<NotificationDto>

    @POST("api/v1/helpdesk")
    suspend fun submitHelpdeskTicket(@Body body: HelpdeskRequest): SimpleResult

    // ---------- Authentication ----------

    @POST("api/v1/auth/register")
    suspend fun register(@Body body: AuthRequest): AuthResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: AuthRequest): AuthResponse

    /** Administrator login — server-side authorisation only. No credentials live in the app. */
    @POST("api/v1/admin/login")
    suspend fun adminLogin(@Body body: AuthRequest): AuthResponse

    // ---------- Push notifications ----------

    @POST("api/v1/devices/register")
    suspend fun registerDevice(@Body body: DeviceRegistration): SimpleResult

    // ---------- Admin (requires Bearer admin token) ----------

    @PUT("api/v1/admin/config")
    suspend fun saveConfig(@Body body: AppConfigDto): AppConfigDto

    @POST("api/v1/admin/news")
    suspend fun createNews(@Body body: NewsDto): NewsDto

    @PUT("api/v1/admin/news/{id}")
    suspend fun updateNews(@Path("id") id: String, @Body body: NewsDto): NewsDto

    @DELETE("api/v1/admin/news/{id}")
    suspend fun deleteNews(@Path("id") id: String): SimpleResult

    @POST("api/v1/admin/stations")
    suspend fun createStation(@Body body: StationDto): StationDto

    @PUT("api/v1/admin/stations/{id}")
    suspend fun updateStation(@Path("id") id: String, @Body body: StationDto): StationDto

    @DELETE("api/v1/admin/stations/{id}")
    suspend fun deleteStation(@Path("id") id: String): SimpleResult

    @POST("api/v1/admin/events")
    suspend fun createEvent(@Body body: EventDto): EventDto

    @DELETE("api/v1/admin/events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): SimpleResult

    @POST("api/v1/admin/announcements")
    suspend fun createAnnouncement(@Body body: AnnouncementDto): AnnouncementDto

    @DELETE("api/v1/admin/announcements/{id}")
    suspend fun deleteAnnouncement(@Path("id") id: String): SimpleResult

    @GET("api/v1/admin/helpdesk")
    suspend fun listHelpdeskTickets(): List<HelpdeskTicketDto>

    @PUT("api/v1/admin/helpdesk/{id}")
    suspend fun updateHelpdeskTicket(@Path("id") id: String, @Body body: HelpdeskTicketDto): HelpdeskTicketDto

    @GET("api/v1/admin/social-links")
    suspend fun listAdminSocialLinks(): List<SocialLinkDto>

    @POST("api/v1/admin/social-links")
    suspend fun createSocialLink(@Body body: SocialLinkDto): SocialLinkDto

    @DELETE("api/v1/admin/social-links/{id}")
    suspend fun deleteSocialLink(@Path("id") id: String): SimpleResult

    /**
     * AI News Assistant — the server proxies OpenAI and keeps the API key
     * server-side. The app NEVER stores an OpenAI key.
     */
    @POST("api/v1/admin/ai/generate")
    suspend fun aiGenerate(@Body body: AiRequest): AiResponse

    /** Admin → all users push notification (delivered via FCM by the backend). */
    @POST("api/v1/admin/notifications")
    suspend fun sendNotification(@Body body: AdminNotificationRequest): SimpleResult
}
