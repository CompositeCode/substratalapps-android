package com.substratalapps.hub.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.time.Duration

val ApiJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    coerceInputValues = true
}

/** Builds the one OkHttp client and Retrofit service the app uses (CLIENT-ARCHITECTURE §4). */
object ApiClientFactory {
    fun okHttp(
        config: ApiConfig,
        tokens: SessionTokenProvider,
        versionMonitor: ApiVersionMonitor,
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(Duration.ofSeconds(10))
        .readTimeout(Duration.ofSeconds(30))
        .addInterceptor(ApiHeadersInterceptor(config, versionMonitor))
        .addInterceptor(AuthInterceptor(tokens))
        .authenticator(TokenRefreshAuthenticator(tokens))
        .build()

    fun api(config: ApiConfig, client: OkHttpClient): SubstratalApi = Retrofit.Builder()
        .baseUrl(config.environment.baseUrl)
        .client(client)
        .addConverterFactory(ApiJson.asConverterFactory("application/json; charset=utf-8".toMediaType()))
        .build()
        .create(SubstratalApi::class.java)
}

/** Runs an API call, turning HTTP and I/O failures into [ApiException] / [NetworkUnavailableException]. */
suspend fun <T> apiCall(block: suspend () -> T): T = try {
    block()
} catch (e: HttpException) {
    throw e.toApiException()
} catch (e: IOException) {
    throw NetworkUnavailableException(e)
}

fun HttpException.toApiException(): ApiException {
    val raw = response()
    val body = raw?.errorBody()?.string()
    val envelope = body?.let { runCatching { ApiJson.decodeFromString(ErrorEnvelope.serializer(), it) }.getOrNull() }
    return ApiException(
        status = code(),
        code = envelope?.error?.code ?: "http_${code()}",
        message = envelope?.error?.message ?: message(),
        details = envelope?.error?.details,
        requestId = raw?.headers()?.get("X-Request-Id"),
        retryAfterSeconds = raw?.headers()?.get("Retry-After")?.toLongOrNull(),
    )
}

/** Decodes the `oneOf` login response. */
fun decodeLoginResult(json: JsonObject): LoginResult =
    if (json["mfa_required"]?.jsonPrimitive?.boolean == true) {
        LoginResult.MfaRequired(ApiJson.decodeFromJsonElement(MfaChallenge.serializer(), json))
    } else {
        LoginResult.SignedIn(ApiJson.decodeFromJsonElement(AuthSession.serializer(), json))
    }
