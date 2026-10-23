package com.substratalapps.hub.help

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.substratalapps.hub.BuildConfig
import com.substratalapps.hub.model.ScreenSpec

/**
 * Links into the Substratal Hub help center, the documentation every Hub app shares
 * (substratalapps-applications-specs, `help/`). A screen's page is `screens/<id>.html` on every
 * platform; see the help center's "Linking to help from an app".
 */
object HelpLinks {
    private val root get() = BuildConfig.HELP_BASE_URL

    fun home() = root

    /** The screen's help page; screens this app adds outside the design package open the help home. */
    fun forScreen(screen: ScreenSpec) = screen.helpPath?.let { root + it } ?: home()

    fun page(path: String) = root + path.trimStart('/')

    fun open(context: Context, url: String) {
        CustomTabsIntent.Builder().setShowTitle(true).build().launchUrl(context, url.toUri())
    }
}

/** The help topics shown in the in-app Help screen; each maps to a docs page. */
data class HelpTopic(val title: String, val summary: String, val path: String)

val helpTopics = listOf(
    HelpTopic("Getting started", "What the Hub is, how it's organized, and where things are.", "guides/getting-started.html"),
    HelpTopic("The Android app", "Layouts, links, offline use and security on Android.", "guides/android-app.html"),
    HelpTopic("Your first hour as an administrator", "A guided tour of the tasks staff do first.", "tutorials/first-hour-as-an-administrator.html"),
    HelpTopic("Users and access", "Find people, invite them, and turn app access on and off.", "guides/users-and-access.html"),
    HelpTopic("Roles and permissions", "What each role can do, and how to change it.", "guides/roles-and-permissions.html"),
    HelpTopic("Applications", "Register and manage applications, keys and webhooks.", "guides/applications.html"),
    HelpTopic("Every screen", "Each screen, who can use it, and the API calls behind it.", "screens/index.html"),
    HelpTopic("API reference", "Interactive OpenAPI reference for the Platform API, by version.", "api/index.html"),
    HelpTopic("FAQ", "Answers to common questions.", "faq.html"),
)
