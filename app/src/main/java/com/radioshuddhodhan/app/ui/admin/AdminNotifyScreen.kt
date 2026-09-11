package com.radioshuddhodhan.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel

/**
 * Admin notification sender: breaking news, news, events, announcements.
 * Demo mode delivers on this device; backend mode fans out to all users
 * through the server's push service.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotifyScreen() {
    val L = LocalAppStrings.current
    val viewModel: AdminDataViewModel = appViewModel { AdminDataViewModel(it) }
    val toast by viewModel.toast.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("breaking") }

    val types = listOf(
        "breaking" to L.notifTypeBreaking,
        "news" to L.notifTypeNews,
        "event" to L.notifTypeEvent,
        "announcement" to L.notifTypeAnnouncement
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(L.sendNotification, fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Filled.NotificationsActive, contentDescription = null)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = L.notifSentLocal,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(L.notifTypeLabel, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.take(2).forEach { (key, label) ->
                    FilterChip(selected = type == key, onClick = { type = key }, label = { Text(label) })
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.drop(2).forEach { (key, label) ->
                    FilterChip(selected = type == key, onClick = { type = key }, label = { Text(label) })
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(L.notifTitleLabel) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text(L.notifBodyLabel) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (title.isNotBlank() && body.isNotBlank()) {
                        viewModel.sendNotification(title, body, type)
                        title = ""
                        body = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(L.sendNow)
            }

            if (toast == "sent") {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = L.notifSentLocal,
                    color = com.radioshuddhodhan.app.ui.theme.SuccessGreen,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
