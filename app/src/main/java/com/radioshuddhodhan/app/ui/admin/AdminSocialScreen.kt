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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radioshuddhodhan.app.data.db.SocialLinkEntity
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel

/**
 * Admin social links manager: Facebook page, Facebook Live, YouTube, website
 * and other platforms shown in the Social screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSocialScreen() {
    val L = LocalAppStrings.current
    val viewModel: AdminDataViewModel = appViewModel { AdminDataViewModel(it) }
    val links by viewModel.socialLinks.collectAsStateWithLifecycle()

    var showEditor by remember { mutableStateOf(false) }
    var platform by remember { mutableStateOf("facebook") }
    var label by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var order by remember { mutableIntStateOf(10) }
    var deleteId by remember { mutableStateOf<String?>(null) }

    val platforms = listOf(
        "facebook" to L.facebook,
        "facebook_live" to L.facebookLive,
        "youtube" to L.youtube,
        "website" to L.website,
        "whatsapp" to L.whatsapp,
        "twitter" to L.twitter,
        "instagram" to L.instagram,
        "telegram" to L.telegram
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(L.manageSocial, fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showEditor = true }) {
                Icon(Icons.Filled.Add, contentDescription = L.addLink)
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
            items(links, key = { it.id }) { link ->
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
                                text = platforms.firstOrNull { it.first == link.platform }?.second ?: link.platform,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = link.url,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { deleteId = link.id }) {
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
            title = { Text(L.addLink) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(L.platform, style = MaterialTheme.typography.labelLarge)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        platforms.take(4).forEach { (key, name) ->
                            androidx.compose.material3.FilterChip(
                                selected = platform == key,
                                onClick = { platform = key },
                                label = { Text(name, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        platforms.drop(4).forEach { (key, name) ->
                            androidx.compose.material3.FilterChip(
                                selected = platform == key,
                                onClick = { platform = key },
                                label = { Text(name, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = label, onValueChange = { label = it },
                        label = { Text(L.label) },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = url, onValueChange = { url = it },
                        label = { Text(L.urlLabel) },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = order.toString(), onValueChange = { order = it.toIntOrNull() ?: 10 },
                        label = { Text(L.stationOrder) },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (url.isNotBlank()) {
                        viewModel.saveSocialLink(
                            null, platform,
                            label.ifBlank { platforms.firstOrNull { it.first == platform }?.second ?: platform },
                            url, order
                        )
                        showEditor = false
                        label = ""
                        url = ""
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
                    viewModel.deleteSocialLink(deleteId!!)
                    deleteId = null
                }) { Text(L.delete) }
            },
            dismissButton = { TextButton(onClick = { deleteId = null }) { Text(L.cancel) } }
        )
    }
}
