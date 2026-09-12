package com.radioshuddhodhan.app.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.radioshuddhodhan.app.data.SettingsRepository
import com.radioshuddhodhan.app.data.repo.ContentRepository
import com.radioshuddhodhan.app.notifications.PushRegistrar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * REAL-TIME ADMIN → USER SYNC
 *
 *   ADMIN  →  BACKEND / DATABASE  →  SYNC  →  USER APP
 *
 * How it works:
 *  - Every screen observes the local Room database reactively.
 *  - [SyncManager] keeps that cache fresh from the backend:
 *      • on app start,
 *      • immediately whenever connectivity returns,
 *      • on a periodic 15-minute cycle while the app is in use,
 *      • whenever the user pulls to refresh / taps retry.
 *  - The moment new data lands in Room, ALL observing screens update
 *    automatically — no restart, no APK update required.
 *  - Push notifications (FCM) are the instant-delivery path for breaking
 *    changes once Firebase is configured (see PushRegistrar).
 *  - With no backend configured (demo mode) the on-device admin console
 *    writes to the same Room database, so the same reactive pipeline is
 *    demonstrated end-to-end on a single device.
 */
class SyncManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val contentRepository: ContentRepository,
    private val settings: SettingsRepository,
    private val isOnline: () -> Boolean
) {

    sealed class SyncStatus {
        data object Idle : SyncStatus()
        data object DemoMode : SyncStatus()
        data object Syncing : SyncStatus()
        data class Success(val timestamp: Long) : SyncStatus()
        data class Failed(val timestamp: Long) : SyncStatus()
    }

    private val _status = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val status: StateFlow<SyncStatus> = _status

    private var syncing = false

    init {
        // Auto-sync whenever the connection returns (debounced).
        scope.launch {
            com.radioshuddhodhan.app.core.connectivity.ConnectivityObserver(context)
                .online
                .collectLatest { online ->
                    if (online) {
                        delay(1_500) // debounce spurious reconnects
                        syncNow()
                        PushRegistrar.registerIfAvailable(context, scope, settings)
                    }
                }
        }
        schedulePeriodicSync()
    }

    /** Runs a full sync if a backend is configured. Safe to call repeatedly. */
    suspend fun syncNow(): Boolean {
        if (syncing) return false
        if (!isOnlineSafe()) {
            _status.value = SyncStatus.Failed(System.currentTimeMillis())
            return false
        }
        val hasBackend = settings.backendUrl.first().isNotBlank()
        if (!hasBackend) {
            _status.value = SyncStatus.DemoMode
            return false
        }
        syncing = true
        _status.value = SyncStatus.Syncing
        val ok = contentRepository.syncFromServer()
        val now = System.currentTimeMillis()
        _status.value = if (ok) {
            settings.setLastSync(now)
            SyncStatus.Success(now)
        } else {
            SyncStatus.Failed(now)
        }
        syncing = false
        return ok
    }

    private fun isOnlineSafe(): Boolean = runCatching { isOnline() }.getOrDefault(false)

    /** 15-minute periodic sync while the app process lives (WorkManager survives process death). */
    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "radio_shuddhodhan_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
