---
title: Tutorials
description: Step-by-step walkthroughs of the most common administration tasks.
---

# Tutorials

Each tutorial covers one task from start to finish. Where the Android screen for a step isn't built yet, the tutorial says so and gives the equivalent API call. You can make those calls with `curl` today.

| Tutorial | You'll need | Time |
|---|---|---|
| [Invite a user](invite-a-user.md) | `users.manage` | 2 min |
| [Turn off someone's access](turn-off-access.md) | `entitlements.manage`, or own the app | 2 min |
| [Find out why someone can't use an app](why-no-access.md) | `users.list` or `entitlements.manage` | 3 min |
| [Register an application](register-an-application.md) | A verified email (staff: `applications.manage`) | 10 min |
| [Create an API key](create-an-api-key.md) | `api_keys.manage`, or own the app | 3 min |
| [Check on the API](check-on-the-api.md) | Operations access | 2 min |

## Calling the API in these tutorials

Every request needs two headers: your access token, and the API version.

```bash
export SUBSTRATAL_API=https://api.substratalapps.com   # no /v1: the version is a header
export TOKEN=…                                          # an access token, from POST /auth/login

curl -s "$SUBSTRATAL_API/users/me" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Substratal-Version: 1"
```

See the [API overview](../developers/api/index.md) for how to get a token.
