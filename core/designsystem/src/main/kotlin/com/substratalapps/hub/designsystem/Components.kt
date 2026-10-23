package com.substratalapps.hub.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Layout metrics from PLATFORM-SPEC §5. */
object Metrics {
    val marginCompact = 16.dp
    val marginWide = 24.dp
    val cardShape = 24.dp
}

@Composable
private fun roleColors(role: SemanticRole): Pair<Color, Color> {
    val c = MaterialTheme.colorScheme
    return when (role) {
        SemanticRole.SUCCESS -> c.primaryContainer to c.onPrimaryContainer
        SemanticRole.INFO -> c.secondaryContainer to c.onSecondaryContainer
        SemanticRole.ACCENT -> c.tertiaryContainer to c.onTertiaryContainer
        SemanticRole.WARNING -> c.surfaceContainerHighest to c.onSurface
        SemanticRole.CRITICAL -> c.errorContainer to c.onErrorContainer
        SemanticRole.NEUTRAL -> c.surfaceContainerHigh to c.onSurfaceVariant
    }
}

/** Status badge: icon + label, never colour alone (ACCESSIBILITY). */
@Composable
fun StatusBadge(field: String, value: String, modifier: Modifier = Modifier) {
    val style = StatusSemantics.style(field, value)
    val (container, content) = roleColors(style.role)
    Surface(
        color = container,
        contentColor = content,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.semantics { contentDescription = "Status: ${style.label}" },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(materialIcon(style.materialIcon), contentDescription = null, modifier = Modifier.size(14.dp))
            Text(style.label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Monospace ID chip, e.g. `usr_01J…` (IDs are shown monospace everywhere). */
@Composable
fun IdText(id: String, modifier: Modifier = Modifier) {
    Text(
        id,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

@Composable
fun MessageState(
    icon: ImageVector,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}

@Composable
fun EmptyState(title: String, body: String, modifier: Modifier = Modifier) =
    MessageState(Icons.Filled.Inbox, title, body, modifier)

@Composable
fun ErrorState(title: String, body: String, onRetry: () -> Unit, modifier: Modifier = Modifier) =
    MessageState(Icons.Filled.ErrorOutline, title, body, modifier, "Try again", onRetry)

@Composable
fun OfflineState(onRetry: () -> Unit, modifier: Modifier = Modifier) =
    MessageState(Icons.Filled.CloudOff, "You're offline", "Connect to the internet and try again.", modifier, "Try again", onRetry)

/** A metric tile for dashboards (OPS-01): label, big value, optional caption. */
@Composable
fun StatTile(label: String, value: String, caption: String?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall)
            if (caption != null) {
                Text(caption, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** Grouped section card with a title, used by settings and detail screens. */
@Composable
fun SectionCard(title: String?, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(PaddingValues(start = 4.dp, bottom = 8.dp)),
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier.fillMaxWidth(),
        ) { content() }
    }
}
