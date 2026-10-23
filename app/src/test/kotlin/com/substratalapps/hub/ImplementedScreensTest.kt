package com.substratalapps.hub

import com.substratalapps.hub.model.ImplementationStatus
import com.substratalapps.hub.model.ScreenCatalog
import com.substratalapps.hub.navigation.Destinations
import com.substratalapps.hub.navigation.implementedScreens
import com.substratalapps.hub.navigation.screensOutsideShell
import org.junit.Assert.assertEquals
import org.junit.Test

class ImplementedScreensTest {
    /** The docs site publishes ImplementationStatus, so it must describe what the app really has. */
    @Test
    fun `navigation registry matches the published implementation status`() {
        assertEquals(
            ImplementationStatus.available + ImplementationStatus.preview,
            implementedScreens.keys + screensOutsideShell,
        )
    }

    @Test
    fun `every navigation item points at a catalog screen`() {
        (Destinations.compact + Destinations.tree.flatMap { it.items }).forEach { ScreenCatalog.byId(it.screenId) }
    }
}
