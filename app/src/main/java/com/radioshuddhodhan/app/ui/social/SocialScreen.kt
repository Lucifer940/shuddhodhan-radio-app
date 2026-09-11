package com.radioshuddhodhan.app.ui.social

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.data.db.SocialLinkEntity
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.components.EmptyState
import com.radioshuddhodhan.app.ui.components.brandGradient
import com.radioshuddhodhan.app.ui.theme.LiveRed
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

private class SocialViewModel(app: RadioApp) : ViewModel() {
    val links = app.contentRepository.observeSocialLinks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

/**
 * Social / news feed section: admin-configured Facebook page, Facebook Live,
 * YouTube, website and other social links. Links open safely in external
 * apps — the app never scrapes or bypasses platform restrictions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(onBack: () -> Unit) {
    val L = LocalAppStrings.current
    val viewModel: SocialViewModel = appViewModel { SocialViewModel(it) }
    val links by viewModel.links.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val facebookLive = links.find { it.platform == "facebook_live" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(L.socialFeed, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = L.back)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Facebook Live hero
            if (facebookLive != null && facebookLive.url.isNotBlank()) {
                item {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(brandGradient())
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Videocam,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            text = L.facebookLive,
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = L.externalLinkNotice,
                                    color = Color.White.copy(alpha = 0.85f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(Modifier.height(14.dp))
                                androidx.compose.material3.Button(onClick = {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(facebookLive.url))
                                    )
                                }) {
                                    Icon(Icons.Filled.Facebook, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(L.openInBrowser)
                                }
                            }
                        }
                    }
                }
            }

            if (links.isEmpty()) {
                item { EmptyState(title = L.noSocialConfigured) }
            } else {
                items(links.filter { it.platform != "facebook_live" }, key = { it.id }) { link ->
                    SocialLinkCard(link = link) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link.url)))
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialLinkCard(link: SocialLinkEntity, onClick: () -> Unit) {
    val icon: ImageVector = when (link.platform) {
        "facebook" -> Icons.Filled.Facebook
        "youtube" -> Icons.Filled.PlayCircle
        "website" -> Icons.Filled.Language
        "whatsapp" -> Icons.Filled.Favorite
        "twitter" -> Icons.Filled.RssFeed
        else -> Icons.AutoMirrored.Filled.OpenInNew
    }
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = link.label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = link.label.ifBlank { link.platform },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = link.url.removePrefix("https://").removePrefix("http://"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
