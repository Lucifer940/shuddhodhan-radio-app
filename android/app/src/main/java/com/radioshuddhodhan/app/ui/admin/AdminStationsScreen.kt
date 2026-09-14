package com.radioshuddhodhan.app.ui.admin

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radioshuddhodhan.app.data.db.StationEntity
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.theme.BrandGold

/**
 * Admin stations manager: add/edit/delete stations, change logo, name,
 * description, stream URL, enable/disable, reorder and feature.
 * Changing the stream URL takes effect for users immediately — no APK update.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStationsScreen() {
    val L = LocalAppStrings.current
    val viewModel: AdminDataViewModel = appViewModel { AdminDataViewModel(it) }
    val stations by viewModel.stations.collectAsStateWithLifecycle()

    var editorTarget by remember { mutableStateOf<StationEntity?>(null) }
    var isNew by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<StationEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(L.manageStations, fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editorTarget = null
                isNew = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = L.addStation)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(stations, key = { it.id }) { station ->
                Card(
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = station.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (station.isFeatured) {
                                        Spacer(Modifier.width(4.dp))
                                        Icon(
                                            Icons.Filled.Star, contentDescription = null,
                                            tint = BrandGold, modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = station.streamUrl.ifBlank { "⚠ " + L.streamNotConfigured },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (station.streamUrl.isBlank())
                                        MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(onClick = { editorTarget = station; isNew = false }) {
                                Icon(Icons.Filled.Edit, contentDescription = L.edit, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { deleteTarget = station }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = L.delete,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = L.enabled + " • " + L.stationOrder + ": " + station.sortOrder,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = station.isEnabled,
                                onCheckedChange = { enabled ->
                                    viewModel.saveStation(
                                        station.id, station.name, station.nameNe, station.description,
                                        station.streamUrl, station.logoUrl ?: "",
                                        enabled, station.isFeatured, station.sortOrder
                                    )
                                },
                                modifier = Modifier.height(24.dp)
                            )
                            IconButton(onClick = {
                                viewModel.saveStation(
                                    station.id, station.name, station.nameNe, station.description,
                                    station.streamUrl, station.logoUrl ?: "",
                                    station.isEnabled, !station.isFeatured, station.sortOrder
                                )
                            }) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = L.markFeatured,
                                    tint = if (station.isFeatured) BrandGold
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (isNew || editorTarget != null) {
        StationEditorDialog(
            station = editorTarget,
            onSave = { id, name, nameNe, desc, url, logo, enabled, featured, order ->
                viewModel.saveStation(id, name, nameNe, desc, url, logo, enabled, featured, order)
                isNew = false
                editorTarget = null
            },
            onDismiss = { isNew = false; editorTarget = null }
        )
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(L.deleteStationConfirm) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteStation(deleteTarget!!.id)
                    deleteTarget = null
                }) { Text(L.delete) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text(L.cancel) } }
        )
    }
}

@Composable
private fun StationEditorDialog(
    station: StationEntity?,
    onSave: (
        id: String?, name: String, nameNe: String, description: String,
        streamUrl: String, logoUrl: String, isEnabled: Boolean,
        isFeatured: Boolean, sortOrder: Int
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val L = LocalAppStrings.current
    var name by remember { mutableStateOf(station?.name ?: "") }
    var nameNe by remember { mutableStateOf(station?.nameNe ?: "") }
    var description by remember { mutableStateOf(station?.description ?: "") }
    var streamUrl by remember { mutableStateOf(station?.streamUrl ?: "") }
    var logoUrl by remember { mutableStateOf(station?.logoUrl ?: "") }
    var enabled by remember { mutableStateOf(station?.isEnabled ?: true) }
    var featured by remember { mutableStateOf(station?.isFeatured ?: false) }
    var order by remember { mutableIntStateOf(station?.sortOrder ?: 99) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (station == null) L.addStation else L.edit) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text(L.stationName) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nameNe, onValueChange = { nameNe = it },
                    label = { Text(L.stationName + " (नेपाली)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text(L.stationDesc) },
                    minLines = 2, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = streamUrl, onValueChange = { streamUrl = it },
                    label = { Text(L.streamUrlLabel) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = logoUrl, onValueChange = { logoUrl = it },
                    label = { Text(L.logoUrlLabel) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = order.toString(), onValueChange = { order = it.toIntOrNull() ?: 99 },
                    label = { Text(L.stationOrder) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(L.enabled, modifier = Modifier.weight(1f))
                    Switch(checked = enabled, onCheckedChange = { enabled = it })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(L.markFeatured, modifier = Modifier.weight(1f))
                    Switch(checked = featured, onCheckedChange = { featured = it })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    onSave(
                        station?.id, name, nameNe, description, streamUrl, logoUrl,
                        enabled, featured, order
                    )
                }
            }) { Text(L.save) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(L.cancel) } }
    )
}
