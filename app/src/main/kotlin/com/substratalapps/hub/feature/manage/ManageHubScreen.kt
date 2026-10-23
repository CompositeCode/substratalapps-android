package com.substratalapps.hub.feature.manage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.substratalapps.hub.designsystem.EmptyState
import com.substratalapps.hub.designsystem.Metrics
import com.substratalapps.hub.designsystem.SectionCard
import com.substratalapps.hub.model.ImplementationStatus
import com.substratalapps.hub.model.BuildState
import com.substratalapps.hub.navigation.Destinations
import com.substratalapps.hub.navigation.ScreenContext
import com.substratalapps.hub.ui.common.ScreenScaffold

/**
 * GLB-02 Manage hub: on compact and medium layouts, Operations, Administration and Developer
 * combined into one grouped list. Sections the person has no rights in are hidden.
 */
@Composable
fun ManageHubScreen(screen: ScreenContext) {
    val capabilities by screen.appViewModel.capabilities.collectAsStateWithLifecycle()
    val sections = Destinations.tree.drop(1) // Personal lives in the bottom bar and Account
        .map { it to it.items.filter { item -> item.visible(capabilities) } }
        .filter { it.second.isNotEmpty() }

    ScreenScaffold(screen) { padding ->
        if (sections.isEmpty()) {
            EmptyState("Nothing to manage", "You don't have administration or developer rights.", Modifier.padding(padding))
            return@ScreenScaffold
        }
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(Metrics.marginCompact),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            sections.forEach { (section, items) ->
                SectionCard(section.title) {
                    Column {
                        items.forEachIndexed { i, item ->
                            if (i > 0) HorizontalDivider()
                            val state = ImplementationStatus.of(item.screenId)
                            ListItem(
                                leadingContent = { Icon(item.icon, contentDescription = null) },
                                headlineContent = { Text(item.label) },
                                supportingContent = if (state == BuildState.AVAILABLE) null else ({ Text(state.label) }),
                                modifier = Modifier.clickable { screen.navigate(item.screenId) },
                            )
                        }
                    }
                }
            }
        }
    }
}
