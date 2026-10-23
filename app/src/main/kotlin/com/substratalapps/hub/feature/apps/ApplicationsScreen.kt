package com.substratalapps.hub.feature.apps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.substratalapps.hub.data.CapabilitiesRepository
import com.substratalapps.hub.designsystem.IdText
import com.substratalapps.hub.designsystem.StatusBadge
import com.substratalapps.hub.navigation.ScreenContext
import com.substratalapps.hub.network.ApplicationSummary
import com.substratalapps.hub.network.SubstratalApi
import com.substratalapps.hub.ui.common.CursorListContent
import com.substratalapps.hub.ui.common.CursorPager
import com.substratalapps.hub.ui.common.ScreenScaffold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * APL-01 Applications. Staff see every app and can filter to the review queue
 * (`review_status=pending_review`, oldest first); developers see the apps they own.
 */
@HiltViewModel
class ApplicationsViewModel @Inject constructor(
    private val api: SubstratalApi,
    private val capabilitiesRepository: CapabilitiesRepository,
) : ViewModel() {
    val isStaff: Boolean get() = capabilitiesRepository.capabilities.value.canManageApplications
    val reviewStatus = MutableStateFlow<String?>(null)

    val pager = CursorPager(viewModelScope) { cursor ->
        capabilitiesRepository.awaitLoaded()
        api.listApplications(
            limit = 25,
            cursor = cursor,
            owned = if (isStaff) null else true,
            reviewStatus = reviewStatus.value,
        )
    }

    init { pager.reload() }

    fun setReviewStatus(value: String?) {
        reviewStatus.value = value
        pager.reload()
    }
}

private val reviewFilters = listOf("approved" to "Approved", "pending_review" to "Review queue")

@Composable
fun ApplicationsScreen(screen: ScreenContext, viewModel: ApplicationsViewModel = hiltViewModel()) {
    val state by viewModel.pager.state.collectAsStateWithLifecycle()
    val filter by viewModel.reviewStatus.collectAsStateWithLifecycle()
    val isStaff = screen.appViewModel.capabilities.collectAsStateWithLifecycle().value.canManageApplications

    ScreenScaffold(
        screen,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { screen.navigate("APL-02") },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Register application") },
            )
        },
    ) { padding ->
        CursorListContent(
            state = state,
            padding = padding,
            emptyTitle = if (filter == "pending_review") "Nothing waiting for review" else "No applications",
            emptyBody = if (isStaff) "Registered applications appear here." else "Register an application to get started.",
            onRetry = { viewModel.pager.reload() },
            onRefresh = { viewModel.pager.reload(pullToRefresh = true) },
            onLoadMore = viewModel.pager::loadMore,
            header = {
                if (isStaff) {
                    item {
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            reviewFilters.forEach { (value, label) ->
                                FilterChip(
                                    selected = filter == value,
                                    onClick = { viewModel.setReviewStatus(if (filter == value) null else value) },
                                    label = { Text(label) },
                                )
                            }
                        }
                    }
                }
            },
        ) { apps ->
            items(apps, key = { it.id }) { app ->
                ApplicationRow(app) { screen.navigate("APL-03", "appId" to app.id) }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ApplicationRow(app: ApplicationSummary, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(app.name) },
        supportingContent = {
            Column {
                IdText(app.id)
                app.accessTier?.let { Text(if (it == "free") "Free tier" else "Standard tier") }
            }
        },
        trailingContent = { app.reviewStatus?.let { StatusBadge("application.review_status", it) } },
        modifier = Modifier.clickable(onClick = onClick),
    )
}
