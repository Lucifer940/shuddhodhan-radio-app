package com.radioshuddhodhan.app.ui.stations

import androidx.compose.animation.animateItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.data.db.StationEntity
import com.radioshuddhodhan.app.ui.LocalAppContainer
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.components.AnimatedEqualizer
import com.radioshuddhodhan.app.ui.components.EmptyState
import com.radioshuddhodhan.app.ui.components.SkeletonList
import com.radioshuddhodhan.app.ui.components.brandGradient
import com.radioshuddhodhan.app.ui.theme.BrandGold
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private class StationsViewModel(private val app: RadioApp) : ViewModel() {

    data class StationsUiState(
        val isLoading: Boolean = true,
        val stations: List<StationEntity> = emptyList(),
        val favorites: List<String> = emptyList(),
        val query: String = "",
        val favoritesOnly: Boolean = false,
        val playingStationId: String? = null
    )

    private val query = MutableStateFlow("")
    private val favoritesOnly = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = combine(
        query.flatMapLatest { q ->
            if (q.isBlank()) app.contentRepository.observeStations()
            else app.contentRepository.searchStations(q.trim())
        },
        app.contentRepository.observeFavoriteIds(),
        query,
        favoritesOnly,
        app.playerManager.state
    ) { searched, favorites, q, favOnly, player ->
        val base = if (favOnly) searched.filter { it.id in favorites } else searched
        StationsUiState(
            isLoading = false,
            stations = base,
            favorites = favorites,
            query = q,
            favoritesOnly = favOnly,
            playingStationId = player.station?.id
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StationsUiState())

    fun setQuery(q: String) { query.value = q }
    fun toggleFavoritesOnly() { favoritesOnly.value = !favoritesOnly.value }

    fun toggleFavorite(stationId: String) {
        viewModelScope.launch { app.contentRepository.toggleFavorite(stationId) }
    }

    fun play(station: StationEntity) {
        app.playerManager.play(station)
    }
}

/**
 * Radio stations browser: search, favourites filter, play and favourite.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsScreen(onOpenPlayer: () -> Unit) {
    val L = LocalAppStrings.current
    val viewModel: StationsViewModel = appViewModel { StationsViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(L.radioStations, fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.setQuery(it) },
                placeholder = { Text(L.searchStations) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.favoritesOnly,
                    onClick = { viewModel.toggleFavoritesOnly() },
                    label = { Text(L.favorites) },
                    leadingIcon = {
                        Icon(Icons.Filled.FavoriteBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                )
            }

            when {
                state.isLoading -> SkeletonList()
                state.stations.isEmpty() -> EmptyState(
                    title = if (state.favoritesOnly) L.noFavorites else L.noStations
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.stations, key = { it.id }) { station ->
                        StationCard(
                            station = station,
                            isFavorite = station.id in state.favorites,
                            isPlaying = state.playingStationId == station.id,
                            onPlay = {
                                viewModel.play(station)
                                onOpenPlayer()
                            },
                            onToggleFavorite = { viewModel.toggleFavorite(station.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StationCard(
    station: StationEntity,
    isFavorite: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val L = LocalAppStrings.current
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            if (!station.logoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = station.logoUrl,
                    contentDescription = station.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(brandGradient()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Radio,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
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
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = BrandGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = station.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isPlaying) {
                AnimatedEqualizer(isPlaying = true)
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) L.unfavorite else L.favorite,
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onPlay) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = L.play,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
