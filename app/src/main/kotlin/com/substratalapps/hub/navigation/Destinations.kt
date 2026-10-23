package com.substratalapps.hub.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.ui.graphics.vector.ImageVector
import com.substratalapps.hub.data.Capabilities
import com.substratalapps.hub.model.ScreenCatalog
import com.substratalapps.hub.model.ScreenSpec

/** A navigation item: a screen, its icon (INFORMATION-ARCHITECTURE §5) and who can see it (§2). */
data class NavItem(
    val screenId: String,
    val label: String,
    val icon: ImageVector,
    val visible: (Capabilities) -> Boolean = { true },
) {
    val screen: ScreenSpec get() = ScreenCatalog.byId(screenId)
}

data class NavSection(val title: String?, val items: List<NavItem>)

/** Route of the in-app Help screen; not a design-package screen. */
const val HELP_ROUTE = "help"

object Destinations {
    /** Compact and medium layouts: the five destinations, identical on every platform. */
    val compact: List<NavItem> = listOf(
        NavItem("APP-01", "Home", Icons.Filled.Apps),
        NavItem("NTF-01", "Inbox", Icons.Filled.Inbox),
        NavItem("ORG-01", "Orgs", Icons.Filled.Groups),
        NavItem("GLB-02", "Manage", Icons.Filled.Tune) { it.canViewOperations || it.showsAdministration || it.showsDeveloper },
        NavItem("ACC-01", "Account", Icons.Filled.AccountCircle),
    )

    /** Expanded layouts: the full tree in a permanent drawer. Operations is this app's addition. */
    val tree: List<NavSection> = listOf(
        NavSection(
            "Personal",
            listOf(
                NavItem("APP-01", "Home", Icons.Filled.Apps),
                NavItem("NTF-01", "Inbox", Icons.Filled.Inbox),
                NavItem("ORG-01", "Organizations", Icons.Filled.Groups),
                NavItem("ACC-01", "Account", Icons.Filled.AccountCircle),
                NavItem("BIL-01", "Plan & Billing", Icons.Filled.CreditCard),
                NavItem("SET-01", "Settings", Icons.Filled.Settings),
            ),
        ),
        NavSection(
            "Operations",
            listOf(
                NavItem("OPS-01", "Overview", Icons.Filled.Dashboard, Capabilities::canViewOperations),
                NavItem("OPS-02", "Traffic & latency", Icons.AutoMirrored.Filled.ShowChart, Capabilities::canViewOperations),
                NavItem("OPS-03", "Errors", Icons.Filled.BugReport, Capabilities::canViewOperations),
                NavItem("OPS-04", "Usage", Icons.Filled.QueryStats, Capabilities::canViewOperations),
                NavItem("OPS-05", "Jobs & delivery", Icons.Filled.WorkHistory, Capabilities::canViewOperations),
                NavItem("OPS-06", "Alerts", Icons.Filled.NotificationsActive, Capabilities::canViewOperations),
                NavItem("OPS-07", "Security events", Icons.Filled.Security, Capabilities::canViewOperations),
                NavItem("OPS-08", "Configuration", Icons.Filled.Bolt, Capabilities::canViewOperations),
                NavItem("OPS-09", "Integrations", Icons.Filled.Extension, Capabilities::canViewOperations),
                NavItem("OPS-10", "Cost", Icons.Filled.Paid, Capabilities::canViewOperations),
            ),
        ),
        NavSection(
            "Administration",
            listOf(
                NavItem("USR-01", "Users", Icons.Filled.Group, Capabilities::canListUsers),
                NavItem("ACX-05", "Access", Icons.Filled.ToggleOn) { it.canManageEntitlements || it.ownsApps },
                NavItem("ROL-01", "Roles", Icons.Filled.AdminPanelSettings, Capabilities::canListUsers),
                NavItem("AUD-01", "Audit Log", Icons.Filled.History, Capabilities::canViewAudit),
                NavItem("NTF-06", "Send notification", Icons.Filled.NotificationAdd, Capabilities::canSendNotifications),
            ),
        ),
        NavSection(
            "Developer",
            listOf(
                NavItem("APL-01", "Applications", Icons.Filled.Widgets) { it.showsDeveloper },
                NavItem("KEY-01", "API Keys", Icons.Filled.Key) { it.ownsApps || it.canManageApiKeys },
                NavItem("WHK-01", "Webhooks", Icons.Filled.Webhook) { it.ownsApps || it.canManageWebhooks },
                NavItem("BIL-04", "Tenant", Icons.Filled.Dns) { it.ownsApps || it.canManageTenants },
            ),
        ),
    )

    val helpIcon: ImageVector = Icons.AutoMirrored.Filled.HelpOutline
    val activityIcon: ImageVector = Icons.Filled.Timeline
}
