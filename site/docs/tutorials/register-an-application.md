---
title: Register an application
description: Add an application to the platform so people can be given access to it.
---

# Register an application

An **Application** is anything built on Substratal Apps that people sign in to. Registering it gives it an ID (`app_<slug>`), a catalog entry, and its own permissions and roles.

- **Staff** (`applications.manage`) create applications that are approved straight away.
- **Developers** with a verified email can submit their own. A submission starts as **Pending review**, and staff approve or reject it from the review queue.

=== "In the app"

    1. Go to **Manage › Applications** and tap **Register application**.
    2. Enter:
        - a **slug**: lowercase, permanent, becomes `app_<slug>`
        - a **name**
        - the **launch URL**
        - any **redirect URIs**
    3. Choose the **access tier** (staff only):
        - **Free:** everyone gets it.
        - **Standard:** people on the $6/month Standard plan get it.
    4. Add the app's permissions (`app.<slug>.<action>`) and the role names it uses, such as `admin`, `editor` and `viewer`.
    5. Tap **Register**.

    !!! info "Android status"
        The Applications list and its review-queue filter are available. The registration wizard (APL-02) is planned.

=== "With the API"

    ```bash
    curl -s -X POST "$SUBSTRATAL_API/applications" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Substratal-Version: 1" \
      -H "Content-Type: application/json" \
      -H "Idempotency-Key: $(uuidgen)" \
      -d '{
            "slug": "timetrack",
            "name": "TimeTrack",
            "launch_url": "https://timetrack.example.com",
            "redirect_uris": ["https://timetrack.example.com/auth/callback"],
            "access_tier": "standard",
            "available_app_roles": ["admin", "member"],
            "permissions": [{"key": "app.timetrack.export", "description": "Export timesheets"}]
          }'
    ```

## After registering

1. [Create the app's API key](create-an-api-key.md) for its backend.
2. Subscribe its backend to `access.revoked` and `role.removed` webhooks. This is required, so the app ends sessions when access is turned off.
3. For push notifications, upload APNs, FCM or Web Push credentials on the app's **Push** tab.

## Removing an application

Today an application can be **suspended**, which blocks new access and new sign-ins but leaves existing access alone. Retiring an application completely is waiting on the API ([issue #52](https://github.com/CompositeCode/substratalapps.com/issues/52)).
