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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.radioshuddhodhan.app.data.db.HelpdeskTicketEntity
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.theme.SuccessGreen

/**
 * Admin helpdesk inbox: read user requests, reply and mark resolved.
 * Replies appear on the user's helpdesk screen instantly (same database /
 * backend pipeline).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHelpdeskScreen() {
    val L = LocalAppStrings.current
    val viewModel: AdminDataViewModel = appViewModel { AdminDataViewModel(it) }
    val tickets by viewModel.helpdeskTickets.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text(L.helpdeskInbox, fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        if (tickets.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.SupportAgent,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(10.dp))
                Text(L.noTickets, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tickets, key = { it.id }) { ticket ->
                    TicketCard(ticket = ticket, onReply = { reply ->
                        viewModel.replyTicket(ticket, reply)
                    })
                }
            }
        }
    }
}

@Composable
private fun TicketCard(ticket: HelpdeskTicketEntity, onReply: (String) -> Unit) {
    val L = LocalAppStrings.current
    var replyText by remember { mutableStateOf("") }

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = ticket.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = when (ticket.status) {
                        "resolved" -> SuccessGreen.copy(alpha = 0.15f)
                        "pending" -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = when (ticket.status) {
                            "resolved" -> L.statusResolved
                            "pending" -> L.statusPending
                            else -> L.statusOpen
                        },
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = ticket.contact + " • " + ticket.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(text = ticket.message, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = Format.relativeTime(ticket.createdAt, L.nepali),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (ticket.reply != null) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = L.reply + ": " + ticket.reply!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text(L.replyHint) },
                        minLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                onReply(replyText)
                                replyText = ""
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = L.send,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { onReply("✓") }) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = L.markResolved,
                            tint = SuccessGreen
                        )
                    }
                }
            }
        }
    }
}
