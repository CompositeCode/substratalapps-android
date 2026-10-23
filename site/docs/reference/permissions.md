---
title: Permissions and roles
description: Platform permissions, the built-in roles, and what each one shows in the app.
---

# Permissions and roles

Platform permissions control what someone can do in Substratal itself. They never grant anything inside an application; that's what app roles are for.

## Platform permissions

| Permission | Allows | Shows in the app |
|---|---|---|
| `users.list` | See accounts and their roles | Users, Roles (read-only) |
| `users.manage` | Invite, suspend, reactivate, delete accounts; revoke sessions; reset two-factor | Users actions |
| `entitlements.manage` | Turn access on and off, and grant it, for any app | Access |
| `roles.manage` | Create, edit and assign roles | Roles |
| `applications.manage` | Create, approve, reject and suspend applications | Applications, review queue |
| `api_keys.manage` | Create and revoke platform API keys | API Keys |
| `webhooks.manage` | Manage any app's webhooks | Webhooks |
| `tenants.manage` | View and change Tenants | Tenant |
| `organizations.manage` | Manage any organization | Organizations |
| `billing.manage` | See and manage subscriptions | Plan & Billing (staff view) |
| `billing.refund` | Issue refunds | — |
| `audit.view` | Read the audit log | Audit Log |
| `notifications.send` | Send platform notifications | Send notification |
| `operations.view` *(proposed)* | See Operations | Operations |
| `operations.manage` *(proposed)* | Acknowledge alerts, change thresholds, redrive queues | Operations actions |

## Built-in roles

| Role | Permissions |
|---|---|
| `superadmin` | All of the above |
| `support` | `users.list`, `entitlements.manage`, `audit.view`, `tenants.manage`, `billing.manage` |
| `billing_admin` | `billing.manage`, `billing.refund`, `audit.view`, `users.list` |
| `member` | None. Everyone has it |

Built-in roles can't be edited. You can create your own platform roles, but only with permissions you hold yourself.

## Application owners

Owning an application gives you its configuration, keys, webhooks, push credentials and roles, plus its users' access. You get this without any platform permission, but only for your own applications.
