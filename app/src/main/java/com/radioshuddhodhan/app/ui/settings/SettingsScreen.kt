package com.radioshuddhodhan.app.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.core.util.Format
import com.radioshuddhodhan.app.data.LanguagePref
import com.radioshuddhodhan.app.data.NotificationPrefs
import com.radioshuddhodhan.app.data.ThemeMode
import com.radioshuddhodhan.app.sync.SyncManager
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private class SettingsViewModel(private val app: RadioApp) : ViewModel() {

    data class SettingsState(
        val backendUrl: String = "",
        val notificationPrefs: NotificationPrefs = NotificationPrefs(),
        val autoReconnect: Boolean = true,
        val lastSync: Long = 0,
        val syncStatus: SyncManager.SyncStatus = SyncManager.SyncStatus.Idle
    )

    private val backendUrlField = MutableStateFlow("")

    val state = kotlinx.coroutines.flow.combine(
        app.settings.backendUrl,
        app.settings.notificationPrefs,
        app.settings.autoReconnect,
        app.settings.lastSync,
        app.syncManager.status
    ) { url, prefs, auto, sync, status ->
        SettingsState(
            backendUrl = url,
            notificationPrefs = prefs,
            autoReconnect = auto,
            lastSync = sync,
            syncStatus = status
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsState())

    init {
        viewModelScope.launch {
            app.settings.backendUrl.collect { backendUrlField.value = it }
        }
    }

    fun updateUrlField(value: String) { backendUrlField.value = value }
    fun urlField(): String = backendUrlField.value

    fun saveBackendUrl(url: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val normalized = url.trim()
            val valid = normalized.isBlank() || normalized.startsWith("http://") || normalized.startsWith("https://")
            if (valid) {
                app.settings.setBackendUrl(normalized)
                app.syncManager.syncNow()
            }
            onResult(valid)
        }
    }

    fun setNotificationPrefs(prefs: NotificationPrefs) {
        viewModelScope.launch {
            app.settings.setNotificationPrefs(prefs)
            com.radioshuddhodhan.app.notifications.PushRegistrar.registerIfAvailable(
                app, app.appScope, app.settings
            )
        }
    }

    fun setAutoReconnect(value: Boolean) {
        viewModelScope.launch { app.settings.setAutoReconnect(value) }
    }

    fun setLanguage(pref: LanguagePref) {
        viewModelScope.launch { app.settings.setLanguagePref(pref) }
    }

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch { app.settings.setThemeMode(mode) }
    }

    fun syncNow() {
        viewModelScope.launch { app.syncManager.syncNow() }
    }
}

/**
 * Settings: language (Nepali/English), theme (light/dark/system),
 * notification preferences, audio (auto-reconnect), account links, privacy,
 * terms, help, about, logout and the backend server URL (advanced).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenAbout: () -> Unit
) {
    val L = LocalAppStrings.current
    val app = com.radioshuddhodhan.app.ui.LocalAppContainer.current
    val viewModel: SettingsViewModel = appViewModel { SettingsViewModel(it) }
    val appViewModel: AppViewModel = appViewModel { AppViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState by appViewModel.state.collectAsStateWithLifecycle()

    var urlInput by rememberSaveable(state.backendUrl) { mutableStateOf(state.backendUrl) }
    var urlError by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(L.settings, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = L.back)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SettingsCard(icon = { Icon(Icons.Filled.DarkMode, null, tint = MaterialTheme.colorScheme.primary) }, title = L.appearance) {
                Text(L.languageSetting, style = MaterialTheme.typography.labelLarge)
                RadioRow(L.nepali, appState.langPref == LanguagePref.NEPALI) {
                    viewModel.setLanguage(LanguagePref.NEPALI)
                }
                RadioRow(L.english, appState.langPref == LanguagePref.ENGLISH) {
                    viewModel.setLanguage(LanguagePref.ENGLISH)
                }
                Spacer(Modifier.height(8.dp))
                Text(L.themeMode, style = MaterialTheme.typography.labelLarge)
                RadioRow(L.systemTheme, appState.themeMode == ThemeMode.SYSTEM) {
                    viewModel.setTheme(ThemeMode.SYSTEM)
                }
                RadioRow(L.lightTheme, appState.themeMode == ThemeMode.LIGHT) {
                    viewModel.setTheme(ThemeMode.LIGHT)
                }
                RadioRow(L.darkTheme, appState.themeMode == ThemeMode.DARK) {
                    viewModel.setTheme(ThemeMode.DARK)
                }
            }

            Spacer(Modifier.height(12.dp))

            SettingsCard(icon = { Icon(Icons.Filled.Notifications, null, tint = MaterialTheme.colorScheme.primary) }, title = L.notificationsSettings) {
                SwitchRow(L.notifBreaking, state.notificationPrefs.breaking) { checked ->
                    viewModel.setNotificationPrefs(state.notificationPrefs.copy(breaking = checked))
                }
                SwitchRow(L.notifNews, state.notificationPrefs.news) { checked ->
                    viewModel.setNotificationPrefs(state.notificationPrefs.copy(news = checked))
                }
                SwitchRow(L.notifEvents, state.notificationPrefs.events) { checked ->
                    viewModel.setNotificationPrefs(state.notificationPrefs.copy(events = checked))
                }
                SwitchRow(L.notifAnnouncements, state.notificationPrefs.announcements) { checked ->
                    viewModel.setNotificationPrefs(state.notificationPrefs.copy(announcements = checked))
                }
                Text(
                    text = L.notifHint,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            SettingsCard(icon = { Icon(Icons.Filled.VolumeUp, null, tint = MaterialTheme.colorScheme.primary) }, title = L.audioSettings) {
                SwitchRow(L.autoReconnect, state.autoReconnect) { checked ->
                    viewModel.setAutoReconnect(checked)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Advanced: backend server
            SettingsCard(icon = { Icon(Icons.Filled.Storage, null, tint = MaterialTheme.colorScheme.primary) }, title = L.advanced + " • " + L.backendServer) {
                Text(L.backendHint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it; urlError = false },
                    label = { Text(L.backendUrl) },
                    singleLine = true,
                    isError = urlError,
                    supportingText = if (urlError) { { Text(L.invalidUrl) } } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    viewModel.saveBackendUrl(urlInput) { ok -> urlError = !ok }
                }) {
                    Text(L.saveUrl)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = when (val s = state.syncStatus) {
                        is SyncManager.SyncStatus.DemoMode -> L.serverNotSet
                        is SyncManager.SyncStatus.Success -> L.serverConnected + " • " + L.lastSync + ": " + Format.dateTime(s.timestamp)
                        else -> if (state.backendUrl.isBlank()) L.serverNotSet
                        else L.lastSync + ": " + (if (state.lastSync == 0L) L.never else Format.dateTime(state.lastSync))
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { viewModel.syncNow() }) {
                    Icon(Icons.Filled.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(L.refresh)
                }
            }

            Spacer(Modifier.height(12.dp))

            SettingsCard(icon = { Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.primary) }, title = L.aboutApp) {
                TextButton(onClick = onOpenAbout) { Text(L.about) }
                TextButton(onClick = { /* privacy policy configurable via backend */ }) { Text(L.privacyPolicy) }
                TextButton(onClick = { /* terms configurable via backend */ }) { Text(L.termsOfService) }
                TextButton(onClick = { app?.let { ctx ->
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/")))
                } }) { Text(L.help) }
                Text(
                    text = "${L.versionInfo}: 1.0.1 (10001)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsCard(
    icon: @Composable () -> Unit,
    title: String,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(Modifier.width(10.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(vertical = 2.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
