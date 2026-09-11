package com.radioshuddhodhan.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.radioshuddhodhan.app.MainActivity
import com.radioshuddhodhan.app.R
import com.radioshuddhodhan.app.data.repo.ContentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * App notification channels + local notification display.
 *
 * The live-radio media notification is handled by Media3 automatically.
 * These channels cover: breaking news, announcements, events and general
 * messages — delivered via backend push (FCM) once configured, or locally
 * from the demo admin console.
 */
object NotificationHelper {

    const val CHANNEL_BREAKING = "breaking_news"
    const val CHANNEL_ANNOUNCEMENTS = "announcements"
    const val CHANNEL_EVENTS = "events"
    const val CHANNEL_GENERAL = "general"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    CHANNEL_BREAKING,
                    "Breaking news",
                    NotificationManager.IMPORTANCE_HIGH
                ),
                NotificationChannel(
                    CHANNEL_ANNOUNCEMENTS,
                    "Radio announcements",
                    NotificationManager.IMPORTANCE_DEFAULT
                ),
                NotificationChannel(
                    CHANNEL_EVENTS,
                    "Events",
                    NotificationManager.IMPORTANCE_DEFAULT
                ),
                NotificationChannel(
                    CHANNEL_GENERAL,
                    "General",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        )
    }

    fun hasNotificationPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    /**
     * Shows a notification and records it in the in-app notification history.
     */
    fun show(
        context: Context,
        scope: CoroutineScope,
        contentRepository: ContentRepository,
        title: String,
        body: String,
        type: String
    ) {
        // Record in the in-app notification centre.
        scope.launch { contentRepository.addLocalNotification(title, body, type) }

        if (!hasNotificationPermission(context)) return

        val channelId = when (type) {
            "breaking" -> CHANNEL_BREAKING
            "announcement" -> CHANNEL_ANNOUNCEMENTS
            "event" -> CHANNEL_EVENTS
            else -> CHANNEL_GENERAL
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(
                if (type == "breaking") NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val id = UUID.randomUUID().hashCode()
        runCatching { manager.notify(id, notification) }
    }
}
