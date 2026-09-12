package com.radioshuddhodhan.app.ui.player

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radioshuddhodhan.app.R
import com.radioshuddhodhan.app.ui.LocalAppContainer
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.components.AnimatedEqualizer
import com.radioshuddhodhan.app.ui.components.LiveBadge
import com.radioshuddhodhan.app.ui.theme.BrandCrimson
import com.radioshuddhodhan.app.ui.theme.BrandNavy
import com.radioshuddhodhan.app.notifications.NotificationHelper
import kotlin.math.roundToInt

/**
 * Full-screen live radio player: play/pause/stop, volume, buffering /
 * connection status, auto-reconnect, error states and stream-not-configured
 * guidance. Requests notification permission on first play (Android 13+).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(onBack: () -> Unit) {
    val L = LocalAppStrings.current
    val app = LocalAppContainer.current ?: return
    val playerState by app.playerManager.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    var volume by remember { mutableIntStateOf(app.playerManager.currentVolume()) }
    val maxVolume = remember { app.playerManager.maxVolume().coerceAtLeast(1) }

    // Live listener count from the backend (shown to everyone — admin and
    // listeners alike). Stays hidden in demo mode: without a server there is
    // no honest number, and we never invent one.
    var listenerCount by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(playerState.station?.id, playerState.isPlaying) {
        val stationId = playerState.station?.id ?: return@LaunchedEffect
        while (true) {
            listenerCount = app.contentRepository.fetchListenerCount(stationId)
                ?: break // demo mode / backend unreachable — stop polling
            kotlinx.coroutines.delay(30_000)
        }
    }

    val pulseScale by animateFloatAsState(
        targetValue = if (playerState.isPlaying) 1.06f else 1f,
        animationSpec = tween(600),
        label = "pulse"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(L.liveRadio, fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Album art / logo with live pulse
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(BrandCrimson, BrandNavy))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (playerState.isPlaying || playerState.isBuffering || playerState.isConnecting) {
                    LiveBadge(label = L.live)
                    Spacer(Modifier.width(8.dp))
                }
                AnimatedEqualizer(isPlaying = playerState.isPlaying)
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = playerState.station?.name ?: L.appName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))

            // Status line
            AnimatedContent(
                targetState = when {
                    playerState.hasError && playerState.errorMessage == "STREAM_NOT_CONFIGURED" ->
                        StatusLine(L.streamNotConfigured, true)
                    playerState.hasError && playerState.retrying -> StatusLine(L.reconnecting, false, busy = true)
                    playerState.hasError -> StatusLine(L.connectionFailed, true)
                    playerState.isConnecting -> StatusLine(L.pleaseWait, false, busy = true)
                    playerState.isBuffering -> StatusLine(L.pleaseWait, false, busy = true)
                    playerState.isPlaying -> StatusLine(L.connected, false)
                    else -> StatusLine(L.nowPlaying, false)
                },
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "status"
            ) { status ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (status.busy) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = status.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (status.isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (listenerCount != null) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Filled.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = L.listeningCount(listenerCount ?: 0),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Stream not configured guidance
            AnimatedVisibility(
                visible = playerState.hasError && playerState.errorMessage == "STREAM_NOT_CONFIGURED"
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = L.streamNotConfiguredHint,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            // Transport controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // Stop
                FilledIconButton(
                    onClick = { app.playerManager.stop() },
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = L.stop)
                }

                // Play / pause
                FilledIconButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= 33 &&
                            !NotificationHelper.hasNotificationPermission(context)
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        app.playerManager.togglePlayPause()
                    },
                    modifier = Modifier.size(84.dp)
                ) {
                    if (playerState.isConnecting || playerState.isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(34.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Filled.Pause
                            else Icons.Filled.PlayArrow,
                            contentDescription = if (playerState.isPlaying) L.pause else L.play,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Retry / reconnect
                FilledIconButton(
                    onClick = { app.playerManager.retry() },
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = L.retryConnection)
                }
            }

            Spacer(Modifier.height(28.dp))

            // Volume
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = L.volume,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = L.volume,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Slider(
                    value = volume.toFloat(),
                    onValueChange = {
                        volume = it.roundToInt().coerceIn(0, maxVolume)
                        app.playerManager.setVolume(volume)
                    },
                    valueRange = 0f..maxVolume.toFloat(),
                    steps = maxVolume - 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = L.backgroundPlayHint,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            if (playerState.hasError && !playerState.retrying &&
                playerState.errorMessage != "STREAM_NOT_CONFIGURED"
            ) {
                OutlinedButton(onClick = { app.playerManager.retry() }) {
                    Icon(Icons.Filled.GraphicEq, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(L.retryConnection)
                }
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private data class StatusLine(val text: String, val isError: Boolean, val busy: Boolean = false)
