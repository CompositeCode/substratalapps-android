package com.substratalapps.hub.feature.ops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.substratalapps.hub.designsystem.Metrics
import com.substratalapps.hub.designsystem.SectionCard
import com.substratalapps.hub.designsystem.StatTile
import com.substratalapps.hub.designsystem.StatusBadge
import com.substratalapps.hub.navigation.ScreenContext
import com.substratalapps.hub.ui.common.ScreenScaffold

/**
 * OPS-01 Overview — preview. The layout is final; the numbers are sample data until the API has
 * usage and operations endpoints (issues #43, #44, #47). Only API reachability is live.
 */
@Composable
fun OpsOverviewScreen(screen: ScreenContext) {
    var apiReachable by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(Unit) { apiReachable = runCatching { screen.appViewModel.capabilities.value.user != null }.getOrDefault(false) }

    ScreenScaffold(screen) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(Metrics.marginCompact),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Science, contentDescription = null)
                    Text(
                        "Preview with sample data. Live numbers arrive with the API's operations endpoints.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            SectionCard("Components") {
                Column {
                    listOf("API", "MCP server", "Stripe webhook", "Delivery worker", "Scheduled jobs", "Database").forEachIndexed { i, name ->
                        if (i > 0) HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(name) },
                            trailingContent = {
                                val live = name == "API"
                                StatusBadge("component", if (!live || apiReachable == true) "active" else "pending")
                            },
                        )
                    }
                }
            }

            Text("Last 24 hours", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatTile("Requests", "12.4k", "sample", Modifier.weight(1f))
                StatTile("5xx", "0.21%", "sample", Modifier.weight(1f))
                StatTile("p95", "88 ms", "target 150", Modifier.weight(1f))
            }

            SectionCard("Alerts") {
                ListItem(
                    headlineContent = { Text("webhook-dlq depth > 0") },
                    supportingContent = { Text("Sample alert") },
                    trailingContent = { StatusBadge("alert", "pending") },
                    modifier = Modifier.clickable { screen.navigate("OPS-06") },
                )
            }

            SectionCard("Budget this month") {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("$41 of $75 warning budget (sample)")
                    LinearProgressIndicator(progress = { 41f / 75f }, modifier = Modifier.fillMaxWidth())
                }
            }

            SectionCard("More") {
                Column {
                    listOf("OPS-02" to "Traffic & latency", "OPS-03" to "Errors", "OPS-05" to "Jobs & delivery", "OPS-10" to "Cost")
                        .forEachIndexed { i, (id, label) ->
                            if (i > 0) HorizontalDivider()
                            ListItem(headlineContent = { Text(label) }, modifier = Modifier.clickable { screen.navigate(id) })
                        }
                }
            }
        }
    }
}
