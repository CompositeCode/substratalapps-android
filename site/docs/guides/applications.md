---
title: Applications, keys and webhooks
description: The developer side of Substratal: registering applications, their credentials, and how they hear about changes.
---

# Applications, keys and webhooks

## Applications

**Manage › Applications** lists the applications you can manage. Staff see every application and can switch to the **Review queue**, which lists submissions waiting for approval, oldest first.

| Review status | Meaning |
|---|---|
| **Pending review** | Submitted by a developer; waiting for staff |
| **Approved** | Live. People can be given access |
| **Rejected** | Not approved. The reviewer's notes say why; the developer can edit and resubmit |
| **Suspended** | Paused by staff. No new access or sign-ins; existing access is untouched |

Each application has:

- **Overview**
- **Settings schema:** the typed settings it stores per person
- **Permissions & roles**
- **Push:** APNs, FCM and Web Push credentials
- its keys and webhooks

Self-registered applications must use the hosted sign-in flow, not embedded sign-in.

## API keys

Keys let a server or agent call the API. See [Create an API key](../tutorials/create-an-api-key.md). Things to know:

- A key's secret is shown **once**, when it's created or rotated.
- **Test** keys only see test data; **live** keys only see live data.
- **Agent** keys can't run destructive operations unless you allow it.
- An application's key is confined to that application.

## Webhooks

Webhooks tell an application's backend when something changes, for example `access.revoked` when someone's access is turned off. Every application must subscribe to `access.revoked` and `role.removed`.

- **Deliveries** shows every attempt, its response and its status. **Redeliver** sends one again.
- Failed deliveries are retried for up to about 12 hours.
- **Rotate secret** issues a new signing secret, shown once.

## Tenant

Your **Tenant** is where your applications' data lives: the shared tier, or dedicated infrastructure for customers who need it. Tier changes are requested from **Tenant** and carried out by Substratal support.
