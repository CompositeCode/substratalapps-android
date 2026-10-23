package com.substratalapps.hub.model

import kotlinx.serialization.Serializable

/** Where a screen sits in the navigation tree (INFORMATION-ARCHITECTURE §1, plus Operations). */
@Serializable
enum class Section(val label: String) {
    AUTH("Sign-in"),
    PERSONAL("Personal"),
    OPERATIONS("Operations"),
    ADMINISTRATION("Administration"),
    DEVELOPER("Developer"),
    GLOBAL("Shell"),
}

@Serializable
enum class Phase(val label: String) {
    MVP("MVP"),
    PHASE_2("Phase 2"),
    PHASE_3("Phase 3"),

    /** Not in the design package yet: new screens this app proposes (Operations). */
    PROPOSED("Proposed"),
}

@Serializable
enum class Archetype {
    AUTH, SETTINGS, FORM, WIZARD, CONFIRM, GRID, POPOVER, HANDOFF, LIST, TABLE,
    DETAIL, TAB, SECRET, OVERLAY, HUB, DASHBOARD,
}

/** One API operation a screen calls. Paths have no version prefix; see [ApiVersioning]. */
@Serializable
data class Op(val method: String, val path: String, val summary: String)

/**
 * A screen from the design package (SCREEN-SPECS.md) or a proposed one. The [route] is the
 * canonical web/deep-link route; the app's navigation and the docs site both derive from it.
 */
@Serializable
data class ScreenSpec(
    val id: String,
    val title: String,
    val domain: String,
    val route: String,
    val archetype: Archetype,
    val phase: Phase,
    val section: Section,
    val purpose: String,
    val who: String,
    val operations: List<Op>,
    /** GitHub issue references in CompositeCode/substratalapps.com that block this screen. */
    val apiGaps: List<String>,
) {
    /** True for screens without a URL of their own: modals and overlays such as KEY-03 or GLB-01. */
    val isOverlay: Boolean get() = route.startsWith("(")

    /** Navigation Compose route: `/admin/users/:userId` → `admin/users/{userId}`; `/` → `home`. */
    val navRoute: String
        get() = when {
            isOverlay -> "overlay/${id.lowercase()}"
            route == "/" -> "home"
            else -> route.removePrefix("/").replace(Regex(":(\\w+)"), "{$1}")
        }

    val pathParameters: List<String>
        get() = Regex(":(\\w+)").findAll(route).map { it.groupValues[1] }.toList()

    /**
     * This screen's page in the Substratal Hub help center, relative to its `help/` root. Only
     * design-package screens have one; [Phase.PROPOSED] screens return null.
     */
    val helpPath: String? get() = if (phase == Phase.PROPOSED) null else "screens/${id.lowercase()}.html"
}

/** How the Platform API is versioned. The URL carries no version; a header does. */
object ApiVersioning {
    const val HEADER = "Substratal-Version"

    /** The API version this build of the app is written against. */
    const val CURRENT = "1"

    /** `openapi.yaml` `info.version` the app and its docs were last synced with (see openapi/VERSION). */
    const val SPEC_VERSION = "1.0.0-draft.9"
}
