package com.substratalapps.hub.network

import com.substratalapps.hub.model.ApiVersioning
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

/**
 * Headers every request carries: the API version (required, `400 api_version_required` without it),
 * the app's User-Agent, the active locale and JSON accept. Also records the version the server
 * echoed back and any deprecation notice.
 */
class ApiHeadersInterceptor(
    private val config: ApiConfig,
    private val versionMonitor: ApiVersionMonitor,
    private val locale: () -> Locale = Locale::getDefault,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(ApiVersioning.HEADER, config.apiVersion)
            .header("User-Agent", config.userAgent)
            .header("Accept", "application/json")
            .header("Accept-Language", locale().toLanguageTag())
            .build()
        val response = chain.proceed(request)
        versionMonitor.record(
            served = response.header(ApiVersioning.HEADER),
            deprecation = response.header("Deprecation"),
            sunset = response.header("Sunset"),
        )
        return response
    }
}

/** Supplies and renews the platform session. Implemented by the data layer over secure storage. */
interface SessionTokenProvider {
    /** The current access token, or null when signed out. Memory only. */
    fun accessToken(): String?

    /**
     * Exchanges the refresh token for a new session and persists the rotated refresh token before
     * returning. Returns the new access token, or null if the session has ended. Must be
     * single-flight: a parallel refresh reuses a rotated token, which the API treats as theft.
     */
    fun refreshBlocking(failedAccessToken: String): String?
}

/** Adds `Authorization: Bearer` when a session exists and the call didn't set its own. */
class AuthInterceptor(private val tokens: SessionTokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.header("Authorization") != null || request.header(NO_AUTH) != null) {
            return chain.proceed(request.newBuilder().removeHeader(NO_AUTH).build())
        }
        val token = tokens.accessToken() ?: return chain.proceed(request)
        return chain.proceed(request.newBuilder().header("Authorization", "Bearer $token").build())
    }

    companion object {
        /** Add to a Retrofit method (`@Headers("$NO_AUTH: true")`) to send it without a token. */
        const val NO_AUTH = "X-Substratal-No-Auth"
    }
}

/** On `401`, refresh once and replay; give up if the refresh fails or the replay is also `401`. */
class TokenRefreshAuthenticator(private val tokens: SessionTokenProvider) : okhttp3.Authenticator {
    override fun authenticate(route: okhttp3.Route?, response: Response): okhttp3.Request? {
        val failed = response.request.header("Authorization")?.removePrefix("Bearer ") ?: return null
        if (response.priorResponse != null) return null
        val fresh = tokens.refreshBlocking(failed) ?: return null
        return response.request.newBuilder().header("Authorization", "Bearer $fresh").build()
    }
}
