package com.radioshuddhodhan.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radioshuddhodhan.app.core.util.Format
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel

/**
 * Admin announcements manager — shown on the user home screen instantly.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnnouncementsScreen() {
    val L = LocalAppStrings.current
    val viewModel: AdminDataViewModel = appViewModel { AdminDataViewModel(it) }
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()

    var showEditor by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var deleteId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(L.manageAnnouncements, fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showEditor = true }) {
                Icon(Icons.Filled.Add, contentDescription = L.addAnnouncement)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(announcements, key = { it.id }) { announcement ->
                Card(
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = announcement.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = announcement.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Format.relativeTime(announcement.createdAt, L.nepali),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { deleteId = announcement.id }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = L.delete,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditor) {
        AlertDialog(
            onDismissRequest = { showEditor = false },
            title = { Text(L.addAnnouncement) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title, onValueChange = { title = it },
                        label = { Text(L.announcementTitle) },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = message, onValueChange = { message = it },
                        label = { Text(L.announcementText) },
                        minLines = 3, modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        viewModel.saveAnnouncement(null, title, message)
                        title = ""
                        message = ""
                        showEditor = false
                    }
                }) { Text(L.save) }
            },
            dismissButton = { TextButton(onClick = { showEditor = false }) { Text(L.cancel) } }
        )
    }

    if (deleteId != null) {
        AlertDialog(
            onDismissRequest = { deleteId = null },
            title = { Text(L.deleteConfirm) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAnnouncement(deleteId!!)
                    deleteId = null
                }) { Text(L.delete) }
            },
            dismissButton = { TextButton(onClick = { deleteId = null }) { Text(L.cancel) } }
        )
    }
}
