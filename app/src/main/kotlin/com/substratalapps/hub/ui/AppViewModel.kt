package com.substratalapps.hub.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.substratalapps.hub.data.AppearancePrefs
import com.substratalapps.hub.data.Capabilities
import com.substratalapps.hub.data.CapabilitiesRepository
import com.substratalapps.hub.data.PreferencesRepository
import com.substratalapps.hub.data.SessionRepository
import com.substratalapps.hub.data.SessionState
import com.substratalapps.hub.network.ApiVersionMonitor
import com.substratalapps.hub.network.ApiVersionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** App-wide state: session, capabilities, appearance and the API version notice. */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val sessions: SessionRepository,
    private val capabilitiesRepository: CapabilitiesRepository,
    private val preferences: PreferencesRepository,
    versionMonitor: ApiVersionMonitor,
) : ViewModel() {
    val session: StateFlow<SessionState> = sessions.session
    val capabilities: StateFlow<Capabilities> = capabilitiesRepository.capabilities
    val apiVersion: StateFlow<ApiVersionStatus> = versionMonitor.status
    val appearance: StateFlow<AppearancePrefs?> =
        preferences.appearance.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val deepLinks = MutableStateFlow<Intent?>(null)

    /** A link delivered while the app was running, waiting for the shell's NavController. */
    val pendingDeepLink: StateFlow<Intent?> = deepLinks.asStateFlow()

    fun openDeepLink(intent: Intent) {
        deepLinks.value = intent
    }

    fun deepLinkHandled() {
        deepLinks.value = null
    }

    init {
        viewModelScope.launch { sessions.restore() }
        viewModelScope.launch {
            sessions.session.distinctUntilChangedBy { it is SessionState.SignedIn }.collect {
                if (it is SessionState.SignedIn) refreshCapabilities() else capabilitiesRepository.clear()
            }
        }
    }

    fun refreshCapabilities() {
        viewModelScope.launch { runCatching { capabilitiesRepository.refresh() } }
    }

    fun signOut() {
        viewModelScope.launch { sessions.signOut() }
    }

    fun setAppearance(prefs: AppearancePrefs) {
        viewModelScope.launch { preferences.setAppearance(prefs) }
    }
}
