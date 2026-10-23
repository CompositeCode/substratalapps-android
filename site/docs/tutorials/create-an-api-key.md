---
title: Create an API key
description: Create a key for a backend service or an AI agent, and keep its secret safe.
---

# Create an API key

API keys are for **servers and agents**, never for apps running on people's devices. The Substratal app itself never uses one; it uses your own sign-in.

=== "In the app"

    1. Go to **Manage › API Keys** and tap **Create key**.
    2. Choose:
        - **Scope:** one application (most keys), or the whole platform (staff only).
        - **Permissions:** only what the service needs. A platform key can't hold a permission you don't have, and an app key can never hold platform-administration permissions such as `users.manage`.
        - **Mode:** **Live**, or **Test**, which only sees test data.
        - **Use:** **Service**, or **Agent**. Agent keys can't run destructive operations unless you allow it.
        - Optionally, an **expiry date**.
    3. Tap **Create**. The **secret is shown once**. Copy it into your password manager or secret store. The app clears it from the clipboard after 60 seconds.

    !!! info "Android status"
        API key screens (KEY-01 to KEY-04) are planned.

=== "With the API"

    ```bash
    curl -s -X POST "$SUBSTRATAL_API/api-keys" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Substratal-Version: 1" \
      -H "Content-Type: application/json" \
      -d '{
            "name": "TimeTrack backend",
            "scope": "app_timetrack",
            "permissions": ["entitlements.manage", "users.list"],
            "mode": "live",
            "intended_use": "service"
          }'
    ```

    The response includes `secret` (`satk_live_…`) **once**. Store it straight away.

!!! danger "Keys don't work in browsers"
    The API refuses an API key sent from a web page (`400 api_key_in_browser`), so a leaked key can't be used from one. Use keys only server-side.

## Keeping keys healthy

- **Rotate** a key from its detail screen (`POST /api-keys/{id}/rotate`). You get a new secret, shown once.
- **Revoke** keys you no longer use. `last_used_at` shows when each key was last used.
