---
title: Users
description: Find, invite, suspend and sign out the people who use Substratal.
---

# Users

**Manage › Users** lists every account. You need `users.list` to see it and `users.manage` to change anything.

## Finding someone

- **Search** by name, email or ID. Type at least 3 characters; results update as you type.
- **Filter** by status: **Active**, **Invited** (hasn't accepted yet) or **Suspended**. Deleted accounts are visible only to `users.manage`.
- Pull down to refresh. The list loads more as you scroll.

## A person's page

Opening someone shows tabs for:

- **Access:** each app and whether it's on for them
- **Roles**
- **Sessions:** where they're signed in
- **Notifications**
- **Activity:** audit events about them

## Suspending and signing out

| Action | Effect | Undo |
|---|---|---|
| **Suspend** | Signs them out everywhere and blocks sign-in. Their access and roles are kept, so reactivating restores everything | **Reactivate** |
| **Sign out everywhere** | Ends every session without suspending them (`POST /users/{id}/sessions/revoke-all`) | They sign in again |
| **Revoke one session** | Ends one device's session | They sign in again |
| **Reset two-factor** | For someone who lost both their device and their recovery codes. Verify who they are first | They set up two-factor again |

The platform owner's account is **protected**: it can't be suspended, deleted or demoted.

## Privacy requests

- **Export** gives everything Substratal holds about the person, as a file (5 per person per day).
- **Erasure** schedules the account for deletion. You can cancel it until it runs.

See also: [Users screen](../reference/screens/usr-01.md) · [Invite a user](../tutorials/invite-a-user.md)
