package com.substratalapps.hub.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.substratalapps.hub.data.SessionRepository
import com.substratalapps.hub.network.ApiException
import com.substratalapps.hub.network.NetworkUnavailableException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val busy: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(private val sessions: SessionRepository) : ViewModel() {
    private val state = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = state.asStateFlow()

    fun signIn(email: String, password: String) = run { sessions.signIn(email, password) }

    fun verify(code: String, useRecoveryCode: Boolean) = run {
        if (useRecoveryCode) sessions.verifyMfa(null, code.trim()) else sessions.verifyMfa(code.trim(), null)
    }

    fun cancelMfa() = sessions.cancelMfa()

    private fun run(block: suspend () -> Unit) {
        if (state.value.busy) return
        state.value = AuthUiState(busy = true)
        viewModelScope.launch {
            val error = runCatching { block() }.exceptionOrNull()
            state.update { AuthUiState(busy = false, error = error?.let(::messageFor)) }
        }
    }
}

/** Copy for sign-in errors (COPY-AND-STATES). Never reveals whether an account exists. */
internal fun messageFor(e: Throwable): String = when (e) {
    is NetworkUnavailableException -> "You're offline. Check your connection and try again."
    is ApiException -> when {
        e.code == "invalid_credentials" -> "That email and password don't match."
        e.code == "invalid_mfa_code" -> "That code didn't work. Check your authenticator app and try again."
        e.code == "too_many_attempts" -> "Too many attempts. Wait 15 minutes and try again."
        e.code == "account_suspended" -> "This account is suspended. Contact your administrator."
        e.code == "test_user_expired" -> "This test account has expired."
        e.isRateLimited -> "Too many attempts. Try again in ${e.retryAfterSeconds ?: 60} seconds."
        e.isUnsupportedVersion -> "This version of the app is no longer supported. Update it to continue."
        e.status == 401 -> "That email and password don't match."
        else -> "Something went wrong (${e.code}). Try again."
    }
    else -> "Something went wrong. Try again."
}
