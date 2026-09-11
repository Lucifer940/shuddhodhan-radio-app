package com.radioshuddhodhan.app.ui.news

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radioshuddhodhan.app.core.util.Format
import com.radioshuddhodhan.app.data.db.NewsEntity
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.components.EmptyState
import com.radioshuddhodhan.app.ui.components.LiveBadge
import com.radioshuddhodhan.app.ui.components.SkeletonList
import com.radioshuddhodhan.app.ui.home.SimpleArticleRow

/**
 * News list: search, category chips, bookmark filter and animated cards.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(onOpenDetail: (String) -> Unit) {
    val L = LocalAppStrings.current
    val viewModel: NewsViewModel = appViewModel { NewsViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(L.news, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmarkFilter() }) {
                        Icon(
                            imageVector = if (state.bookmarkFilter) Icons.Filled.Bookmark
                            else Icons.Filled.BookmarkBorder,
                            contentDescription = L.bookmarks
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .animateContentSize()
        ) {
            // Search
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.setQuery(it) },
                placeholder = { Text(L.searchNews) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Category chips
            if (state.categories.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.category == null,
                        onClick = { viewModel.setCategory(null) },
                        label = { Text(L.all) }
                    )
                    state.categories.forEach { category ->
                        FilterChip(
                            selected = state.category == category,
                            onClick = { viewModel.setCategory(category) },
                            label = { Text(category) }
                        )
                    }
                }
            }

            when {
                state.isLoading -> SkeletonList()
                state.news.isEmpty() -> EmptyState(
                    title = if (state.bookmarkFilter) L.noBookmarks else L.noNews,
                    subtitle = if (state.bookmarkFilter) L.noBookmarks else null
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(state.news, key = { it.id }) { news ->
                        SimpleArticleRow(
                            title = news.title,
                            subtitle = news.summary,
                            imageUrl = news.imageUrl,
                            meta = Format.relativeTime(news.publishedAt, L.nepali),
                            onClick = { onOpenDetail(news.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * News detail: full article with bookmark + share, related news.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: String,
    onBack: () -> Unit,
    onOpenOther: (String) -> Unit
) {
    val L = LocalAppStrings.current
    val viewModel: NewsDetailViewModel = appViewModel { NewsDetailViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(newsId) { viewModel.load(newsId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.news?.title ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = L.back)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark() }) {
                        Icon(
                            imageVector = if (state.isBookmarked) Icons.Filled.Bookmark
                            else Icons.Filled.BookmarkBorder,
                            contentDescription = L.bookmark,
                            tint = if (state.isBookmarked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = {
                        state.news?.let { news ->
                            val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_SUBJECT, news.title)
                                putExtra(
                                    android.content.Intent.EXTRA_TEXT,
                                    news.title + "\n\n" + news.summary + "\n\n— Radio Shuddhodhan"
                                )
                            }
                            context.startActivity(android.content.Intent.createChooser(send, L.share))
                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = L.share)
                    }
                }
            )
        }
    ) { padding ->
        val news = state.news
        if (news == null) {
            SkeletonList(itemCount = 3)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    if (news.isBreaking) {
                        LiveBadge(label = L.breakingNews)
                        Spacer(Modifier.height(10.dp))
                    }
                    Text(
                        text = news.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = news.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = Format.dateTime(news.publishedAt),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    if (!news.imageUrl.isNullOrBlank()) {
                        coil.compose.AsyncImage(
                            model = news.imageUrl,
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .padding(bottom = 16.dp)
                        )
                    }
                    Text(
                        text = news.summary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = news.content,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(12.dp))
                    if (news.author.isNotBlank()) {
                        Text(
                            text = "— ${news.author}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }

                if (state.related.isNotEmpty()) {
                    item {
                        Text(
                            text = L.seeAll,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    items(state.related, key = { "rel-" + it.id }) { related ->
                        SimpleArticleRow(
                            title = related.title,
                            subtitle = related.summary,
                            imageUrl = related.imageUrl,
                            meta = Format.relativeTime(related.publishedAt, L.nepali),
                            onClick = { onOpenOther(related.id) }
                        )
                    }
                }
            }
        }
    }
}
