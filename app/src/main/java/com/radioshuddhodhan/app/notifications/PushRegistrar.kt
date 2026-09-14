package com.radioshuddhodhan.app.notifications

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.radioshuddhodhan.app.data.NotificationPrefs
import com.radioshuddhodhan.app.data.SettingsRepository
import com.radioshuddhodhan.app.data.remote.ApiClient
import com.radioshuddhodhan.app.data.remote.DeviceRegistration
import com.radioshuddhodhan.app.data.remote.NotificationPreferencesDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Registers the device for backend push notifications — INTEGRATION POINT.
 *
 * Fully guarded: if no Firebase project is configured (no google-services.json)
 * this becomes a no-op and the app continues to work. When Firebase IS
 * configured, the FCM token is sent to the backend together with the user's
 * notification preferences so the server can target this device.
 */
object PushRegistrar {

    fun registerIfAvailable(
        context: Context,
        scope: CoroutineScope,
        settings: SettingsRepository
    ) {
        scope.launch {
            val initialised = runCatching {
                FirebaseApp.getApps(context).isNotEmpty()
            }.getOrDefault(false)
            if (!initialised) return@launch

            val backendUrl = settings.backendUrl.first()
            if (backendUrl.isBlank()) return@launch

            runCatching {
                val cached = PushTokenHolder.latestToken
                val token = if (!cached.isNullOrBlank()) {
                    cached
                } else {
                    FirebaseMessaging.getInstance().token.await()
                }
                val prefs: NotificationPrefs = settings.notificationPrefs.first()
                val api = ApiClient.create(backendUrl)
                api.registerDevice(
                    DeviceRegistration(
                        pushToken = token,
                        preferences = NotificationPreferencesDto(
                            breaking = prefs.breaking,
                            news = prefs.news,
                            events = prefs.events,
                            announcements = prefs.announcements
                        )
                    )
                )
            }
        }
    }
}
