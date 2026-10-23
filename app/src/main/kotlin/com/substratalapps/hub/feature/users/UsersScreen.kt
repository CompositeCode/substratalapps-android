package com.substratalapps.hub.feature.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.substratalapps.hub.designsystem.IdText
import com.substratalapps.hub.designsystem.StatusBadge
import com.substratalapps.hub.navigation.ScreenContext
import com.substratalapps.hub.network.SubstratalApi
import com.substratalapps.hub.network.User
import com.substratalapps.hub.ui.common.CursorListContent
import com.substratalapps.hub.ui.common.CursorPager
import com.substratalapps.hub.ui.common.ScreenScaffold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

/** USR-01 Users: search (≥ 3 characters, 300 ms debounce), status filter, infinite scroll. */
@HiltViewModel
class UsersViewModel @Inject constructor(
    private val api: SubstratalApi,
) : ViewModel() {
    val query = MutableStateFlow("")
    val status = MutableStateFlow<String?>(null)

    val pager = CursorPager(viewModelScope) { cursor ->
        val q = query.value.trim().takeIf { it.length >= 3 }
        api.listUsers(limit = 25, cursor = cursor, status = status.value, query = q)
    }

    init {
        pager.reload()
        @OptIn(FlowPreview::class)
        viewModelScope.launch { query.drop(1).debounce(300).collect { pager.reload() } }
    }

    fun setStatus(value: String?) {
        status.value = value
        pager.reload()
    }
}

private val statusFilters = listOf("active", "invited", "suspended")

@Composable
fun UsersScreen(screen: ScreenContext, viewModel: UsersViewModel = hiltViewModel()) {
    val state by viewModel.pager.state.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    val capabilities by screen.appViewModel.capabilities.collectAsStateWithLifecycle()

    ScreenScaffold(
        screen,
        floatingActionButton = {
            if (capabilities.canManageUsers) {
                ExtendedFloatingActionButton(
                    onClick = { screen.navigate("USR-02") },
                    icon = { Icon(Icons.Filled.PersonAdd, contentDescription = null) },
                    text = { Text("Invite user") },
                )
            }
        },
    ) { padding ->
        CursorListContent(
            state = state,
            padding = padding,
            emptyTitle = if (query.isBlank() && status == null) "No users yet" else "No matching users",
            emptyBody = "Try a different search or filter.",
            onRetry = { viewModel.pager.reload() },
            onRefresh = { viewModel.pager.reload(pullToRefresh = true) },
            onLoadMore = viewModel.pager::loadMore,
            header = {
                item {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { viewModel.query.value = it },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            placeholder = { Text("Search name, email or usr_ ID") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            statusFilters.forEach { s ->
                                FilterChip(
                                    selected = status == s,
                                    onClick = { viewModel.setStatus(if (status == s) null else s) },
                                    label = { Text(s.replaceFirstChar { it.uppercase() }) },
                                )
                            }
                        }
                    }
                }
            },
        ) { users ->
            items(users, key = { it.id }) { user ->
                UserRow(user) { screen.navigate("USR-03", "userId" to user.id) }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun UserRow(user: User, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(user.email ?: user.id) },
        supportingContent = { IdText(user.id) },
        trailingContent = { StatusBadge("user.status", user.status) },
        modifier = Modifier.clickable(onClick = onClick),
    )
}
