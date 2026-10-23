// Generated once from substratalapps-applications-specs (SCREEN-SPECS.md index, Purpose/Who lines,
// API-SCREEN-MAP.csv) plus the proposed Operations screens. From here on this file is the source of
// truth: the app's navigation and the documentation site's screen reference are both built from it.
// API paths carry no /v1 prefix: the API is versioned by the Substratal-Version header.
// Since generation: AUD-01, USR-04, ACC-04 and ORG-08 gained the operations added in API
// 1.0.0-draft.5..draft.9 (audit exports, revoke-all sessions, SCIM tokens).
package com.substratalapps.hub.model

object ScreenCatalog {
    val all: List<ScreenSpec> = listOf(
        ScreenSpec(
            id = "AUTH-01", title = "Sign in", domain = "D1", route = "/sign-in",
            archetype = Archetype.AUTH, phase = Phase.MVP, section = Section.AUTH,
            purpose = "Exchange email + password for a session, or start an MFA challenge.",
            who = "Unauthenticated",
            operations = listOf(
            Op("POST", "/auth/login", "Exchange email and password for a session (or an MFA challenge)"),
            Op("POST", "/auth/sso/{provider}/callback", "Complete an SSO login (reserved — 501 until the broker integration ships)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "AUTH-02", title = "Two-factor challenge", domain = "D1", route = "/sign-in/mfa",
            archetype = Archetype.AUTH, phase = Phase.MVP, section = Section.AUTH,
            purpose = "Complete sign-in with a TOTP code or a recovery code.",
            who = "Holder of mfa_token",
            operations = listOf(
            Op("POST", "/auth/mfa/verify", "Complete a login that returned an MFA challenge")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "AUTH-03", title = "Create account", domain = "D1", route = "/sign-up",
            archetype = Archetype.AUTH, phase = Phase.MVP, section = Section.AUTH,
            purpose = "Self-service signup with email and password.",
            who = "Unauthenticated",
            operations = listOf(
            Op("POST", "/auth/signup", "Create an account with email and password")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "AUTH-04", title = "Verify your email", domain = "D1", route = "/verify-email",
            archetype = Archetype.AUTH, phase = Phase.MVP, section = Section.AUTH,
            purpose = "Tell the user to check email; complete verification from the emailed link.",
            who = "Signed in (unverified) or token holder",
            operations = listOf(
            Op("POST", "/auth/email/verify", "Confirm an email address from an emailed token"),
            Op("POST", "/auth/email/verify/resend", "Re-send a verification email (always 202)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "AUTH-05", title = "Forgot password", domain = "D1", route = "/forgot-password",
            archetype = Archetype.AUTH, phase = Phase.MVP, section = Section.AUTH,
            purpose = "Request a password-reset email.",
            who = "Unauthenticated",
            operations = listOf(
            Op("POST", "/auth/password/forgot", "Send a password-reset email (always 202)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "AUTH-06", title = "Set a new password", domain = "D1", route = "/reset-password",
            archetype = Archetype.AUTH, phase = Phase.MVP, section = Section.AUTH,
            purpose = "Choose a new password from the emailed reset link.",
            who = "Reset-token holder",
            operations = listOf(
            Op("POST", "/auth/password/reset", "Set a new password from a reset token (revokes every session)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "AUTH-07", title = "Accept invitation", domain = "D1", route = "/invite",
            archetype = Archetype.AUTH, phase = Phase.MVP, section = Section.AUTH,
            purpose = "Activate an account an admin created: set password and sign in.",
            who = "Invitation-token holder",
            operations = listOf(
            Op("POST", "/auth/invitations/accept", "Set a password on an invited account, activate it, and sign in")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACC-01", title = "Account & profile", domain = "D2", route = "/account",
            archetype = Archetype.SETTINGS, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "View and edit the signed-in user's global profile.",
            who = "Self",
            operations = listOf(
            Op("POST", "/auth/logout", "Revoke the current session, or every session"),
            Op("GET", "/users/{id}", "Fetch one user"),
            Op("GET", "/users/{id}/profile", "Fetch global Profile"),
            Op("PATCH", "/users/{id}/profile", "Update global Profile (self or users.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACC-02", title = "Change email", domain = "D2", route = "/account/email",
            archetype = Archetype.FORM, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Start an email change; the new address must be verified.",
            who = "Self",
            operations = listOf(
            Op("PATCH", "/users/{id}", "Update email (self, pending verification) or status (users.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACC-03", title = "Change password", domain = "D2", route = "/account/security/password",
            archetype = Archetype.FORM, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Change password knowing the current one.",
            who = "Self",
            operations = listOf(
            Op("POST", "/users/{id}/password", "Change password given the current one (self only)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACC-04", title = "Security", domain = "D2", route = "/account/security",
            archetype = Archetype.SETTINGS, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Password, two-factor and active sessions in one place.",
            who = "Self",
            operations = listOf(
            Op("GET", "/users/{id}/sessions", "List active sessions (self or users.manage)"),
            Op("DELETE", "/users/{id}/sessions/{sessionId}", "Revoke one session"),
            Op("GET", "/users/{id}", "Fetch one user"),
            Op("POST", "/users/{id}/sessions/revoke-all", "Revoke every session (self or users.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACC-05", title = "Turn on two-factor", domain = "D2", route = "/account/security/2fa/enable",
            archetype = Archetype.WIZARD, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Enroll an authenticator app (TOTP) and save recovery codes.",
            who = "Self",
            operations = listOf(
            Op("POST", "/users/{id}/mfa/totp", "Begin TOTP enrollment (self only)"),
            Op("POST", "/users/{id}/mfa/totp/confirm", "Confirm TOTP enrollment with a first code; returns recovery codes once")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACC-06", title = "Turn off two-factor", domain = "D2", route = "/account/security/2fa/disable",
            archetype = Archetype.CONFIRM, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Disable TOTP with a current code or a recovery code.",
            who = "Self",
            operations = listOf(
            Op("POST", "/users/{id}/mfa/totp/disable", "Turn TOTP off given a current code or recovery code (self only)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACC-07", title = "Privacy", domain = "D2", route = "/account/privacy",
            archetype = Archetype.SETTINGS, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Export personal data and delete the account.",
            who = "Self",
            operations = listOf(
            Op("GET", "/users/{id}/export", "Export everything this API holds about this user")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACC-08", title = "Delete account", domain = "D2", route = "/account/privacy/delete",
            archetype = Archetype.CONFIRM, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Request erasure: deactivate now, erase after 7 days.",
            who = "Self",
            operations = listOf(
            Op("POST", "/users/{id}/erasure-requests", "Request right-to-erasure (soft-delete now, hard-delete cascade in 7 days)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APP-01", title = "Home — My Apps", domain = "D3", route = "/",
            archetype = Archetype.GRID, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "See every app you can use, its access status, and launch it.",
            who = "Signed in",
            operations = listOf(
            Op("GET", "/users/{id}", "Fetch one user"),
            Op("GET", "/applications", "List the catalog (filtered by visibility rules)"),
            Op("GET", "/users/{id}/entitlements", "A user's entitlements, personal and org-sourced"),
            Op("GET", "/organizations", "List your Organizations (all, with organizations.manage)"),
            Op("GET", "/users/{id}/notifications/unread-count", "Count unread notifications, in total and per Application (self or the app's own key)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APP-02", title = "App info", domain = "D3", route = "/apps/:appId",
            archetype = Archetype.POPOVER, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Explain an app's access status and how the user has access.",
            who = "Signed in",
            operations = listOf(
            Op("GET", "/applications/{id}", "Fetch one Application"),
            Op("GET", "/users/{id}/apps/{appId}/effective-permissions", "The live access check (always 200)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APP-03", title = "Launch app", domain = "D3", route = "/apps/:appId/launch",
            archetype = Archetype.HANDOFF, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Mint an app token (or authorization code) and open the app.",
            who = "Active entitlement",
            operations = listOf(
            Op("POST", "/auth/app-tokens", "Mint a per-app JWT from a platform session (embedded login)"),
            Op("POST", "/auth/oauth/authorization-codes", "Mint a one-time authorization code for a hosted launch (PKCE)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APP-04", title = "App profile", domain = "D3", route = "/apps/:appId/profile",
            archetype = Archetype.FORM, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "Per-app display handle and custom profile fields.",
            who = "Self, active entitlement",
            operations = listOf(
            Op("GET", "/users/{id}/apps/{appId}/profile", "Fetch per-app AppProfile (self, the app's own key, or platform users.list; defaults if none written yet)"),
            Op("PATCH", "/users/{id}/apps/{appId}/profile", "Update per-app AppProfile (custom is merged one level; null removes a key)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APP-05", title = "Past apps", domain = "D3", route = "/apps/past",
            archetype = Archetype.LIST, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Apps whose access was revoked or expired.",
            who = "Signed in",
            operations = listOf(
            Op("GET", "/users/{id}/entitlements", "A user's entitlements, personal and org-sourced")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "SET-01", title = "Settings — Global", domain = "D4", route = "/settings",
            archetype = Archetype.SETTINGS, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Preferences that apply to every app.",
            who = "Self",
            operations = listOf(
            Op("GET", "/users/{id}/settings", "Fetch global Settings (self, or platform users.list)"),
            Op("PATCH", "/users/{id}/settings", "Update global Settings (self or users.manage)"),
            Op("GET", "/users/{id}/apps/{appId}/settings", "Fetch fully resolved per-app settings (self, the app's own key, or platform users.list)"),
            Op("PATCH", "/users/{id}/apps/{appId}/settings", "Write per-app overrides (validated against settings_schema; null clears)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "SET-02", title = "Settings — Per app", domain = "D4", route = "/settings/apps/:appId",
            archetype = Archetype.SETTINGS, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "Override global settings and set app-specific preferences from the app's settings_schema.",
            who = "Self, active entitlement",
            operations = listOf(
            Op("GET", "/users/{id}/apps/{appId}/settings", "Fetch fully resolved per-app settings (self, the app's own key, or platform users.list)"),
            Op("PATCH", "/users/{id}/apps/{appId}/settings", "Write per-app overrides (validated against settings_schema; null clears)"),
            Op("GET", "/applications/{id}", "Fetch one Application")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ORG-01", title = "Organizations", domain = "D5", route = "/orgs",
            archetype = Archetype.LIST, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Organizations you belong to, plus pending invitations.",
            who = "Signed in (all orgs with organizations.manage)",
            operations = listOf(
            Op("GET", "/organizations", "List your Organizations (all, with organizations.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ORG-02", title = "New organization", domain = "D5", route = "/orgs/new",
            archetype = Archetype.FORM, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Create an organization; creator becomes org admin.",
            who = "Any active user",
            operations = listOf(
            Op("POST", "/organizations", "Create an Organization (any active User; caller becomes org_admin)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ORG-03", title = "Organization — Members", domain = "D5", route = "/orgs/:orgId/members",
            archetype = Archetype.DETAIL, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "See and manage members and pending invitations.",
            who = "Member (read), org_admin / organizations.manage (write)",
            operations = listOf(
            Op("GET", "/organizations/{id}", "Fetch one Organization"),
            Op("GET", "/organizations/{id}/members", "List members")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ORG-04", title = "Invite member", domain = "D5", route = "/orgs/:orgId/members/invite",
            archetype = Archetype.FORM, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Invite people by email (or add an existing user, staff only).",
            who = "org_admin; organizations.manage may add by user_id",
            operations = listOf(
            Op("POST", "/organizations/{id}/members", "Invite a member by email (org admin), or add one directly by user_id (organizations.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ORG-05", title = "Member actions", domain = "D5", route = "/orgs/:orgId/members/:userId",
            archetype = Archetype.CONFIRM, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Change a member's role or remove them.",
            who = "org_admin / organizations.manage",
            operations = listOf(
            Op("PATCH", "/organizations/{id}/members/{userId}", "Change a member's role"),
            Op("DELETE", "/organizations/{id}/members/{userId}", "Remove a member or withdraw an invitation; or leave or decline (userId = me)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ORG-06", title = "Organization — Apps & seats", domain = "D5", route = "/orgs/:orgId/apps",
            archetype = Archetype.TAB, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "Org-wide app grants and who they include.",
            who = "Member (read), org_admin (write)",
            operations = listOf(
            Op("PATCH", "/entitlements/{id}", "The toggle: change status, term, source (trial→purchase), order_id, or an org grant's member scope"),
            Op("GET", "/organizations/{id}/entitlements", "List org-wide (seat) grants")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ORG-07", title = "Grant app to organization / member scope", domain = "D5", route = "/orgs/:orgId/apps/grant",
            archetype = Archetype.FORM, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "Create an org-wide grant or change who it includes.",
            who = "entitlements.manage or app owner; scope edits by org_admin",
            operations = listOf(
            Op("PATCH", "/entitlements/{id}", "The toggle: change status, term, source (trial→purchase), order_id, or an org grant's member scope"),
            Op("POST", "/organizations/{id}/entitlements", "Grant an app to the whole Organization")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ORG-08", title = "Organization — Settings", domain = "D5", route = "/orgs/:orgId/settings",
            archetype = Archetype.SETTINGS, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Rename, leave, or (staff) suspend the organization.",
            who = "org_admin (rename), organizations.manage (suspend), any member (leave)",
            operations = listOf(
            Op("PATCH", "/organizations/{id}", "Rename (org admin), or suspend/reactivate (organizations.manage)"),
            Op("DELETE", "/organizations/{id}/members/{userId}", "Remove a member or withdraw an invitation; or leave or decline (userId = me)"),
            Op("GET", "/organizations/{id}/scim-token", "Show the Organization's SCIM token (never its secret)"),
            Op("POST", "/organizations/{id}/scim-token", "Create (or rotate) the Organization's SCIM token"),
            Op("DELETE", "/organizations/{id}/scim-token", "Turn SCIM off for the Organization")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ORG-09", title = "Invitation", domain = "D5", route = "/orgs/:orgId/invitation",
            archetype = Archetype.CONFIRM, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Accept or decline an organization invitation.",
            who = "Invited user",
            operations = listOf(
            Op("POST", "/organizations/{id}/members/me/accept", "Accept a pending membership (the invited User's own token)"),
            Op("DELETE", "/organizations/{id}/members/{userId}", "Remove a member or withdraw an invitation; or leave or decline (userId = me)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "USR-01", title = "Users", domain = "D6", route = "/admin/users",
            archetype = Archetype.TABLE, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Find and manage user accounts.",
            who = "users.list (write actions need users.manage)",
            operations = listOf(
            Op("GET", "/users", "List or search users")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "USR-02", title = "Invite user", domain = "D6", route = "/admin/users/new",
            archetype = Archetype.FORM, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Create a user and send an invitation (or create active).",
            who = "users.manage",
            operations = listOf(
            Op("POST", "/users", "Create (invite) a user")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "USR-03", title = "User — Overview", domain = "D6", route = "/admin/users/:userId",
            archetype = Archetype.DETAIL, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "One user's account state and administrative actions.",
            who = "users.list (read), users.manage (write)",
            operations = listOf(
            Op("DELETE", "/users/{id}/mfa/totp", "Support reset of a user's TOTP (requires users.manage)"),
            Op("GET", "/users/{id}", "Fetch one user"),
            Op("PATCH", "/users/{id}", "Update email (self, pending verification) or status (users.manage)"),
            Op("DELETE", "/users/{id}", "Soft-delete a user (self or users.manage)"),
            Op("POST", "/users/{id}/invitation", "Re-send the invitation email to an invited user (users.manage)"),
            Op("GET", "/users/{id}/export", "Export everything this API holds about this user"),
            Op("DELETE", "/users/{id}/erasure-requests/current", "Cancel a scheduled erasure inside its 7-day window (users.manage)"),
            Op("GET", "/users/{id}/profile", "Fetch global Profile"),
            Op("GET", "/users/{id}/subscription", "A User's plan, what covers it, and their own subscription (self or billing.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "USR-04", title = "User — Sessions", domain = "D6", route = "/admin/users/:userId/sessions",
            archetype = Archetype.TAB, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "See and revoke a user's sessions.",
            who = "users.manage",
            operations = listOf(
            Op("GET", "/users/{id}/sessions", "List active sessions (self or users.manage)"),
            Op("DELETE", "/users/{id}/sessions/{sessionId}", "Revoke one session"),
            Op("POST", "/users/{id}/sessions/revoke-all", "Revoke every session (self or users.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "USR-05", title = "Suspend user", domain = "D6", route = "/admin/users/:userId/suspend",
            archetype = Archetype.CONFIRM, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Suspend an account (signs them out everywhere).",
            who = "users.manage",
            operations = listOf(
            Op("POST", "/users/{id}/suspend", "Suspend a user (users.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACX-01", title = "User — Access", domain = "D7", route = "/admin/users/:userId/access",
            archetype = Archetype.TAB, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Turn apps on or off for one person; see every path that grants access.",
            who = "users.list (read), entitlements.manage or app owner (write)",
            operations = listOf(
            Op("GET", "/applications", "List the catalog (filtered by visibility rules)"),
            Op("GET", "/users/{id}/entitlements", "A user's entitlements, personal and org-sourced"),
            Op("PATCH", "/entitlements/{id}", "The toggle: change status, term, source (trial→purchase), order_id, or an org grant's member scope"),
            Op("DELETE", "/entitlements/{id}", "Hard-remove a grant made in error (no order_id, under 24 hours old)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACX-02", title = "Grant access", domain = "D7", route = "/admin/users/:userId/access/grant",
            archetype = Archetype.FORM, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Give a person access to an app.",
            who = "entitlements.manage or app owner (own app)",
            operations = listOf(
            Op("POST", "/users/{id}/entitlements", "Grant a personal Entitlement")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACX-03", title = "Turn off / edit access", domain = "D7", route = "/admin/entitlements/:entId",
            archetype = Archetype.CONFIRM, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Confirm turning off access, or edit term/source.",
            who = "entitlements.manage or app owner",
            operations = listOf(
            Op("GET", "/entitlements/{id}", "Fetch one entitlement"),
            Op("PATCH", "/entitlements/{id}", "The toggle: change status, term, source (trial→purchase), order_id, or an org grant's member scope")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACX-04", title = "Access check (Why?)", domain = "D7", route = "/admin/users/:userId/apps/:appId/why",
            archetype = Archetype.POPOVER, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Explain exactly why a person can or can't use an app.",
            who = "users.list or app owner",
            operations = listOf(
            Op("GET", "/users/{id}/apps/{appId}/effective-permissions", "The live access check (always 200)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ACX-05", title = "Access (all)", domain = "D7", route = "/admin/access",
            archetype = Archetype.TABLE, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Every grant across users: audit expirations, trials, revocations.",
            who = "entitlements.manage or app owner (confined to own apps)",
            operations = listOf(
            Op("GET", "/entitlements", "List entitlements across users (entitlements.manage or the Application's owner; app keys and owners confined)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ROL-01", title = "Roles", domain = "D8", route = "/admin/roles",
            archetype = Archetype.LIST, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Platform and app roles.",
            who = "users.list (read), roles.manage (write)",
            operations = listOf(
            Op("GET", "/permissions", "List every Permission key the caller can see"),
            Op("GET", "/roles", "List Roles")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ROL-02", title = "Role detail", domain = "D8", route = "/admin/roles/:roleId",
            archetype = Archetype.DETAIL, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "View or edit a role's permissions.",
            who = "users.list (read), roles.manage (write)",
            operations = listOf(
            Op("GET", "/permissions", "List every Permission key the caller can see"),
            Op("GET", "/roles/{id}", "Fetch one Role"),
            Op("PATCH", "/roles/{id}", "Change a Role's permissions or description"),
            Op("DELETE", "/roles/{id}", "Delete an unassigned, non-seed Role")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ROL-03", title = "New role", domain = "D8", route = "/admin/roles/new",
            archetype = Archetype.FORM, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Define a custom role.",
            who = "roles.manage",
            operations = listOf(
            Op("POST", "/roles", "Define a Role")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "ROL-04", title = "User — Roles", domain = "D8", route = "/admin/users/:userId/roles",
            archetype = Archetype.TAB, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Assign and remove roles for a user.",
            who = "users.list (read), roles.manage (write)",
            operations = listOf(
            Op("GET", "/users/{id}/roles", "List a user's Role assignments (including implicit default_app_role)"),
            Op("POST", "/users/{id}/roles/{roleId}", "Assign a Role (idempotent — 200 if already held)"),
            Op("DELETE", "/users/{id}/roles/{roleId}", "Remove a Role assignment (idempotent)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APL-01", title = "Applications", domain = "D9", route = "/dev/apps",
            archetype = Archetype.LIST, phase = Phase.MVP, section = Section.DEVELOPER,
            purpose = "Applications you own or administer.",
            who = "Any (catalog), owner/applications.manage (manage)",
            operations = listOf(
            Op("GET", "/applications", "List the catalog (filtered by visibility rules)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APL-02", title = "Register application", domain = "D9", route = "/dev/apps/new",
            archetype = Archetype.WIZARD, phase = Phase.MVP, section = Section.DEVELOPER,
            purpose = "Register a new application.",
            who = "applications.manage",
            operations = listOf(
            Op("POST", "/applications", "Register an Application (applications.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APL-03", title = "Application — Overview", domain = "D9", route = "/dev/apps/:appId",
            archetype = Archetype.DETAIL, phase = Phase.MVP, section = Section.DEVELOPER,
            purpose = "Configure an application.",
            who = "Owner (configure), applications.manage (anything)",
            operations = listOf(
            Op("GET", "/applications/{id}", "Fetch one Application"),
            Op("PATCH", "/applications/{id}", "Update a catalog entry (owner — configuration; applications.manage — anything)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APL-04", title = "Settings schema", domain = "D9", route = "/dev/apps/:appId/schema",
            archetype = Archetype.TAB, phase = Phase.PHASE_2, section = Section.DEVELOPER,
            purpose = "Declare per-user settings the app stores in Substratal.",
            who = "Owner / applications.manage",
            operations = listOf(
            Op("PATCH", "/applications/{id}", "Update a catalog entry (owner — configuration; applications.manage — anything)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APL-05", title = "Permissions & roles", domain = "D9", route = "/dev/apps/:appId/roles",
            archetype = Archetype.TAB, phase = Phase.PHASE_2, section = Section.DEVELOPER,
            purpose = "Declare app permissions, available app roles and the default role.",
            who = "Owner / applications.manage",
            operations = listOf(
            Op("PATCH", "/applications/{id}", "Update a catalog entry (owner — configuration; applications.manage — anything)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APL-06", title = "Review", domain = "D9", route = "/dev/apps/:appId/review",
            archetype = Archetype.FORM, phase = Phase.PHASE_3, section = Section.DEVELOPER,
            purpose = "Staff moderation of an application.",
            who = "applications.manage",
            operations = listOf(
            Op("PATCH", "/applications/{id}", "Update a catalog entry (owner — configuration; applications.manage — anything)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "KEY-01", title = "API keys", domain = "D10", route = "/dev/keys",
            archetype = Archetype.TABLE, phase = Phase.MVP, section = Section.DEVELOPER,
            purpose = "Service and agent credentials.",
            who = "api_keys.manage or app owner",
            operations = listOf(
            Op("GET", "/api-keys", "List keys the caller may manage")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "KEY-02", title = "Create API key", domain = "D10", route = "/dev/keys/new",
            archetype = Archetype.FORM, phase = Phase.MVP, section = Section.DEVELOPER,
            purpose = "Create a key with least-privilege permissions.",
            who = "api_keys.manage or app owner (app-scoped)",
            operations = listOf(
            Op("POST", "/api-keys", "Create a key (api_keys.manage, or the Application's owner for an app-scoped key)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "KEY-03", title = "Secret reveal", domain = "D10", route = "(modal)",
            archetype = Archetype.SECRET, phase = Phase.MVP, section = Section.PERSONAL,
            purpose = "Show a new API key secret, webhook signing secret, or recovery codes exactly once.",
            who = "Creator",
            operations = listOf(),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "KEY-04", title = "API key detail", domain = "D10", route = "/dev/keys/:keyId",
            archetype = Archetype.DETAIL, phase = Phase.MVP, section = Section.DEVELOPER,
            purpose = "Edit, rotate or revoke a key.",
            who = "api_keys.manage or app owner",
            operations = listOf(
            Op("GET", "/audit-events", "Query the audit log (hot window only)"),
            Op("GET", "/api-keys/{id}", "Fetch one key (never the secret)"),
            Op("PATCH", "/api-keys/{id}", "Change name, permissions, or restrict_destructive"),
            Op("DELETE", "/api-keys/{id}", "Revoke permanently"),
            Op("POST", "/api-keys/{id}/rotate", "Issue a new secret, invalidating the old one immediately (no overlap)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "WHK-01", title = "Webhooks", domain = "D10", route = "/dev/webhooks",
            archetype = Archetype.LIST, phase = Phase.PHASE_2, section = Section.DEVELOPER,
            purpose = "Endpoints that receive access-change events.",
            who = "webhooks.manage or app owner",
            operations = listOf(
            Op("GET", "/webhooks", "List subscriptions visible to the caller")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "WHK-02", title = "Add / edit webhook", domain = "D10", route = "/dev/webhooks/new",
            archetype = Archetype.FORM, phase = Phase.PHASE_2, section = Section.DEVELOPER,
            purpose = "Subscribe a URL to events.",
            who = "webhooks.manage or app owner",
            operations = listOf(
            Op("POST", "/webhooks", "Subscribe a URL to event types"),
            Op("PATCH", "/webhooks/{id}", "Change url, events, description, api_version, or re-enable")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "WHK-03", title = "Webhook — Deliveries", domain = "D10", route = "/dev/webhooks/:hookId",
            archetype = Archetype.DETAIL, phase = Phase.PHASE_2, section = Section.DEVELOPER,
            purpose = "Delivery history, testing and secret rotation.",
            who = "webhooks.manage or app owner",
            operations = listOf(
            Op("GET", "/webhooks/{id}", "Fetch one subscription"),
            Op("DELETE", "/webhooks/{id}", "Unsubscribe"),
            Op("POST", "/webhooks/{id}/rotate-secret", "Issue a new signing secret (24-hour overlap)"),
            Op("POST", "/webhooks/{id}/test", "Send a webhook.test event now"),
            Op("GET", "/webhooks/{id}/deliveries", "Recent delivery attempts (30-day log)"),
            Op("POST", "/webhooks/{id}/deliveries/{deliveryId}/redeliver", "Re-send one past event (same event id)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "WHK-04", title = "Delivery detail", domain = "D10", route = "/dev/webhooks/:hookId/deliveries/:deliveryId",
            archetype = Archetype.POPOVER, phase = Phase.PHASE_2, section = Section.DEVELOPER,
            purpose = "One delivery attempt in detail.",
            who = "webhooks.manage or app owner",
            operations = listOf(),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "BIL-01", title = "My plan", domain = "D11", route = "/billing",
            archetype = Archetype.SETTINGS, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "Free or Standard: what covers you, your own subscription, upgrade and manage billing.",
            who = "Self; billing.manage can view any User's (read-only)",
            operations = listOf(
            Op("GET", "/applications", "List the catalog (filtered by visibility rules)"),
            Op("GET", "/users/{id}/subscription", "A User's plan, what covers it, and their own subscription (self or billing.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "BIL-02", title = "Organization subscription", domain = "D11", route = "/orgs/:orgId/subscription",
            archetype = Archetype.TAB, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "An organization's Standard subscription and which members it covers.",
            who = "org_admin (manage); billing.manage (read + portal)",
            operations = listOf(
            Op("GET", "/organizations/{id}/members", "List members"),
            Op("GET", "/organizations/{id}/subscription", "An Organization's plan and Standard subscription (org admin or billing.manage)"),
            Op("PATCH", "/organizations/{id}/subscription", "Choose which members the Organization's subscription covers (org admin)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "BIL-03", title = "Checkout / billing portal", domain = "D11", route = "/billing/return",
            archetype = Archetype.HANDOFF, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "Hand off to Stripe in the browser and confirm the result on return.",
            who = "Self (individual); org_admin (org checkout); org_admin or billing.manage (org portal)",
            operations = listOf(
            Op("POST", "/users/me/billing/checkout-sessions", "Start a Stripe Checkout to subscribe yourself to Standard (User token)"),
            Op("POST", "/users/me/billing/portal-sessions", "Open the Stripe Customer Portal for your own subscription (User token)"),
            Op("POST", "/organizations/{id}/billing/checkout-sessions", "Start a Stripe Checkout to subscribe the Organization to Standard (org admin, User token)"),
            Op("POST", "/organizations/{id}/billing/portal-sessions", "Open the Stripe Customer Portal for the Organization's subscription (org admin or billing.manage, User token)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "BIL-04", title = "Tenant & infrastructure", domain = "D11", route = "/dev/tenant",
            archetype = Archetype.DETAIL, phase = Phase.PHASE_2, section = Section.DEVELOPER,
            purpose = "Where an Application owner's data lives, and support-run tier changes. Tenants aren't billed.",
            who = "Tenant owner (read), tenants.manage (write)",
            operations = listOf(
            Op("GET", "/tenants", "List Tenants (your own; all with tenants.manage)"),
            Op("GET", "/tenants/{id}", "Fetch one Tenant"),
            Op("GET", "/tenants/{id}/tier-change-requests", "List tier-change requests for a Tenant"),
            Op("POST", "/tenants/{id}/tier-change-requests", "Open a tier-change request on a customer's behalf (tenants.manage)"),
            Op("GET", "/tenants/{id}/tier-change-requests/{requestId}", "Fetch one tier-change request"),
            Op("PATCH", "/tenants/{id}/tier-change-requests/{requestId}", "Schedule, start, complete, or cancel a tier-change request (tenants.manage)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "AUD-01", title = "Audit log", domain = "D12", route = "/admin/audit",
            archetype = Archetype.TABLE, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "Who changed what, when.",
            who = "audit.view",
            operations = listOf(
            Op("GET", "/audit-events", "Query the audit log (hot window only)"),
            Op("POST", "/audit-exports", "Export Audit Events to a file (asynchronous)"),
            Op("GET", "/audit-exports/{id}", "Fetch an export's status, and its download link once ready")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "AUD-02", title = "Audit event", domain = "D12", route = "/admin/audit/:eventId",
            archetype = Archetype.POPOVER, phase = Phase.MVP, section = Section.ADMINISTRATION,
            purpose = "One event with before/after diff.",
            who = "audit.view",
            operations = listOf(
            Op("GET", "/audit-events/{id}", "Fetch one Audit Event")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "NTF-01", title = "Inbox", domain = "D13", route = "/inbox",
            archetype = Archetype.LIST, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "Every notification the person has received — from every app and from Substratal — with read and archived state.",
            who = "Self",
            operations = listOf(
            Op("GET", "/applications", "List the catalog (filtered by visibility rules)"),
            Op("GET", "/users/{id}/notifications", "List a User's notification inbox (self, the app's own key, or users.list)"),
            Op("GET", "/users/{id}/notifications/unread-count", "Count unread notifications, in total and per Application (self or the app's own key)"),
            Op("POST", "/users/{id}/notifications/mark-read", "Mark every unread notification matching the filters as read (self or the app's own key)"),
            Op("PATCH", "/users/{id}/notifications/{notificationId}", "Mark one notification read, unread, or archived (self or the app's own key)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "NTF-02", title = "Notification", domain = "D13", route = "/inbox/:notificationId",
            archetype = Archetype.POPOVER, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "One notification in full, with where it leads and how its push went.",
            who = "Self (users.list via NTF-05)",
            operations = listOf(
            Op("GET", "/users/{id}/notifications/{notificationId}", "Fetch one notification, with its per-device deliveries"),
            Op("PATCH", "/users/{id}/notifications/{notificationId}", "Mark one notification read, unread, or archived (self or the app's own key)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "NTF-03", title = "Devices & notifications", domain = "D13", route = "/account/devices",
            archetype = Archetype.SETTINGS, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "Where push notifications reach this person, and controls for this device.",
            who = "Self",
            operations = listOf(
            Op("GET", "/users/{id}/settings", "Fetch global Settings (self, or platform users.list)"),
            Op("GET", "/users/{id}/devices", "List a User's push devices (self, the app's own key, or users.list)"),
            Op("PATCH", "/users/{id}/devices/{deviceId}", "Update a device's name, app version, environment, or rotated token (self or the app's own key)"),
            Op("DELETE", "/users/{id}/devices/{deviceId}", "Unregister a device (self, the app's own key, or users.manage)"),
            Op("POST", "/users/{id}/devices/{deviceId}/test", "Send a test push to one device and wait for the provider's answer (self or the app's own key)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "NTF-04", title = "Turn on notifications", domain = "D13", route = "/account/devices/enable",
            archetype = Archetype.WIZARD, phase = Phase.PHASE_2, section = Section.PERSONAL,
            purpose = "Explain, request the OS permission, and register this install for push.",
            who = "Self",
            operations = listOf(
            Op("POST", "/users/{id}/devices", "Register a push device, or refresh an existing registration (self or the app's own key)"),
            Op("PATCH", "/users/{id}/devices/{deviceId}", "Update a device's name, app version, environment, or rotated token (self or the app's own key)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "NTF-05", title = "User — Notifications & devices", domain = "D13", route = "/admin/users/:userId/notifications",
            archetype = Archetype.TAB, phase = Phase.PHASE_2, section = Section.ADMINISTRATION,
            purpose = "Support view of a person's inbox and push devices for investigations.",
            who = "users.list (read), users.manage (delete devices)",
            operations = listOf(
            Op("GET", "/users/{id}/devices", "List a User's push devices (self, the app's own key, or users.list)"),
            Op("GET", "/users/{id}/devices/{deviceId}", "Fetch one device"),
            Op("DELETE", "/users/{id}/devices/{deviceId}", "Unregister a device (self, the app's own key, or users.manage)"),
            Op("GET", "/users/{id}/notifications", "List a User's notification inbox (self, the app's own key, or users.list)"),
            Op("GET", "/users/{id}/notifications/{notificationId}", "Fetch one notification, with its per-device deliveries")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "NTF-06", title = "Send notification", domain = "D13", route = "/admin/notifications/new",
            archetype = Archetype.FORM, phase = Phase.PHASE_2, section = Section.ADMINISTRATION,
            purpose = "Staff tool: send one notification to one person on behalf of an Application.",
            who = "platform notifications.send",
            operations = listOf(
            Op("GET", "/users", "List or search users"),
            Op("POST", "/users/{id}/notifications", "Send one notification to one User (the app's own key with notifications.send, or platform notifications.send)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "APL-07", title = "Push credentials", domain = "D9", route = "/dev/apps/:appId/push",
            archetype = Archetype.TAB, phase = Phase.PHASE_2, section = Section.DEVELOPER,
            purpose = "Configure APNs, FCM and Web Push for an Application so it can send push notifications.",
            who = "Owner or applications.manage with a User token (write); app key / owner (read)",
            operations = listOf(
            Op("GET", "/applications/{id}/push-credentials", "List the push providers an Application has configured (the owner, the app's own key, or applications.manage)"),
            Op("POST", "/applications/{id}/push-credentials", "Configure one push provider (the owner or applications.manage, User token only)"),
            Op("GET", "/applications/{id}/push-credentials/{provider}", "Fetch one provider's configuration"),
            Op("PATCH", "/applications/{id}/push-credentials/{provider}", "Replace key material, change non-secret fields, or re-enable a failed credential (User token only)"),
            Op("DELETE", "/applications/{id}/push-credentials/{provider}", "Remove a push provider (User token only)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "GLB-01", title = "Command palette", domain = "G", route = "(overlay)",
            archetype = Archetype.OVERLAY, phase = Phase.MVP, section = Section.GLOBAL,
            purpose = "Jump anywhere and run any action by typing.",
            who = "Signed in",
            operations = listOf(
            Op("GET", "/users", "List or search users")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "GLB-02", title = "Manage hub", domain = "G", route = "/manage",
            archetype = Archetype.HUB, phase = Phase.MVP, section = Section.GLOBAL,
            purpose = "Compact-layout landing list for the Administration and Developer sections.",
            who = "Any admin or developer permission",
            operations = listOf(),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "GLB-03", title = "System states", domain = "G", route = "(global)",
            archetype = Archetype.OVERLAY, phase = Phase.MVP, section = Section.GLOBAL,
            purpose = "",
            who = "",
            operations = listOf(
            Op("GET", "/health", "Health check"),
            Op("POST", "/auth/token/refresh", "Rotate a platform refresh token for a new access token"),
            Op("POST", "/auth/logout", "Revoke the current session, or every session"),
            Op("GET", "/users/{id}/notifications/unread-count", "Count unread notifications, in total and per Application (self or the app's own key)")
            ),
            apiGaps = listOf(),
        ),
        ScreenSpec(
            id = "OPS-01", title = "Overview", domain = "OPS", route = "/ops",
            archetype = Archetype.DASHBOARD, phase = Phase.PROPOSED, section = Section.OPERATIONS,
            purpose = "Health of each component, 24-hour traffic, error rate and p95 latency, firing alarms, queue backlog, budget and the deployed release.",
            who = "operations.view (proposed, API issue #47)",
            operations = listOf(),
            apiGaps = listOf("#43", "#44", "#47"),
        ),
        ScreenSpec(
            id = "OPS-02", title = "Traffic & latency", domain = "OPS", route = "/ops/traffic",
            archetype = Archetype.DASHBOARD, phase = Phase.PROPOSED, section = Section.OPERATIONS,
            purpose = "Requests and p50/p95/p99 latency by route over 1h, 24h, 7d and 30d, against the NFR targets.",
            who = "operations.view (proposed, API issue #47)",
            operations = listOf(),
            apiGaps = listOf("#43", "#44"),
        ),
        ScreenSpec(
            id = "OPS-03", title = "Errors", domain = "OPS", route = "/ops/errors",
            archetype = Archetype.TABLE, phase = Phase.PROPOSED, section = Section.OPERATIONS,
            purpose = "4xx and 5xx by route and error code, with recent 5xx samples linked by request id.",
            who = "operations.view (proposed, API issue #47)",
            operations = listOf(),
            apiGaps = listOf("#43"),
        ),
        ScreenSpec(
            id = "OPS-04", title = "Usage", domain = "OPS", route = "/ops/usage",
            archetype = Archetype.TABLE, phase = Phase.PROPOSED, section = Section.OPERATIONS,
            purpose = "Requests, errors and 429s by Application, API key and route, with 90 days of daily history.",
            who = "operations.view (proposed, API issue #47)",
            operations = listOf(),
            apiGaps = listOf("#44"),
        ),
        ScreenSpec(
            id = "OPS-05", title = "Jobs & delivery", domain = "OPS", route = "/ops/jobs",
            archetype = Archetype.DASHBOARD, phase = Phase.PROPOSED, section = Section.OPERATIONS,
            purpose = "Webhook and push delivery health, dead-letter queue depth, and scheduled job runs.",
            who = "operations.view (proposed, API issue #47)",
            operations = listOf(),
            apiGaps = listOf("#45", "#46"),
        ),
        ScreenSpec(
            id = "OPS-06", title = "Alerts", domain = "OPS", route = "/ops/alerts",
            archetype = Archetype.LIST, phase = Phase.PROPOSED, section = Section.OPERATIONS,
            purpose = "Firing and resolved alarms, acknowledgement, and alert thresholds.",
            who = "operations.view (proposed, API issue #47)",
            operations = listOf(),
            apiGaps = listOf("#47"),
        ),
        ScreenSpec(
            id = "OPS-07", title = "Security events", domain = "OPS", route = "/ops/security",
            archetype = Archetype.TABLE, phase = Phase.PROPOSED, section = Section.OPERATIONS,
            purpose = "Failed sign-ins, lockouts, refresh-token reuse, 401/403 spikes and rate-limit hits.",
            who = "operations.view (proposed, API issue #47)",
            operations = listOf(),
            apiGaps = listOf("#43", "#47"),
        ),
        ScreenSpec(
            id = "OPS-08", title = "Configuration", domain = "OPS", route = "/ops/config",
            archetype = Archetype.SETTINGS, phase = Phase.PROPOSED, section = Section.OPERATIONS,
            purpose = "Feature flags, rate-limit defaults and per-key overrides, CORS origins, maintenance mode and staff MFA policy.",
            who = "operations.view (proposed, API issue #47)",
            operations = listOf(),
            apiGaps = listOf("#48", "#49", "#50"),
        ),
        ScreenSpec(
            id = "OPS-09", title = "Integrations", domain = "OPS", route = "/ops/integrations",
            archetype = Archetype.LIST, phase = Phase.PROPOSED, section = Section.OPERATIONS,
            purpose = "Alert destinations, the MCP server and agent keys, and links to external consoles.",
            who = "operations.view (proposed, API issue #47)",
            operations = listOf(),
            apiGaps = listOf("#47"),
        ),
        ScreenSpec(
            id = "OPS-10", title = "Cost", domain = "OPS", route = "/ops/cost",
            archetype = Archetype.DASHBOARD, phase = Phase.PROPOSED, section = Section.OPERATIONS,
            purpose = "Month-to-date AWS spend against the warn and hard budgets, forecast and anomalies.",
            who = "operations.view (proposed, API issue #47)",
            operations = listOf(),
            apiGaps = listOf("#47"),
        ),
    )

    private val byId: Map<String, ScreenSpec> = all.associateBy { it.id }

    fun byId(id: String): ScreenSpec = byId[id] ?: error("Unknown screen id: $id")

    fun findById(id: String): ScreenSpec? = byId[id]

    fun inSection(section: Section): List<ScreenSpec> = all.filter { it.section == section }
}
