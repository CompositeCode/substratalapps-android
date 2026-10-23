plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val appVersionName = providers.gradleProperty("substratal.appVersion").get()
// In-app help opens the Substratal Hub help center that every Hub app shares.
val helpBaseUrl = providers.gradleProperty("substratal.helpBaseUrl")
    .orElse("https://compositecode.github.io/substratalapps-applications-specs/help/").get()
val apiEnvironment = providers.gradleProperty("substratal.apiEnvironment").orElse("PRODUCTION").get()

android {
    namespace = "com.substratalapps.hub"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.substratalapps.hub"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = appVersionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "HELP_BASE_URL", "\"$helpBaseUrl\"")
        buildConfigField("String", "API_ENVIRONMENT", "\"$apiEnvironment\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.compose.material3.adaptive.navigation.suite)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
}
