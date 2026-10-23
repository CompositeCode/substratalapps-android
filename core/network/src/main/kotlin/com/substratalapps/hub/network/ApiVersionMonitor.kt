package com.substratalapps.hub.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * What the API said about the version this app requested. When a version is deprecated, every
 * response carries `Deprecation` and `Sunset` headers (Conventions → Versioning); the app shows an
 * "Update the app" banner instead of waiting for `400 unsupported_api_version` after the sunset.
 */
data class ApiVersionStatus(
    val requested: String,
    val served: String? = null,
    val deprecation: String? = null,
    val sunset: String? = null,
) {
    val isDeprecated: Boolean get() = deprecation != null || sunset != null
}

class ApiVersionMonitor(requested: String) {
    private val state = MutableStateFlow(ApiVersionStatus(requested))
    val status: StateFlow<ApiVersionStatus> = state.asStateFlow()

    internal fun record(served: String?, deprecation: String?, sunset: String?) {
        val next = state.value.copy(
            served = served ?: state.value.served,
            deprecation = deprecation,
            sunset = sunset,
        )
        if (next != state.value) state.value = next
    }
}
