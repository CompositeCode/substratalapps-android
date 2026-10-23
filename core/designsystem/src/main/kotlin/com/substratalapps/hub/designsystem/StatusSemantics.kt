// Generated from substratalapps-applications-specs 00-shared/status-semantics.json.
// Every badge is icon + label; colours come from the theme's roles, never literals.
package com.substratalapps.hub.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class SemanticRole { SUCCESS, INFO, WARNING, CRITICAL, NEUTRAL, ACCENT }

data class StatusStyle(val label: String, val role: SemanticRole, val materialIcon: String)

object StatusSemantics {
    val statuses: Map<String, StatusStyle> = mapOf(
        "active" to StatusStyle("Active", SemanticRole.SUCCESS, "check_circle"),
        "scheduled" to StatusStyle("Scheduled", SemanticRole.INFO, "calendar_today"),
        "disabled" to StatusStyle("Turned off", SemanticRole.WARNING, "pause_circle"),
        "expired" to StatusStyle("Expired", SemanticRole.NEUTRAL, "schedule"),
        "revoked" to StatusStyle("Revoked", SemanticRole.CRITICAL, "cancel"),
        "none" to StatusStyle("No access", SemanticRole.NEUTRAL, "do_not_disturb_on"),
        "invited" to StatusStyle("Invited", SemanticRole.ACCENT, "mail"),
        "suspended" to StatusStyle("Suspended", SemanticRole.WARNING, "pause_circle"),
        "deleted" to StatusStyle("Deleted", SemanticRole.CRITICAL, "delete"),
        "pending" to StatusStyle("Pending", SemanticRole.INFO, "hourglass_empty"),
        "important" to StatusStyle("Important", SemanticRole.CRITICAL, "priority_high"),
        "unread" to StatusStyle("Unread", SemanticRole.ACCENT, "fiber_manual_record"),
    )

    /** Domain field → (API value → status key), e.g. `application.review_status`. */
    val mappings: Map<String, Map<String, String>> = mapOf(
        "entitlement.resolved_status" to mapOf("active" to "active", "scheduled" to "scheduled", "disabled" to "disabled", "expired" to "expired", "revoked" to "revoked", "none" to "none"),
        "user.status" to mapOf("active" to "active", "invited" to "invited", "suspended" to "suspended", "deleted" to "deleted"),
        "organization.status" to mapOf("active" to "active", "suspended" to "suspended"),
        "membership_status" to mapOf("pending" to "invited", "active" to "active"),
        "application.review_status" to mapOf("approved" to "active", "pending_review" to "pending", "rejected" to "revoked", "suspended" to "suspended"),
        "webhook.status" to mapOf("healthy" to "active", "unhealthy" to "disabled", "disabled" to "expired"),
        "webhook_delivery.status" to mapOf("pending" to "pending", "succeeded" to "active", "failed" to "revoked"),
        "tenant.status" to mapOf("active" to "active", "migrating" to "scheduled", "suspended" to "suspended"),
        "tier_change_request.status" to mapOf("pending" to "pending", "scheduled" to "scheduled", "in_progress" to "scheduled", "completed" to "active", "cancelled" to "expired"),
        "erasure_request.status" to mapOf("scheduled" to "scheduled", "cancelled" to "expired", "completed" to "deleted"),
        "subscription.status" to mapOf("active" to "active", "trialing" to "scheduled", "past_due" to "disabled", "canceled" to "expired", "unpaid" to "revoked", "incomplete" to "pending", "incomplete_expired" to "expired", "paused" to "suspended"),
        "api_key" to mapOf("active" to "active", "expired (expires_at past)" to "expired", "revoked (revoked_at set)" to "revoked"),
        "user.plan" to mapOf("free" to "none", "standard" to "active"),
        "entitlement.source_labels" to mapOf("purchase" to "Purchased", "trial" to "Trial", "admin_grant" to "Granted by admin", "org_seat" to "Via {org}", "free" to "Free (managed by Substratal)", "subscription" to "Standard (managed by Substratal)"),
        "device.status" to mapOf("active" to "active", "inactive" to "expired"),
        "notification.state" to mapOf("unread" to "unread", "read" to "(no badge)", "archived" to "(no badge)"),
        "notification.priority" to mapOf("high" to "important", "normal" to "(no badge)"),
        "notification.push.status" to mapOf("queued" to "pending", "sent" to "active", "failed" to "revoked", "skipped" to "none"),
        "push_credential.status" to mapOf("active" to "active", "failed" to "disabled"),
    )

    /** The style for an API value of [field]; unknown values fall back to a neutral badge. */
    fun style(field: String, value: String): StatusStyle {
        val key = mappings[field]?.get(value) ?: value
        return statuses[key] ?: StatusStyle(value.replace('_', ' ').replaceFirstChar { it.uppercase() }, SemanticRole.NEUTRAL, "help")
    }
}

internal fun materialIcon(name: String): ImageVector = when (name) {
    "calendar_today" -> Icons.Filled.CalendarToday
    "cancel" -> Icons.Filled.Cancel
    "check_circle" -> Icons.Filled.CheckCircle
    "delete" -> Icons.Filled.Delete
    "do_not_disturb_on" -> Icons.Filled.DoNotDisturbOn
    "fiber_manual_record" -> Icons.Filled.FiberManualRecord
    "hourglass_empty" -> Icons.Filled.HourglassEmpty
    "mail" -> Icons.Filled.Mail
    "pause_circle" -> Icons.Filled.PauseCircle
    "priority_high" -> Icons.Filled.PriorityHigh
    "schedule" -> Icons.Filled.Schedule
    else -> Icons.Filled.Info
}
