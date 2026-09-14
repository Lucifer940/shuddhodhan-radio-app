package com.radioshuddhodhan.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.radioshuddhodhan.app.core.nepalidate.BsCalendar
import com.radioshuddhodhan.app.data.db.EventEntity
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel

/**
 * Admin calendar events manager.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventsScreen() {
    val L = LocalAppStrings.current
    val viewModel: AdminDataViewModel = appViewModel { AdminDataViewModel(it) }
    val events by viewModel.events.collectAsStateWithLifecycle()

    var editorTarget by remember { mutableStateOf<EventEntity?>(null) }
    var isNew by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<EventEntity?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(L.manageEvents, fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { isNew = true }) {
                Icon(Icons.Filled.Add, contentDescription = L.addEvent)
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
            items(events, key = { it.id }) { event ->
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
                                text = event.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = event.adDate + " • " + event.timeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { editorTarget = event }) {
                            Icon(Icons.Filled.Edit, contentDescription = L.edit, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { deleteTarget = event }) {
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

    if (isNew || editorTarget != null) {
        EventEditorDialog(
            event = editorTarget,
            onSave = { id, title, description, date, time, location ->
                viewModel.saveEvent(id, title, description, date, time, location)
                isNew = false
                editorTarget = null
            },
            onDismiss = { isNew = false; editorTarget = null }
        )
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(L.deleteConfirm) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEvent(deleteTarget!!.id)
                    deleteTarget = null
                }) { Text(L.delete) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text(L.cancel) } }
        )
    }
}

@Composable
private fun EventEditorDialog(
    event: EventEntity?,
    onSave: (id: String?, title: String, description: String, date: String, time: String, location: String) -> Unit,
    onDismiss: () -> Unit
) {
    val L = LocalAppStrings.current
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    // Default to today in NEPAL time (the calendar and event filters run on
    // Nepal dates; using the device timezone could be a day off).
    var date by remember { mutableStateOf(event?.adDate ?: BsCalendar.todayInNepal().toString()) }
    var time by remember { mutableStateOf(event?.timeLabel ?: "19:00") }
    var location by remember { mutableStateOf(event?.location ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (event == null) L.addEvent else L.edit) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text(L.eventTitle) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text(L.eventDesc) },
                    minLines = 2, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date, onValueChange = { date = it },
                    label = { Text(L.eventDate + " (AD yyyy-MM-dd)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = bsDatePreview(date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = time, onValueChange = { time = it },
                    label = { Text("Time") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location, onValueChange = { location = it },
                    label = { Text("Location") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        // The date must be a valid, in-range AD date (the calendar filters
        // events by ISO string comparison — a malformed date would silently
        // never show up anywhere). Computed inside the confirmButton lambda
        // so it revalidates as the text changes.
        confirmButton = {
            val dateValid = runCatching {
                BsCalendar.fromAd(java.time.LocalDate.parse(date))
            }.isSuccess
            TextButton(
                onClick = {
                    if (title.isNotBlank() && dateValid) {
                        onSave(event?.id, title, description, date, time, location)
                    }
                },
                enabled = title.isNotBlank() && dateValid
            ) { Text(L.save) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(L.cancel) } }
    )
}

private fun bsDatePreview(adIso: String): String {
    val ad = runCatching { java.time.LocalDate.parse(adIso) }.getOrNull() ?: return ""
    return runCatching {
        val bs = BsCalendar.fromAd(ad)
        "BS: ${bs.year}-${bs.month.toString().padStart(2, '0')}-${bs.day.toString().padStart(2, '0')}"
    }.getOrDefault("")
}
