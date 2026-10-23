package com.substratalapps.hub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.substratalapps.hub.feature.account.AccountScreen
import com.substratalapps.hub.feature.apps.ApplicationsScreen
import com.substratalapps.hub.feature.help.HelpScreen
import com.substratalapps.hub.feature.manage.ManageHubScreen
import com.substratalapps.hub.feature.ops.OpsOverviewScreen
import com.substratalapps.hub.feature.settings.SettingsScreen
import com.substratalapps.hub.feature.users.UsersScreen
import com.substratalapps.hub.model.ScreenCatalog
import com.substratalapps.hub.model.ScreenSpec
import com.substratalapps.hub.ui.AppViewModel
import com.substratalapps.hub.ui.common.PlaceholderScreen

/** Everything a screen needs from the shell. */
class ScreenContext(
    val spec: ScreenSpec,
    val entry: NavBackStackEntry,
    val navController: NavHostController,
    val appViewModel: AppViewModel,
    /** True when the screen is a top-level destination (no back arrow). */
    val isRoot: Boolean,
) {
    fun arg(name: String): String? = entry.arguments?.getString(name)
    fun navigate(id: String, vararg args: Pair<String, String>) = navController.navigateToScreen(ScreenCatalog.byId(id), *args)
    fun back() = navController.popBackStack()
}

/**
 * Screens built so far, by design-package id. Every other catalog screen gets [PlaceholderScreen],
 * which shows its spec, so the whole app is navigable and deep-linkable from day one.
 * Must match ImplementationStatus (ImplementedScreensTest); AUTH-01/02 render outside the shell.
 */
val implementedScreens: Map<String, @Composable (ScreenContext) -> Unit> = mapOf(
    "ACC-01" to { AccountScreen(it) },
    "SET-01" to { SettingsScreen(it) },
    "GLB-02" to { ManageHubScreen(it) },
    "USR-01" to { UsersScreen(it) },
    "APL-01" to { ApplicationsScreen(it) },
    "OPS-01" to { OpsOverviewScreen(it) },
)

val screensOutsideShell = setOf("AUTH-01", "AUTH-02")

private const val DASHBOARD_HOST = "https://dashboard.substratalapps.com"

@Composable
fun HubNavHost(
    navController: NavHostController,
    appViewModel: AppViewModel,
    rootScreenIds: Set<String>,
    modifier: Modifier = Modifier,
) {
    NavHost(navController, startDestination = ScreenCatalog.byId("APP-01").navRoute, modifier = modifier) {
        ScreenCatalog.all.forEach { spec ->
            composable(
                route = spec.navRoute,
                arguments = spec.pathParameters.map { navArgument(it) { type = NavType.StringType } },
                deepLinks = if (spec.isOverlay) emptyList() else listOf(
                    // substratal://admin/users/usr_123 ; the root route is substratal://home
                    navDeepLink { uriPattern = "substratal://${spec.navRoute}" },
                    navDeepLink { uriPattern = "$DASHBOARD_HOST${if (spec.route == "/") "/" else "/" + spec.navRoute}" },
                ),
            ) { entry ->
                val context = ScreenContext(spec, entry, navController, appViewModel, spec.id in rootScreenIds)
                val content = implementedScreens[spec.id]
                if (content != null) content(context) else PlaceholderScreen(context)
            }
        }
        composable(HELP_ROUTE) { HelpScreen(onBack = { navController.popBackStack() }) }
    }
}

/** Fills `{param}` placeholders and navigates. */
fun NavHostController.navigateToScreen(spec: ScreenSpec, vararg args: Pair<String, String>) {
    var route = spec.navRoute
    args.forEach { (k, v) -> route = route.replace("{$k}", v) }
    navigate(route)
}

/** Switches top-level destination, keeping each destination's back stack (Material guidance). */
fun NavHostController.switchTo(spec: ScreenSpec) {
    navigate(spec.navRoute) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
