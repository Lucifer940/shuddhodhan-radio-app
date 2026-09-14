package com.radioshuddhodhan.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import kotlinx.coroutines.launch

/**
 * AI News Assistant — SECURE BACKEND INTEGRATION POINT.
 *
 * The OpenAI API key is NEVER stored in the app (that would be a serious
 * security hole). Generation is delegated to the backend endpoint
 * POST /api/v1/admin/ai/generate which proxies OpenAI server-side.
 *
 * In demo mode (no backend) the screen explains this clearly instead of
 * faking a working integration. AI output can only be saved as an
 * UNPUBLISHED draft — administrator approval is always required before
 * publication.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAiScreen() {
    val L = LocalAppStrings.current
    val viewModel: AdminDataViewModel = appViewModel { AdminDataViewModel(it) }
    val toast by viewModel.toast.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var action by remember { mutableStateOf("headline") }

    val actions = listOf(
        "headline" to L.aiActionHeadline,
        "summarize" to L.aiActionSummarize,
        "rewrite" to L.aiActionRewrite,
        "translate" to L.aiActionTranslate,
        "script" to L.aiActionScript,
        "draft" to L.aiActionDraft
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(L.aiAssistant, fontWeight = FontWeight.Bold) }) }
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
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = L.aiNeedsBackend,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                actions.take(3).forEach { (key, label) ->
                    FilterChip(
                        selected = action == key,
                        onClick = { action = key },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                actions.drop(3).forEach { (key, label) ->
                    FilterChip(
                        selected = action == key,
                        onClick = { action = key },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text(L.aiInputPlaceholder) },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        busy = true
                        failed = false
                        result = ""
                        scope.launch {
                            val outcome = viewModel.aiGenerate(action, input)
                            busy = false
                            outcome.fold(
                                onSuccess = { result = it },
                                onFailure = { failed = true }
                            )
                        }
                    }
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (busy) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(L.aiGenerate)
            }

            if (failed) {
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(L.aiNeedsBackend, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
                }
            }

            if (result.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                Text(L.aiResult, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card(
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = result,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(14.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = L.aiRequiresApproval,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        viewModel.saveAiDraft(
                            title = result.lineSequence().firstOrNull()?.take(90) ?: "AI Draft",
                            content = result
                        )
                        result = ""
                        input = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(L.aiSaveAsDraft)
                }
            }

            if (toast == "draftSaved") {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = L.aiDraftSaved,
                    color = com.radioshuddhodhan.app.ui.theme.SuccessGreen,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
