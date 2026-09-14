package com.radioshuddhodhan.app.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Periodic background sync (WorkManager, every 15 minutes when online).
 * Keeps the local cache fresh so content is up to date even without push.
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? com.radioshuddhodhan.app.RadioApp ?: return Result.success()
        val ok = app.syncManager.syncNow()
        return if (ok || app.syncManager.status.value is SyncManager.SyncStatus.DemoMode) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
