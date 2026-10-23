---
title: Find out why someone can't use an app
description: Use the access check to see exactly what decides whether a person can reach an application.
---

# Find out why someone can't use an app

Whether someone can use an application comes down to three questions:

1. Is their **account** active? Suspended accounts can't use anything.
2. Is the app **on for them**? This is their entitlement status, which can come from several places: free, their subscription, an organization seat, a trial or an admin grant.
3. If it's on, **what can they do** in it? That's decided by their roles.

The access check answers all three at once.

=== "In the app"

    1. Open the person (**Manage › Users**), then the **Access** tab.
    2. Next to the application, tap **Why?**
    3. You'll see the resolved status, every path that gives them access (and which ones are off), and their roles and permissions in that app.

    !!! info "Android status"
        The access check (ACX-04) is planned.

=== "With the API"

    ```bash
    curl -s "$SUBSTRATAL_API/users/usr_…/apps/app_timetrack/effective-permissions" \
      -H "Authorization: Bearer $TOKEN" -H "Substratal-Version: 1"
    ```

    ```json
    {
      "user_id": "usr_…",
      "application_id": "app_timetrack",
      "allowed": false,
      "user_status": "active",
      "entitlement_status": "disabled",
      "access_paths": [
        { "source": "admin_grant", "entitlement_id": "ent_…", "status": "disabled" }
      ],
      "effective_permissions": []
    }
    ```

    Here, the only path is an admin grant that's been turned off. Turn it back on ([Turn off someone's access](turn-off-access.md) in reverse), or grant access another way.

## Common answers

| `entitlement_status` | Means | Fix |
|---|---|---|
| `none` | No path gives them this app | Grant access, or check the app's tier (Free or Standard) against their plan |
| `disabled` | Someone turned it off | Turn it on, if that's right |
| `expired` | A trial or subscription ended | Renew or grant again |
| `revoked` | Ownership was removed (refund, chargeback, terms) | Grant again only if appropriate |
| `scheduled` | Access starts later | Wait for `starts_at`, or change it |
