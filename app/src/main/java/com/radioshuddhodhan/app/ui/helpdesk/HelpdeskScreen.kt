package com.radioshuddhodhan.app.ui.helpdesk

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.core.util.Format
import com.radioshuddhodhan.app.data.db.HelpdeskTicketEntity
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private class HelpdeskViewModel(private val app: RadioApp) : ViewModel() {

    data class HelpdeskState(
        val sending: Boolean = false,
        val sent: Boolean = false,
        val failed: Boolean = false,
        val myTickets: List<HelpdeskTicketEntity> = emptyList(),
        val config: com.radioshuddhodhan.app.data.remote.AppConfigDto =
            com.radioshuddhodhan.app.data.remote.AppConfigDto(),
        /** Contact used to look up the user's own tickets. */
        val contactKey: String = ""
    )

    private val sending = MutableStateFlow(false)
    private val sent = MutableStateFlow(false)
    private val failed = MutableStateFlow(false)
    private val contactFilter = MutableStateFlow("")

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val state: StateFlow<HelpdeskState> = kotlinx.coroutines.flow.combine(
        sending, sent, failed, contactFilter, app.configRepository.config
    ) { s, se, f, contact, config ->
        HelpdeskState(sending = s, sent = se, failed = f, config = config, contactKey = contact)
    }.flatMapLatest { partial ->
        app.helpdeskRepository.observeTicketsForContact(partial.contactKey)
            .map { tickets -> partial.copy(myTickets = tickets) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HelpdeskState())

    fun submit(name: String, contact: String, category: String, message: String) {
        viewModelScope.launch {
            sending.value = true
            sent.value = false
            failed.value = false
            contactFilter.value = contact.trim()
            val ok = app.helpdeskRepository.submit(name.trim(), contact.trim(), category, message.trim())
            sending.value = false
            sent.value = ok
            failed.value = !ok
        }
    }

    fun consumeSent() {
        sent.value = false
        failed.value = false
    }
}

/**
 * Helpdesk: contact form (name, contact, category, message), direct contact
 * channels (phone/email/WhatsApp/website from remote config) and the user's
 * own requests with admin replies.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpdeskScreen(onBack: () -> Unit) {
    val L = LocalAppStrings.current
    val viewModel: HelpdeskViewModel = appViewModel { HelpdeskViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var contact by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(L.helpCategoryGeneral) }

    val categories = listOf(
        L.helpCategoryGeneral, L.helpCategoryTechnical, L.helpCategoryFeedback,
        L.helpCategoryNewsTip, L.helpCategoryAds, L.helpCategoryOther
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(L.helpdesk, fontWeight = FontWeight.Bold) },
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
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(Icons.Filled.SupportAgent, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text(L.helpdeskHint, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(L.yourName) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text(L.emailOrPhone) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            Text(L.category, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.take(3).forEach { c ->
                    FilterChip(
                        selected = category == c,
                        onClick = { category = c },
                        label = { Text(c) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                categories.drop(3).forEach { c ->
                    FilterChip(
                        selected = category == c,
                        onClick = { category = c },
                        label = { Text(c) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text(L.message) },
                minLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = state.sent || state.failed) {
                Text(
                    text = if (state.sent) L.messageSent else L.messageSendFailed,
                    color = if (state.sent) com.radioshuddhodhan.app.ui.theme.SuccessGreen
                    else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (name.isNotBlank() && contact.isNotBlank() && message.isNotBlank()) {
                        viewModel.submit(name, contact, category, message)
                    }
                },
                enabled = !state.sending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.sending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(L.sending)
                } else {
                    Icon(Icons.Filled.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(L.sendMessage)
                }
            }

            // Direct contact channels (admin-configurable)
            val cfg = state.config
            if (cfg.contactPhone.isNotBlank() || cfg.contactEmail.isNotBlank() ||
                cfg.contactWhatsapp.isNotBlank() || cfg.contactWebsite.isNotBlank()
            ) {
                Spacer(Modifier.height(20.dp))
                Text(L.directContact, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row {
                    if (cfg.contactPhone.isNotBlank()) {
                        ContactAction(Icons.Filled.Call, L.phone) {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cfg.contactPhone)))
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    if (cfg.contactEmail.isNotBlank()) {
                        ContactAction(Icons.Filled.Email, L.email) {
                            context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + cfg.contactEmail)))
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    if (cfg.contactWhatsapp.isNotBlank()) {
                        ContactAction(Icons.Filled.Send, L.whatsapp) {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + cfg.contactWhatsapp))
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    if (cfg.contactWebsite.isNotBlank()) {
                        ContactAction(Icons.Filled.Language, L.website) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(cfg.contactWebsite)))
                        }
                    }
                }
            }

            // My requests
            if (state.myTickets.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text(L.myRequests, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                state.myTickets.forEach { ticket ->
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ticket.category,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.weight(1f))
                                StatusBadge(status = ticket.status, L)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = ticket.message,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2
                            )
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
                                    Column(Modifier.padding(8.dp)) {
                                        Text(
                                            text = L.reply + " • " + L.admin,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = ticket.reply!!,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.small,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun StatusBadge(status: String, L: com.radioshuddhodhan.app.core.language.AppStrings) {
    val (label, color) = when (status) {
        "resolved" -> L.statusResolved to com.radioshuddhodhan.app.ui.theme.SuccessGreen
        "pending" -> L.statusPending to MaterialTheme.colorScheme.tertiary
        else -> L.statusOpen to MaterialTheme.colorScheme.primary
    }
    Surface(color = color.copy(alpha = 0.14f), shape = MaterialTheme.shapes.small) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
