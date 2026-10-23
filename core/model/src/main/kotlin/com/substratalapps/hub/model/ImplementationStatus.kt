package com.substratalapps.hub.model

import kotlinx.serialization.Serializable

/** How far a screen is built in this app. Published to the docs site with the screen catalog. */
@Serializable
enum class BuildState(val label: String) {
    /** A working screen backed by the API. */
    AVAILABLE("Available"),

    /** The screen's layout is built but it shows sample data until its API exists. */
    PREVIEW("Preview"),

    /** Reachable by route and deep link; shows the screen's spec until it's built. */
    PLANNED("Planned"),
}

/**
 * Which screens the Android app has built. The app's navigation registry must agree with this
 * (checked by a unit test in :app), and the docs build fails if an AVAILABLE or PREVIEW screen has
 * no hand-written help page, so shipping a screen without documenting it can't reach main.
 */
object ImplementationStatus {
    val available: Set<String> = setOf(
        "AUTH-01", "AUTH-02", "ACC-01", "SET-01", "GLB-02", "USR-01", "APL-01",
    )

    val preview: Set<String> = setOf("OPS-01")

    fun of(id: String): BuildState = when (id) {
        in available -> BuildState.AVAILABLE
        in preview -> BuildState.PREVIEW
        else -> BuildState.PLANNED
    }
}
