---
title: Statuses
description: What each status badge in the app means.
---

# Statuses

Every status in the app is shown as an icon and a label, never colour alone. The same statuses are used on every platform.

| Badge | Kind | Material icon |
|---|---|---|
| **Active** (`active`) | Good | `check_circle` |
| **Scheduled** (`scheduled`) | Information | `calendar_today` |
| **Turned off** (`disabled`) | Needs attention | `pause_circle` |
| **Expired** (`expired`) | Neutral | `schedule` |
| **Revoked** (`revoked`) | Problem | `cancel` |
| **No access** (`none`) | Neutral | `do_not_disturb_on` |
| **Invited** (`invited`) | Highlight | `mail` |
| **Suspended** (`suspended`) | Needs attention | `pause_circle` |
| **Deleted** (`deleted`) | Problem | `delete` |
| **Pending** (`pending`) | Information | `hourglass_empty` |
| **Important** (`important`) | Problem | `priority_high` |
| **Unread** (`unread`) | Highlight | `fiber_manual_record` |

## Where each badge is used

| Field | API value → badge |
|---|---|
| `entitlement.resolved_status` | `active` → Active, `scheduled` → Scheduled, `disabled` → Turned off, `expired` → Expired, `revoked` → Revoked, `none` → No access |
| `user.status` | `active` → Active, `invited` → Invited, `suspended` → Suspended, `deleted` → Deleted |
| `organization.status` | `active` → Active, `suspended` → Suspended |
| `membership_status` | `pending` → Invited, `active` → Active |
| `application.review_status` | `approved` → Active, `pending_review` → Pending, `rejected` → Revoked, `suspended` → Suspended |
| `webhook.status` | `healthy` → Active, `unhealthy` → Turned off, `disabled` → Expired |
| `webhook_delivery.status` | `pending` → Pending, `succeeded` → Active, `failed` → Revoked |
| `tenant.status` | `active` → Active, `migrating` → Scheduled, `suspended` → Suspended |
| `tier_change_request.status` | `pending` → Pending, `scheduled` → Scheduled, `in_progress` → Scheduled, `completed` → Active, `cancelled` → Expired |
| `erasure_request.status` | `scheduled` → Scheduled, `cancelled` → Expired, `completed` → Deleted |
| `subscription.status` | `active` → Active, `trialing` → Scheduled, `past_due` → Turned off, `canceled` → Expired, `unpaid` → Revoked, `incomplete` → Pending, `incomplete_expired` → Expired, `paused` → Suspended |
| `api_key` | `active` → Active, `expired (expires_at past)` → Expired, `revoked (revoked_at set)` → Revoked |
| `user.plan` | `free` → No access, `standard` → Active |
| `entitlement.source_labels` | `purchase` → Purchased, `trial` → Trial, `admin_grant` → Granted by admin, `org_seat` → Via {org}, `free` → Free (managed by Substratal), `subscription` → Standard (managed by Substratal) |
| `device.status` | `active` → Active, `inactive` → Expired |
| `notification.state` | `unread` → Unread, `read` → (no badge), `archived` → (no badge) |
| `notification.priority` | `high` → Important, `normal` → (no badge) |
| `notification.push.status` | `queued` → Pending, `sent` → Active, `failed` → Revoked, `skipped` → No access |
| `push_credential.status` | `active` → Active, `failed` → Turned off |

Source: `status-semantics.json` in the shared design package. The Android app generates its badges from the same file.
