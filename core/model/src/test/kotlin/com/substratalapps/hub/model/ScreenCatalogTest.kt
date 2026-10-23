package com.substratalapps.hub.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenCatalogTest {
    @Test
    fun `has the 75 design-package screens plus 10 proposed Operations screens`() {
        assertEquals(85, ScreenCatalog.all.size)
        assertEquals(75, ScreenCatalog.all.count { it.phase != Phase.PROPOSED })
        assertEquals(10, ScreenCatalog.inSection(Section.OPERATIONS).size)
    }

    @Test
    fun `ids and navigation routes are unique`() {
        assertEquals(ScreenCatalog.all.size, ScreenCatalog.all.map { it.id }.toSet().size)
        assertEquals(ScreenCatalog.all.size, ScreenCatalog.all.map { it.navRoute }.toSet().size)
    }

    @Test
    fun `no API path carries a version prefix`() {
        val versioned = ScreenCatalog.all.flatMap { it.operations }.filter { it.path.startsWith("/v1") }
        assertTrue("Versioned paths: $versioned", versioned.isEmpty())
    }

    @Test
    fun `routes convert to navigation patterns`() {
        val usr03 = ScreenCatalog.byId("USR-03")
        assertEquals("admin/users/{userId}", usr03.navRoute)
        assertEquals(listOf("userId"), usr03.pathParameters)
        assertEquals("home", ScreenCatalog.byId("APP-01").navRoute)
        assertEquals("overlay/key-03", ScreenCatalog.byId("KEY-03").navRoute)
        assertFalse(ScreenCatalog.byId("USR-01").isOverlay)
    }
}
