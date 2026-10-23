---
title: Sign in
description: Sign in with your email, password and two-factor code, and what to do when it doesn't work.
---

# Sign in

1. Open the app and enter the **email** and **password** for your Substratal account.
2. If your account has two-factor authentication, enter the **6-digit code** from your authenticator app. If you've lost your device, choose **Use a recovery code** and enter one of the codes you saved when you turned two-factor on.
3. You land on **Home**.

The app keeps you signed in. Your session refreshes in the background for up to 30 days of inactivity, and 90 days at most.

!!! warning "Staff accounts: turn on two-factor"
    Anyone with administration rights should turn on two-factor authentication straight away, in **Account › Security**.

## If it doesn't work

| You see | What to do |
|---|---|
| *That email and password don't match.* | Check the email, then the password. For security, the app never says which one is wrong. |
| *Too many attempts.* | After 5 failed attempts in 15 minutes the account is locked for 15 minutes. Wait, then try again. |
| *This account is suspended.* | An administrator has suspended your account. Contact them. |
| *That code didn't work.* | Codes change every 30 seconds. Check that your phone's clock is set automatically, then try the current code. |
| *This version of the app is no longer supported.* | Update the app. The API version it uses has been retired (see [API versions](../developers/api/versions.md)). |
| *You're offline.* | Connect to the internet and try again. |

## Forgot your password

Password reset happens on the web: choose **Forgot password or need help signing in?** and follow the link that's emailed to you. The in-app reset screens (AUTH-05, AUTH-06) are planned.

See also: [Sign in screen](../reference/screens/auth-01.md) · [Two-factor screen](../reference/screens/auth-02.md)
