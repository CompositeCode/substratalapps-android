---
title: Error messages
description: What the app's error messages mean, and what to do about them.
---

# Error messages

| You see | Cause | What to do |
|---|---|---|
| *You're offline.* | No connection to the API | Reconnect, then **Try again**. Screens you've opened show what was last loaded |
| *Session ended* | Your session expired or was revoked, for example signed out from another device | Sign in again |
| *You don't have access* | Your role doesn't include this (`403`) | Ask an administrator. The app also rechecks your permissions |
| *Someone else changed this* | Another person saved a change first (`409 version_conflict`) | Choose **Reload** to see theirs, or **Re-apply** to save yours on top |
| *Too many requests. Try again in N seconds.* | Rate limit (`429`). The API allows 100 requests a minute per session | Wait for the countdown |
| *Substratal is temporarily unavailable* | The API is down or in maintenance (`503`) | Changes aren't retried automatically. Try again later |
| *This version of the app is no longer supported* | The API version this app uses has been retired (`400 unsupported_api_version`) | Update the app |
| *Update the app* banner | The API version this app uses is deprecated and has a retirement date | Update before that date |

## Error details

Where a message has **Details**, they include the error code and a **request ID**. Quote the request ID when you contact support; it identifies the exact request in the API's logs.

For the full list of API error codes, see the [API reference](../developers/api/v1/index.md) and the Platform API's [Conventions → Errors](https://compositecode.github.io/substratalapps.com/api-reference/conventions/#errors).
