package com.substratalapps.hub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.substratalapps.hub.data.SessionState
import com.substratalapps.hub.ui.AppViewModel
import com.substratalapps.hub.ui.HubApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        // Keep the splash up while the stored session is restored, so the first frame is the right one.
        splash.setKeepOnScreenCondition { appViewModel.session.value is SessionState.Restoring }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { HubApp(appViewModel) }
    }

    /** singleTask: links that arrive while the app is open (substratal://…, App Links) come here. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        appViewModel.openDeepLink(intent)
    }
}
