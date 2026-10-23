package com.substratalapps.hub.network

import kotlinx.serialization.json.JsonObject

/**
 * A non-2xx response decoded from the standard error envelope. Screens map [code] to copy
 * (COPY-AND-STATES); [message] is only shown inside a "Details" disclosure with [requestId].
 */
class ApiException(
    val status: Int,
    val code: String,
    override val message: String,
    val details: JsonObject? = null,
    val requestId: String? = null,
    val retryAfterSeconds: Long? = null,
) : Exception("$status $code: $message") {
    val isUnauthenticated get() = status == 401
    val isForbidden get() = status == 403
    val isNotImplemented get() = status == 501
    val isRateLimited get() = status == 429

    /** The request named an API version this server doesn't serve: the app is too old or too new. */
    val isUnsupportedVersion get() = code == "unsupported_api_version" || code == "api_version_required"
}

/** No response at all: offline, DNS, TLS or timeout. */
class NetworkUnavailableException(cause: Throwable) : Exception(cause.message, cause)
