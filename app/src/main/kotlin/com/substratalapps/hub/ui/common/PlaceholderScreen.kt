package com.substratalapps.hub.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.substratalapps.hub.designsystem.IdText
import com.substratalapps.hub.designsystem.Metrics
import com.substratalapps.hub.designsystem.SectionCard
import com.substratalapps.hub.designsystem.StatusBadge
import com.substratalapps.hub.help.HelpLinks
import com.substratalapps.hub.model.ImplementationStatus
import com.substratalapps.hub.navigation.ScreenContext

private const val API_ISSUES = "https://github.com/CompositeCode/substratalapps.com/issues/"

/**
 * Stand-in for a screen that isn't built yet. It's still a real destination with its route and
 * deep link, and shows the screen's spec: purpose, who can use it, API calls and blocking API gaps.
 */
@Composable
fun PlaceholderScreen(screen: ScreenContext) {
    val spec = screen.spec
    val context = LocalContext.current
    ScreenScaffold(screen) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(Metrics.marginCompact),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IdText(spec.id)
                StatusBadge("build_state", ImplementationStatus.of(spec.id).label)
                StatusBadge("phase", spec.phase.label)
            }
            Text(spec.purpose.ifBlank { "Part of the ${spec.section.label} section." }, style = MaterialTheme.typography.bodyLarge)

            SectionCard("Who can use it") {
                Text(spec.who.ifBlank { "Everyone signed in" }, Modifier.padding(16.dp))
            }

            if (spec.pathParameters.isNotEmpty()) {
                SectionCard("Opened with") {
                    Column {
                        spec.pathParameters.forEach { p ->
                            ListItem(headlineContent = { Text(p) }, supportingContent = { IdText(screen.arg(p) ?: "—") })
                        }
                    }
                }
            }

            if (spec.operations.isNotEmpty()) {
                SectionCard("API calls") {
                    Column {
                        spec.operations.forEachIndexed { i, op ->
                            if (i > 0) HorizontalDivider()
                            ListItem(
                                headlineContent = {
                                    Text("${op.method} ${op.path}", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium)
                                },
                                supportingContent = { Text(op.summary) },
                            )
                        }
                    }
                }
            }

            if (spec.apiGaps.isNotEmpty()) {
                SectionCard("Waiting on the API") {
                    Column {
                        spec.apiGaps.forEach { ref ->
                            ListItem(
                                headlineContent = { Text("Issue $ref") },
                                supportingContent = { Text("CompositeCode/substratalapps.com") },
                                modifier = Modifier.padding(0.dp),
                            )
                        }
                    }
                }
                OutlinedButton(onClick = { HelpLinks.open(context, API_ISSUES + spec.apiGaps.first().removePrefix("#")) }) {
                    Text("Open the API issue")
                }
            }

            OutlinedButton(onClick = { HelpLinks.open(context, HelpLinks.forScreen(spec)) }) { Text("Read about this screen") }
            Text(
                "Route ${spec.route} · ${spec.archetype.name.lowercase()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
