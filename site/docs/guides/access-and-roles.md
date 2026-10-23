---
title: Access and roles
description: How Substratal decides whether a person can use an application, and what they can do in it.
---

# Access and roles

## Is the app on for this person?

Each person has an **entitlement status** for each application:

| Status | Can they use it? |
|---|---|
| **Active** | Yes |
| **Scheduled** | Not yet. It starts later |
| **Turned off** (`disabled`) | No. Someone switched it off |
| **Expired** | No. A trial or subscription ended |
| **Revoked** | No. Ownership was removed |
| **None** | No. Nothing gives them this app |

A person can have **several paths** to the same app at once:

- **Managed by Substratal:**
    - **free**, for free apps
    - **subscription**, for Standard apps while they're on the Standard plan, personally or through an organization
- **Granted:** **admin grant**, **trial**, **purchase**, **organization seat**

The app shows the combined answer first and lists the paths one tap away. Managed paths can only be turned off and back on, never edited or deleted.

## What can they do?

Once an app is on, **roles** decide what they can do in it.

- **App roles** belong to one application, such as `admin`, `editor` and `viewer` for TimeTrack. They grant that app's permissions (`app.timetrack.export`).
- **Platform roles** are for running Substratal itself. See [Permissions and roles](../reference/permissions.md).

Nobody can create or assign a role more powerful than their own.

## Why can't someone use an app?

Use the access check. See [Find out why someone can't use an app](../tutorials/why-no-access.md).
