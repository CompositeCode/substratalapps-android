package com.substratalapps.hub.data

import com.substratalapps.hub.network.ApiClientFactory
import com.substratalapps.hub.network.ApiConfig
import com.substratalapps.hub.network.ApiVersionMonitor
import com.substratalapps.hub.network.SessionTokenProvider
import com.substratalapps.hub.network.SubstratalApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/** Wires the API client. The app module provides [ApiConfig] from its BuildConfig. */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun versionMonitor(config: ApiConfig) = ApiVersionMonitor(config.apiVersion)

    @Provides @Singleton
    fun okHttp(config: ApiConfig, tokens: SessionTokenProvider, monitor: ApiVersionMonitor): OkHttpClient =
        ApiClientFactory.okHttp(config, tokens, monitor)

    @Provides @Singleton
    fun api(config: ApiConfig, client: OkHttpClient): SubstratalApi = ApiClientFactory.api(config, client)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {
    @Binds
    abstract fun tokenProvider(impl: SessionRepository): SessionTokenProvider
}
