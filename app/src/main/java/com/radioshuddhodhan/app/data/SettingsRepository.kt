package com.radioshuddhodhan.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** Theme preference. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Language preference. */
enum class LanguagePref { SYSTEM, NEPALI, ENGLISH }

/** Notification preference bundle. */
data class NotificationPrefs(
    val breaking: Boolean = true,
    val news: Boolean = true,
    val events: Boolean = true,
    val announcements: Boolean = true
)

/**
 * Local app settings stored in DataStore. NOTE: this never contains server
 * secrets — only the backend base URL (public information) and local UI
 * preferences.
 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val THEME = stringPreferencesKey("theme_mode")
        val BACKEND_URL = stringPreferencesKey("backend_url")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val ADMIN_TOKEN = stringPreferencesKey("admin_token")
        val FIRST_RUN_DONE = booleanPreferencesKey("first_run_done")
        val SEEDED = booleanPreferencesKey("db_seeded")
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        val NOTIF_BREAKING = booleanPreferencesKey("notif_breaking")
        val NOTIF_NEWS = booleanPreferencesKey("notif_news")
        val NOTIF_EVENTS = booleanPreferencesKey("notif_events")
        val NOTIF_ANNOUNCEMENTS = booleanPreferencesKey("notif_announcements")
        val AUTO_RECONNECT = booleanPreferencesKey("auto_reconnect")
        val LAST_SYNC = longPreferencesKey("last_sync")
    }

    // ---- Observers ----
    val languagePref: Flow<LanguagePref> = context.dataStore.data.map {
        runCatching { LanguagePref.valueOf(it[Keys.LANGUAGE] ?: "SYSTEM") }.getOrDefault(LanguagePref.SYSTEM)
    }
    val themeMode: Flow<ThemeMode> = context.dataStore.data.map {
        runCatching { ThemeMode.valueOf(it[Keys.THEME] ?: "SYSTEM") }.getOrDefault(ThemeMode.SYSTEM)
    }
    val backendUrl: Flow<String> = context.dataStore.data.map { it[Keys.BACKEND_URL] ?: "" }
    val authToken: Flow<String> = context.dataStore.data.map { it[Keys.AUTH_TOKEN] ?: "" }
    val adminToken: Flow<String> = context.dataStore.data.map { it[Keys.ADMIN_TOKEN] ?: "" }
    val firstRunDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.FIRST_RUN_DONE] ?: false }
    val seeded: Flow<Boolean> = context.dataStore.data.map { it[Keys.SEEDED] ?: false }
    val currentUserId: Flow<String> = context.dataStore.data.map { it[Keys.CURRENT_USER_ID] ?: "" }
    val notificationPrefs: Flow<NotificationPrefs> = context.dataStore.data.map {
        NotificationPrefs(
            breaking = it[Keys.NOTIF_BREAKING] ?: true,
            news = it[Keys.NOTIF_NEWS] ?: true,
            events = it[Keys.NOTIF_EVENTS] ?: true,
            announcements = it[Keys.NOTIF_ANNOUNCEMENTS] ?: true
        )
    }
    val autoReconnect: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTO_RECONNECT] ?: true }
    val lastSync: Flow<Long> = context.dataStore.data.map { it[Keys.LAST_SYNC] ?: 0L }

    // ---- Mutators ----
    suspend fun setLanguagePref(pref: LanguagePref) =
        context.dataStore.edit { it[Keys.LANGUAGE] = pref.name }

    suspend fun setThemeMode(mode: ThemeMode) =
        context.dataStore.edit { it[Keys.THEME] = mode.name }

    suspend fun setBackendUrl(url: String) =
        context.dataStore.edit { it[Keys.BACKEND_URL] = url.trim() }

    suspend fun setAuthToken(token: String) =
        context.dataStore.edit { it[Keys.AUTH_TOKEN] = token }

    suspend fun setAdminToken(token: String) =
        context.dataStore.edit { it[Keys.ADMIN_TOKEN] = token }

    suspend fun setFirstRunDone() = context.dataStore.edit { it[Keys.FIRST_RUN_DONE] = true }

    suspend fun setSeeded() = context.dataStore.edit { it[Keys.SEEDED] = true }

    suspend fun setCurrentUserId(id: String) = context.dataStore.edit { it[Keys.CURRENT_USER_ID] = id }

    suspend fun setNotificationPrefs(prefs: NotificationPrefs) = context.dataStore.edit {
        it[Keys.NOTIF_BREAKING] = prefs.breaking
        it[Keys.NOTIF_NEWS] = prefs.news
        it[Keys.NOTIF_EVENTS] = prefs.events
        it[Keys.NOTIF_ANNOUNCEMENTS] = prefs.announcements
    }

    suspend fun setAutoReconnect(value: Boolean) =
        context.dataStore.edit { it[Keys.AUTO_RECONNECT] = value }

    suspend fun setLastSync(timestamp: Long) =
        context.dataStore.edit { it[Keys.LAST_SYNC] = timestamp }

}
