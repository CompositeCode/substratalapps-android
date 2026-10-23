package com.substratalapps.hub.feature.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ColorScheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.substratalapps.hub.data.AppearancePrefs
import com.substratalapps.hub.designsystem.Metrics
import com.substratalapps.hub.designsystem.SectionCard
import com.substratalapps.hub.navigation.ScreenContext
import com.substratalapps.hub.ui.common.ScreenScaffold
import com.substratalapps.hub.ui.theme.DarkTheme
import com.substratalapps.hub.ui.theme.LightTheme
import com.substratalapps.hub.ui.theme.scheme

/** SET-01 Settings — Global: appearance (mode + one of 5 light and 5 dark themes, THEMES.md). */
@Composable
fun SettingsScreen(screen: ScreenContext) {
    val appearance by screen.appViewModel.appearance.collectAsStateWithLifecycle()
    val prefs = appearance ?: AppearancePrefs()
    val set = screen.appViewModel::setAppearance

    ScreenScaffold(screen) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(Metrics.marginCompact),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            SectionCard("Appearance") {
                val modes = listOf("light" to "Light", "dark" to "Dark", "system" to "Match system")
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(16.dp)) {
                    modes.forEachIndexed { i, (id, label) ->
                        SegmentedButton(
                            selected = prefs.mode == id,
                            onClick = { set(prefs.copy(mode = id)) },
                            shape = SegmentedButtonDefaults.itemShape(i, modes.size),
                        ) { Text(label) }
                    }
                }
            }
            ThemeGrid("Light theme", LightTheme.entries.map { Triple(it.id, it.displayName, it.scheme) }, prefs.lightTheme) {
                set(prefs.copy(lightTheme = it))
            }
            ThemeGrid("Dark theme", DarkTheme.entries.map { Triple(it.id, it.displayName, it.scheme) }, prefs.darkTheme) {
                set(prefs.copy(darkTheme = it))
            }
            Text(
                "Themes are saved on this device for now. They'll sync across your devices once the API can store them.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ThemeGrid(title: String, themes: List<Triple<String, String, ColorScheme>>, selected: String, onSelect: (String) -> Unit) {
    SectionCard(title) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            themes.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { (id, name, scheme) ->
                        Swatch(name, scheme, id == selected, Modifier.weight(1f)) { onSelect(id) }
                    }
                    repeat(3 - row.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun Swatch(name: String, scheme: ColorScheme, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier.selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Surface(
            color = scheme.background,
            shape = shape,
            modifier = Modifier.fillMaxWidth().aspectRatio(1.4f).border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = shape,
            ),
        ) {
            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.fillMaxWidth(0.7f).height(8.dp).padding(0.dp)) {
                    Surface(color = scheme.onBackground, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxSize()) {}
                }
                Surface(color = scheme.primary, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth(0.5f).height(14.dp)) {}
                Surface(color = scheme.surfaceContainer, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth().height(14.dp)) {}
            }
        }
        Text(name, style = MaterialTheme.typography.labelMedium)
    }
}
