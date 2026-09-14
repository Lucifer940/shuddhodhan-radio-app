package com.radioshuddhodhan.app.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.radioshuddhodhan.app.R
import com.radioshuddhodhan.app.core.nepalidate.BsCalendar
import com.radioshuddhodhan.app.core.util.Format
import com.radioshuddhodhan.app.data.db.AnnouncementEntity
import com.radioshuddhodhan.app.data.db.NewsEntity
import com.radioshuddhodhan.app.data.db.StationEntity
import com.radioshuddhodhan.app.ui.LocalAppContainer
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.components.AnimatedEqualizer
import com.radioshuddhodhan.app.ui.components.LiveBadge
import com.radioshuddhodhan.app.ui.components.NepalClockBar
import com.radioshuddhodhan.app.ui.components.SectionHeader
import com.radioshuddhodhan.app.ui.components.SkeletonList
import com.radioshuddhodhan.app.ui.components.brandGradient
import com.radioshuddhodhan.app.ui.theme.BrandGold

/**
 * Home screen: brand header, live Nepal clock (BS + AD), live radio hero,
 * latest news, posts, stations, announcements, upcoming events and quick
 * shortcuts (helpdesk, social, Facebook Live).
 */
@Composable
fun HomeScreen(
    onOpenPlayer: () -> Unit,
    onOpenNewsDetail: (String) -> Unit,
    onOpenPostDetail: (String) -> Unit,
    onOpenAllNews: () -> Unit,
    onOpenAllPosts: () -> Unit,
    onOpenStations: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenHelpdesk: () -> Unit,
    onOpenSocial: () -> Unit
) {
    val L = LocalAppStrings.current
    val app = LocalAppContainer.current ?: return
    val viewModel: HomeViewModel = appViewModel { HomeViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val playerState by app.playerManager.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
    ) {
        // ---------- Header ----------
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        Color.Transparent
                    )))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.app_logo),
                        contentDescription = L.appName,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = L.appName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = greeting(L),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                NepalClockBar(L = L)
            }
        }

        // ---------- Banner (admin-configurable) ----------
        item {
            val banner = if (L.nepali) {
                state.config.homeBannerTextNe.ifBlank { L.homeBannerDefault }
            } else {
                state.config.homeBannerText.ifBlank { L.homeBannerDefault }
            }
            if (banner.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(Icons.Filled.Campaign, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(banner, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // ---------- Live Radio hero ----------
        item {
            if (state.config.liveRadioEnabled) {
                LiveRadioHero(
                    stationName = state.config.primaryStationName.ifBlank {
                        L.appName
                    },
                    programTitle = if (L.nepali) {
                        state.config.currentProgramNe.ifBlank { L.noProgramScheduled }
                    } else {
                        state.config.currentProgram.ifBlank { L.noProgramScheduled }
                    },
                    isPlaying = playerState.isPlaying,
                    isBuffering = playerState.isBuffering || playerState.isConnecting,
                    onListen = {
                        val station = playerState.station
                            ?: featuredStation(state.stations, state.config)
                        if (station != null) {
                            app.playerManager.play(station)
                            onOpenPlayer()
                        } else {
                            onOpenStations()
                        }
                    }
                )
            }
        }

        // ---------- Breaking / announcements strip ----------
        if (state.news.any { it.isBreaking }) {
            item {
                BreakingStrip(
                    breaking = state.news.filter { it.isBreaking },
                    nepali = L.nepali,
                    onClick = { onOpenNewsDetail(it.id) }
                )
            }
        }
        if (state.announcements.isNotEmpty()) {
            item {
                AnnouncementsRow(
                    announcements = state.announcements,
                    nepali = L.nepali,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // ---------- Latest news ----------
        if (state.config.newsEnabled && state.news.isNotEmpty()) {
            item {
                SectionHeader(
                    title = L.latestNews,
                    onSeeAll = onOpenAllNews,
                    seeAllLabel = L.seeAll,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            item {
                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.news, key = { it.id }) { news ->
                        NewsCard(news, L.nepali, onClick = { onOpenNewsDetail(news.id) })
                    }
                }
            }
        }

        // ---------- Quick shortcuts ----------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ShortcutTile(
                    icon = { Icon(Icons.Filled.Event, null, tint = MaterialTheme.colorScheme.primary) },
                    label = L.calendar,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenCalendar
                )
                ShortcutTile(
                    icon = { Icon(Icons.Filled.Radio, null, tint = MaterialTheme.colorScheme.primary) },
                    label = L.radioStations,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenStations
                )
                ShortcutTile(
                    icon = { Icon(Icons.Filled.HelpCenter, null, tint = MaterialTheme.colorScheme.primary) },
                    label = L.helpdesk,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenHelpdesk
                )
                ShortcutTile(
                    icon = { Icon(Icons.Filled.Headphones, null, tint = MaterialTheme.colorScheme.primary) },
                    label = L.socialFeed,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenSocial
                )
            }
        }

        // ---------- Featured stations ----------
        if (state.config.stationsEnabled && state.stations.isNotEmpty()) {
            item {
                SectionHeader(L.radioStations, onSeeAll = onOpenStations, seeAllLabel = L.seeAll)
            }
            item {
                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.stations, key = { it.id }) { station ->
                        StationChip(station = station, onClick = {
                            app.playerManager.play(station)
                            onOpenPlayer()
                        })
                    }
                }
            }
        }

        // ---------- Latest posts ----------
        if (state.config.postsEnabled && state.posts.isNotEmpty()) {
            item {
                SectionHeader(
                    L.latestPosts,
                    onSeeAll = onOpenAllPosts,
                    seeAllLabel = L.seeAll,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(state.posts, key = { "post-" + it.id }) { post ->
                SimpleArticleRow(
                    title = post.title,
                    subtitle = post.summary,
                    imageUrl = post.imageUrl,
                    meta = Format.relativeTime(post.publishedAt, L.nepali),
                    onClick = { onOpenPostDetail(post.id) }
                )
            }
        }

        // ---------- Upcoming events ----------
        if (state.config.calendarEnabled && state.events.isNotEmpty()) {
            item {
                SectionHeader(L.upcomingEvents, onSeeAll = onOpenCalendar, seeAllLabel = L.seeAll)
            }
            items(state.events, key = { "event-" + it.id }) { event ->
                EventRow(event = event, nepali = L.nepali)
            }
        }

        // ---------- Station details (bottom of the main menu) ----------
        item {
            StationDetailsCard(
                config = state.config,
                nepali = L.nepali
            )
        }

        if (state.isLoading) {
            item { SkeletonList() }
        }
    }
}

/**
 * Station details card: frequency, address, tagline and the station team
 * (Station Manager, Technician, Marketing Manager). All values come from the
 * admin-controlled config, so the owner can change them any time and every
 * user sees the update instantly.
 */
@Composable
private fun StationDetailsCard(
    config: com.radioshuddhodhan.app.data.remote.AppConfigDto,
    nepali: Boolean
) {
    val L = LocalAppStrings.current
    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                text = (if (nepali) "रेडियो शुद्धोधन " else "Radio Shuddhodhan ") + config.stationFrequency,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (nepali) config.taglineNe else config.taglineEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))

            DetailRow(Icons.Filled.LocationOn, config.stationAddress)
            if (config.contactPhone.isNotBlank()) {
                DetailRow(Icons.Filled.Call, config.contactPhone)
            }
            if (config.contactEmail.isNotBlank()) {
                DetailRow(Icons.Filled.Email, config.contactEmail)
            }

            val team = config.teamMembers.sortedBy { it.sortOrder }
            if (team.any { it.name.isNotBlank() }) {
                Spacer(Modifier.height(12.dp))
                Text(L.ourTeam, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                team.filter { it.name.isNotBlank() }.forEach { member ->
                    val role = when (member.roleKey) {
                        "manager" -> L.stationManagerRole
                        "technician" -> L.technicianRole
                        "marketing" -> L.marketingManagerRole
                        else -> if (nepali) member.roleNe.ifBlank { member.role } else member.role
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (member.contact.isBlank()) role
                                else "$role • ${member.contact}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun greeting(L: com.radioshuddhodhan.app.core.language.AppStrings): String {
    val hour = remember {
        java.time.LocalTime.now(BsCalendar.NEPAL_ZONE).hour
    }
    return when {
        hour < 12 -> L.goodMorning
        hour < 17 -> L.goodAfternoon
        else -> L.goodEvening
    }
}

private fun featuredStation(
    stations: List<StationEntity>,
    config: com.radioshuddhodhan.app.data.remote.AppConfigDto
): StationEntity? {
    config.featuredStationId?.let { id -> stations.find { it.id == id }?.let { return it } }
    return stations.firstOrNull { it.streamUrl.isNotBlank() }
}

@Composable
private fun LiveRadioHero(
    stationName: String,
    programTitle: String,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onListen: () -> Unit
) {
    val L = LocalAppStrings.current
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onListen),
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
                    LiveBadge()
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = L.liveRadio,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    if (isPlaying) {
                        AnimatedEqualizer(isPlaying = true, barColor = Color.White)
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = stationName,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${L.currentProgram}: $programTitle",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(16.dp))
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.VolumeUp else Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when {
                                isPlaying -> L.pause
                                isBuffering -> L.buffering
                                else -> L.listenLive
                            },
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakingStrip(
    breaking: List<NewsEntity>,
    nepali: Boolean,
    onClick: (NewsEntity) -> Unit
) {
    val L = LocalAppStrings.current
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                Icons.Filled.Bolt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = L.breakingNews,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = breaking.first().title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onClick(breaking.first()) }
            )
        }
    }
}

@Composable
private fun AnnouncementsRow(
    announcements: List<AnnouncementEntity>,
    nepali: Boolean,
    modifier: Modifier = Modifier
) {
    val latest = announcements.firstOrNull() ?: return
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(Icons.Filled.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = latest.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = latest.message,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun NewsCard(news: NewsEntity, nepali: Boolean, onClick: () -> Unit) {
    val L = LocalAppStrings.current
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .width(250.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (!news.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = news.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(brandGradient()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Bolt, contentDescription = null, tint = Color.White)
                }
            }
            Column(Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = news.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    if (news.isBreaking) {
                        LiveBadge(label = L.breaking)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = Format.relativeTime(news.publishedAt, nepali),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StationChip(station: StationEntity, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            if (!station.logoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = station.logoUrl,
                    contentDescription = station.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(brandGradient()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = station.name.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = station.name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (station.isFeatured) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = BrandGold,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun ShortcutTile(
    icon: @Composable () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 14.dp)
        ) {
            icon()
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SimpleArticleRow(
    title: String,
    subtitle: String,
    imageUrl: String?,
    meta: String,
    onClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brandGradient()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Radio, contentDescription = null, tint = Color.White)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = meta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EventRow(
    event: com.radioshuddhodhan.app.data.db.EventEntity,
    nepali: Boolean
) {
    val date = remember(event.adDate) {
        runCatching { java.time.LocalDate.parse(event.adDate) }.getOrNull()
    }
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val bs = date?.let { runCatching { BsCalendar.fromAd(it) }.getOrNull() }
                    Text(
                        text = if (bs != null) {
                            if (nepali) BsCalendar.toNepaliDigits(bs.day.toString()) else bs.day.toString()
                        } else "--",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (bs != null) {
                            (if (nepali) BsCalendar.monthNamesNe[bs.month - 1]
                            else BsCalendar.monthNamesEn[bs.month - 1]).take(3)
                        } else "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (nepali) event.titleNe.ifBlank { event.title } else event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildString {
                        if (event.timeLabel.isNotBlank()) append(event.timeLabel)
                        if (event.location.isNotBlank()) {
                            if (isNotEmpty()) append(" • ")
                            append(event.location)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Filled.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}
