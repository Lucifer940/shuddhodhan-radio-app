package com.radioshuddhodhan.app.ui.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.core.util.Format
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.components.EmptyState
import kotlinx.coroutines.launch

private class NotificationsViewModel(private val app: RadioApp) : ViewModel() {
    val notifications: kotlinx.coroutines.flow.StateFlow<List<com.radioshuddhodhan.app.data.db.NotificationItemEntity>> =
        app.contentRepository.observeNotifications()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun markAllRead() {
        viewModelScope.launch { app.contentRepository.markNotificationsRead() }
    }

    fun clearAll() {
        viewModelScope.launch { app.contentRepository.clearNotifications() }
    }
}

/**
 * In-app notification centre: history of breaking news, announcements,
 * events and general messages (delivered via backend push when configured).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val L = LocalAppStrings.current
    val viewModel: NotificationsViewModel = appViewModel { NotificationsViewModel(it) }
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(L.notifications, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = L.back)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.markAllRead() }) {
                        Icon(Icons.Filled.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(L.markAllRead)
                    }
                    IconButton(onClick = { viewModel.clearAll() }) {
                        Icon(Icons.Filled.Delete, contentDescription = L.clearAll)
                    }
                }
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                EmptyState(title = L.noNotifications)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!item.isRead)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            val icon = when (item.type) {
                                "breaking" -> Icons.Filled.Bolt
                                "announcement" -> Icons.Filled.Campaign
                                "event" -> Icons.Filled.Event
                                else -> Icons.Filled.Notifications
                            }
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = if (item.type == "breaking")
                                    com.radioshuddhodhan.app.ui.theme.LiveRed
                                else MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (item.isRead) FontWeight.Medium else FontWeight.Bold
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = item.body,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = Format.relativeTime(item.receivedAt, L.nepali),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
