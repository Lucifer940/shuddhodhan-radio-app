package com.radioshuddhodhan.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.radioshuddhodhan.app.core.language.AppStrings

/** Shimmering skeleton placeholder used while content loads. */
@Composable
fun SkeletonBox(modifier: Modifier = Modifier, cornerRadius: Int = 12) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    Box(
        modifier = modifier
            .alpha(alpha)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(cornerRadius.dp)
            )
    )
}

/** A simple skeleton list for loading states. */
@Composable
fun SkeletonList(itemCount: Int = 4, itemHeight: Int = 96) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) {
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight.dp)
            )
        }
    }
}

/** Section header with optional "see all" action. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onSeeAll: (() -> Unit)? = null,
    seeAllLabel: String = ""
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (onSeeAll != null && seeAllLabel.isNotBlank()) {
            Text(
                text = seeAllLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

/** Empty state placeholder. */
@Composable
fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📻",
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Error state with a retry button. */
@Composable
fun ErrorState(
    title: String,
    L: AppStrings,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.CloudOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (onRetry != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(L.retry)
            }
        }
    }
}

/** Persistent offline banner shown while there is no connection. */
@Composable
fun OfflineBanner(visible: Boolean, text: String, modifier: Modifier = Modifier) {
    AnimatedVisibility(visible = visible, modifier = modifier) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudOff,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(text = text, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

/**
 * Animated equalizer bars indicating live playback. Decorative visual
 * indicator (not a real audio analyser) — intentionally lightweight so it
 * never impacts low-end devices.
 */
@Composable
fun AnimatedEqualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    barCount: Int = 4
) {
    val transition = rememberInfiniteTransition(label = "eq")
    val alphas = List(barCount) { index ->
        transition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 350 + index * 130,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "eq$index"
        )
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        alphas.forEach { alpha ->
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 18.dp)
                    .background(
                        barColor.copy(alpha = if (isPlaying) alpha.value else 0.3f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

/** Branded "LIVE" pill. */
@Composable
fun LiveBadge(modifier: Modifier = Modifier, label: String = "LIVE") {
    Surface(
        color = com.radioshuddhodhan.app.ui.theme.LiveRed,
        contentColor = Color.White,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/** Horizontal brand gradient used behind hero cards. */
fun brandGradient(): Brush = Brush.horizontalGradient(
    listOf(
        com.radioshuddhodhan.app.ui.theme.BrandCrimson,
        com.radioshuddhodhan.app.ui.theme.BrandNavy
    )
)
