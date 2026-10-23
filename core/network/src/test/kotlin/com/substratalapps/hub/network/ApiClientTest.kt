package com.substratalapps.hub.network

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

class ApiClientTest {
    private lateinit var server: MockWebServer
    private val config = ApiConfig(userAgent = "Substratal-Android/test (JVM)")
    private val monitor = ApiVersionMonitor(config.apiVersion)
    private var accessToken: String? = "old"
    private var refreshes = 0

    private val tokens = object : SessionTokenProvider {
        override fun accessToken() = accessToken
        override fun refreshBlocking(failedAccessToken: String): String? {
            refreshes++
            accessToken = "new"
            return accessToken
        }
    }

    private lateinit var api: SubstratalApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val client = ApiClientFactory.okHttp(config, tokens, monitor)
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(ApiJson.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SubstratalApi::class.java)
    }

    @After
    fun tearDown() = server.close()

    private fun json(body: String, code: Int = 200, vararg headers: Pair<String, String>) =
        MockResponse.Builder().code(code).body(body).apply {
            addHeader("Content-Type", "application/json")
            headers.forEach { (k, v) -> addHeader(k, v) }
        }.build()

    @Test
    fun `every request sends the version header and no version in the path`() = runTest {
        server.enqueue(json("""{"data":[],"page":{"next_cursor":null,"has_more":false}}""", 200, "Substratal-Version" to "1"))
        api.listUsers()
        val recorded = server.takeRequest()
        assertEquals("1", recorded.headers["Substratal-Version"])
        assertTrue(recorded.target.startsWith("/users"))
        assertEquals("Bearer old", recorded.headers["Authorization"])
        assertEquals("1", monitor.status.value.served)
        assertFalse(monitor.status.value.isDeprecated)
    }

    @Test
    fun `login is sent without a token and decodes an MFA challenge`() = runTest {
        server.enqueue(json("""{"mfa_required":true,"mfa_token":"m","methods":["totp"],"expires_in":300}"""))
        val result = decodeLoginResult(api.login(LoginRequest("a@example.com", "pw")))
        assertTrue(result is LoginResult.MfaRequired)
        assertNull(server.takeRequest().headers["Authorization"])
    }

    @Test
    fun `error envelope decodes into ApiException`() = runTest {
        server.enqueue(
            json(
                """{"error":{"code":"unsupported_api_version","message":"no","details":{"supported":["1"]}}}""",
                400, "X-Request-Id" to "req_1",
            ),
        )
        val e = runCatching { apiCall { api.getUser() } }.exceptionOrNull() as ApiException
        assertEquals(400, e.status)
        assertTrue(e.isUnsupportedVersion)
        assertEquals("req_1", e.requestId)
    }

    @Test
    fun `401 triggers one refresh and a replay`() = runTest {
        server.enqueue(json("""{"error":{"code":"unauthenticated","message":"x"}}""", 401))
        server.enqueue(json("""{"id":"usr_1","status":"active"}"""))
        val user = api.getUser()
        assertEquals("usr_1", user.id)
        assertEquals(1, refreshes)
        server.takeRequest()
        assertEquals("Bearer new", server.takeRequest().headers["Authorization"])
    }

    @Test
    fun `deprecation and sunset headers are surfaced`() = runTest {
        server.enqueue(json("""{"status":"ok"}""", 200, "Deprecation" to "@1767225600", "Sunset" to "Wed, 01 Jul 2027 00:00:00 GMT"))
        api.health()
        assertTrue(monitor.status.value.isDeprecated)
        assertEquals("Wed, 01 Jul 2027 00:00:00 GMT", monitor.status.value.sunset)
    }
}
