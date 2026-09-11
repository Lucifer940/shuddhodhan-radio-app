package com.radioshuddhodhan.app.ui.posts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.core.util.Format
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.components.EmptyState
import com.radioshuddhodhan.app.ui.components.SkeletonList
import com.radioshuddhodhan.app.ui.home.SimpleArticleRow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

private class PostsViewModel(app: RadioApp) : ViewModel() {
    val posts = app.contentRepository.observePosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

/**
 * Posts list — community articles and updates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(onOpenDetail: (String) -> Unit) {
    val L = LocalAppStrings.current
    val viewModel: PostsViewModel = appViewModel { PostsViewModel(it) }
    val posts by viewModel.posts.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(L.posts, fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        when {
            posts == null -> SkeletonList()
            posts!!.isEmpty() -> EmptyState(title = L.noPosts, subtitle = L.postsDesc)
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(posts!!, key = { it.id }) { post ->
                    SimpleArticleRow(
                        title = post.title,
                        subtitle = post.summary,
                        imageUrl = post.imageUrl,
                        meta = Format.relativeTime(post.publishedAt, L.nepali),
                        onClick = { onOpenDetail(post.id) }
                    )
                }
            }
        }
    }
}

/**
 * Post detail.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onBack: () -> Unit
) {
    val L = LocalAppStrings.current
    val app = com.radioshuddhodhan.app.ui.LocalAppContainer.current
    val context = LocalContext.current
    val post by rememberPost(postId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(L.posts) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = L.back)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        post?.let { p ->
                            val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_SUBJECT, p.title)
                                putExtra(
                                    android.content.Intent.EXTRA_TEXT,
                                    p.title + "\n\n" + p.summary + "\n\n— Radio Shuddhodhan"
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
        val p = post
        if (p == null) {
            SkeletonList(itemCount = 2)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        text = p.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = Format.dateTime(p.publishedAt),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                    )
                    Text(text = p.content, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun rememberPost(postId: String): androidx.compose.runtime.State<com.radioshuddhodhan.app.data.db.PostEntity?> {
    val app = com.radioshuddhodhan.app.ui.LocalAppContainer.current
    return androidx.compose.runtime.produceState<com.radioshuddhodhan.app.data.db.PostEntity?>(
        initialValue = null,
        key1 = postId
    ) {
        app?.database?.postDao()?.observeById(postId)?.collect { value = it }
    }
}
