---
title: Audit log
description: Every change to users, access, roles, applications, keys and webhooks — who made it, and what changed.
---

# Audit log

**Manage › Audit Log** lists every change made on the platform. You need `audit.view`. App owners see events for their own applications.

Each event records:

- the **actor**: a user, an API key, or the system
- the **action**, such as `entitlement.disabled` or `role.assigned`
- the **target**
- the **time**
- the **request ID**
- for changes, the values **before and after**

## Filtering

Filter by actor, actor type, target, action, application, and date range. The person page's **Activity** tab is the audit log filtered to that person.

## How long events are kept

- Events are searchable for **one year**.
- Older events are archived, without the before and after values, and kept indefinitely. Support can restore an archived event when it's needed for an investigation. Retrieval takes about 12 hours.

## Exporting

You can export a filtered view as NDJSON or CSV:

- `POST /audit-exports` starts an export.
- `GET /audit-exports/{id}` returns a download link once it's ready, usually within a minute.

You can run 20 exports a day. Files are deleted after 7 days. In-app export is planned.
