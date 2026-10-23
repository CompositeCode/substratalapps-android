package com.substratalapps.hub.network

import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The Platform API operations the app calls so far. Paths are relative to the base URL and carry
 * no version; [ApiHeadersInterceptor] sends `Substratal-Version`. Grows screen by screen; the
 * full contract is openapi/openapi.yaml.
 */
interface SubstratalApi {
    @GET("health")
    suspend fun health(): Health

    @Headers("${AuthInterceptor.NO_AUTH}: true")
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): JsonObject

    @Headers("${AuthInterceptor.NO_AUTH}: true")
    @POST("auth/mfa/verify")
    suspend fun verifyMfa(@Body body: MfaVerifyRequest): AuthSession

    @Headers("${AuthInterceptor.NO_AUTH}: true")
    @POST("auth/token/refresh")
    suspend fun refresh(@Body body: RefreshRequest): AuthSession

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequest)

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String = "me"): User

    @GET("users")
    suspend fun listUsers(
        @Query("limit") limit: Int = 25,
        @Query("cursor") cursor: String? = null,
        @Query("status") status: String? = null,
        @Query("q") query: String? = null,
    ): Page<User>

    @GET("users/{id}/roles")
    suspend fun listUserRoles(@Path("id") id: String = "me"): Page<UserRoleAssignment>

    @GET("roles")
    suspend fun listRoles(
        @Query("scope") scope: String = "platform",
        @Query("limit") limit: Int = 50,
    ): Page<Role>

    @GET("applications")
    suspend fun listApplications(
        @Query("limit") limit: Int = 25,
        @Query("cursor") cursor: String? = null,
        @Query("owned") owned: Boolean? = null,
        @Query("review_status") reviewStatus: String? = null,
    ): Page<ApplicationSummary>
}
