package com.radioshuddhodhan.app.core.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Lightweight stream availability probe used for the LIVE / OFFLINE badges on
 * station cards.
 *
 * Opens the stream URL but never reads the body: we only care whether a
 * server answers with a success code. Any HTTP status below 400 (most live
 * streams answer 200, redirects are followed automatically) counts as LIVE;
 * timeouts, DNS failures and HTTP errors count as OFFLINE.
 */
object StreamHealthChecker {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    /** Returns true when [streamUrl] answers like a working live stream. */
    suspend fun isOnline(streamUrl: String): Boolean = withContext(Dispatchers.IO) {
        if (streamUrl.isBlank()) return@withContext false
        runCatching {
            val request = Request.Builder()
                .url(streamUrl.trim())
                .header("User-Agent", "RadioShuddhodhan/1.0.1 (stream check)")
                .build()
            client.newCall(request).execute().use { response ->
                response.code in 200..399
            }
        }.getOrDefault(false)
    }
}
