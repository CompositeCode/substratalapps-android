plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

kotlin { jvmToolchain(21) }

dependencies {
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}

// `./gradlew :core:model:exportHelp` writes build/help/screens.json (for the API coverage check) and
// build/help/android.json (this app's build status for the Substratal Hub help center).
application { mainClass.set("com.substratalapps.hub.model.ExportScreenCatalogKt") }

tasks.register<JavaExec>("exportHelp") {
    group = "documentation"
    description = "Writes the screen catalog and the help center's build-status file to build/help/"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.substratalapps.hub.model.ExportScreenCatalogKt")
    val out = rootProject.layout.buildDirectory.dir("help").get().asFile
    args(out.absolutePath, providers.gradleProperty("substratal.appVersion").get())
    outputs.dir(out)
}
