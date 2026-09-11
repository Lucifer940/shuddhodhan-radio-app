package com.radioshuddhodhan.app.data.repo

import com.radioshuddhodhan.app.data.SettingsRepository
import com.radioshuddhodhan.app.data.db.AppDatabase
import com.radioshuddhodhan.app.data.db.AppConfigEntity
import com.radioshuddhodhan.app.data.remote.ApiClient
import com.radioshuddhodhan.app.data.remote.ApiService
import com.radioshuddhodhan.app.data.remote.AppConfigDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString

/**
 * Remote configuration repository.
 *
 * The server is the source of truth. The bundled defaults below are used
 * until a backend is connected (demo mode) — after that the server copy
 * always wins on every sync.
 */
class ConfigRepository(
    private val db: AppDatabase,
    private val settings: SettingsRepository
) {

    val config: Flow<AppConfigDto> = db.configDao().observe().map { entity ->
        entity?.let { decode(it.json) } ?: DEFAULT_CONFIG
    }

    suspend fun current(): AppConfigDto = config.first()

    suspend fun save(config: AppConfigDto) {
        db.configDao().upsert(AppConfigEntity(json = ApiClient.json.encodeToString(config)))
    }

    /** Pulls the remote configuration. Returns null in demo mode / on failure. */
    suspend fun syncFromServer(): AppConfigDto? {
        val url = settings.backendUrl.first()
        if (url.isBlank()) return null
        return runCatching {
            val api: ApiService = ApiClient.create(url)
            val remote = api.getConfig()
            save(remote)
            remote
        }.getOrNull()
    }

    /** True when a backend server is configured. */
    suspend fun hasBackend(): Boolean = settings.backendUrl.first().isNotBlank()

    companion object {
        val DEFAULT_CONFIG = AppConfigDto()
    }
}
