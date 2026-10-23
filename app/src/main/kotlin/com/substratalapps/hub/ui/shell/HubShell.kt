package com.substratalapps.hub.ui.shell

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.substratalapps.hub.designsystem.R as DesignR
import com.substratalapps.hub.model.ScreenCatalog
import com.substratalapps.hub.model.ScreenSpec
import com.substratalapps.hub.model.Section
import com.substratalapps.hub.navigation.Destinations
import com.substratalapps.hub.navigation.HELP_ROUTE
import com.substratalapps.hub.navigation.HubNavHost
import com.substratalapps.hub.navigation.NavItem
import com.substratalapps.hub.navigation.switchTo
import com.substratalapps.hub.ui.AppViewModel
import com.substratalapps.hub.ui.common.ApiVersionBanner

/** Expanded width: ≥ 840 dp shows the full tree in a permanent drawer (PLATFORM-SPEC §2). */
private const val EXPANDED_WIDTH_DP = 840

@Composable
fun HubShell(appViewModel: AppViewModel) {
    val navController = rememberNavController()
    val capabilities by appViewModel.capabilities.collectAsStateWithLifecycle()
    val apiVersion by appViewModel.apiVersion.collectAsStateWithLifecycle()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoutes = backStack?.destination?.hierarchy?.mapNotNull { it.route }?.toSet().orEmpty()
    val expanded = LocalConfiguration.current.screenWidthDp >= EXPANDED_WIDTH_DP
    val pendingDeepLink by appViewModel.pendingDeepLink.collectAsStateWithLifecycle()

    // Links that arrive while signed in. The launch intent itself is handled by NavHost; a link
    // that arrived while signed out is replayed here once the shell exists.
    LaunchedEffect(pendingDeepLink) {
        pendingDeepLink?.let {
            navController.handleDeepLink(it)
            appViewModel.deepLinkHandled()
        }
    }

    val currentScreen = ScreenCatalog.all.firstOrNull { it.navRoute in currentRoutes }

    fun isSelected(item: NavItem): Boolean =
        item.screen.navRoute in currentRoutes || (!expanded && compactParentOf(currentScreen) == item.screenId)

    val content: @Composable () -> Unit = {
        Column(Modifier.fillMaxSize()) {
            ApiVersionBanner(apiVersion)
            HubNavHost(
                navController = navController,
                appViewModel = appViewModel,
                rootScreenIds = if (expanded) {
                    Destinations.tree.flatMap { s -> s.items.map { it.screenId } }.toSet()
                } else {
                    Destinations.compact.map { it.screenId }.toSet()
                },
                modifier = Modifier.weight(1f),
            )
        }
    }

    if (expanded) {
        PermanentNavigationDrawer(
            drawerContent = { FullTreeDrawer(navController, ::isSelected, capabilities) },
            content = content,
        )
    } else {
        val items = Destinations.compact.filter { it.visible(capabilities) }
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                items.forEach { item ->
                    item(
                        selected = isSelected(item),
                        onClick = { navController.switchTo(item.screen) },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) },
                    )
                }
            },
        ) { content() }
    }
}

@Composable
private fun FullTreeDrawer(
    navController: NavHostController,
    isSelected: (NavItem) -> Boolean,
    capabilities: com.substratalapps.hub.data.Capabilities,
) {
    PermanentDrawerSheet(Modifier.width(300.dp)) {
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 12.dp)) {
            Spacer(Modifier.height(16.dp))
            Image(
                painterResource(DesignR.drawable.brand_lockup),
                contentDescription = "Substratal Apps",
                modifier = Modifier.padding(horizontal = 16.dp).height(32.dp),
            )
            Spacer(Modifier.height(16.dp))
            Destinations.tree.forEach { section ->
                val visible = section.items.filter { it.visible(capabilities) }
                if (visible.isEmpty()) return@forEach
                section.title?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                    )
                }
                visible.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        icon = { Icon(item.icon, contentDescription = null) },
                        selected = isSelected(item),
                        onClick = { navController.switchTo(item.screen) },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            NavigationDrawerItem(
                label = { Text("Help & documentation") },
                icon = { Icon(Destinations.helpIcon, contentDescription = null) },
                selected = HELP_ROUTE == navController.currentDestination?.route,
                onClick = { navController.navigate(HELP_ROUTE) { launchSingleTop = true } },
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Compact layouts: which of the five destinations a nested screen belongs to (IA §3). */
private fun compactParentOf(screen: ScreenSpec?): String? {
    val route = screen?.route ?: return null
    return when {
        screen.section in setOf(Section.OPERATIONS, Section.ADMINISTRATION, Section.DEVELOPER) -> "GLB-02"
        listOf("/account", "/settings", "/billing").any(route::startsWith) -> "ACC-01"
        route.startsWith("/inbox") -> "NTF-01"
        route.startsWith("/orgs") -> "ORG-01"
        route.startsWith("/apps") -> "APP-01"
        else -> null
    }
}
