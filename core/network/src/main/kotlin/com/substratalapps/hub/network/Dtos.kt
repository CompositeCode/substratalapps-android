package com.substratalapps.hub.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// Hand-written models for the operations the skeleton calls. Field names follow openapi.yaml.
// Unknown fields are ignored, so additive API changes within a version never break decoding.

@Serializable
data class Health(val status: String)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    @SerialName("test_mode") val testMode: Boolean = false,
)

@Serializable
data class MfaVerifyRequest(
    @SerialName("mfa_token") val mfaToken: String,
    val code: String? = null,
    @SerialName("recovery_code") val recoveryCode: String? = null,
)

@Serializable
data class RefreshRequest(@SerialName("refresh_token") val refreshToken: String)

@Serializable
data class LogoutRequest(@SerialName("all_sessions") val allSessions: Boolean = false)

@Serializable
data class AuthSession(
    @SerialName("token_type") val tokenType: String,
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("refresh_token_expires_in") val refreshTokenExpiresIn: Long? = null,
    @SerialName("session_id") val sessionId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("email_verified") val emailVerified: Boolean? = null,
)

@Serializable
data class MfaChallenge(
    @SerialName("mfa_required") val mfaRequired: Boolean,
    @SerialName("mfa_token") val mfaToken: String,
    val methods: List<String>,
    @SerialName("expires_in") val expiresIn: Long,
)

/** `POST /auth/login` returns either a session or an MFA challenge (`oneOf`). */
sealed interface LoginResult {
    data class SignedIn(val session: AuthSession) : LoginResult
    data class MfaRequired(val challenge: MfaChallenge) : LoginResult
}

@Serializable
data class PageInfo(
    @SerialName("next_cursor") val nextCursor: String? = null,
    @SerialName("has_more") val hasMore: Boolean = false,
)

@Serializable
data class Page<T>(val data: List<T>, val page: PageInfo)

@Serializable
data class User(
    val id: String,
    val email: String? = null,
    @SerialName("email_verified") val emailVerified: Boolean = false,
    val status: String,
    @SerialName("mfa_enabled") val mfaEnabled: Boolean = false,
    @SerialName("test_mode") val testMode: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("last_login_at") val lastLoginAt: String? = null,
    val plan: String? = null,
)

@Serializable
data class ApplicationSummary(
    val id: String,
    val slug: String,
    val name: String,
    val description: String? = null,
    @SerialName("icon_url") val iconUrl: String? = null,
    val visibility: String? = null,
    @SerialName("access_tier") val accessTier: String? = null,
    @SerialName("review_status") val reviewStatus: String? = null,
)

@Serializable
data class UserRoleAssignment(
    @SerialName("user_id") val userId: String,
    @SerialName("role_id") val roleId: String,
    @SerialName("application_id") val applicationId: String? = null,
    val implicit: Boolean = false,
)

@Serializable
data class Role(
    val id: String,
    val name: String,
    val scope: String,
    val description: String? = null,
    val permissions: List<String> = emptyList(),
    val seed: Boolean = false,
)

/** The standard error envelope: `{"error":{"code","message","details"}}`. */
@Serializable
data class ErrorEnvelope(val error: ErrorBody)

@Serializable
data class ErrorBody(val code: String, val message: String, val details: JsonObject? = null)
