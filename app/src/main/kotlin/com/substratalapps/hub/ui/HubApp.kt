package com.substratalapps.hub.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.substratalapps.hub.data.AppearancePrefs
import com.substratalapps.hub.data.SessionState
import com.substratalapps.hub.designsystem.LoadingState
import com.substratalapps.hub.feature.auth.MfaScreen
import com.substratalapps.hub.feature.auth.SignInScreen
import com.substratalapps.hub.ui.shell.HubShell
import com.substratalapps.hub.ui.theme.DarkTheme
import com.substratalapps.hub.ui.theme.LightTheme
import com.substratalapps.hub.ui.theme.SubstratalTheme
import com.substratalapps.hub.ui.theme.ThemeMode

@Composable
fun HubApp(viewModel: AppViewModel) {
    val appearance by viewModel.appearance.collectAsStateWithLifecycle()
    val session by viewModel.session.collectAsStateWithLifecycle()
    val prefs = appearance ?: AppearancePrefs()

    SubstratalTheme(
        mode = when (prefs.mode) { "light" -> ThemeMode.LIGHT; "dark" -> ThemeMode.DARK; else -> ThemeMode.SYSTEM },
        light = LightTheme.from(prefs.lightTheme),
        dark = DarkTheme.from(prefs.darkTheme),
    ) {
        when (val s = session) {
            SessionState.Restoring -> LoadingState()
            SessionState.SignedOut -> SignInScreen()
            is SessionState.MfaRequired -> MfaScreen(s.challenge)
            is SessionState.SignedIn -> HubShell(viewModel)
        }
    }
}
