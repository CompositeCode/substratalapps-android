package com.substratalapps.hub.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.substratalapps.hub.help.HelpLinks
import com.substratalapps.hub.navigation.Destinations
import com.substratalapps.hub.navigation.ScreenContext
import com.substratalapps.hub.network.ApiVersionStatus

/**
 * The frame every screen uses: top app bar with back (pushed screens), a Help action that opens this
 * screen's page on the docs site, optional extra actions and an Extended FAB slot.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    screen: ScreenContext,
    title: String = screen.spec.title,
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (!screen.isRoot && screen.navController.previousBackStackEntry != null) {
                        IconButton(onClick = { screen.back() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    actions()
                    IconButton(onClick = { HelpLinks.open(context, HelpLinks.forScreen(screen.spec)) }) {
                        Icon(Destinations.helpIcon, contentDescription = "Help for this screen")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = floatingActionButton,
        content = content,
    )
}

/**
 * Shown when the API says the version this app requests is deprecated (`Deprecation` / `Sunset`
 * headers). After the sunset date requests fail with `400 unsupported_api_version`.
 */
@Composable
fun ApiVersionBanner(status: ApiVersionStatus) {
    if (!status.isDeprecated) return
    Surface(color = MaterialTheme.colorScheme.tertiaryContainer, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("Update the app", style = MaterialTheme.typography.titleSmall)
            Text(
                "API version ${status.requested} is being retired" +
                    (status.sunset?.let { " on $it" } ?: "") + ". Update to keep using Substratal.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
