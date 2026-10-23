---
title: API versions and changes
description: How the Platform API is versioned, how deprecation works, and the spec updates this site has picked up.
---

# API versions and changes

There are **two version numbers** to know about.

| | What it is | Where you see it |
|---|---|---|
| **API version** | A whole number for the API's contract: `1` today. Breaking changes get a new number | The `Substratal-Version` request header; one reference per version under *Developers* |
| **Spec version** | `openapi.yaml`'s `info.version`, such as `1.0.0-draft.9`. Changes with every additive update within an API version | The header of each API reference page, and the table below |

The docs site has its own versions too (the menu at the top of the page), which follow the app's releases.

## How API versions change

- **Additive changes ship within a version.** New endpoints, new optional fields and new enum values can appear at any time. Clients must ignore fields they don't know. The Android app does.
- **Breaking changes ship as the next number**, for example `Substratal-Version: 2`. The old version keeps working for **at least 6 months**.
- **Deprecation is announced in every response.** Responses to a deprecated version carry `Deprecation` and `Sunset` headers. The app shows an *Update the app* banner as soon as it sees them.
- **After the sunset date** the old version returns `400 unsupported_api_version`.
- **Webhook payloads** are versioned separately, by date (`api_version` on the subscription, such as `2026-10-05`).

When API version 2 exists, this site will have an API reference for each version side by side, and each app release will say which version it sends.

## Spec updates picked up

Every time the pinned `openapi.yaml` is updated from the API repository, a row is added here. The sync is automated: see [How these docs stay current](../docs-workflow.md).

--8<-- "openapi/CHANGELOG.md"

The API's own changelog has the full detail: [compositecode.github.io/substratalapps.com/changelog](https://compositecode.github.io/substratalapps.com/changelog/).
