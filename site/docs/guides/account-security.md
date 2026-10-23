---
title: Your account and security
description: Two-factor authentication, sessions and signing out.
---

# Your account and security

**Account** shows your email, ID and status, with links to **Security**, **Devices & notifications**, **Privacy**, **Plan & Billing**, **Settings** and **Help**.

## Two-factor authentication

Turn it on in **Account › Security**:

1. Scan the code with an authenticator app.
2. Enter a code to confirm.
3. Save your **recovery codes** somewhere safe. Each one works once.

Staff accounts should always have two-factor on.

## Sessions

**Security** lists where you're signed in. You can sign out of one device, or **sign out everywhere else** and stay signed in here.

## Signing out

**Account › Sign out**. Signing out:

- ends the session on the server
- removes your session from this device
- stops push notifications to this device

## How the app protects your session

- Your session is stored encrypted with a key that lives in the device's secure hardware. It's never included in backups.
- Access tokens are kept in memory only and last 15 minutes. The app renews them automatically.
- If two refreshes ever happened at once, the API would treat it as theft and end the session. The app makes sure that never happens.
