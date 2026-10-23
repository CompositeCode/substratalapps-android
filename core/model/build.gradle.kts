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

// `./gradlew :core:model:exportScreenCatalog` writes the screen catalog as JSON for the docs site.
application { mainClass.set("com.substratalapps.hub.model.ExportScreenCatalogKt") }

tasks.register<JavaExec>("exportScreenCatalog") {
    group = "documentation"
    description = "Writes the screen catalog to site/.generated/screens.json"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.substratalapps.hub.model.ExportScreenCatalogKt")
    val out = rootProject.layout.projectDirectory.file("site/.generated/screens.json").asFile
    args(out.absolutePath)
    outputs.file(out)
}
