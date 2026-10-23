package com.substratalapps.hub.data

import com.substratalapps.hub.network.ApiException
import com.substratalapps.hub.network.AuthSession
import com.substratalapps.hub.network.LoginRequest
import com.substratalapps.hub.network.LoginResult
import com.substratalapps.hub.network.LogoutRequest
import com.substratalapps.hub.network.MfaChallenge
import com.substratalapps.hub.network.MfaVerifyRequest
import com.substratalapps.hub.network.RefreshRequest
import com.substratalapps.hub.network.SessionTokenProvider
import com.substratalapps.hub.network.SubstratalApi
import com.substratalapps.hub.network.apiCall
import com.substratalapps.hub.network.decodeLoginResult
import dagger.Lazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

sealed interface SessionState {
    data object Restoring : SessionState
    data object SignedOut : SessionState
    data class MfaRequired(val challenge: MfaChallenge) : SessionState
    data class SignedIn(val userId: String, val sessionId: String) : SessionState
}

/**
 * Owns the platform session: sign-in, MFA, restore on launch, single-flight refresh and sign-out.
 * The access token lives in memory only; the refresh token in [SecureTokenStore].
 */
@Singleton
class SessionRepository @Inject constructor(
    // Lazy: the API's OkHttp client depends on this class as its SessionTokenProvider.
    private val api: Lazy<SubstratalApi>,
    private val store: SecureTokenStore,
) : SessionTokenProvider {
    private val state = MutableStateFlow<SessionState>(SessionState.Restoring)
    val session: StateFlow<SessionState> = state.asStateFlow()

    @Volatile private var accessToken: String? = null
    private val refreshLock = Any()

    override fun accessToken(): String? = accessToken

    /** Called on launch: exchange a stored refresh token for a session, or show sign-in. */
    suspend fun restore() {
        val refresh = store.read()
        if (refresh == null) {
            state.value = SessionState.SignedOut
            return
        }
        state.value = runCatching { adopt(apiCall { api.get().refresh(RefreshRequest(refresh)) }) }
            .getOrElse { error ->
                if (error is ApiException) store.clear()
                // Offline with a stored session: stay signed out of the network but keep the token
                // so the next attempt can restore. Screens show the offline state.
                SessionState.SignedOut
            }
    }

    suspend fun signIn(email: String, password: String): LoginResult {
        val result = decodeLoginResult(apiCall { api.get().login(LoginRequest(email.trim(), password)) })
        when (result) {
            is LoginResult.SignedIn -> state.value = adopt(result.session)
            is LoginResult.MfaRequired -> state.value = SessionState.MfaRequired(result.challenge)
        }
        return result
    }

    suspend fun verifyMfa(code: String?, recoveryCode: String?) {
        val challenge = (state.value as? SessionState.MfaRequired)?.challenge
            ?: error("No MFA challenge in progress")
        val session = apiCall {
            api.get().verifyMfa(MfaVerifyRequest(challenge.mfaToken, code = code, recoveryCode = recoveryCode))
        }
        state.value = adopt(session)
    }

    fun cancelMfa() {
        state.value = SessionState.SignedOut
    }

    /** Sign out = `POST /auth/logout`, then wipe credentials (CLIENT-ARCHITECTURE §3 rule 4). */
    suspend fun signOut(allSessions: Boolean = false) {
        runCatching { apiCall { api.get().logout(LogoutRequest(allSessions)) } }
        endSession()
    }

    override fun refreshBlocking(failedAccessToken: String): String? = synchronized(refreshLock) {
        // Another thread already refreshed while this one waited: reuse its token.
        accessToken?.let { if (it != failedAccessToken) return it }
        val refresh = runBlocking { store.read() } ?: return null
        return runCatching {
            runBlocking {
                val session = apiCall { api.get().refresh(RefreshRequest(refresh)) }
                state.value = adopt(session)
                session.accessToken
            }
        }.getOrElse {
            runBlocking { endSession() }
            null
        }
    }

    private suspend fun adopt(session: AuthSession): SessionState.SignedIn {
        store.write(session.refreshToken)
        accessToken = session.accessToken
        return SessionState.SignedIn(session.userId, session.sessionId)
    }

    private suspend fun endSession() {
        accessToken = null
        store.clear()
        state.value = SessionState.SignedOut
    }
}
