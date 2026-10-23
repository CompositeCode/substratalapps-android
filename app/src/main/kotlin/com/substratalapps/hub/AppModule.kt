package com.substratalapps.hub

import android.os.Build
import com.substratalapps.hub.network.ApiConfig
import com.substratalapps.hub.network.ApiEnvironment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /** `-Psubstratal.apiEnvironment=LOCAL` points a build at the API devserver. */
    @Provides @Singleton
    fun apiConfig(): ApiConfig = ApiConfig(
        environment = ApiEnvironment.valueOf(BuildConfig.API_ENVIRONMENT),
        userAgent = "Substratal-Android/${BuildConfig.VERSION_NAME} (Android ${Build.VERSION.RELEASE}; ${Build.MODEL})",
    )
}
