package com.radioshuddhodhan.app

import android.app.Application
import com.radioshuddhodhan.app.audio.PlayerManager
import com.radioshuddhodhan.app.core.connectivity.ConnectivityObserver
import com.radioshuddhodhan.app.data.SettingsRepository
import com.radioshuddhodhan.app.data.db.AppDatabase
import com.radioshuddhodhan.app.data.repo.AuthRepository
import com.radioshuddhodhan.app.data.repo.ConfigRepository
import com.radioshuddhodhan.app.data.repo.ContentRepository
import com.radioshuddhodhan.app.data.repo.HelpdeskRepository
import com.radioshuddhodhan.app.data.seed.SeedData
import com.radioshuddhodhan.app.notifications.NotificationHelper
import com.radioshuddhodhan.app.notifications.PushCapableApp
import com.radioshuddhodhan.app.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Application class: manual dependency container + app-scope services.
 *
 * Single-activity architecture — the entire UI lives in MainActivity with
 * Jetpack Compose navigation.
 */
class RadioApp : Application(), PushCapableApp {

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // ---- Core services ----
    val settings by lazy { SettingsRepository(this) }
    val database by lazy { AppDatabase.get(this) }
    val connectivity by lazy { ConnectivityObserver(this) }

    // ---- Repositories ----
    override val contentRepository by lazy {
        ContentRepository(database, settings)
    }
    val configRepository by lazy { ConfigRepository(database, settings) }
    val authRepository by lazy { AuthRepository(database, settings) }
    val helpdeskRepository by lazy { HelpdeskRepository(database, settings) }

    // ---- Player (live radio) ----
    val playerManager: PlayerManager by lazy {
        PlayerManager(this, appScope) { stationId ->
            database.stationDao().getById(stationId)
        }
    }

    // ---- Sync engine ----
    val syncManager: SyncManager by lazy {
        SyncManager(this, appScope, contentRepository, settings) { connectivity.isOnline }
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)

        // Seed demo content on first launch (only once).
        appScope.launch {
            val alreadySeeded = settings.seeded.first()
            SeedData.seedIfNeeded(database, alreadySeeded)
            settings.setSeeded()
        }

        // Keep auto-reconnect setting in sync with the player.
        // Main dispatcher: the MediaController inside PlayerManager is built
        // on the main thread.
        appScope.launch(Dispatchers.Main) {
            settings.autoReconnect.collect { playerManager.autoReconnectEnabled = it }
        }
    }
}
