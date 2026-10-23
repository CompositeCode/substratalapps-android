package com.substratalapps.hub.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.substratalapps.hub.designsystem.EmptyState
import com.substratalapps.hub.designsystem.ErrorState
import com.substratalapps.hub.designsystem.LoadingState
import com.substratalapps.hub.designsystem.OfflineState
import com.substratalapps.hub.network.ApiException
import com.substratalapps.hub.network.NetworkUnavailableException
import com.substratalapps.hub.network.Page
import com.substratalapps.hub.network.apiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Screen state machine (CLIENT-ARCHITECTURE: idle → loading → loaded/empty/error) for a cursor list. */
data class CursorListState<T>(
    val items: List<T> = emptyList(),
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val loadingMore: Boolean = false,
    val error: Throwable? = null,
    val nextCursor: String? = null,
    val hasMore: Boolean = false,
)

/**
 * Loads a cursor-paginated endpoint page by page (`limit` + `cursor`, stop when `has_more` is
 * false), keeping loaded pages. Paging 3 replaces this once lists need placeholders.
 */
class CursorPager<T>(
    private val scope: CoroutineScope,
    private val fetch: suspend (cursor: String?) -> Page<T>,
) {
    private val mutable = MutableStateFlow(CursorListState<T>())
    val state: StateFlow<CursorListState<T>> = mutable.asStateFlow()
    private var job: Job? = null

    fun reload(pullToRefresh: Boolean = false) {
        job?.cancel()
        mutable.update { it.copy(loading = !pullToRefresh, refreshing = pullToRefresh, error = null) }
        job = scope.launch {
            runCatching { apiCall { fetch(null) } }
                .onSuccess { page ->
                    mutable.value = CursorListState(page.data, loading = false, nextCursor = page.page.nextCursor, hasMore = page.page.hasMore)
                }
                .onFailure { e -> mutable.update { it.copy(loading = false, refreshing = false, error = e) } }
        }
    }

    fun loadMore() {
        val current = mutable.value
        if (!current.hasMore || current.loadingMore || current.loading) return
        mutable.update { it.copy(loadingMore = true) }
        job = scope.launch {
            runCatching { apiCall { fetch(current.nextCursor) } }
                .onSuccess { page ->
                    mutable.update {
                        it.copy(items = it.items + page.data, loadingMore = false, nextCursor = page.page.nextCursor, hasMore = page.page.hasMore)
                    }
                }
                .onFailure { e -> mutable.update { it.copy(loadingMore = false, error = e) } }
        }
    }
}

/** Renders a [CursorListState] with loading, empty, error, offline and forbidden states. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CursorListContent(
    state: CursorListState<T>,
    padding: PaddingValues,
    emptyTitle: String,
    emptyBody: String,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    header: LazyListScope.() -> Unit = {},
    item: LazyListScope.(List<T>) -> Unit,
) {
    val listState = rememberLazyListState()
    val nearEnd by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= listState.layoutInfo.totalItemsCount - 5
        }
    }
    LaunchedEffect(nearEnd, state.items.size) { if (nearEnd) onLoadMore() }

    Box(Modifier.fillMaxSize().padding(padding)) {
        when {
            state.loading -> LoadingState()
            state.error != null && state.items.isEmpty() -> when (val e = state.error) {
                is NetworkUnavailableException -> OfflineState(onRetry)
                is ApiException if e.isForbidden ->
                    EmptyState("You don't have access", "Your role doesn't include this. Ask an administrator if you need it.")
                else -> ErrorState("Couldn't load this", (e as? ApiException)?.code ?: "Try again in a moment.", onRetry)
            }
            else -> PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = onRefresh) {
                LazyColumn(Modifier.fillMaxSize(), state = listState) {
                    header()
                    if (state.items.isEmpty()) {
                        item { EmptyState(emptyTitle, emptyBody, Modifier.padding(top = 48.dp)) }
                    } else {
                        item(state.items)
                    }
                    if (state.loadingMore) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
