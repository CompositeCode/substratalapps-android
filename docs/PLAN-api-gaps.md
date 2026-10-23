# What the API Is Missing for the Management App

> **Superseded 2026-10-23.**
>
> These gaps are now tracked as `[api-gaps]` issues in [CompositeCode/substratalapps.com](https://github.com/CompositeCode/substratalapps.com/issues?q=label%3Aapi-gaps), shared by every platform's console.
>
> **Built since this was written:**
>
> - CORS (#38)
> - role filter on users (#54)
> - revoke all sessions (#51)
> - audit export (#33/#56)
> - self-service registration (#32)
> - test-user expiry (#30)
>
> **Still open:**
>
> | Item | Issue |
> |---|---|
> | Structured request logs | #43 |
> | Usage and stats | #44 |
> | Delivery health | #45 |
> | Job runs | #46 |
> | `operations.view` / `operations.manage` (named `ops.*` below) | #47 |
> | Platform settings and flags | #48 |
> | Per-key rate limits | #49 |
> | Staff MFA and step-up | #50 |
> | Retiring applications | #52 |
> | List counts | #53 |
> | Bulk operations | #55 |
> | Spec/UI package refresh | #40 |
>
> The API also moved its version from the URL to the `Substratal-Version` header. Paths below have had `/v1` removed.
>
> Kept for history; the issues are authoritative.

Status: proposal, 2026-10-21
Reviewed: `CompositeCode/substratalapps.com` at `967e114` (main): `docs/openapi.yaml` (118 ops), docs site, `infra/`, `migrations/`, `plan-remaining.md`, `remaining_manual_steps.md`; live probes of `https://api.substratalapps.com`.
Companion: [PLAN-management-app.md](PLAN-management-app.md)

---

## 1. Current state

| Area | State | Evidence |
|---|---|---|
| Deployed and serving | ✅ | `GET /health` → `200 {"status":"ok"}`; unauthenticated `GET /users` → `401 unauthenticated`; JWKS served |
| Identity and access admin (users, sessions, MFA, roles, entitlements, orgs, tenants) | ✅ complete | 118 operations implemented, contract-tested |
| Applications: create, update, review/suspend, push credentials | ✅, except removal | No `DELETE /applications/{id}` or retire status |
| API keys and webhooks | ✅ | `last_used_at` (1-min granularity) is the only usage signal |
| Audit log | ✅ list/get with good filters | No export (Phase 3); 1-year hot window, then Glacier |
| Security log | ⚠️ exists, **not exposed** | CloudWatch Logs only (NFR §Security log) |
| Metrics, usage, latency, errors | ❌ none in the API | CloudWatch only; 4 alarm types in Terraform |
| Alarms | ⚠️ defined, **no recipient** | `alarm_email` is empty in `infra/terraform/prod/variables.tf` |
| Runtime configuration (rate limits, flags, CORS) | ❌ env vars / Terraform only | `CORS_ALLOWED_ORIGINS` from TF var; rate limits hard-coded defaults |
| Feature flags endpoint | ❌ | The client spec expects "remote-configurable" flags (CLIENT-ARCHITECTURE §10) but nothing serves them |
| Dashboard host (`dashboard.substratalapps.com`) | ❌ doesn't exist | Emailed invite/verify/reset links point there; App Links need `assetlinks.json` there |

So the API is a complete **identity and access control plane**, but it has no **operations plane**. Below, P1 items block Milestone M3 (Operations v1), P2 items block M4, and P3 items are quality improvements.

All proposals follow the API repo's own rules:

- spec first (`openapi.yaml` + docs page + Changelog)
- `has_platform_permission` on every endpoint
- standard error envelope, cursors and ETags
- contract tests
- expand-only migrations
- **no new always-on AWS resources**

---

## 2. Operations plane (new)

### 2.1 New permissions and role (P1)

| Permission | Grants |
|---|---|
| `ops.view` | Read every `/ops/*` and `/security-events` endpoint; receive `ops.*` platform alerts |
| `ops.manage` | Acknowledge alerts, edit alert thresholds and destinations, redrive queues |
| `platform.configure` | `PATCH /platform/config` and flags |

- Add them to `role_platform_superadmin` in a migration, as `0003_push.sql` did for `notifications.send`.
- Add a seed role `operator` with `{ops.view, ops.manage}` and **no** user-data permissions.
- `support` optionally gets `ops.view`.

### 2.2 Metrics and usage (P1 summary/errors, P2 usage/history)

```
GET /ops/summary
  → { components: [{name: api|mcp|stripe_webhook|worker|jobs|database, status: ok|degraded|down, detail}],
      window_24h: { requests, error_rate_5xx, error_rate_4xx, p50_ms, p95_ms, p99_ms, throttles },
      alarms: { firing: n }, queues: { webhook_dlq_depth, push_backlog },
      budget: { month_to_date_usd, warn_usd, hard_usd }, release: { version, commit, migration, deployed_at } }

GET /ops/metrics?metric=requests|latency|errors|throttles|db_acu&route=&period=1h|24h|7d|30d&step=
  → { series: [{ label, points: [{t, v}] }], reference: { p95_target_ms } }

GET /ops/errors?since=&until=&status=5xx|4xx&route=&code=   (cursor-paginated)
  → items: [{ timestamp, route, status, error_code, request_id, actor_type, application_id }]

GET /ops/usage?group_by=application|api_key|route&since=&until=   (cursor-paginated)
  → items: [{ key, requests, errors, rate_limited, p95_ms, active_users }]
```

**Implementation, cheapest path:**

1. **Structured access log line.** The API Lambda emits one JSON line per request with route template, status, duration, `application_id`, `api_key_id`, `actor_type`, `request_id` and error code. Use CloudWatch **Embedded Metric Format** only for low-cardinality metrics (route × status class). Per-app and per-key data stays in the log line, so there's no custom-metric cost explosion.
2. **Summary and metrics:** `GetMetricData` against the API Gateway, Lambda, Aurora and SQS built-in metrics, plus the EMF metrics. Cache in memory per Lambda for 60 s.
3. **Errors and recent usage:** Logs Insights queries bounded by time range (≤ 7 days live), cached for 5 min.
4. **Usage history:** a nightly job in the existing `jobs` Lambda rolls the previous day into `usage_daily(day, application_id, api_key_id, route, requests, errors, rate_limited, p95_ms)` in Aurora, kept 400 days. Usage reports and the APL-03 Usage tab read from here and never scan logs.
5. **Release info:** add `version`, `commit` and `migration` to the build (ldflags) and expose them in the summary. Optionally extend `/health` with `?verbose=true` for `ops.view` holders.

IAM: the API Lambda role gains `cloudwatch:GetMetricData`, `logs:StartQuery` / `GetQueryResults` on the API log group, `sqs:GetQueueAttributes`, and `ce:GetCostAndUsage`. Better: put these in a separate small `ops` Lambda so the main API role stays narrow. That matches the repo's one-function-per-trust-boundary pattern.

### 2.3 Jobs, queues and delivery health (P1)

```
GET  /ops/jobs                      → scheduled jobs: name, schedule, last_run_at, last_status, last_duration_ms, next_run_at
GET  /ops/queues                    → webhook queue + DLQ: depth, oldest_age_s; push backlog; outbox lag
POST /ops/queues/{name}/redrive     → ops.manage; Idempotency-Key; audited (ops.queue_redriven)
GET  /ops/deliveries?kind=webhook|push&group_by=application|provider&period=
     → success %, failures by reason (from webhook_deliveries / notification_deliveries tables)
```

The jobs Lambda records each run in a new `job_runs` table (name, started_at, finished_at, status, summary JSON) so job history is queryable without CloudWatch.

### 2.4 Security events (P1)

`GET /security-events?type=&actor_id=&ip=&since=&until=` (cursor). Requires `audit.view` or `ops.view`.

The log already exists with the right fields (NFR: login, failure, MFA, refresh reuse, lockout, 401/403, rate-limit hits, request id, IP, user agent, outcome). Serve it through Logs Insights for ≤ 30 days, cached. Optionally mirror lockouts and refresh reuse into an Aurora `security_events` table, since those are low-volume and high-value.

### 2.5 Alerts (P1 delivery, P2 rule editing)

1. **Now, no code:** set `alarm_email` so the existing alarms reach someone.
2. **Push to admins:** a small `ops-alert` Lambda subscribed to the `substratal-prod-alarms` SNS topic. It sends a **platform alert** with category `ops.alarm.<name>` (state `ALARM` / `OK`) to every user holding `ops.view`, through the existing notifications pipeline. Changes needed:
   - add `ops.*` to the notification category enum
   - allow `system` as the platform-alert sender
   - include `ops.*` in the categories `app_hub` receives
3. **Endpoints:**

   ```
   GET   /ops/alerts?state=firing|resolved|all       → from CloudWatch DescribeAlarms + alarm history, cached 60 s
   POST  /ops/alerts/{name}/acknowledge               → stored in Aurora (who/when/note), audited
   GET   /ops/alert-rules                             → threshold, period, evaluation, enabled
   PATCH /ops/alert-rules/{name}                      → ops.manage; If-Match; calls PutMetricAlarm; audited
   ```

   Terraform ignores those attributes (`lifecycle { ignore_changes = [threshold] }`), or Terraform keeps ownership and rule edits become a PR instead. **Decide which**: ignoring drift is simpler for an app-driven workflow.
4. **Missing alarms worth adding:**
   - failed-login spike and refresh-token reuse (stated in NFR; confirm it exists in TF)
   - push delivery failure rate
   - `push_credential` marked `failed`
   - job didn't run in 2× its interval
   - p95 latency over target on the hot paths
   - 429 spike

### 2.6 Alert destinations and integrations (P2)

`/ops/alert-destinations` CRUD: `email`, `webhook` (generic signed JSON, which covers Slack and PagerDuty through their incoming-webhook URLs). Delivery reuses the webhook signing secret and worker.

The existing outbound webhooks and MCP server already cover "integrate with external tools" for domain events. This adds the same for operational events.

### 2.7 Cost (P2)

`GET /ops/cost` returns month-to-date, forecast, the top 5 services, and the warn/hard thresholds, from Cost Explorer `GetCostAndUsage` cached for 24 h. That's about $0.30/month. Anomaly findings come from Cost Anomaly Detection `GetAnomalies`.

---

## 3. Gaps in existing resources

### 3.1 Role holders (P2)
`GET /users?role_id=role_…` so ROL-02 can show who holds a role, and to audit "who is superadmin". This is open in the client specs as OPEN-QUESTIONS #5.

### 3.2 Bulk operations (P3)
`POST /users/bulk` (suspend/reactivate) and `POST /entitlements/bulk` (grant/disable), with per-item results and an Idempotency-Key. Until then the client fans out at most 4 in parallel, which counts against the 100 req/min limit.

### 3.3 Application removal (P1 for "add/remove applications")
Today an app can only be `suspended`. Proposal:

- Add `review_status: retired`. Allowed transitions: `suspended → retired` (needs `applications.manage` and `review_notes`). The status is terminal.
- **On retire:**
  - revoke the app's API keys
  - disable its webhooks
  - expire its developer-granted entitlements, firing `access.revoked` with reason `application_retired`
  - deactivate devices and delete push credentials
  - keep the row, slug, roles and audit
  - writes `application.retired`
- `GET /applications/{id}/impact` returns counts of active entitlements, keys, webhooks, devices and org grants, for the confirmation screen.
- Hard delete is not recommended: the audit log and Entitlement history reference the app.

### 3.4 Security policy (P2)
- **Require MFA for platform-permission holders.** Today nothing enforces it: login of a staff user without TOTP should return `403 mfa_enrollment_required` with an enrollment path, or the policy should be a platform config flag (§4).
- **Admin "sign out everywhere"** for another user, without suspending them. Today only self-logout supports `all_sessions`; for others it's one `DELETE` per session. Proposal: `DELETE /users/{id}/sessions` (`users.manage`, destructive, audited).
- **IP allow-list for platform-scoped API keys** (optional).

### 3.5 Audit export (P3, already Phase 3)
`POST /audit-events/exports` creates an async job that writes NDJSON to S3 and returns a short-lived pre-signed URL. Also extend `audit_action` to cover the new ops/config actions.

### 3.6 Capabilities in one call (P2)
`GET /users/me/capabilities` returns permission keys, owned apps and tenants, org-admin memberships, plan, and **feature flags**. This replaces the 6 calls the client makes at startup and on every foreground refresh, which saves requests against the 100/min budget and cuts startup latency on mobile.

### 3.7 Conditional GET (P3)
Support `If-None-Match` → `304` on single-resource GETs and on the applications catalog and roles list. ETags already exist; this is cheap and saves bandwidth for mobile refreshes.

### 3.8 Per-key rate-limit overrides (P2)
The NFR says to "raise per-key limits for high-volume integrations", but `ApiKey` has no field for it. Add `rate_limit_per_minute` (nullable; `platform.configure` to set), and expose it in KEY-04.

### 3.9 Items carried from the client specs' OPEN-QUESTIONS

| # | Gap | Proposal |
|---|---|---|
| 1 | Avatar upload | Signed S3 upload URL endpoint |
| 2 | WNS for Windows | Add the `wns` provider (not needed for Android) |
| 9 | App launch contract | `launch_mode` field on Application |
| 15 | CORS for the dashboard origin | Confirm `cors_origins` includes `https://dashboard.substratalapps.com` and exposes `ETag`, `Retry-After`, `Audit-Hot-Window-Start` |
| 22 | Theme choice storage | Add `light_theme`/`dark_theme` to `Settings` (simpler than per-app settings for `app_hub`) |

---

## 4. Platform configuration (P2)

```
GET   /platform/config      (ops.view)            → ETag
PATCH /platform/config      (platform.configure)  → If-Match, audited as platform.config_updated with before/after
GET   /platform/flags       (any authenticated)   → effective feature flags for the caller (also folded into capabilities)
```

| Key | Type | Today |
|---|---|---|
| `flags.*` | per flag: `on`/`off`/`percent`, optional `platforms[]` | none: clients hard-code |
| `rate_limits.default_per_minute`, `.auth_per_ip`, `.hot_path_per_minute`, … | int | hard-coded NFR defaults |
| `cors.allowed_origins` | string[] | Terraform var |
| `signup.enabled` | bool | always open |
| `maintenance.mode` | `off` / `read_only` | none (Tenant `migrating` exists per tenant only) |
| `security.require_staff_mfa` | bool | none |
| `notifications.recipient_hourly_limit` | int | hard-coded 60 |

**Storage:** a single-row `platform_config` JSONB table in Aurora, validated against a JSON Schema (reuse `internal/schema`), cached in each Lambda for 60 s. This is consistent with "non-secret config in env vars" for **deploy-time** settings while making **operational** settings changeable without a deploy. SSM Parameter Store standard tier (free) is an alternative, but Aurora gives ETags, RLS and audit in the same transaction for free.

Secrets (Stripe keys and the like) stay in Secrets Manager and are **never** exposed or edited through the API.

---

## 5. Prioritised backlog

| P | Item | Section | Unblocks |
|---|---|---|---|
| **P0** | Set `alarm_email`; set superadmin password + MFA; SES sandbox exit or verified test addresses; register `app_hub` + FCM credential; host `assetlinks.json` and minimal verify/reset/invite pages on S3+CloudFront at `dashboard.substratalapps.com` | runbook | Testing the app at all; App Links; push |
| **P1** | `ops.*` permissions + `operator` role | 2.1 | All OPS screens |
| **P1** | `/ops/summary`, `/ops/errors`, structured access log | 2.2 | OPS-01, OPS-03 |
| **P1** | `/ops/jobs`, `/ops/queues`, redrive, `job_runs` | 2.3 | OPS-05 |
| **P1** | `/security-events` | 2.4 | OPS-07 |
| **P1** | `ops-alert` Lambda → `ops.*` platform alerts; `/ops/alerts` + acknowledge | 2.5 | OPS-06, push alerts |
| **P1** | `retired` status + `/applications/{id}/impact` | 3.3 | Remove application |
| **P2** | `/ops/metrics`, `/ops/usage`, `usage_daily` rollup | 2.2 | OPS-02, OPS-04, APL-03 Usage |
| **P2** | Alert rule editing, alert destinations | 2.5–2.6 | OPS-06 rules, OPS-09 |
| **P2** | `/platform/config`, `/platform/flags`, capabilities endpoint | 4, 3.6 | OPS-08, faster startup |
| **P2** | Staff MFA enforcement, admin sign-out-everywhere, per-key rate limits, role-holder filter | 3.4, 3.8, 3.1 | Security posture |
| **P2** | `/ops/cost` | 2.7 | OPS-10 |
| **P3** | Bulk endpoints, conditional GET, audit export, avatar upload | 3.2, 3.7, 3.5 | Efficiency and polish |

## 6. Cost impact

| Addition | Monthly at Tier 0 |
|---|---|
| `ops` Lambda + `ops-alert` Lambda (no VPC, on-demand) | < $0.50 (free tier) |
| CloudWatch `GetMetricData` (cached 60 s, a few admins) | < $1 |
| Logs Insights (bounded queries, cached, history from rollups) | < $1 |
| EMF custom metrics (≈ 20 low-cardinality metrics) | ≈ $6. Cut by keeping only route-class × status-class, or skip EMF and derive from built-in API Gateway metrics: **$0** |
| Cost Explorer (1 call/day) | ≈ $0.30 |
| Aurora tables (`usage_daily`, `job_runs`, `platform_config`, acks) | ≈ $0 (storage only, within the existing ACU floor) |
| S3 + CloudFront for `dashboard.substratalapps.com` static pages | ≈ $0–1 |
| **Total** | **≈ $1–3/month** (≈ $8 with EMF), against the existing ~$45–55 floor; well inside the $75 warn budget |
