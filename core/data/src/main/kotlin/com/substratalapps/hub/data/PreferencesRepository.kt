package com.substratalapps.hub.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.preferences: DataStore<Preferences> by preferencesDataStore(name = "preferences")

/** Appearance choice as stored ids (THEMES.md): mode light/dark/system plus a light and a dark theme. */
data class AppearancePrefs(
    val mode: String = "system",
    val lightTheme: String = "moss-paper",
    val darkTheme: String = "forest-floor",
)

/**
 * Device-local preferences. Theme ids stay on the device until the API can store them
 * (client OPEN-QUESTIONS #22); applied before first paint, then reconciled with Settings.
 */
@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val modeKey = stringPreferencesKey("theme_mode")
    private val lightKey = stringPreferencesKey("light_theme")
    private val darkKey = stringPreferencesKey("dark_theme")

    val appearance: Flow<AppearancePrefs> = context.preferences.data.map {
        AppearancePrefs(
            mode = it[modeKey] ?: "system",
            lightTheme = it[lightKey] ?: "moss-paper",
            darkTheme = it[darkKey] ?: "forest-floor",
        )
    }

    suspend fun setAppearance(prefs: AppearancePrefs) {
        context.preferences.edit {
            it[modeKey] = prefs.mode
            it[lightKey] = prefs.lightTheme
            it[darkKey] = prefs.darkTheme
        }
    }
}
