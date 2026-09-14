package com.radioshuddhodhan.app.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Push notification receiver — INTEGRATION POINT for backend-driven push.
 *
 * This service only receives messages once a Firebase project is connected:
 *  1. Create a Firebase project for Radio Shuddhodhan.
 *  2. Add google-services.json to the app/ folder.
 *  3. Apply the google-services Gradle plugin (see docs/BACKEND_API.md).
 *  4. Point the backend at Firebase Cloud Messaging to send pushes.
 *
 * Until then it is inert: no messages are delivered, nothing crashes, and
 * the admin console delivers notifications locally in demo mode.
 */
class RadioFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title
            ?: message.data["title"]
            ?: return
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val type = message.data["type"] ?: "general"

        NotificationHelper.show(
            applicationContext,
            kotlinx.coroutines.MainScope(),
            (applicationContext as? PushCapableApp)?.contentRepository
                ?: return,
            title,
            body,
            type
        )
    }

    override fun onNewToken(token: String) {
        // Token is forwarded to the backend so the server can target this
        // device. Handled by PushRegistrar in the main app process.
        PushTokenHolder.latestToken = token
    }
}

/** Holds the latest FCM token until the main process registers it with the server. */
object PushTokenHolder {
    @Volatile
    var latestToken: String? = null
}

/** Implemented by the Application class so the messaging service can reach the repositories. */
interface PushCapableApp {
    val contentRepository: com.radioshuddhodhan.app.data.repo.ContentRepository
}
