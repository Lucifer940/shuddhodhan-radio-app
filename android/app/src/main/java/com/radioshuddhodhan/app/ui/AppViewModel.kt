package com.radioshuddhodhan.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.core.language.AppStrings
import com.radioshuddhodhan.app.data.LanguagePref
import com.radioshuddhodhan.app.data.ThemeMode
import com.radioshuddhodhan.app.data.db.UserEntity
import com.radioshuddhodhan.app.data.remote.AppConfigDto
import com.radioshuddhodhan.app.sync.SyncManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

/** Convenience alias for the app-wide UI state snapshot. */
typealias AppStateHolder = AppViewModel.AppState

/**
 * Root view model: language, theme, remote config, connectivity, current user
 * and sync status. Shared by every screen through CompositionLocals.
 */
class AppViewModel(private val app: RadioApp) : ViewModel() {

    data class AppState(
        val strings: AppStrings = AppStrings(nepali = false),
        val langPref: LanguagePref = LanguagePref.SYSTEM,
        val themeMode: ThemeMode = ThemeMode.SYSTEM,
        val config: AppConfigDto = AppConfigDto(),
        val isOnline: Boolean = true,
        val isFirstRun: Boolean = true,
        val syncStatus: SyncManager.SyncStatus = SyncManager.SyncStatus.Idle,
        val user: UserEntity? = null
    ) {
        val isNepali: Boolean get() = strings.nepali
    }

    val state: StateFlow<AppState> = combine(
        app.settings.languagePref,
        app.settings.themeMode,
        app.configRepository.config,
        app.connectivity.online,
        app.settings.firstRunDone,
        app.syncManager.status,
        app.authRepository.currentUser
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        AppState(
            strings = AppStrings(resolveNepali(values[0] as LanguagePref)),
            langPref = values[0] as LanguagePref,
            themeMode = values[1] as ThemeMode,
            config = values[2] as AppConfigDto,
            isOnline = values[3] as Boolean,
            isFirstRun = !(values[4] as Boolean),
            syncStatus = values[5] as SyncManager.SyncStatus,
            user = values[6] as UserEntity?
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppState())

    private fun resolveNepali(pref: LanguagePref): Boolean = when (pref) {
        LanguagePref.NEPALI -> true
        LanguagePref.ENGLISH -> false
        LanguagePref.SYSTEM ->
            Locale.getDefault().language.equals("ne", ignoreCase = true)
    }

    fun setLanguage(pref: LanguagePref) {
        viewModelScope.launch { app.settings.setLanguagePref(pref) }
    }

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch { app.settings.setThemeMode(mode) }
    }

    fun markFirstRunDone() {
        viewModelScope.launch { app.settings.setFirstRunDone() }
    }

    fun syncNow() {
        viewModelScope.launch { app.syncManager.syncNow() }
    }

    fun continueAsGuest(onDone: () -> Unit) {
        viewModelScope.launch {
            app.authRepository.continueAsGuest()
            onDone()
        }
    }

    fun logout() {
        viewModelScope.launch { app.authRepository.logout() }
    }
}
