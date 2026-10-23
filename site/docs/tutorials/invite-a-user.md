---
title: Invite a user
description: Create an account for someone and email them an invitation.
---

# Invite a user

You'll need the `users.manage` permission (superadmins have it).

=== "In the app"

    1. Go to **Manage › Users**. On a tablet, **Users** is in the side menu.
    2. Tap **Invite user**.
    3. Enter their email and, optionally, a display name.
    4. Tap **Send invitation**.

    They get an email with a link. When they open it, they set a password and their account becomes **Active**. Until then it shows as **Invited**.

    !!! info "Android status"
        The Users list is available. The **Invite user** form (USR-02) is planned; until it ships, use the API tab.

=== "With the API"

    ```bash
    curl -s -X POST "$SUBSTRATAL_API/users" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Substratal-Version: 1" \
      -H "Content-Type: application/json" \
      -H "Idempotency-Key: $(uuidgen)" \
      -d '{"email": "dana@example.com", "display_name": "Dana Ruiz"}'
    ```

    Returns `201` with the new User, `status: "invited"`. Sending the same `Idempotency-Key` again returns the same result instead of creating a second user.

## Then

- Give them access to an app: open the user, then **Access › Grant access**.
- Give them a staff role: open the user, then **Roles**. See [Permissions and roles](../reference/permissions.md).
- If the email didn't arrive: open the user and choose **Resend invitation** (`POST /users/{id}/invitation`).
