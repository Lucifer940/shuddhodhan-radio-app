package com.radioshuddhodhan.app.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.R
import com.radioshuddhodhan.app.core.AppInfo
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.core.util.ExternalApps
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.components.brandGradient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

private class AboutViewModel(app: RadioApp) : ViewModel() {
    val config: StateFlow<com.radioshuddhodhan.app.data.remote.AppConfigDto> = app.configRepository.config
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), com.radioshuddhodhan.app.data.remote.AppConfigDto())
}

/**
 * About screen: app identity (Radio Shuddhodhan, v1.0.1, created by
 * Umesh Tharu), configurable about text and contact links from the server.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    onOpenSocial: () -> Unit
) {
    val L = LocalAppStrings.current
    val viewModel: AboutViewModel = appViewModel { AboutViewModel(it) }
    val config by viewModel.config.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(L.about, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        Color.Transparent
                    )))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.app_logo),
                        contentDescription = AppInfo.APP_NAME,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = AppInfo.APP_NAME,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${L.versionInfo}: v${AppInfo.VERSION}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${L.createdBy}: ${AppInfo.CREATOR}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = if (L.nepali) config.aboutTextNe.ifBlank { L.aboutTextDefault }
                    else config.aboutText.ifBlank { L.aboutTextDefault },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // Configurable contact info
                if (config.contactWebsite.isNotBlank() || config.contactPhone.isNotBlank() ||
                    config.contactEmail.isNotBlank()
                ) {
                    Text(
                        text = L.contactInfo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    if (config.contactPhone.isNotBlank()) {
                        ContactRow(Icons.Filled.Call, config.contactPhone) {
                            ExternalApps.dial(context, config.contactPhone)
                        }
                    }
                    if (config.contactEmail.isNotBlank()) {
                        ContactRow(Icons.Filled.Email, config.contactEmail) {
ExternalApps.email(context, config.contactEmail)
                        }
                    }
                    if (config.contactWebsite.isNotBlank()) {
                        ContactRow(Icons.Filled.Language, config.contactWebsite) {
                            ExternalApps.openUrl(context, config.contactWebsite)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                OutlinedButton(onClick = onOpenSocial, modifier = Modifier.fillMaxWidth()) {
                    Text(L.followUs + " • " + L.social)
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = L.openSourceNotice,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
