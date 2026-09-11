package com.radioshuddhodhan.app.ui.admin

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radioshuddhodhan.app.data.db.NewsEntity
import com.radioshuddhodhan.app.data.db.PostEntity
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel

/**
 * Admin news / posts manager: create, edit, delete, publish, feature and
 * breaking flags. Every change is live for user screens immediately.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNewsScreen(postMode: Boolean = false) {
    val L = LocalAppStrings.current
    val backDispatcher = androidx.compose.ui.platform.LocalOnBackPressedDispatcherOwner.current
        ?.onBackPressedDispatcher
    val viewModel: AdminDataViewModel = appViewModel { AdminDataViewModel(it) }
    val newsList by viewModel.news.collectAsStateWithLifecycle()
    val postList by viewModel.posts.collectAsStateWithLifecycle()
    val toast by viewModel.toast.collectAsStateWithLifecycle()

    var editorTarget by remember { mutableStateOf<Any?>(null) } // NewsEntity | PostEntity | "new"
    var deleteTarget by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(toast) {
        if (toast != null) {
            kotlinx.coroutines.delay(2500)
            viewModel.consumeToast()
        }
    }

    val onBack = { backDispatcher?.onBackPressed() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (postMode) L.managePosts else L.manageNews,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = L.back)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editorTarget = "new" }) {
                Icon(Icons.Filled.Add, contentDescription = if (postMode) L.add else L.addNews)
            }
        }
    ) { padding ->
        val items: List<Any> = if (postMode) postList else newsList

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items, key = { (it as? NewsEntity)?.id ?: (it as? PostEntity)?.id ?: "" }) { item ->
                when (item) {
                    is NewsEntity -> NewsAdminCard(
                        title = item.title,
                        published = item.isPublished,
                        breaking = item.isBreaking,
                        featured = item.isFeatured,
                        meta = item.category,
                        onEdit = { editorTarget = item },
                        onDelete = { deleteTarget = item.id },
                        onTogglePublish = { viewModel.toggleNewsFlags(item.copy(isPublished = !item.isPublished)) },
                        onToggleBreaking = { viewModel.toggleNewsFlags(item.copy(isBreaking = !item.isBreaking)) },
                        onToggleFeatured = { viewModel.toggleNewsFlags(item.copy(isFeatured = !item.isFeatured)) }
                    )
                    is PostEntity -> NewsAdminCard(
                        title = item.title,
                        published = item.isPublished,
                        breaking = false,
                        featured = item.isFeatured,
                        meta = "Post",
                        onEdit = { editorTarget = item },
                        onDelete = { deleteTarget = item.id },
                        onTogglePublish = {
                            viewModel.savePost(
                                item.id, item.title, item.summary, item.content,
                                item.imageUrl ?: "", !item.isPublished, item.isFeatured
                            )
                        },
                        onToggleBreaking = {},
                        onToggleFeatured = {
                            viewModel.savePost(
                                item.id, item.title, item.summary, item.content,
                                item.imageUrl ?: "", item.isPublished, !item.isFeatured
                            )
                        }
                    )
                }
            }
        }

        if (items.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(if (postMode) L.noPosts else L.noNews, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (toast != null) {
            androidx.compose.material3.Snackbar(
                modifier = Modifier.padding(16.dp)
            ) { Text(L.saved) }
        }
    }

    // Editor dialog
    if (editorTarget != null) {
        NewsEditorDialog(
            postMode = postMode,
            existing = editorTarget,
            onSave = { id, title, summary, content, category, imageUrl, published, featured, breaking ->
                if (postMode) {
                    viewModel.savePost(id, title, summary, content, imageUrl, published, featured)
                } else {
                    viewModel.saveNews(id, title, summary, content, category, imageUrl, published, featured, breaking)
                }
                editorTarget = null
            },
            onDismiss = { editorTarget = null }
        )
    }

    // Delete confirmation
    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(L.deleteConfirm) },
            confirmButton = {
                TextButton(onClick = {
                    if (postMode) viewModel.deletePost(deleteTarget!!)
                    else viewModel.deleteNews(deleteTarget!!)
                    deleteTarget = null
                }) { Text(L.delete) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text(L.cancel) }
            }
        )
    }
}

@Composable
private fun NewsAdminCard(
    title: String,
    published: Boolean,
    breaking: Boolean,
    featured: Boolean,
    meta: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePublish: () -> Unit,
    onToggleBreaking: () -> Unit,
    onToggleFeatured: () -> Unit
) {
    val L = LocalAppStrings.current
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = L.edit, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = L.delete,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (published) L.enabled else L.disabled,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (published) com.radioshuddhodhan.app.ui.theme.SuccessGreen
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(checked = published, onCheckedChange = { onTogglePublish() }, modifier = Modifier.height(24.dp))
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onToggleBreaking, enabled = !meta.startsWith("Post")) {
                    Icon(
                        Icons.Filled.Bolt,
                        contentDescription = L.markBreaking,
                        tint = if (breaking) com.radioshuddhodhan.app.ui.theme.LiveRed
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onToggleFeatured) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = L.markFeatured,
                        tint = if (featured) com.radioshuddhodhan.app.ui.theme.BrandGold
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NewsEditorDialog(
    postMode: Boolean,
    existing: Any?,
    onSave: (
        id: String?, title: String, summary: String, content: String,
        category: String, imageUrl: String, published: Boolean,
        featured: Boolean, breaking: Boolean
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val L = LocalAppStrings.current

    val existingNews = existing as? NewsEntity
    val existingPost = existing as? PostEntity

    var title by remember { mutableStateOf(existingNews?.title ?: existingPost?.title ?: "") }
    var summary by remember { mutableStateOf(existingNews?.summary ?: existingPost?.summary ?: "") }
    var content by remember { mutableStateOf(existingNews?.content ?: existingPost?.content ?: "") }
    var category by remember { mutableStateOf(existingNews?.category ?: "समाचार") }
    var imageUrl by remember { mutableStateOf(existingNews?.imageUrl ?: existingPost?.imageUrl ?: "") }
    var published by remember { mutableStateOf(existingNews?.isPublished ?: existingPost?.isPublished ?: true) }
    var featured by remember { mutableStateOf(existingNews?.isFeatured ?: existingPost?.isFeatured ?: false) }
    var breaking by remember { mutableStateOf(existingNews?.isBreaking ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) (if (postMode) L.add else L.addNews) else L.editNews) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text(L.headline) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = summary, onValueChange = { summary = it },
                    label = { Text(L.summary) },
                    minLines = 2, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content, onValueChange = { content = it },
                    label = { Text(L.content) },
                    minLines = 4, modifier = Modifier.fillMaxWidth()
                )
                if (!postMode) {
                    OutlinedTextField(
                        value = category, onValueChange = { category = it },
                        label = { Text(L.category) }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedTextField(
                    value = imageUrl, onValueChange = { imageUrl = it },
                    label = { Text(L.imageUrl) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (published) L.publish else L.unpublish, modifier = Modifier.weight(1f))
                    Switch(checked = published, onCheckedChange = { published = it })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(L.markFeatured, modifier = Modifier.weight(1f))
                    Switch(checked = featured, onCheckedChange = { featured = it })
                }
                if (!postMode) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(L.markBreaking, modifier = Modifier.weight(1f))
                        Switch(checked = breaking, onCheckedChange = { breaking = it })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank()) {
                    onSave(
                        existingNews?.id ?: existingPost?.id,
                        title, summary, content, category, imageUrl, published, featured, breaking
                    )
                }
            }) { Text(L.save) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(L.cancel) }
        }
    )
}
