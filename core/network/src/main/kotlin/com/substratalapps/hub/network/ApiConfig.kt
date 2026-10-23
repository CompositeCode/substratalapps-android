package com.substratalapps.hub.network

import com.substratalapps.hub.model.ApiVersioning

/** Where the app sends requests. Base URLs carry no version segment: the API is versioned by header. */
enum class ApiEnvironment(val label: String, val baseUrl: String) {
    PRODUCTION("Production", "https://api.substratalapps.com/"),

    /** The API repo's devserver (`make run`) as seen from the Android emulator. */
    LOCAL("Local devserver", "http://10.0.2.2:8080/"),
}

data class ApiConfig(
    val environment: ApiEnvironment = ApiEnvironment.PRODUCTION,
    /** `Substratal-Android/{version} ({os})`; the API shows it in Sessions (ACC-04, USR-04). */
    val userAgent: String,
    val apiVersion: String = ApiVersioning.CURRENT,
)
