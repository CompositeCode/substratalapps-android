package com.substratalapps.hub.help

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.substratalapps.hub.BuildConfig
import com.substratalapps.hub.model.ScreenSpec

/**
 * Links into the Substratal documentation site. URLs are versioned like the site:
 * `{base}{docsVersion}/{path}`, where a release build's docs version is its minor version and
 * debug builds use `dev`. Every platform's app uses the same paths, so a screen id is a stable
 * help address on all of them (`/reference/screens/usr-01/`).
 */
object HelpLinks {
    private val root get() = "${BuildConfig.HELP_BASE_URL}${BuildConfig.DOCS_VERSION}/"

    fun home() = root
    fun forScreen(screen: ScreenSpec) = root + screen.docsPath
    fun page(path: String) = root + path.trimStart('/')

    fun open(context: Context, url: String) {
        CustomTabsIntent.Builder().setShowTitle(true).build().launchUrl(context, url.toUri())
    }
}

/** The help topics shown in the in-app Help screen; each maps to a docs page. */
data class HelpTopic(val title: String, val summary: String, val path: String)

val helpTopics = listOf(
    HelpTopic("Getting started", "Install the app, sign in and find your way around.", "getting-started/"),
    HelpTopic("Tutorials", "Step-by-step: invite a user, turn access off, register an app, create a key.", "tutorials/"),
    HelpTopic("Managing users", "Search, invite, suspend and sign people out.", "guides/users/"),
    HelpTopic("Access and roles", "Turn apps on and off for people, and decide what they can do.", "guides/access-and-roles/"),
    HelpTopic("Applications and keys", "Register applications, manage API keys, webhooks and push.", "guides/applications/"),
    HelpTopic("Operations", "Monitor the API: health, errors, usage, alerts and cost.", "guides/operations/"),
    HelpTopic("Screen reference", "Every screen, what it does and the API calls behind it.", "reference/screens/"),
    HelpTopic("API reference", "OpenAPI / Swagger reference for the Platform API, by version.", "developers/api/"),
    HelpTopic("FAQ", "Answers to common questions.", "faq/"),
)
