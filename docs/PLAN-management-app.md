# Substratal Management App for Android: Plan

Status: proposal, 2026-10-21
Target API: `https://api.substratalapps.com` with header `Substratal-Version: 1` (`openapi.yaml` 1.0.0-draft.9, 124 operations; the plan was first written against draft.3)
Design source: `CompositeCode/substratalapps-applications-specs` (Substratal Hub package, `android/` + `00-shared/`)
Companion: [PLAN-api-gaps.md](PLAN-api-gaps.md), which covers what the API is missing for this app. It's now tracked as GitHub issues; see that file's header.

> **Update 2026-10-23: API versioning moved from the URL to a header.**
>
> - The API no longer has `/v1` in its paths. Every request sends `Substratal-Version: 1` against the base URL `https://api.substratalapps.com`.
> - `/v1/...` now returns `404`. A missing header returns `400 api_version_required`; an unknown version returns `400 unsupported_api_version`.
> - Exempt: `GET /health`, `POST /auth/oauth/token`, `/.well-known/*`, `/scim/v2`.
> - Breaking changes ship as a new number with ≥ 6 months of overlap, announced by `Deprecation` and `Sunset` headers.
>
> Paths in this plan were updated to match. The app implements this in `ApiHeadersInterceptor` / `ApiVersionMonitor`. The docs site keeps one API reference per header version (`openapi/v1/` → `developers/api/v1/`).
>
> **Also since the first draft:**
>
> - The proposed ops permissions are named `operations.view` / `operations.manage` (API issue #47).
> - The web console plan reads AWS infrastructure metrics through its own Ops gateway rather than new public-API endpoints, and keeps product usage in the API (`GET /usage`, `GET /stats`, issue #44). Android must call the same gateway with its bearer token, or the gateway must be exposed for native clients. **Agree this with the web team before M3.**

---

## 1. Summary

**Verdict: you can start building now.** The core administration surface is implemented and deployed. That covers users, access (entitlements), roles, applications, API keys, webhooks, push credentials, audit, tenants and organizations: all 118 operations exist, `GET /health` returns `200` in production, auth is enforced, and JWKS is published. The design package already specifies every one of those screens for Android (Material 3 Expressive, Compose, 75 screens).

**What isn't in place:** everything an API operator needs beyond identity administration. The API has no endpoints for:

- traffic, latency and error metrics
- per-application or per-key usage
- background job and queue health
- the security log (it exists only in CloudWatch)
- alarms
- runtime configuration (rate limits, feature flags, CORS)
- cost

The design package has no screens for these either. This plan adds a new **Operations** domain (OPS-01 to OPS-10) to the app, plus a small set of API additions, detailed in the companion doc, that keep to the API's own cost rules: no new always-on resources and no new auth system.

**Recommendation:** build the Android app as the Substratal Hub client defined in the specs, but sequence it admin-first:

1. **Milestone A, Admin Core.** Everything that works against today's API.
2. **Milestone B, Operations.** Needs the API additions.
3. **Milestone C.** The end-user and billing surfaces.

This keeps the app consistent with the cross-platform contract (same screen IDs, routes and navigation) instead of forking a separate "admin app" that the other five clients won't match.

---

## 2. Who the app serves

These personas come from the spec's seed roles. The management app is primarily for the top three.

| Persona | Platform permissions | Primary jobs in this app |
|---|---|---|
| **Superadmin** (you; protected account) | all, incl. `notifications.send` | Everything. Ops dashboards, config, alerts, app registration, staff roles |
| **Support** | `users.list`, `entitlements.manage`, `audit.view`, `tenants.manage`, `billing.manage` | Find a user, see why they can't reach an app, turn access on/off, revoke sessions, read audit |
| **Billing admin** | `billing.manage`, `billing.refund`, `audit.view`, `users.list` | Subscription status, portal hand-off, audit |
| **App owner (developer)** | owns ≥ 1 Application | Their app's config, keys, webhooks, push credentials, their users' access (confined) |
| *(proposed)* **Operator** | new `operations.view` / `operations.manage` | Monitoring, alerts, config without user-data rights. See gaps doc §2.1 |

Navigation and every action are gated by a client-side `Capabilities` object computed from the caller's roles (spec CLIENT-ARCHITECTURE §5). The server remains the authority, and a `403` triggers a capabilities refresh.

---

## 3. Information architecture

### 3.1 Navigation

The spec's tree is kept, with **Operations** added as a third management section. Section order is identical on every platform. Proposed additions are marked ★.

```
Substratal (Android)
├── PERSONAL
│   ├── Home / Inbox / Organizations / Account / Plan & Billing / Settings   (spec, Milestone C except Account+Settings)
├── ★ OPERATIONS               /ops         (visible with operations.view or superadmin)
│   ├── ★ Overview             /ops                       OPS-01
│   ├── ★ Traffic & latency    /ops/traffic               OPS-02
│   ├── ★ Errors               /ops/errors                OPS-03
│   ├── ★ Usage                /ops/usage                 OPS-04  (by application, key, route)
│   ├── ★ Jobs & delivery      /ops/jobs                  OPS-05  (webhooks, push, scheduled jobs, DLQ)
│   ├── ★ Alerts               /ops/alerts                OPS-06
│   ├── ★ Security events      /ops/security              OPS-07
│   ├── ★ Configuration        /ops/config                OPS-08  (flags, rate limits, CORS, maintenance)
│   ├── ★ Integrations         /ops/integrations          OPS-09  (alert destinations, MCP, external tools)
│   └── ★ Cost                 /ops/cost                  OPS-10
├── ADMINISTRATION             /admin
│   ├── Users                  /admin/users               USR-01 (+ USR-03 tabs: Access, Roles, Sessions, Notifications, Activity)
│   ├── Access                 /admin/access              ACX-05
│   ├── Roles                  /admin/roles               ROL-01
│   ├── Audit Log              /admin/audit               AUD-01
│   └── Send notification      /admin/notifications/new   NTF-06
└── DEVELOPER                  /dev
    ├── Applications           /dev/apps                  APL-01 (+ APL-03 tabs incl. ★ Usage, Push APL-07)
    ├── API Keys               /dev/keys                  KEY-01
    ├── Webhooks               /dev/webhooks              WHK-01
    └── Tenant                 /dev/tenant                BIL-04
```

### 3.2 Android containers (from PLATFORM-SPEC, plus Operations)

| Width | Container | Destinations |
|---|---|---|
| Compact < 600 dp | bottom `NavigationBar` | **Home · Inbox · Orgs · Manage · Account** (spec). For a staff user, **Manage** opens GLB-02, which now lists Operations first, then Administration and Developer |
| Medium 600–839 dp | `NavigationRail` | same 5 |
| Expanded ≥ 840 dp | permanent drawer | full tree including Operations |

> **Decision for you (§13, Q1):** the spec's compact bar is end-user-first (Home, Inbox, Orgs). For a management-first app, an admin-optimized compact bar such as **Overview · Users · Apps · Alerts · Account**, shown only when the user holds staff permissions, would be faster. It would also break the cross-platform "identical navigation" contract, which then needs a spec amendment. I recommend keeping the spec bar for now and making GLB-02 the admin landing page, revisiting after Milestone A.

### 3.3 Cross-cutting shell

- **GLB-01 Command palette** (Ctrl+K / search icon). Jump by ID prefix (`usr_`, `app_`, `key_`, `evt_`…) or text. This is the fastest way to triage on mobile.
- **GLB-03 System states.** Offline, session ended, 503 banner, 501 = flag off silently.
- **App shortcuts** (long-press launcher icon): Overview, Users, API Keys, Audit Log, Alerts.
- **Glance widget ★:** "API status" (health, 5xx rate, active alarms count). It reads a cached OPS-01 summary and refreshes through WorkManager at most every 30 min, triggered by FCM ops alerts in between.

---

## 4. Feature set by area

Each area lists screens, the API operations behind them, and whether today's API supports it (✅ yes · 🟡 partly · ❌ needs API work, see gaps doc).

### 4.1 Authentication & session (D1/D2) ✅

| Feature | Screens | API |
|---|---|---|
| Email/password sign-in, TOTP 2FA, forgot/reset | AUTH-01, 02, 05, 06 | `auth.login`, `auth.verifyMfa`, `auth.forgotPassword`, `auth.resetPassword` |
| Accept staff invitation (App Link) | AUTH-07 | `auth.acceptInvitation` |
| Own security: password, 2FA on/off, sessions | ACC-03..06, ACC-04 | `auth.changePassword`, `auth.*MfaTotp`, `auth.listSessions`, `auth.revokeSession` |
| Biometric unlock, 15-min idle lock on admin screens | n/a (client) | n/a |
| **Require 2FA for staff** | client nudge now, enforced later | ❌ no server policy (gaps §3.4) |

Token handling follows CLIENT-ARCHITECTURE §3:

- Access JWT in memory only, 15 min.
- Rotating refresh token in Keystore-backed, Tink-encrypted DataStore, excluded from backup.
- Single-flight refresh in one OkHttp `Authenticator`. Parallel refresh is detected as token reuse and kills the session.
- Proactive refresh 60 s before expiry.

### 4.2 User management (D6) ✅

| Feature | Screens | API |
|---|---|---|
| Directory with search (`q` ≥ 3 chars), status filter, infinite scroll | USR-01 | `users.list` |
| Invite user (Idempotency-Key) | USR-02 | `users.create`, `users.resendInvitation` |
| Overview with tabs: Access, Roles, Sessions, Notifications, Activity (audit filtered by `target_user_id`) | USR-03 | `users.get`, `entitlements.listForUser`, `roles.listForUser`, `auth.listSessions`, `audit.list` |
| Suspend / reactivate, revoke sessions, reset MFA | USR-05, USR-04 | `users.suspend`, `users.update`, `auth.revokeSession`, `auth.resetMfaTotp` |
| Export (GDPR), erasure request / cancel | USR-03 overflow | `users.export`, `users.requestErasure`, `users.cancelErasure` |
| Bulk actions (suspend, grant) | USR-01 multi-select | 🟡 one request per row, max 4 parallel (gaps §3.2) |

### 4.3 Roles & permissions (D8) ✅

- ROL-01 shows platform and app-scoped roles. ROL-02 is the permission checklist (seed roles are read-only, with a reason shown). ROL-03 creates a role, guarded against escalation (`403 role_escalation_forbidden` is surfaced inline). ROL-04 shows a user's roles.
- API: `roles.*`, `permissions.list`, `permissions.getEffective`.
- 🟡 "Who holds this role" isn't possible yet; there is no `GET /users?role_id=` (gaps §3.1).
- **"Why?" explainer (ACX-04):** `effective-permissions` plus entitlement paths answer "why can or can't this person do X in app Y". This is support's most-used tool.

### 4.4 Access control: entitlements (D7) ✅

- AccessSwitch on every user/app pair. Turning access off gives a 10 s Undo snackbar. If-Match is sent on every PATCH, and `409 version_conflict` offers Reload or Re-apply.
- Platform-managed rows (`free`, `subscription`) can only be turned off or on, never edited.
- ACX-05 "All access" is filtered by app and status.
- API: `entitlements.*`, `organizations.listEntitlements` / `grantEntitlement`.

### 4.5 Application management (D9) ✅ add / 🟡 remove / ❌ usage

| Feature | Screens | API |
|---|---|---|
| List all registered apps; filter by `review_status`, `access_tier`, owner | APL-01 | `applications.list` |
| **Add** an application (slug, name, `launch_url`, `redirect_uris`, permissions, `available_app_roles`, `access_tier`, visibility) | APL-02 (wizard) | `applications.create` (platform admin) |
| Overview: status, owner, tier, keys, webhooks, roles, push | APL-03 | `applications.get`, `apiKeys.list?application_id`, `webhooks.list`, `roles.list` |
| Settings schema editor (JSON-Schema subset, validated client-side) | APL-04 | `applications.update` |
| App permissions and app roles | APL-05 | `applications.update`, `roles.*` |
| Review queue: approve, reject (notes), **suspend / reinstate** | APL-06 | `applications.update` (`review_status`) |
| Push credentials: APNs `.p8`, FCM JSON, VAPID (file picked via SAF, sent once, never cached) | APL-07 | `pushCredentials.*` |
| Who has access to this app | APL-03 › Access | `entitlements.list?application_id` |
| **★ Usage & activity tab:** requests, errors, latency, active users, last key use | APL-03 › Usage | ❌ gaps §2.2. Interim: key `last_used_at` plus audit by `application_id` |
| **Remove** an application | APL-03 overflow | 🟡 only `suspend` exists; no retire/delete (gaps §3.3) |

The remove flow, once the API supports it, is a guarded wizard:

1. Show the impact: active entitlements, keys, webhooks and devices.
2. Suspend.
3. Revoke keys and disable webhooks.
4. Retire, with a type-the-slug confirmation.

Until then, "Remove" runs steps 1–3 and leaves the app `suspended`.

### 4.6 API key management (D10) ✅

**How keys are handled on the client.** The phone never holds an API key for its own use. The management app authenticates with the admin's platform session (a user JWT) and only manages keys. That means:

- **Create (KEY-02):** scope (platform or one app), permissions picker limited to permissions the admin holds, `mode` live/test, `intended_use` service/agent (agent defaults to `restrict_destructive=true`), optional `expires_at`. Idempotency-Key is generated when the form opens.
- **Secret reveal (KEY-03):** a non-dismissible dialog, `FLAG_SECURE` on the window, clipboard marked sensitive and auto-cleared after 60 s, plus a share-to-password-manager option. The secret is shown once and never stored, logged or cached.
- **Detail (KEY-04):** permissions, `last_used_at`, created by, expiry. Actions: rotate (shows the new secret through KEY-03), revoke, edit name/permissions, toggle `restrict_destructive` (audited as `api_key.restrict_destructive_disabled`).
- **Hygiene view ★ (KEY-01 filters):** "unused 30+ days", "expiring in 14 days", "agent keys with destructive allowed", "platform-scoped keys". These are computed client-side from list data, so no API change is needed.
- ❌ Per-key request volume and 429 counts need gaps §2.2.

**Internal-use keys.** The app backends and smoke-test keys are visible like any other key, but tagged with the owning app and `intended_use`. The superadmin can see every key; app owners see only their own.

### 4.7 Webhooks (WHK) ✅

List, add/edit with an event picker, rotate secret (KEY-03 reveal), send test, deliveries list with status filter, delivery detail with request/response, and redeliver. API: `webhooks.*`.

❌ A cross-webhook failure rate and DLQ depth on OPS-05 need gaps §2.3.

### 4.8 Audit log (D12) ✅

- AUD-01 has filters for actor, actor type, target, action, application, and since/until.
- AUD-02 shows the event with a before/after diff.
- The `Audit-Hot-Window-Start` header tells the user when older events are archived (Glacier, support restore).
- 🟡 Export is Phase 3 (gaps §3.5). Interim: share the current filtered page as JSON through the Sharesheet.

### 4.9 ★ Operations domain (new screens; all ❌ until the API additions land)

| ID | Screen | Content | Backed by (proposed, gaps doc) |
|---|---|---|---|
| OPS-01 | **Overview** | Health of each component (API, MCP, Stripe webhook, worker, jobs, DB); a strip of requests, 5xx %, p95 over the last 24 h; active alarms; queue backlog; budget-used %; last deploy (version, commit, migration). Pull to refresh | `GET /ops/summary` |
| OPS-02 | **Traffic & latency** | Requests and latency (p50/p95/p99) by route over 1h/24h/7d/30d. NFR targets drawn as reference lines (p95 < 150 ms on hot paths) | `GET /ops/metrics` |
| OPS-03 | **Errors** | 4xx/5xx by route and error code; a sample of recent 5xx with `request_id` that opens the matching audit/security entries | `GET /ops/errors` |
| OPS-04 | **Usage** | By application, by API key, by route; top callers; 429 counts; daily history for 90 days | `GET /ops/usage` |
| OPS-05 | **Jobs & delivery** | Webhook outbox and delivery success %, push delivery by provider, DLQ depth with a redrive action, scheduled job runs (entitlement sweep, push maintenance, audit archive) with last run and status | `GET /ops/jobs`, `POST /ops/queues/{name}/redrive` |
| OPS-06 | **Alerts** | Firing and resolved alarms with history; acknowledge; threshold rules (edit thresholds for 5xx %, latency, failed logins, DLQ, budget); notification preferences | `GET/PATCH /ops/alerts…` + `ops.*` push category |
| OPS-07 | **Security events** | Failed logins, lockouts, refresh-token reuse, 401/403 spikes, `destructive_operation_restricted`, rate-limit hits; filter by user, key or IP | `GET /security-events` |
| OPS-08 | **Configuration** | Feature flags (the 20 flags in CLIENT-ARCHITECTURE §10), rate-limit defaults and per-key overrides, CORS origins, maintenance/read-only mode, signup open/closed, staff MFA requirement. Every change uses If-Match and is audited | `GET/PATCH /platform/config`, `GET /platform/flags` |
| OPS-09 | **Integrations** | Alert destinations (email, Slack/webhook URL, PagerDuty-style webhook), MCP server status and agent keys, outbound webhook catalog. Links to CloudWatch / Stripe / SES consoles | `alert-destinations` CRUD; existing `apiKeys.list?intended_use=agent` |
| OPS-10 | **Cost** | Month-to-date AWS spend against the $75 warn and $150 hard budgets, forecast, top services, anomaly flags | `GET /ops/cost` (daily-cached Cost Explorer) |

Charts use Vico (Compose-native), following the shared dataviz rules: theme roles only, no literal colours, and every chart backed by an accessible data table ("View as table").

### 4.10 Alerts & notifications to admins

This reuses the API's own push pipeline instead of adding a new service:

```
CloudWatch Alarm ─► SNS topic (exists: substratal-prod-alarms)
                     ├─► email (alarm_email, currently empty: set it)
                     └─► ★ ops-alert Lambda (small, new) ─► internal call: platform alert
                                                            category ops.alarm.{name}
                                                            to users holding operations.view
                                                            ─► existing push worker ─► FCM ─► app_hub on Android
```

- **Android:** a new notification channel **Operations alerts** (`IMPORTANCE_HIGH`), alongside the spec's Security, Account and App updates channels. Tapping opens OPS-06 at the alarm. Actions: **Acknowledge** and **Open**.
- Pushes are prompts, not truth: the app always refetches OPS-01/OPS-06.
- The inbox (NTF-01) keeps a 90-day history of every ops alert, so a missed push is never a missed alert.

### 4.11 User activity tracking & compliance

| Need | Source |
|---|---|
| What changed, who did it | Audit log (✅) |
| Who tried to get in, from where | Security log (❌ API-exposed in gaps §2.4) |
| Where an admin is signed in | Sessions (✅) |
| Staff actions in this app | Every write is already audited server-side; the app adds no separate tracking |
| Privacy-preserving product telemetry | Spec §12 event names, hashed install ID, opt-out in SET-01. **No** IDs, emails or tokens |

### 4.12 Personal & end-user surfaces (Milestone C)

Home/launch (APP-*), Inbox (NTF-01/02), devices (NTF-03/04), Organizations (ORG-*), Plan & Billing (BIL-01..03, with Upgrade gated off on Android pending Play policy), Settings and themes (SET-01/02). All are specified and API-backed. They're scheduled last only because this is the management build.

---

## 5. Interface design

The visual system is already defined; this plan follows it rather than inventing one.

- **Material 3 Expressive** with `material3-adaptive` (`NavigationSuiteScaffold`, `ListDetailPaneScaffold`, `SupportingPaneScaffold`).
- **Themes:** the 5 light and 5 dark Substratal themes from `android/theme/SubstratalThemes.kt`; defaults are Moss Paper and Forest Floor. Dynamic colour is off.
- **Brand:** assets come from `android/brand-assets/res`, merged as-is.
- **Status semantics:** `00-shared/status-semantics.json` is the single mapping from status to badge colour role, icon and label. Ops statuses (OK, Degraded, Firing, Acknowledged, Resolved) should be added to it so all platforms agree.
- **Layout:** 16/24 dp margins, 48 dp touch targets, IDs in monospace, Extended FAB for the primary action.

### 5.1 Key screen sketches (compact)

```
OPS-01 Overview                           USR-03 User                    APL-03 Application
┌──────────────────────────────┐   ┌──────────────────────────┐   ┌──────────────────────────┐
│ Operations          ⟳  ⋮    │   │ ← Dana Ruiz      ⋮        │   │ ← TimeTrack   [Approved] │
│ ● All systems normal         │   │ usr_01J… · Active · 2FA ✓ │   │ app_timetrack · Standard │
│ ┌────────┐┌────────┐┌──────┐ │   │ [Access][Roles][Sessions] │   │ [Overview][Usage][Keys]  │
│ │ 12.4k  ││ 0.21%  ││ 88ms │ │   │ [Notifications][Activity] │   │ [Webhooks][Roles][Push]  │
│ │ req/24h││ 5xx    ││ p95  │ │   │ TimeTrack     ● Active ◉  │   │ Requests 24h  ▁▂▅▃▂▆▄    │
│ └────────┘└────────┘└──────┘ │   │  via subscription  Why? › │   │ Errors 0.4%   p95 61ms   │
│ Alerts (1 firing)            │   │ Notes         ○ Off   ◯   │   │ 128 users with access ›  │
│ ▲ webhook-dlq depth > 0   ›  │   │  admin_grant · disabled   │   │ 2 keys · last used 3m ›  │
│ Delivery  webhooks 99.2%     │   │ Ledger        ● Active ◉  │   │ 1 webhook · 1 failing ›  │
│           push 97.8%         │   │                           │   │ ⋮ Suspend · Remove       │
│ Budget  $41 / $75  ▓▓▓▓▓░░   │   │                           │   │                          │
│ Deploy  v1.4.0 · 967e114     │   │            [ Grant access]│   │          [ New key ]     │
├──────────────────────────────┤   ├──────────────────────────┤   ├──────────────────────────┤
│ Home Inbox Orgs Manage Acct  │   │ Home Inbox Orgs Manage …  │   │ Home Inbox Orgs Manage … │
└──────────────────────────────┘   └──────────────────────────┘   └──────────────────────────┘
```

On expanded widths every list → detail pair becomes two panes, and OPS-01 lays out as a dashboard grid with alerts in a supporting pane.

### 5.2 Interaction rules (from spec, applied to ops)

- Destructive actions (revoke key, suspend app, redrive DLQ, change a rate limit) require a confirm dialog that names the object. High-impact config changes (maintenance mode, signup closed, global rate limits) also require biometric re-auth.
- Every swipe or long-press action has a visible equivalent in the overflow menu, for TalkBack and keyboard users.
- Charts have a time-range chip row (1h · 24h · 7d · 30d), pinch to zoom on expanded layouts, and "View as table".

---

## 6. Technology stack

### 6.1 Android client

| Concern | Choice | Notes |
|---|---|---|
| Language / UI | Kotlin 2.x, Jetpack Compose, Material 3 + `material3-adaptive` | Per PLATFORM-SPEC |
| SDK | minSdk 26, targetSdk 36, edge-to-edge, predictive back | |
| Architecture | MVVM + UDF (ViewModel + `StateFlow`), Hilt, Navigation Compose with type-safe routes mirroring screen IDs | Multi-module: `:core:network`, `:core:data`, `:core:designsystem`, `:feature:ops`, `:feature:users`, `:feature:apps`, … |
| HTTP | **OkHttp 5 + Retrofit + kotlinx.serialization**, HTTP/2 to API Gateway, gzip | One OkHttp client; interceptors for auth, `Idempotency-Key`, `If-Match`, `User-Agent: Substratal-Android/{ver}`, `Accept-Language`, request-id capture; `Authenticator` for single-flight refresh |
| API models | **openapi-generator** (`kotlin`, `jvm-retrofit2`, kotlinx-serialization) for GET/list models; **hand-written PATCH bodies** | The API team found that generated models can't express explicit `null` or field presence for PATCH, and the same holds for Kotlin. Use a small `Patch<T>` builder over `JsonObject` |
| Paging | Paging 3 `PagingSource` over cursor pagination (`limit` 25 compact / 50 expanded) | |
| Cache | Room for the catalog, own profile and capabilities; admin lists in memory only (spec §7) | |
| Secure storage | Keystore AES-GCM key → Tink-encrypted DataStore for the refresh token; `dataExtractionRules` excludes it from backup | |
| Background | WorkManager for the daily device re-register, widget refresh and notification actions | |
| Push | Firebase Cloud Messaging (free) as `app_hub` | |
| Charts | **Vico** (Compose) | Lightweight; no WebView |
| Images | Coil 3 | Avatars, app icons |
| Auth extras | Credential Manager, `BiometricPrompt` | |
| Crash reporting | Firebase Crashlytics (free) with PII scrubbing, or Sentry if you'd rather avoid Google | |
| Testing | JUnit5, Turbine, MockK, MockWebServer with **OpenAPI-validated fixtures**, Compose UI tests, Roborazzi screenshot tests (both default themes), one end-to-end suite against the local devserver | The API repo's `make up && make run` gives a full local API |
| Build / CI | Gradle version catalogs, GitHub Actions (lint, detekt, unit, screenshot, assemble), Gradle Managed Devices for instrumented tests | |
| Distribution | Play Console internal → closed → production tracks; Firebase App Distribution for ad-hoc staff builds | |

### 6.2 AWS footprint for this app (kept to near zero)

The Android app is a client; it needs no servers of its own. What it needs in AWS:

| Need | Choice | Cost |
|---|---|---|
| Ops data (metrics, logs, cost) | **New endpoints in the existing API Lambda** (or one small `ops` Lambda), reading CloudWatch `GetMetricData`, Logs Insights, SQS attributes and Cost Explorer server-side and **caching** the results (60 s metrics, 5 min usage, 24 h cost) | Cents per month at Tier 0. GetMetricData is about $0.01 per 1k metrics, Logs Insights $0.005/GB scanned, Cost Explorer $0.01/request (one request a day) |
| Historical usage | Nightly job in the existing `jobs` Lambda that rolls structured request logs into a `usage_daily` table in the existing Aurora | ≈ $0 |
| Admin alert push | One tiny `ops-alert` Lambda subscribed to the existing SNS alarms topic | ≈ $0 |
| App Links + emailed-link fallback | `assetlinks.json` and minimal verify/reset/invite pages on **S3 + CloudFront** at `dashboard.substratalapps.com` (the spec already lists S3+CloudFront in Tier 0) | ≈ $0–1 |
| Config storage | `platform_config` table in the existing Aurora, cached in Lambda memory for 60 s | $0 |

What not to do:

- **No Cognito or identity pool** to let the phone call CloudWatch directly. That would add a second auth system, put AWS credentials on devices, and bypass the platform's permission model and audit.
- **No Managed Grafana or OpenSearch.** They cost $9–$100+/month at idle and break the cost principles.
- **No direct database access.**

Everything goes through the Platform API (or the shared Ops gateway), authorized by `has_platform_permission`, rate-limited and audited like every other call.

### 6.3 Efficient API access

- **Startup capabilities:** today this takes 6 parallel GETs (spec §5). Propose a single `GET /users/me/capabilities` (gaps §3.6).
- **Single OkHttp connection pool,** HTTP/2 multiplexing, and a 10 s connect / 30 s read timeout. Reads retry twice with jitter; writes retry only with an Idempotency-Key.
- **Respect rate limits:** 100 req/min per session. Read the `RateLimit-Remaining` headers and slow background refresh before hitting `429`. On `429` honour `Retry-After` with a visible countdown.
- **Don't poll for alerts;** FCM wakes the app. OPS-01 refreshes on foreground and pull, and every 60 s only while visible.
- **Pagination** at 25 per page; keep pages on back-navigation.
- **No conditional GET today** (`If-None-Match` unsupported). Gaps §3.7 proposes it for cheap refreshes of big lists.

---

## 7. Security model for the management app

- No API keys or AWS credentials on device. The admin's platform session is the only credential.
- Refresh token in hardware-backed Keystore. Opt-in biometric unlock; auto-lock after 15 min idle.
- `FLAG_SECURE` on secret reveal and on all admin screens in the recents snapshot.
- Sensitive clipboard with a 60 s clear.
- Step-up (biometric plus a fresh token) for the highest-impact ops: config changes, platform role assignment, key creation with platform scope, app removal.
- App Links verified. Deep links and `action_url`s are validated before routing; emailed tokens are exchanged immediately and stripped.
- No PII, tokens or notification content in logs, analytics or crash reports.
- Root/integrity: Play Integrity check advisory only (warn, don't block), so a rooted admin device isn't silently trusted.

---

## 8. Error, empty, loading and offline states

Every screen implements the spec's state machine (idle → loading → loaded/empty/error) and the error map:

| Status | Behaviour |
|---|---|
| 401 | Refresh once; on failure, "Session ended" → AUTH-01, keeping the destination |
| 403 | Forbidden state plus a capabilities refresh |
| 404 | Not-found state |
| 409 | Version conflict → Reload / Re-apply; idempotency-in-flight → retry after 1 s |
| 422 | Field errors from `details[].field` |
| 429 | Countdown, then retry |
| 501 | Flag off silently |
| 503 | Banner; reads auto-retry, writes never do |

Offline, cached screens (Overview summary, own profile) render read-only with a timestamp, and all writes are disabled. There is no offline write queue.

---

## 9. Phasing and milestones

| Milestone | Scope | Depends on | Rough size* |
|---|---|---|---|
| **M0 Foundation** | Project scaffold, modules, theme and brand, generated client, auth/session, capabilities, navigation shell (GLB-01/02/03), error/state framework, CI, screenshot tests | API as-is | 2–3 wks |
| **M1 Admin Core** | USR-*, ACX-*, ROL-*, AUD-*, ACC-03..06 (own security), SET-01 | API as-is | 3–4 wks |
| **M2 Developer & Apps** | APL-01..07, KEY-*, WHK-*, BIL-04; key hygiene filters; "Remove app" (suspend + revoke flow) | API as-is | 3 wks |
| **M3 Operations v1** | OPS-01, 03, 05, 06, 07 + ops push channel + Glance widget | Gaps P1 items (§5 of gaps doc) | 3 wks after API work |
| **M4 Operations v2** | OPS-02, 04 (+ APL-03 Usage tab), 08, 09, 10 | Gaps P2 items | 3 wks |
| **M5 Personal & end-user** | APP-*, NTF-01..04, ORG-*, BIL-01..03, SET-02 | API as-is | 4 wks |
| **M6 Hardening** | Accessibility (TalkBack, 200 % font, Switch Access), foldables/ChromeOS, performance budgets, Play release | n/a | 2 wks |

\*One engineer with an AI assistant; for sequencing, not a commitment.

M0–M2 can start immediately and run in parallel with the API work for M3/M4.

### 9.1 Before M0: operational blockers (from the API repo's `remaining_manual_steps.md`)

1. Confirm the Lambda concurrency quota increase and the re-deploy finished. Production returns 200 on health and 400/401 on auth, so the API Lambda is serving.
2. Set the superadmin password and **turn on 2FA**. This is the account you'll use to test the app.
3. **Set `alarm_email`.** Operational alarms currently go to no one.
4. SES out of sandbox, or verify the test addresses you'll invite as staff.
5. Register `app_hub` (`access_tier: free`, `visibility: internal`, `receives_platform_notifications: true`) and upload its FCM credential through the API. This is needed for push in M3.
6. Host `assetlinks.json` at `dashboard.substratalapps.com/.well-known/`. Invite, verify and reset links point there, and the host doesn't exist yet.

---

## 10. Testing strategy

- **Contract:** every MockWebServer fixture is validated against `openapi.yaml` in CI, so a fixture that drifts from the spec fails the build.
- **Integration:** a nightly job runs the app's data layer against the API devserver (`docker compose` + `make run`) with seeded personas: superadmin, support, billing admin, app owner, member.
- **Persona matrix:** for each persona, assert visible destinations and enabled actions (spec acceptance criterion).
- **Screenshots:** every screen in Moss Paper and Forest Floor, at compact and expanded sizes, and at 200 % font scale.
- **Manual:** Pixel, foldable, tablet, ChromeOS; FCM end to end; App Links verification.

---

## 11. Repository layout (proposed)

```
substratalapps-android/
├── app/                         # Application, MainActivity, NavHost, DI graph
├── core/
│   ├── network/                 # OkHttp, interceptors, Authenticator, generated API, Patch builder
│   ├── data/                    # Repositories, Room, DataStore+Tink, Capabilities
│   ├── designsystem/            # SubstratalTheme (from specs), components (StatusBadge, AccessSwitch, SecretReveal, Charts)
│   ├── model/                   # Domain models
│   └── testing/                 # Fixtures, fakes, OpenAPI validator
├── feature/
│   ├── auth/  account/  users/  access/  roles/  audit/
│   ├── apps/  keys/  webhooks/  tenant/
│   ├── ops/                     # OPS-01..10
│   └── home/  inbox/  orgs/  billing/  settings/   (M5)
├── widget/                      # Glance "API status"
├── openapi/openapi.yaml         # Pinned copy; CI checks it matches the API repo's tag
├── docs/                        # This plan, ADRs
└── .github/workflows/
```

---

## 12. Risks

| Risk | Mitigation |
|---|---|
| Ops endpoints slip and the app's headline features wait | M0–M2 have no dependency on them; OPS screens ship behind flags (501 = hidden) |
| CloudWatch / Logs Insights query cost grows with traffic | Server-side caching; queries are bounded by time range; nightly rollups serve history; review at the spec's 10× growth trigger |
| Design package diverges from the API (e.g. a 118-op count change) | The spec wins; record the difference in OPEN-QUESTIONS; CI pins the `openapi.yaml` version |
| A lost or rooted admin phone | Hardware-backed tokens, biometric step-up, remote session revoke from any other client (USR-04 / ACC-04) |
| Play policy on subscription upgrades | Not relevant to management; Upgrade stays flag-off |

---

## 13. Decisions needed from you

1. **Compact nav for staff:** keep the spec's end-user bar with Manage (recommended for now), or an admin-first bar?
2. **New `operations.view` / `operations.manage` permissions** (and an `operator` seed role) versus reusing superadmin only? Recommended: add them, so a future on-call person doesn't need user-data rights.
3. **App removal semantics:** soft "retired" status (recommended; keeps the audit and the slug reserved) versus hard delete.
4. **Crash reporting:** Crashlytics (free; already on Firebase for FCM) or Sentry.
5. **Scope of the first Play release:** staff-only internal track with M0–M3, or wait for M5 so one app serves everyone?
