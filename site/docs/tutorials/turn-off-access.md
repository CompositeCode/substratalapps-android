---
title: Turn off someone's access
description: Switch an application off for one person, immediately, with an undo window.
---

# Turn off someone's access

Turning access off stops a person from using one application right away. It doesn't delete anything, and you can turn it back on.

=== "In the app"

    1. Open the person: **Manage › Users**, then search by name, email or `usr_` ID.
    2. Open the **Access** tab. Each application has a switch.
    3. Turn the switch off and enter a short reason, which is recorded in the audit log.
    4. A message with **Undo** appears for 10 seconds.

    Some access is **Managed by Substratal**: free apps, and apps covered by a Standard subscription. You can only turn that kind off and on again, never edit or delete it.

    !!! info "Android status"
        The Access tab (ACX-01) and the switch (ACX-03) are planned.

=== "With the API"

    Find the entitlement, then patch it with the `ETag` you got:

    ```bash
    # 1. The person's entitlements (note the id and the ETag header)
    curl -si "$SUBSTRATAL_API/users/usr_…/entitlements?application_id=app_timetrack" \
      -H "Authorization: Bearer $TOKEN" -H "Substratal-Version: 1"

    # 2. Turn it off
    curl -s -X PATCH "$SUBSTRATAL_API/entitlements/ent_…" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Substratal-Version: 1" \
      -H "Content-Type: application/json" \
      -H 'If-Match: "…etag…"' \
      -d '{"status": "disabled", "disabled_reason": "Left the team"}'
    ```

    If someone else changed it first you get `409 version_conflict`: fetch it again and retry.

## What happens next

- The application is told at once through its `access.revoked` webhook, and should end the person's session.
- App tokens already issued expire within 5 minutes.
- The change is in the [audit log](../guides/audit.md) with your name and reason.
