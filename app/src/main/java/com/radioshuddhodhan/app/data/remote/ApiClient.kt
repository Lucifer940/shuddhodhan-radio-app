package com.radioshuddhodhan.app.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Builds the Retrofit client for the configurable backend server.
 * The base URL is read from settings; when blank the app stays in demo mode
 * and no network calls are attempted.
 */
object ApiClient {

    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
        coerceInputValues = true
    }

    // Single shared OkHttp base (connection pool + dispatcher). Each ApiService
    // derives from it via newBuilder() so the app never leaks one pool per
    // Retrofit instance across the 15-minute sync cycle.
    private val baseClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    fun okHttp(authTokenProvider: () -> String?): OkHttpClient =
        baseClient.newBuilder()
            .addInterceptor { chain ->
                val token = authTokenProvider()
                val request = if (token.isNullOrBlank()) {
                    chain.request()
                } else {
                    chain.request().newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                }
                chain.proceed(request)
            }
            .build()

    fun create(baseUrl: String, authTokenProvider: () -> String? = { null }): ApiService {
        val normalized = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(normalized)
            .client(okHttp(authTokenProvider))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}
