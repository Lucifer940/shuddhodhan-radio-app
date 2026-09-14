package com.radioshuddhodhan.app.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Safe helpers for leaving the app: opening websites, dialing, email and
 * sharing.
 *
 * Every helper swallows [ActivityNotFoundException] (no browser / dialer /
 * email app on the device — e.g. tablets, Android TV, work profiles) so a
 * missing external app can never crash the radio app.
 */
object ExternalApps {

    /** Opens a web URL; adds an https:// scheme if the admin entered a bare host. */
    fun openUrl(context: Context, rawUrl: String) {
        val url = rawUrl.trim()
        if (url.isBlank()) return
        val withScheme = if (url.startsWith("http://") || url.startsWith("https://")) url
        else "https://$url"
        launch(context, Intent(Intent.ACTION_VIEW, Uri.parse(withScheme)))
    }

    /** Opens the dialer pre-filled with [phone]. */
    fun dial(context: Context, phone: String) {
        val p = phone.trim()
        if (p.isBlank()) return
        launch(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + p.filter { it.isDigit() || it == '+' })))
    }

    /** Opens the email app pre-filled with [email]. */
    fun email(context: Context, email: String, subject: String? = null) {
        val address = email.trim()
        if (address.isBlank()) return
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            if (!subject.isNullOrBlank()) putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        launch(context, intent)
    }

    /** Shares plain text via the system chooser. */
    fun shareText(context: Context, text: String, chooserTitle: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        launch(context, Intent.createChooser(intent, chooserTitle))
    }

    private fun launch(context: Context, intent: Intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // No app can handle this action — silently ignore.
        } catch (_: SecurityException) {
            // Permission/chooser restriction — ignore rather than crash.
        }
    }
}
