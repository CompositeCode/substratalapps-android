package com.substratalapps.hub.feature.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.substratalapps.hub.BuildConfig
import com.substratalapps.hub.designsystem.IdText
import com.substratalapps.hub.designsystem.Metrics
import com.substratalapps.hub.designsystem.SectionCard
import com.substratalapps.hub.designsystem.StatusBadge
import com.substratalapps.hub.model.ApiVersioning
import com.substratalapps.hub.navigation.Destinations
import com.substratalapps.hub.navigation.HELP_ROUTE
import com.substratalapps.hub.navigation.ScreenContext
import com.substratalapps.hub.ui.common.ScreenScaffold

/** ACC-01 Account & profile. On compact layouts it also holds Plan & Billing, Settings and Help. */
@Composable
fun AccountScreen(screen: ScreenContext) {
    val capabilities by screen.appViewModel.capabilities.collectAsStateWithLifecycle()
    val apiVersion by screen.appViewModel.apiVersion.collectAsStateWithLifecycle()
    var confirmSignOut by remember { mutableStateOf(false) }
    val user = capabilities.user

    ScreenScaffold(screen) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(Metrics.marginCompact),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionCard(null) {
                ListItem(
                    headlineContent = { Text(user?.email ?: "Loading…", style = MaterialTheme.typography.titleMedium) },
                    supportingContent = { user?.let { IdText(it.id) } },
                    trailingContent = { user?.let { StatusBadge("user.status", it.status) } },
                )
            }
            SectionCard("Account") {
                Column {
                    Row(Icons.Filled.Lock, "Security", if (user?.mfaEnabled == true) "Two-factor on" else "Two-factor off") {
                        screen.navigate("ACC-04")
                    }
                    HorizontalDivider()
                    Row(Icons.Filled.Devices, "Devices & notifications", null) { screen.navigate("NTF-03") }
                    HorizontalDivider()
                    Row(Icons.Filled.PrivacyTip, "Privacy", null) { screen.navigate("ACC-07") }
                }
            }
            SectionCard("App") {
                Column {
                    Row(Icons.Filled.CreditCard, "Plan & Billing", user?.plan?.replaceFirstChar { it.uppercase() }) { screen.navigate("BIL-01") }
                    HorizontalDivider()
                    Row(Icons.Filled.Settings, "Settings", "Appearance and preferences") { screen.navigate("SET-01") }
                    HorizontalDivider()
                    Row(Destinations.helpIcon, "Help & documentation", "Guides, tutorials, FAQ and API reference") {
                        screen.navController.navigate(HELP_ROUTE)
                    }
                    HorizontalDivider()
                    Row(Icons.AutoMirrored.Filled.Logout, "Sign out", null) { confirmSignOut = true }
                }
            }
            Text(
                "Substratal for Android ${BuildConfig.VERSION_NAME} · API version ${apiVersion.served ?: ApiVersioning.CURRENT}" +
                    " (spec ${ApiVersioning.SPEC_VERSION})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    if (confirmSignOut) {
        AlertDialog(
            onDismissRequest = { confirmSignOut = false },
            title = { Text("Sign out?") },
            text = { Text("You'll need your password and two-factor code to sign in again.") },
            confirmButton = { TextButton(onClick = { confirmSignOut = false; screen.appViewModel.signOut() }) { Text("Sign out") } },
            dismissButton = { TextButton(onClick = { confirmSignOut = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun Row(icon: ImageVector, title: String, supporting: String?, onClick: () -> Unit) {
    ListItem(
        leadingContent = { Icon(icon, contentDescription = null) },
        headlineContent = { Text(title) },
        supportingContent = supporting?.let { { Text(it) } },
        modifier = Modifier.clickable(onClick = onClick),
    )
}
