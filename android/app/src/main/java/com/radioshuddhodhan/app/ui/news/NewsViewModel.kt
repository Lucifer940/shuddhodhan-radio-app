package com.radioshuddhodhan.app.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.data.db.NewsEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * News list state: search, category filter and bookmark filter.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModel(private val app: RadioApp) : ViewModel() {

    data class NewsUiState(
        val isLoading: Boolean = true,
        val news: List<NewsEntity> = emptyList(),
        val categories: List<String> = emptyList(),
        val bookmarkIds: List<String> = emptyList(),
        val query: String = "",
        val category: String? = null,
        val bookmarkFilter: Boolean = false
    )

    private val query = MutableStateFlow("")
    private val category = MutableStateFlow<String?>(null)
    private val bookmarkFilter = MutableStateFlow(false)

    private val newsFlow = combine(query, category) { q, c -> q to c }
        .flatMapLatest { (q, _) ->
            if (q.isBlank()) app.contentRepository.observeNews()
            else app.contentRepository.searchNews(q.trim())
        }

    val state: StateFlow<NewsUiState> = combine(
        newsFlow,
        app.contentRepository.observeNewsCategories(),
        app.contentRepository.observeBookmarkIds(),
        query,
        category,
        bookmarkFilter
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val news = values[0] as List<NewsEntity>
        val bookmarked = values[2] as List<String>
        val q = values[3] as String
        val cat = values[4] as String?
        val bf = values[5] as Boolean
        val byCategory = cat?.let { c -> news.filter { it.category == c } } ?: news
        val result = if (bf) byCategory.filter { it.id in bookmarked } else byCategory
        NewsUiState(
            isLoading = false,
            news = result,
            categories = values[1] as List<String>,
            bookmarkIds = bookmarked,
            query = q,
            category = cat,
            bookmarkFilter = bf
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsUiState())

    fun setQuery(q: String) { query.value = q }
    fun setCategory(c: String?) { category.value = if (category.value == c) null else c }
    fun toggleBookmarkFilter() { bookmarkFilter.value = !bookmarkFilter.value }

    fun toggleBookmark(newsId: String) {
        viewModelScope.launch { app.contentRepository.toggleBookmark(newsId) }
    }
}

/**
 * Single news article state.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewsDetailViewModel(private val app: RadioApp) : ViewModel() {

    data class DetailState(
        val news: NewsEntity? = null,
        val related: List<NewsEntity> = emptyList(),
        val isBookmarked: Boolean = false
    )

    private val newsId = MutableStateFlow("")

    val state: StateFlow<DetailState> = combine(
        newsId.flatMapLatest { id ->
            if (id.isBlank()) flowOf(null)
            else app.contentRepository.observeNewsById(id)
        },
        app.contentRepository.observeNews(),
        app.contentRepository.observeBookmarkIds()
    ) { news, allNews, bookmarks ->
        DetailState(
            news = news,
            related = allNews
                .filter { it.id != news?.id && it.category == news?.category }
                .take(4),
            isBookmarked = news?.id in bookmarks
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailState())

    fun load(id: String) { newsId.value = id }

    fun toggleBookmark() {
        val id = newsId.value
        if (id.isNotBlank()) {
            viewModelScope.launch { app.contentRepository.toggleBookmark(id) }
        }
    }
}
